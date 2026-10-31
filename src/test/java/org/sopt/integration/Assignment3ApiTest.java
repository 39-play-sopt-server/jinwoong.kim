package org.sopt.integration;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CLASS: 임의 포트의 실제 서버에 HTTP 요청을 보내 assignment3 API와 DB 반영을 검증합니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class Assignment3ApiTest {
    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbc;

    private final List<Long> memberIds = new ArrayList<>(); // 예: 테스트 후 정리할 회원 ID
    private final String prefix = "api-" + UUID.randomUUID(); // 예: 다른 실행과 구분할 제목
    private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();

    /**
     * METHOD: 개발 DB에 테스트 데이터를 쓰지 않도록 실제 연결된 DB를 확인합니다.
     */
    @BeforeEach
    void checkDatabase() {
        assertEquals("assignment_test", jdbc.queryForObject("SELECT DATABASE()", String.class));
    }

    /**
     * METHOD: 이번 테스트가 만든 회원의 좋아요와 게시글만 삭제합니다.
     */
    @AfterEach
    void cleanUp() {
        for (Long id : memberIds) {
            jdbc.update("DELETE FROM post_like WHERE member_id = ?", id);
            jdbc.update("DELETE FROM post_like WHERE post_id IN (SELECT id FROM post WHERE member_id = ?)", id);
            jdbc.update("DELETE FROM post WHERE member_id = ?", id);
        }
        for (Long id : memberIds) {
            jdbc.update("DELETE FROM member WHERE id = ?", id);
        }
    }

    /**
     * METHOD: 회원 생성의 이름 정규화와 삭제·재삭제 응답을 검증합니다.
     */
    @Test
    void memberCreationAndDeletion() throws Exception {
        long memberId = createMember("  API 회원  ");
        assertEquals("API 회원", jdbc.queryForObject("SELECT name FROM member WHERE id = ?", String.class, memberId));
        assertStatus(200, request("DELETE", "/members/" + memberId, null));
        assertStatus(404, request("DELETE", "/members/" + memberId, null));
    }

    /**
     * METHOD: 게시글 CRUD에서 작성자·생성일·조회수·본문 저장을 검증합니다.
     */
    @Test
    void postCrudIncludesAuthorAndPreservesAuthorOnUpdate() throws Exception {
        long author = createMember("작성자");
        long other = createMember("다른 회원");
        long postId = createPost(author, prefix + "-crud", "GENERAL");
        var detail = assertStatus(200, request("GET", "/posts/" + postId, null));
        assertEquals(author, number(detail, "$.data.memberId"));
        assertEquals("작성자", detail.read("$.data.authorName"));
        assertNotNull(detail.read("$.data.createdAt"));
        assertEquals(1L, number(detail, "$.data.viewCount"));
        assertEquals(0L, number(detail, "$.data.likeCount"));

        assertStatus(200, request("PUT", "/posts/" + postId,
                "{\"title\":\"수정 제목\",\"content\":\"수정 본문\",\"memberId\":" + other + "}"));
        detail = assertStatus(200, request("GET", "/posts/" + postId, null));
        assertEquals("수정 제목", detail.read("$.data.title"));
        assertEquals("수정 본문", detail.read("$.data.content"));
        assertEquals(author, number(detail, "$.data.memberId"));
        assertEquals(2L, number(detail, "$.data.viewCount"));

        assertStatus(200, request("DELETE", "/posts/" + postId, null));
        assertStatus(404, request("GET", "/posts/" + postId, null));
    }

    /**
     * METHOD: 최신순 페이지 조회와 작성자·카테고리·제목 조건의 AND 조합을 검증합니다.
     */
    @Test
    void paginationAndDynamicFilters() throws Exception {
        long author = createMember("검색 작성자");
        long other = createMember("다른 작성자");
        long first = createPost(author, prefix + "-java-old", "QUESTION");
        createPost(author, prefix + "-spring", "GENERAL");
        long last = createPost(author, prefix + "-java-new", "QUESTION");
        createPost(other, prefix + "-java-other", "QUESTION");
        // 시각을 고정해 최신순과 동일 생성일의 ID 내림차순을 함께 검증합니다.
        jdbc.update("UPDATE post SET created_at = '2026-01-01 00:00:00' WHERE member_id = ?", author);
        jdbc.update("UPDATE post SET created_at = '2025-01-01 00:00:00' WHERE id = ?", first);

        String path = "/posts?memberId=" + author + "&category=QUESTION&title=java&size=1&page=";
        var page0 = assertStatus(200, request("GET", path + "0", null));
        assertEquals(2L, number(page0, "$.data.totalElements"));
        assertEquals(2L, number(page0, "$.data.totalPages"));
        assertEquals(last, number(page0, "$.data.content[0].id"));
        assertTrue(page0.read("$.data.hasNext", Boolean.class));

        var page1 = assertStatus(200, request("GET", path + "1", null));
        assertEquals(first, number(page1, "$.data.content[0].id"));
        assertFalse(page1.read("$.data.hasNext", Boolean.class));
        var empty = assertStatus(200, request("GET", path + "99", null));
        assertTrue(empty.read("$.data.content", List.class).isEmpty());
        assertEquals(0L, jdbc.queryForObject("SELECT view_count FROM post WHERE id = ?", Long.class, first));

        var allCategories = assertStatus(200, request("GET", "/posts?memberId=" + author + "&size=5", null));
        assertEquals(3L, number(allCategories, "$.data.totalElements"));
        assertEquals(last, number(allCategories, "$.data.content[0].id"));
    }

    /**
     * METHOD: LIKE의 특수문자도 검색어에 포함된 실제 문자로 검색하는지 검증합니다.
     */
    @Test
    void titleSearchEscapesSqlWildcards() throws Exception {
        long author = createMember("문자 검색 회원");
        long exact = createPost(author, prefix + "-!%_", "GENERAL");
        createPost(author, prefix + "-!abcX", "GENERAL");
        String title = URLEncoder.encode("!%_", StandardCharsets.UTF_8);
        var response = assertStatus(200, request("GET", "/posts?memberId=" + author + "&title=" + title, null));
        assertEquals(1L, number(response, "$.data.totalElements"));
        assertEquals(exact, number(response, "$.data.content[0].id"));
    }

    /**
     * METHOD: 중복 좋아요는 하나로 유지하고 기록 없는 취소는 404로 응답하는지 검증합니다.
     */
    @Test
    void likesAndMissingLikeCancellation() throws Exception {
        long author = createMember("좋아요 작성자");
        long reader = createMember("좋아요 회원");
        long nonLiker = createMember("누르지 않은 회원");
        long postId = createPost(author, prefix + "-likes", "GENERAL");
        String path = "/posts/" + postId + "/likes/" + reader;
        assertStatus(200, request("PUT", path, null));
        assertStatus(200, request("PUT", path, null));
        var missing = assertStatus(404, request("DELETE", "/posts/" + postId + "/likes/" + nonLiker, null));
        assertEquals("좋아요를 누른 흔적이 없습니다.", missing.read("$.message"));
        var detail = assertStatus(200, request("GET", "/posts/" + postId, null));
        assertEquals(1L, number(detail, "$.data.likeCount"));
        assertStatus(200, request("DELETE", path, null));
        assertStatus(404, request("DELETE", path, null));
        detail = assertStatus(200, request("GET", "/posts/" + postId, null));
        assertEquals(0L, number(detail, "$.data.likeCount"));
    }

    /**
     * METHOD: 글이 남은 회원은 탈퇴할 수 없고 글 삭제 시 좋아요도 정리되는지 검증합니다.
     */
    @Test
    void deletionRespectsForeignKeys() throws Exception {
        long author = createMember("탈퇴 작성자");
        long reader = createMember("좋아요 독자");
        long postId = createPost(author, prefix + "-delete", "GENERAL");
        assertStatus(200, request("PUT", "/posts/" + postId + "/likes/" + reader, null));
        assertStatus(409, request("DELETE", "/members/" + author, null));
        assertStatus(200, request("DELETE", "/posts/" + postId, null));
        assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM post_like WHERE post_id = ?", Long.class, postId));
        assertStatus(200, request("DELETE", "/members/" + author, null));
    }

    /**
     * METHOD: 잘못된 입력과 존재하지 않는 회원으로 작성할 때 오류 응답을 검증합니다.
     */
    @Test
    void invalidInputReturnsClientErrors() throws Exception {
        assertStatus(400, request("POST", "/members", "{\"name\":\"  \"}"));
        long author = createMember("검증 회원");
        assertStatus(400, request("POST", "/posts",
                "{\"title\":\" \",\"content\":\"본문\",\"category\":\"GENERAL\",\"memberId\":" + author + "}"));
        assertStatus(400, request("POST", "/posts",
                "{\"title\":\"제목\",\"content\":\"본문\",\"category\":\"GENERAL\"}"));
        assertStatus(404, request("POST", "/posts",
                "{\"title\":\"제목\",\"content\":\"본문\",\"category\":\"GENERAL\",\"memberId\":9223372036854775807}"));
        assertStatus(400, request("GET", "/posts?page=-1", null));
        assertStatus(400, request("GET", "/posts?size=0", null));
        assertStatus(400, request("GET", "/posts?size=101", null));
        assertStatus(400, request("GET", "/posts?memberId=0", null));
    }

    /**
     * METHOD: Spring 기본 예외가 500으로 바뀌지 않고 상태·식별 코드·Allow 헤더를 유지하는지 검증합니다.
     */
    @Test
    void frameworkExceptionsKeepTheirHttpStatus() throws Exception {
        var missing = assertStatus(404, request("GET", "/missing-route", null));
        assertEquals("RESOURCE_NOT_FOUND", missing.read("$.code"));

        var badId = assertStatus(400, request("GET", "/posts/not-a-number", null));
        assertEquals("INVALID_PARAMETER_TYPE", badId.read("$.code"));
        var badJson = assertStatus(400, request("POST", "/members", "{"));
        assertEquals("INVALID_REQUEST_BODY", badJson.read("$.code"));

        long member = createMember("메서드 확인 회원");
        var methodResponse = request("PATCH", "/members/" + member, null);
        var wrongMethod = assertStatus(405, methodResponse);
        assertEquals("METHOD_NOT_ALLOWED", wrongMethod.read("$.code"));
        assertTrue(methodResponse.headers().firstValue("Allow").orElseThrow().contains("DELETE"));

        var unsupported = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/members"))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("plain text"))
                .build();
        var media = assertStatus(415, client.send(unsupported, HttpResponse.BodyHandlers.ofString()));
        assertEquals("UNSUPPORTED_MEDIA_TYPE", media.read("$.code"));
    }

    /**
     * METHOD: JSON을 거부한 생성 요청은 데이터 저장 전에 406으로 거절하는지 검증합니다.
     */
    @Test
    void unsupportedAcceptDoesNotCreateResources() throws Exception {
        String memberName = prefix + "-accept";
        var memberRequest = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/members"))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "text/plain").header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"" + memberName + "\"}"))
                .build();
        var response = client.send(memberRequest, HttpResponse.BodyHandlers.ofString());
        // 실패하더라도 테스트가 만든 회원만 정리할 수 있도록 ID를 기록합니다.
        memberIds.addAll(jdbc.queryForList("SELECT id FROM member WHERE name = ?", Long.class, memberName));
        assertEquals(406, response.statusCode());
        assertTrue(response.body().isEmpty());
        assertTrue(memberIds.isEmpty(), "거절된 요청으로 회원이 저장되면 안 됩니다.");

        long author = createMember("Accept 테스트 회원");
        String title = prefix + "-rejected-post";
        var postRequest = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1/posts"))
                .timeout(Duration.ofSeconds(20))
                .header("Accept", "text/plain").header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"title\":\"" + title
                        + "\",\"content\":\"본문\",\"category\":\"GENERAL\",\"memberId\":" + author + "}"))
                .build();
        assertEquals(406, client.send(postRequest, HttpResponse.BodyHandlers.ofString()).statusCode());
        assertEquals(0L, jdbc.queryForObject("SELECT COUNT(*) FROM post WHERE member_id = ?", Long.class, author));
    }

    /**
     * METHOD: 좋아요만 남은 회원의 탈퇴가 도메인 오류 코드로 거절되는지 검증합니다.
     */
    @Test
    void remainingLikesBlockMemberDeletionWithDomainCode() throws Exception {
        long author = createMember("좋아요 글 작성자");
        long reader = createMember("좋아요만 가진 회원");
        long postId = createPost(author, prefix + "-withdraw", "GENERAL");
        String likePath = "/posts/" + postId + "/likes/" + reader;
        assertStatus(200, request("PUT", likePath, null));
        var rejected = assertStatus(409, request("DELETE", "/members/" + reader, null));
        assertEquals("MEMBER_HAS_LIKES", rejected.read("$.code"));
        assertStatus(200, request("DELETE", likePath, null));
        assertStatus(200, request("DELETE", "/members/" + reader, null));
    }

    /**
     * METHOD: 생성된 OpenAPI 문서가 회원·게시글·좋아요 그룹과 실제 응답 코드를 포함하는지 검증합니다.
     */
    @Test
    void swaggerDocumentsEveryDomainAndResponse() throws Exception {
        var request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/v3/api-docs"))
                .timeout(Duration.ofSeconds(20)).GET().build();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), response.body());
        var doc = JsonPath.parse(response.body());
        assertEquals("MEMBER", doc.read("$.paths['/api/v1/members'].post.tags[0]"));
        assertEquals("POST", doc.read("$.paths['/api/v1/posts'].post.tags[0]"));
        assertEquals("LIKE", doc.read("$.paths['/api/v1/posts/{postId}/likes/{memberId}'].put.tags[0]"));
        assertNotNull(doc.read("$.paths['/api/v1/members'].post.summary"));
        assertNotNull(doc.read("$.paths['/api/v1/members/{memberId}'].delete.responses['409']"));
        assertNotNull(doc.read("$.paths['/api/v1/posts'].post.responses['201']"));
        assertNotNull(doc.read("$.paths['/api/v1/posts'].post.responses['406']"));
        assertNotNull(doc.read("$.paths['/api/v1/posts/{postId}/likes/{memberId}'].delete.responses['404']"));
    }

    /**
     * METHOD: 회원을 HTTP로 생성하고 정리할 ID를 기록합니다.
     */
    private long createMember(String name) throws Exception {
        var response = assertStatus(201, request("POST", "/members", "{\"name\":\"" + name + "\"}"));
        long id = number(response, "$.data.id");
        memberIds.add(id);
        return id;
    }

    /**
     * METHOD: 고유 제목의 게시글을 HTTP로 생성하고 DB에서 ID를 확인합니다.
     */
    private long createPost(long author, String title, String category) throws Exception {
        var http = request("POST", "/posts", "{\"title\":\"" + title
                + "\",\"content\":\"테스트 본문\",\"category\":\"" + category + "\",\"memberId\":" + author + "}");
        var body = assertStatus(201, http);
        long id = number(body, "$.data.id");
        assertEquals("POST_CREATED", body.read("$.code"));
        assertEquals("/api/v1/posts/" + id, http.headers().firstValue("Location").orElseThrow());
        assertEquals(title, jdbc.queryForObject("SELECT title FROM post WHERE id = ?", String.class, id));
        return id;
    }

    /**
     * METHOD: 실제 로컬 서버에 JSON HTTP 요청을 전송합니다.
     */
    private HttpResponse<String> request(String method, String path, String body) throws Exception {
        var builder = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/v1" + path))
                .timeout(Duration.ofSeconds(20)).header("Content-Type", "application/json");
        builder.method(method, body == null ? HttpRequest.BodyPublishers.noBody() : HttpRequest.BodyPublishers.ofString(body));
        return client.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    /**
     * METHOD: 상태 코드와 공통 성공 여부를 검증하고 JSON 본문을 반환합니다.
     */
    private DocumentContext assertStatus(int expected, HttpResponse<String> response) {
        assertEquals(expected, response.statusCode(), response.body());
        DocumentContext body = JsonPath.parse(response.body());
        assertEquals(expected < 400, body.read("$.success", Boolean.class));
        assertFalse(body.read("$.code", String.class).isBlank(), "모든 JSON 응답에 식별 코드가 있어야 합니다.");
        return body;
    }

    /**
     * METHOD: JSON 숫자를 실제 파싱된 정수 타입에 관계없이 long으로 읽습니다.
     */
    private long number(DocumentContext body, String path) {
        return ((Number) body.read(path)).longValue();
    }
}
