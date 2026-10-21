package org.sopt.like.controller;

import org.sopt.common.response.SuccessCode;
import org.springframework.http.HttpStatus;

/**
 * ENUM: LikeSuccessCode의 HTTP 상태·식별 코드·메시지를 관리합니다.
 */
public enum LikeSuccessCode implements SuccessCode {
    LIKE_CREATED(HttpStatus.OK, "좋아요가 적용되었습니다."),

    LIKE_DELETED(HttpStatus.OK, "좋아요가 취소되었습니다.");

    private final HttpStatus status; // 예: 성공 또는 실패의 HTTP 상태
    private final String message; // 예: 사용자에게 전달할 기본 메시지

    /**
     * CONSTRUCTOR: 각 코드의 상태와 메시지를 설정합니다.
     */
    LikeSuccessCode(HttpStatus status, String message) {
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
