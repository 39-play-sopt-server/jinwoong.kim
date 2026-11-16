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

/**
 * CLASS: 경로별 인증 정책과 JWT 인증 필터의 실행 순서를 설정합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider; // 예: Spring이 주입한 JWT 발급·검증 객체

    /**
     * CONSTRUCTOR: JWT 인증 필터에 전달할 JwtProvider를 생성자로 주입받습니다.
     */
    public SecurityConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * METHOD: 인증 허용 경로, 세션 정책, 인증 실패 응답과 JWT 필터를 구성합니다.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 1. 인증 토큰을 브라우저가 자동 첨부하는 쿠키 대신 Authorization 헤더로 받는 현재 구조에 맞춰 CSRF 검사 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                // 2. 기본 폼 로그인과 HTTP Basic 인증 비활성화
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                // 3. 인증 정보를 HTTP 세션에 저장하거나 세션에서 조회하지 않도록 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 4. /api/v1/auth/** 전체는 인증 없이 허용하고 나머지 경로는 인증 필요
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .anyRequest().authenticated())
                // 5. 인증 없이 보호된 경로에 접근하면 401 응답
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                // 6. UsernamePasswordAuthenticationFilter 앞에서 JWT 인증 필터 실행
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
