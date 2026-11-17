package org.sopt.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * CLASS: JWT 서명, 만료, 토큰 종류 검증과 클레인 파싱 담당
 */
@Component
@RequiredArgsConstructor
public class JwtParser {

    private final SecretKey secretKey; // JWT 서명 검증용 비밀키

    /**
     * METHOD: 서명, 만료, 토큰 종류 검증 후 클레임 반환
     */
    public Claims parse(String token, TokenType expectedType) {
        // 1. 서명과 만료 검증 후 클레임 추출
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
}
