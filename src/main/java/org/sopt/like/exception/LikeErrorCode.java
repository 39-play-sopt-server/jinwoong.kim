package org.sopt.like.exception;

import org.sopt.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * ENUM: LikeErrorCode의 HTTP 상태·식별 코드·메시지를 관리합니다.
 */
public enum LikeErrorCode implements ErrorCode {
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요를 누른 흔적이 없습니다.");

    private final HttpStatus status; // 예: 성공 또는 실패의 HTTP 상태
    private final String message; // 예: 사용자에게 전달할 기본 메시지

    /**
     * CONSTRUCTOR: 각 코드의 상태와 메시지를 설정합니다.
     */
    LikeErrorCode(HttpStatus status, String message) {
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
