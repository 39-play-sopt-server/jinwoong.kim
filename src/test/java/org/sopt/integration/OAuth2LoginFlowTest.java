package org.sopt.integration;

import com.jayway.jsonpath.JsonPath;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS: 실제 HTTP 제공자 대역과 테스트 DB로 OAuth state·회원 연결·JWT 응답 검증
 * GitHub 공식 token/user/emails 응답 형식을 사용한 대역이며 실제 GitHub 동의 화면은 별도 확인 대상
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class OAuth2LoginFlowTest {

    private record Scenario(long id, String email, boolean verified, boolean emailFailure) {}

    private static final AtomicReference<Scenario> scenario = new AtomicReference<>();
    private static final AtomicInteger tokenCalls = new AtomicInteger();
    private static final AtomicInteger emailCalls = new AtomicInteger();
    private static final HttpServer provider = startProvider();

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbc;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private MemberRepository members;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String prefix = "oauth-test-" + UUID.randomUUID();
    private final HttpClient client = HttpClient.newBuilder()
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();

    @DynamicPropertySource
    static void providerProperties(DynamicPropertyRegistry properties) {
        String base = "http://127.0.0.1:" + provider.getAddress().getPort();
        properties.add("spring.security.oauth2.client.provider.github.authorization-uri", () -> base + "/authorize");
        properties.add("spring.security.oauth2.client.provider.github.token-uri", () -> base + "/token");
        properties.add("spring.security.oauth2.client.provider.github.user-info-uri", () -> base + "/user");
        properties.add("app.oauth.github-api-base-url", () -> base);
        properties.add("app.cookie.secure", () -> "false");
    }

    @BeforeEach
    void prepareScenario() {
        assertEquals("assignment_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
        scenario.set(new Scenario(Math.abs(UUID.randomUUID().getMostSignificantBits()),
                prefix + "@example.com", true, false));
        tokenCalls.set(0);
        emailCalls.set(0);
    }

    @AfterEach
    void removeOnlyScenarioData() {
        List<Long> ids = jdbc.queryForList("SELECT id FROM member WHERE email LIKE ?", Long.class, prefix + "%");
        for (Long id : ids) {
            jdbc.update("DELETE FROM refresh_token WHERE member_id = ?", id);
            jdbc.update("DELETE FROM social_account WHERE member_id = ?", id);
            jdbc.update("DELETE FROM member WHERE id = ?", id);
        }
    }

    @AfterAll
    static void stopProvider() {
        provider.stop(0);
    }

    @Test
    void successfulCallbackIssuesLocalJwtAndRefreshCookieWithoutSessionAuthentication() throws Exception {
        String state = beginLogin();
        HttpResponse<String> response = callback(state);
        assertEquals(200, response.statusCode(), response.body());
        assertEquals("LOGIN_SUCCESS", JsonPath.read(response.body(), "$.code"));
        assertEquals("no-store", response.headers().firstValue("Cache-Control").orElseThrow());

        String authorization = response.headers().firstValue("Authorization").orElseThrow();
        Long memberId = jwtProvider.getMemberId(authorization.substring(7));
        Member member = members.findById(memberId).orElseThrow();
        assertNull(member.getPassword());
        assertEquals(scenario.get().email(), member.getEmail());
        assertEquals(1L, jdbc.queryForObject("SELECT COUNT(*) FROM social_account WHERE member_id = ?", Long.class, memberId));
        assertEquals(1L, jdbc.queryForObject("SELECT COUNT(*) FROM refresh_token WHERE member_id = ?", Long.class, memberId));
        assertEquals(1, tokenCalls.get());
        assertEquals(1, emailCalls.get());
        assertTrue(response.headers().allValues("Set-Cookie").stream().anyMatch(value ->
                value.startsWith("refreshToken=") && value.contains("HttpOnly") && value.contains("SameSite=Strict")));

        assertEquals(401, get("/api/v1/posts", null).statusCode(), "임시 OAuth 세션만으로 API 인증 불가");
        assertEquals(200, get("/api/v1/posts", authorization).statusCode());

        String csrf = csrfToken(null);
        HttpResponse<String> reissue = send("POST", "/api/v1/auth/reissue", null, csrf, null);
        assertEquals(200, reissue.statusCode(), reissue.body());
        assertEquals(memberId, jwtProvider.getMemberId(reissue.headers().firstValue("Authorization").orElseThrow().substring(7)));

        assertEquals(401, callback(state).statusCode(), "이미 소비한 OAuth state 재사용 거절");
        assertEquals(1, tokenCalls.get(), "state 재사용 시 GitHub 토큰 교환 미실행");
    }

    @Test
    void missingStateIsRejectedBeforeTokenExchange() throws Exception {
        HttpResponse<String> response = get("/login/oauth2/code/github?code=test-code", null);
        assertEquals(401, response.statusCode());
        assertEquals("SOCIAL_LOGIN_FAILED", JsonPath.read(response.body(), "$.code"));
        assertEquals(0, tokenCalls.get());
    }

    @Test
    void mismatchedStateIsRejectedBeforeTokenExchange() throws Exception {
        beginLogin();
        assertEquals(401, callback("wrong-state").statusCode());
        assertEquals(0, tokenCalls.get());
        assertEquals(0L, memberCount());
    }

    @Test
    void localEmailCollisionNeverLinksOrOverwritesExistingAccount() throws Exception {
        Member local = members.saveAndFlush(new Member(
                "기존 일반 회원", scenario.get().email(), passwordEncoder.encode("local-password")
        ));
        HttpResponse<String> response = callback(beginLogin());
        assertEquals(409, response.statusCode(), response.body());
        assertEquals("SOCIAL_EMAIL_CONFLICT", JsonPath.read(response.body(), "$.code"));
        assertTrue(response.headers().firstValue("Authorization").isEmpty());
        assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM social_account WHERE member_id = ?", Long.class, local.getId()));
        assertTrue(passwordEncoder.matches("local-password", members.findById(local.getId()).orElseThrow().getPassword()));
        assertEquals(1L, memberCount());

        HttpResponse<String> login = send("POST", "/api/v1/auth/login", null, csrfToken(null),
                "{\"email\":\"" + scenario.get().email() + "\",\"password\":\"local-password\"}");
        assertEquals(200, login.statusCode(), "이메일 충돌 후에도 기존 비밀번호 로그인 유지");
    }

    @Test
    void repeatedProviderIdentityReusesMemberEvenIfProviderEmailChanges() throws Exception {
        HttpResponse<String> first = callback(beginLogin());
        assertEquals(200, first.statusCode(), first.body());
        Long firstId = jwtProvider.getMemberId(first.headers().firstValue("Authorization").orElseThrow().substring(7));
        Scenario previous = scenario.get();
        scenario.set(new Scenario(previous.id(), prefix + "-changed@example.com", true, false));

        HttpResponse<String> second = callback(beginLogin());
        assertEquals(200, second.statusCode(), second.body());
        assertEquals(firstId, jwtProvider.getMemberId(second.headers().firstValue("Authorization").orElseThrow().substring(7)));
        assertEquals(1L, memberCount());
        assertEquals(1L, jdbc.queryForObject("SELECT COUNT(*) FROM social_account WHERE member_id = ?", Long.class, firstId));
    }

    @Test
    void unverifiedEmailIsRejectedWithoutCreatingMember() throws Exception {
        Scenario previous = scenario.get();
        scenario.set(new Scenario(previous.id(), previous.email(), false, false));
        HttpResponse<String> response = callback(beginLogin());
        assertEquals(401, response.statusCode(), response.body());
        assertEquals("SOCIAL_LOGIN_FAILED", JsonPath.read(response.body(), "$.code"));
        assertEquals(0L, memberCount());
    }

    @Test
    void providerEmailFailureDoesNotCreateMemberOrExposeProviderDetails() throws Exception {
        Scenario previous = scenario.get();
        scenario.set(new Scenario(previous.id(), previous.email(), true, true));
        HttpResponse<String> response = callback(beginLogin());
        assertEquals(401, response.statusCode(), response.body());
        assertFalse(response.body().contains("upstream-sensitive-detail"));
        assertEquals(0L, memberCount());
    }

    @Test
    void userCancellationIsHandledWithoutTokenExchange() throws Exception {
        String state = beginLogin();
        HttpResponse<String> response = get("/login/oauth2/code/github?error=access_denied&state=" + encode(state), null);
        assertEquals(401, response.statusCode());
        assertEquals(0, tokenCalls.get());
        assertEquals(0L, memberCount());
    }

    private long memberCount() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM member WHERE email LIKE ?", Long.class, prefix + "%");
    }

    private String beginLogin() throws Exception {
        HttpResponse<String> response = get("/oauth2/authorization/github", null);
        assertEquals(302, response.statusCode(), response.body());
        URI location = URI.create(response.headers().firstValue("Location").orElseThrow());
        assertEquals(provider.getAddress().getPort(), location.getPort());
        assertTrue(location.getQuery().contains("scope="));
        return java.util.Arrays.stream(location.getRawQuery().split("&"))
                .filter(value -> value.startsWith("state="))
                .map(value -> URLDecoder.decode(value.substring(6), StandardCharsets.UTF_8))
                .findFirst().orElseThrow();
    }

    private HttpResponse<String> callback(String state) throws Exception {
        return get("/login/oauth2/code/github?code=test-code&state=" + encode(state), null);
    }

    private String csrfToken(String authorization) throws Exception {
        HttpResponse<String> response = get("/api/v1/auth/csrf", authorization);
        assertEquals(200, response.statusCode());
        return response.headers().firstValue("X-XSRF-TOKEN").orElseThrow();
    }

    private HttpResponse<String> get(String path, String authorization) throws Exception {
        return send("GET", path, authorization, null, null);
    }

    private HttpResponse<String> send(String method, String path, String authorization, String csrf, String body) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + path));
        if (authorization != null) builder.header("Authorization", authorization);
        if (csrf != null) builder.header("X-XSRF-TOKEN", csrf);
        if (body != null) builder.header("Content-Type", "application/json");
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static HttpServer startProvider() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/token", exchange -> {
                tokenCalls.incrementAndGet();
                String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                if (!body.contains("code=test-code")) {
                    respond(exchange, 400, "{\"error\":\"invalid_grant\"}");
                    return;
                }
                respond(exchange, 200, "{\"access_token\":\"fake-github-token\",\"token_type\":\"bearer\",\"scope\":\"read:user user:email\"}");
            });
            server.createContext("/user/emails", exchange -> {
                emailCalls.incrementAndGet();
                if (!"Bearer fake-github-token".equals(exchange.getRequestHeaders().getFirst("Authorization"))) {
                    respond(exchange, 401, "{}");
                    return;
                }
                Scenario current = scenario.get();
                if (current.emailFailure()) {
                    respond(exchange, 500, "{\"message\":\"upstream-sensitive-detail\"}");
                } else {
                    respond(exchange, 200, "[{\"email\":\"" + current.email() + "\",\"primary\":true,\"verified\":" + current.verified() + ",\"visibility\":\"private\"}]");
                }
            });
            server.createContext("/user", exchange -> respond(exchange, 200,
                    "{\"id\":" + scenario.get().id() + ",\"login\":\"oauth-test\",\"name\":\"OAuth 테스트 회원\"}"));
            server.start();
            return server;
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }
}
