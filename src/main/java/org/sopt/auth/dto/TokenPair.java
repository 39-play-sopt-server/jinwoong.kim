package org.sopt.auth.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * CLASS: Service에서 Controller로 Access Token과 Refresh Token을 전달
 */

@Getter
@RequiredArgsConstructor
public class TokenPair {

    private final String accessToken; // 일반 API 접근에 사용하는 토큰
    private final String refreshToken; // Access Token 재발급에 사용하는 토큰
}
