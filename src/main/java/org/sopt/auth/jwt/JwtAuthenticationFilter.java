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

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;

/**
 * CLASS: 요청 헤더의 JWT를 검증하고 현재 요청의 인증 정보를 SecurityContext에 저장합니다.
 */
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final MemberRepository members; // 회원 존재 여부와 현재 토큰 버전을 조회

    private static final String BEARER_PREFIX = "Bearer "; // 토큰 앞에 붙는 접두사; 끝 공백 포함

    private final JwtProvider jwtProvider; // 예: SecurityConfig에서 전달한 JWT 검증 객체

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
                // 2. Access Token의 서명. 만료와 종류를 검증
                Claims claims = jwtProvider.getAccessClaims(token);
                Long memberId = Long.valueOf(claims.getSubject());

                // 3. 토큰의 주인인 회원이 현재도 존재하는지 확인
                Member member = members.findById(memberId)
                        .orElseThrow(() -> new JwtException("존재하지 않는 회원입니다."));

                // 4. 발급 당시 버전과 현재 DB 버전이 같은지 확인
                Long tokenVersion = claims.get("tokenVersion", Long.class);

                if (tokenVersion == null
                        || tokenVersion.longValue() != member.getTokenVersion()) {
                    throw new JwtException("무효화된 토큰입니다.");
                }

                // 5. 모든 검증을 통화하면 현재 요청의 인증 정보를 등록
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        member.getId(), null, List.of()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException | IllegalArgumentException e) {
                // 6. 검증에 실패한 요청의 인증 정보를 제거
                SecurityContextHolder.clearContext();
            }
        }

        // 7. 토큰 유무와 관계없이 다음 필터로 넘겨 경로별 접근 권한을 판단하도록 합니다.
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