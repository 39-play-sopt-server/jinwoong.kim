package org.sopt.auth.jwt;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * CLASS: JWT 서명과 검증에 사용할 비밀키 설정
 */
@Configuration
public class JwtKeyConfig {

    /**
     * METHOD: Base64 설정값을 서명용 비밀키로 변환하고 Bean등록
     */
    @Bean
    public SecretKey jwtSecretKey(
            @Value("${jwt.secret}") String secret
    ) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
    }
}
