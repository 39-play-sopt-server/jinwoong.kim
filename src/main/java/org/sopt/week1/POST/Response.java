package org.sopt.week1.POST;

// 심화: 서버의 성공·실패 결과를 동일한 구조로 전달합니다.
public class Response<T> {
    private final boolean success;
    private final String message;
    private T data;

    // CONSTRUCTOR
    private Response(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    //------- METHODS

    // 심화 METHOD: 데이터가 포함된 성공 응답 생성
    public static <T> Response<T> success(String message, T data) {
        return new Response<>(true, message, data);
    }

    // 심화 METHOD: 데이터가 없는 실패 응답 생성
    public static <T> Response<T> failure(String message) {
        return new Response<>(false, message, null);
    }

    // GETTER: 성공 여부 조회
    public boolean isSuccess() {
        return success;
    }

    // GETTHER: 응답 메시지 조회
    public String getMessage() {
        return message;
    }

    // GETTER: 응답 데이터 조회
    public T getData() {
        return  data;
    }
}
