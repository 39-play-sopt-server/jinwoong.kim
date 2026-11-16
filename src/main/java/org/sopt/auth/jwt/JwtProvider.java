package org.sopt.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CLASS: JWT 발급과 서명·만료 검증 및 회원 ID 추출을 담당합니다.
 */
@Component
public class JwtProvider {

    private final SecretKey secretKey; // JWT 서명 생성과 검증에 사용하는 비밀키
    private final long accessTokenExpiration; // 예: 1800000밀리초 = 30분의 유효 기간

    /**
     * CONSTRUCTOR: 설정의 Base64 비밀키를 서명용 키로 변환하고 유효 기간을 초기화합니다.
     */
    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
        this.accessTokenExpiration = accessTokenExpiration;
    }

    /**
     * METHOD: 회원 ID를 subject에 담고 발급·만료 시각을 설정한 Access Token을 생성합니다.
     */
    public String createAccessToken(Long memberId) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId)) // 예: 회원 ID 1L을 문자열 "1"로 저장
                .issuedAt(now) // 토큰을 발급한 시각
                .expiration(new Date(now.getTime() + accessTokenExpiration)) // 발급 시각 + 유효 기간
                .signWith(secretKey) // 내용의 변조 여부를 검증할 수 있도록 서명
                .compact(); // 헤더.페이로드.서명 형식의 문자열로 변환
    }

    /**
     * METHOD: JWT의 서명과 만료를 검증한 뒤 subject의 회원 ID를 Long으로 반환합니다.
     */
    public Long getMemberId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey) // 발급에 사용한 비밀키로 서명을 검증하도록 설정
                .build()
                .parseSignedClaims(token) // 서명이 잘못되거나 만료된 토큰이면 예외 발생
                .getPayload(); // 검증을 통과한 토큰의 클레임을 가져옴

        return Long.valueOf(claims.getSubject());
    }
}