package org.sopt.member.exception;

import org.sopt.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * ENUM: MemberErrorCode의 HTTP 상태·식별 코드·메시지를 관리합니다.
 */
public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    MEMBER_HAS_POSTS(HttpStatus.CONFLICT, "게시글을 먼저 삭제해주세요."),

    MEMBER_HAS_LIKES(HttpStatus.CONFLICT, "좋아요를 먼저 취소해주세요.");

    private final HttpStatus status; // 예: 성공 또는 실패의 HTTP 상태
    private final String message; // 예: 사용자에게 전달할 기본 메시지

    /**
     * CONSTRUCTOR: 각 코드의 상태와 메시지를 설정합니다.
     */
    MemberErrorCode(HttpStatus status, String message) {
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
