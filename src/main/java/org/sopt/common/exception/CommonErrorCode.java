package org.sopt.common.exception;

import org.springframework.http.HttpStatus;

/**
 * ENUM: CommonErrorCode의 HTTP 상태·식별 코드·메시지를 관리합니다.
 */
public enum CommonErrorCode implements ErrorCode {
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    INVALID_REQUEST_BODY(HttpStatus.BAD_REQUEST, "요청 본문의 JSON 형식이나 입력값을 확인해주세요."),

    INVALID_PARAMETER_TYPE(HttpStatus.BAD_REQUEST, "요청 값의 형식이 올바르지 않습니다."),

    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP 메서드입니다."),

    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 경로를 찾을 수 없습니다."),

    UNSUPPORTED_MEDIA_TYPE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 요청 Content-Type입니다."),

    NOT_ACCEPTABLE(HttpStatus.NOT_ACCEPTABLE, "지원하지 않는 응답 형식입니다."),

    DATA_INTEGRITY_VIOLATION(HttpStatus.CONFLICT, "데이터 제약조건과 충돌합니다."),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "요청을 처리할 수 없습니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status; // 예: 성공 또는 실패의 HTTP 상태
    private final String message; // 예: 사용자에게 전달할 기본 메시지

    /**
     * CONSTRUCTOR: 각 코드의 상태와 메시지를 설정합니다.
     */
    CommonErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * METHOD: HTTP 상태를 반환합니다.
     */
    @Override
    public HttpStatus getStatus() { return status; }

    /**
     * METHOD: 메시지와 독립적인 식별 코드를 반환합니다.
     */
    @Override
    public String getCode() { return name(); }

    /**
     * METHOD: 기본 응답 메시지를 반환합니다.
     */
    @Override
    public String getMessage() { return message; }
}
