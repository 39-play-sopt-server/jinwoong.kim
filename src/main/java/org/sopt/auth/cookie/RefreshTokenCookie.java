package org.sopt.auth.cookie;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * CLASS: Refresh Token을 전달할 쿠키를 생성
 */
@Component
public class RefreshTokenCookie {

    private final long expiration; // 쿠키 유효기간, 밀리초 단위
    private final boolean secure; // HTTPS 요청에서만 쿠키를 전송할지 여부

    /**
     * CONSTRUCTOR: 설정에서 쿠키 유효기간과 Secure 적용 여부를 받음
     */
    public RefreshTokenCookie(
            @Value("${jwt.refresh-token-expiration}") long expiration,
            @Value("${app.cookie.secure:true}")boolean secure
    ) {
        this.expiration = expiration;
        this.secure = secure;
    }

    /**
     * METHOD: Refresh Token을 담은 Set-Cookie 헤더 값을 생성
     */
    public String create(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/api/v1/auth")
                .maxAge(Duration.ofMillis(expiration))
                .build()
                .toString();
    }
}
