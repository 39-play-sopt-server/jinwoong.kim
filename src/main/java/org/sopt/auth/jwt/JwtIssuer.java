package org.sopt.auth.jwt;

import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * CLASS: Access Token과 Refresh Token 발급 담당
 */
@Component
public class JwtIssuer {

    private final SecretKey secretKey; // JWT 서명용 비밀키
    private final long accessTokenExpiration; // Access Token 유효기간
    private final long refreshTokenExpiration; // Refresh Token 유효기간

    /**
     * CONSTRUCTOR: 서명용 비밀키와 토큰별 유효기간 주입
     */
    public JwtIssuer(
            SecretKey secretKey,
            @Value("${jwt.access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration}") long refreshTokenExpiration
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    /**
     * METHOD: 회원 ID와 토큰 버젼을 담은 Access Token 발급
     */
    public String createAccessToken(Long memberId, long tokenVersion) {
        return createToken(
                memberId,
                TokenType.ACCESS,
                accessTokenExpiration,
                tokenVersion
        );
    }

    /**
     * METHOD: 회원 ID와 토큰 버전을 담은 Refresh Token 발급
     */
    public String createRefreshToken(Long memberId, long tokenVersion) {
        return createToken(
                memberId, TokenType.REFRESH, refreshTokenExpiration, tokenVersion
        );
    }

    /**
     * METHOD: 공통 클레임 설정과 서명을 통한 JWT 생성
     */
    private String createToken(
            Long memberId,
            TokenType tokenType,
            long expiration,
            long tokenVersion
    ) {
        Date now = new Date();

        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("tokenType", tokenType.name())
                .claim("tokenVersion", tokenVersion)
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }
}
