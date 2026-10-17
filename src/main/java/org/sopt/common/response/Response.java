package org.sopt.common.response;

/**
 * CLASS: 모든 API의 성공·실패 본문을 success, message, data 형식으로 제공합니다.
 */
public class Response<T> {
    private final boolean success; // 예: 성공 시 true, 실패 시 false
    private final String message; // 예: "게시글을 조회했습니다."
    private final T data; // 예: PostResponse 또는 실패 시 null

    /**
     * CONSTRUCTOR: 성공 여부·메시지·데이터를 설정합니다. 외부에서는 success 또는 failure로 생성합니다.
     */
    private Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    /**
     * METHOD: 성공 여부와 메시지, 데이터를 담은 응답을 생성합니다.
     */
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(true, message, data);
    }

    /**
     * METHOD: 실패 여부와 메시지를 담고, data는 null로 설정합니다.
     */
    public static <T> Response<T> failure(String message) {
        return new Response<>(false, message, null);
    }

    /**
     * METHOD: 응답의 성공 여부를 반환합니다.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * METHOD: 응답 메시지를 반환합니다.
     */
    public String getMessage() {
        return message;
    }

    /**
     * METHOD: 응답 데이터를 반환합니다. 실패 응답의 데이터는 null입니다.
     */
    public T getData() {
        return  data;
    }
}
