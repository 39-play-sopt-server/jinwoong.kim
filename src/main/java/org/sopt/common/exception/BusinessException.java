package org.sopt.common.exception;

/**
 * CLASS: ErrorCode를 보관하는 업무 예외의 공통 부모 클래스입니다.
 */
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode; // 예: 게시글 또는 회원 도메인의 오류 코드

    /**
     * CONSTRUCTOR: 오류 코드를 보관하고, 해당 코드의 기본 메시지를 예외 메시지로 설정합니다.
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * METHOD: 업무 예외에 지정된 오류 코드를 반환합니다.
     */
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
