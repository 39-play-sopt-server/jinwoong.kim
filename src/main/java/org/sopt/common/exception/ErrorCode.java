package org.sopt.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    POST_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "존재하지 않는 게시글입니다."
    ),

    INVALID_INPUT(
            HttpStatus.BAD_REQUEST,
            "입력값이 올바르지 않습니다."
    ),

    INVALID_REQUEST_BODY(
            HttpStatus.BAD_REQUEST,
            "요청 본문의 JSON 형식이나 입력값을 확인해주세요."
    ),

    INVALID_PARAMETER_TYPE(
            HttpStatus.BAD_REQUEST,
            "요청 값의 형식이 올바르지 않습니다."
    ),

    METHOD_NOT_ALLOWED(
            HttpStatus.METHOD_NOT_ALLOWED,
            "지원하지 않는 HTTP 메서드입니다."
    ),
    INTERNAL_SERVER_ERROR(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다."
    );

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
