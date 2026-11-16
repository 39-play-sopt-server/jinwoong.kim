package org.sopt.auth.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * CLASS: 요청 헤더의 JWT를 검증하고 현재 요청의 인증 정보를 SecurityContext에 저장합니다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer "; // 토큰 앞에 붙는 접두사; 끝 공백 포함

    private final JwtProvider jwtProvider; // 예: SecurityConfig에서 전달한 JWT 검증 객체

    /**
     * CONSTRUCTOR: 토큰 검증과 회원 ID 추출을 담당할 JwtProvider를 전달받습니다.
     */
    public JwtAuthenticationFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    /**
     * METHOD: JWT가 있으면 검증해 인증 정보를 등록하고 다음 필터로 요청을 전달합니다.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        // 1. Authorization 헤더에서 Bearer 접두사를 제외한 토큰을 추출합니다.
        String token = resolveToken(request);

        if (token != null) {
            try {
                // 2. 서명과 만료를 검증한 토큰에서 회원 ID를 가져옵니다.
                Long memberId = jwtProvider.getMemberId(token);

                // 3. 회원 ID를 principal로 담은 인증 완료 객체를 만듭니다. 권한 목록은 현재 비어 있습니다.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(memberId, null, List.of());
                // 현재 요청의 후속 필터와 Controller에서 인증 정보를 사용할 수 있도록 저장합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (JwtException | IllegalArgumentException e) {
                // 검증 실패 시 새 인증 정보를 등록하지 않습니다. 보호된 경로의 접근 거절은 후속 보안 처리에서 결정합니다.
            }
        }

        // 4. 토큰 유무와 관계없이 다음 필터로 넘겨 경로별 접근 권한을 판단하도록 합니다.
        filterChain.doFilter(request, response);
    }

    /**
     * METHOD: Authorization 헤더가 Bearer 접두사와 토큰으로 구성되면 토큰만 반환하고 그 외에는 null을 반환합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}