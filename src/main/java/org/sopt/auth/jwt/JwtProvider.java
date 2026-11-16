package org.sopt.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;

import org.sopt.auth.dto.TokenPair;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CLASS: JWT 발급과 서명·만료 검증 및 회원 ID 추출을 담당
 */
@Component
public class JwtProvider {

    private final SecretKey secretKey; // JWT 서명 생성과 검증에 사용하는 비밀키
    private final long accessTokenExpiration; // 예: 1800000밀리초 = 30분의 유효 기간

    private final long refreshTokenExpiration; // Refresh Token의 유효 기간, 밀리초 단위

    /**
     * CONSTRUCTOR: 설정의 Base64 비밀키를 서명용 키로 변환하고 유효 기간을 초기화
     * Lombok쓰지 않은 이융는 Base64 문자열을 SecretKey로 변환하는 초기화 작업을 하기때문에.
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

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
     * METHOD: 서명, 만료와 토큰 종류를 검증한 뒤 클레임을 반환
     */
    private Claims parseClaims(String token, TokenType expectedType) {
        // 1. 서명과 만료를 검증
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 2. 사용 목적에 맞는 토큰인지 확인
        String tokenType = claims.get("tokenType", String.class);

        if (!expectedType.name().equals(tokenType)) {
            throw new JwtException("토큰의 종류가 올바르지 않습니다.");
        }

        return claims;
    }

    /**
     * METHOD: 일반 API 접근에 사용할 AccessToken 을 생성
     */
    public String createAccessToken(Long memberId) {
        return createToken(memberId, TokenType.ACCESS, accessTokenExpiration);
    }

    /**
     * METHOD: Access Token 재발급에 사용할 Refresh Token을 생성
     */
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, TokenType.REFRESH, refreshTokenExpiration);
    }

    /**
     * METHOD: 회원 ID, 토큰 종류와 유효 기간을 받아 서명된 JWT를 생성합니다.
     */
    private String createToken(
            Long memberId,
            TokenType tokenType,
            long expiration
    ) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 토큰의 주인인 회원 ID
                .claim("tokenType", tokenType.name()) // ACCESS또는 REFRESH
                .id(UUID.randomUUID().toString()) // 개별 토큰의 고유 식별자
                .issuedAt(now) // 발급 시각
                .expiration(new Date(now.getTime() + expiration)) // 만료 시간
                .signWith(secretKey) // 비밀키로 서명
                .compact(); // JWT 문자열로 변환
    }
}