package org.sopt.auth.jwt;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * CLASS: JWT 발급, 검증 기능의 호출 창구와 감당 객체 위임
 */
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtIssuer jwtIssuer; // JWT 발급 담당 객체
    private final JwtParser jwtParser; // JWT 검증·파싱 담당 객체

    /**
     * METHOD: Access Token을 검증하고 회원 ID를 반환
     */
    public Long getMemberId(String token) {
        return getMemberId(token, TokenType.ACCESS);
    }

    /**
     * METHOD: Refresh Token을 검증하고 회원 ID를 반환합니다
     */
    public Long getRefreshMemberId(String token) {
        return getMemberId(token, TokenType.REFRESH);
    }

    /**
     * METHOD: 서명, 만료, 토큰 종류를 검증한 뒤 회원 ID를 반환
     */
    private Long getMemberId(String token, TokenType expectedType) {
        Claims claims = parseClaims(token, expectedType);
        return Long.valueOf(claims.getSubject());
    }

    /**
     * METHOD: Refresh Token을 검증하고 저장에 필요한 클레임을 반환
     */
    public Claims getRefreshClaims(String token) {
        return parseClaims(token, TokenType.REFRESH);
    }

    /**
     * METHOD: JWT검증과 파싱을 JwTParser에 위임
     */
    private Claims parseClaims(String token, TokenType expectedType) {
        return jwtParser.parse(token,expectedType);
    }

    /**
     * METHOD: 회원 ID와 현재 토큰 버전을 담은 Access Token을 생성
     */
    public String createAccessToken(Long memberId, long tokenVersion) {
        return jwtIssuer.createAccessToken(memberId, tokenVersion);
    }

    /**
     * METHOD: Access Token 재발급에 사용할 Refresh Token을 생성
     */
    public String createRefreshToken(Long memberId, long tokenVersion) {
        return jwtIssuer.createRefreshToken(memberId, tokenVersion);
    }

    /**
     * METHOD: Access Token을 검증하고 클레임을 반환
     */
    public Claims getAccessClaims(String token) {
        return parseClaims(token, TokenType.ACCESS);
    }
}