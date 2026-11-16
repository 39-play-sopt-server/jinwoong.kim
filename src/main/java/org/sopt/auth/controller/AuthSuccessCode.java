package org.sopt.auth.controller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.common.response.SuccessCode;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthSuccessCode implements SuccessCode {

    LOGIN_SUCCESS(HttpStatus.OK, "로그인에 성공했습니다."),

    TOKEN_REISSUED(HttpStatus.OK, "Access Token을 재발급했습니다.");

    private final HttpStatus status;
    private final String message;

    /**
     * METHOD: 성공 응답의 식별 코드를 반환
     */
    @Override
    public String getCode() {
        return name();
    }
}
