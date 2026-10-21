package org.sopt.common.response;

import org.sopt.common.exception.ErrorCode;

/**
 * CLASS: 성공 여부·식별 코드·메시지·데이터를 담는 공통 API 응답입니다.
 */
public class ApiResponse<T> {
    private final boolean success; // 예: 성공이면 true
    private final String code; // 예: POST_CREATED, POST_NOT_FOUND
    private final String message; // 예: 게시글이 작성되었습니다.
    private final T data; // 예: 생성 ID 또는 조회 DTO, 실패 시 null

    /**
     * CONSTRUCTOR: 응답 구성은 성공·실패 팩토리 메서드로 제한합니다.
     */
    private ApiResponse(boolean success, String code, String message, T data) {
        this.success = success;
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * METHOD: 성공 코드와 응답 데이터를 전달받아 성공 응답을 생성합니다.
     */
    public static <T> ApiResponse<T> success(SuccessCode code, T data) {
        return new ApiResponse<>(true, code.getCode(), code.getMessage(), data);
    }

    /**
     * METHOD: 오류 코드의 기본 메시지로 실패 응답을 생성합니다.
     */
    public static ApiResponse<Void> failure(ErrorCode code) {
        return failure(code, code.getMessage());
    }

    /**
     * METHOD: 안정적인 오류 코드는 유지하면서 구체적인 안내 메시지를 제공합니다.
     */
    public static ApiResponse<Void> failure(ErrorCode code, String message) {
        return new ApiResponse<>(false, code.getCode(), message, null);
    }

    /** METHOD: 성공 여부를 반환합니다. */
    public boolean isSuccess() { return success; }
    /** METHOD: 식별 코드를 반환합니다. */
    public String getCode() { return code; }
    /** METHOD: 메시지를 반환합니다. */
    public String getMessage() { return message; }
    /** METHOD: 응답 데이터를 반환합니다. */
    public T getData() { return data; }
}
