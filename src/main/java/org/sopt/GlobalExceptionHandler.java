package org.sopt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // Controller에서 발생한 예외를 공통으로 처리
public class GlobalExceptionHandler {

    // 이 메서드가 처리할 예외 종류를 지정
    // 404 Error Not Exist
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Response<Void>> handlePostNotFound( PostNotFoundException exc) {
        // NO를 T_FOUND: HTTP 상태 코드404, Response.failure(...): 기존 공통응답 형식 유지
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Response.failure(exc.getMessage()));
    }

    // 400 Wrong Input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Void>> handleIleegalArgument( IllegalArgumentException exc) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure(exc.getMessage()));
    }

}
