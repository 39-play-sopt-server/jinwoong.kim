package org.sopt.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.jwt.JwtAuthenticationFilter;
import org.springframework.http.HttpMethod;
import lombok.RequiredArgsConstructor;
import org.sopt.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.sopt.auth.oauth.CustomOAuth2UserService;
import org.sopt.auth.oauth.OAuth2LoginHandler;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizedClientRepository;
import org.springframework.security.web.context.NullSecurityContextRepository;

/**
 * CLASS: 경로별 인증 정책과 JWT 인증 필터의 실행 순서를 설정합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider; // 예: Spring이 주입한 JWT 발급·검증 객체
    private final MemberRepository members; // JWT 필터에서 회원과 토큰 버전을 확인할 저장소
    private final CustomOAuth2UserService oauth2UserService; // 플랫폼 정보와 우리 회원 연결 담당
    private final OAuth2LoginHandler oauth2LoginHandler; // 소셜 인증 성공·실패 응답 담당

    /**
     * METHOD: 인증 허용 경로, 세션 정책, 인증 실패 응답과 JWT 필터를 구성합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            @Value("${app.cookie.secure:true}") boolean secure)
            throws Exception {
        // 세션 대신 쿠키에 CSRF 검증 기준값을 저장
        CookieCsrfTokenRepository csrfRepository = new CookieCsrfTokenRepository();

        csrfRepository.setCookieCustomizer(cookie -> cookie
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
        );

        return http
                // 1. 상태 변경 요청에 쿠키 기반 CSRF 검증 적용
                .csrf(csrf -> csrf.csrfTokenRepository(csrfRepository))
                // 2. 기본 폼 로그인과 HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 3. 인증 정보를 HTTP 세션에 저장하거나 세션에서 조회하지 않도록 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // OAuth 과정의 임시 세션과 일반 API 인증 분리
                .securityContext(context -> context
                        .securityContextRepository(new NullSecurityContextRepository()))
                .requestCache(cache -> cache.disable())
                // GitHub 인증 결과를 우리 회원과 JWT 응답으로 연결
                .oauth2Login(oauth -> oauth
                        .authorizedClientRepository(new HttpSessionOAuth2AuthorizedClientRepository())
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService))
                        .successHandler(oauth2LoginHandler)
                        .failureHandler(oauth2LoginHandler))
                // 4. 공개 API와 인증이 필요한 API의 접근 정책 설정
                .authorizeHttpRequests(auth -> auth
                        // OAuth 로그인 시작과 콜백은 프레임워크의 state 검증으로 보호
                        .requestMatchers(HttpMethod.GET,
                                "/oauth2/authorization/*", "/login/oauth2/code/*").permitAll()
                        // 로그인 전에도 CSRF 토큰 조회 허용
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/auth/csrf"
                        ).permitAll()
                        // 회원가입, 로그인, 재발급, POST 요청만 Access Token 없이 접근 허용
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/auth/signup",
                                "/api/v1/auth/login",
                                "/api/v1/auth/reissue"
                        ).permitAll()
                        // 배포 확인에 사용하는 Swagger 화면과 문서 접근 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**"
                        ).permitAll()
                        // 전체 회원 목록 조회는 관리자만 허용
                        .requestMatchers(HttpMethod.GET, "/api/v1/members")
                        .hasRole("ADMIN")
                        // 나머지 API는 인증 필요
                        .anyRequest().authenticated())
                // 5. 인증 없이 보호된 경로에 접근하면 401 응답
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                        // CSRF 검증 실패나 접근 권한 부족은 403
                        .accessDeniedHandler((request, response, exception) ->
                                response.setStatus(HttpStatus.FORBIDDEN.value())
                        )
                )
                // 6. UsernamePasswordAuthenticationFilter 앞에서 JWT 인증 필터 실행
                .addFilterBefore(
                        new JwtAuthenticationFilter(members, jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
