package org.sopt;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.HttpRequestMethodNotSupportedException;

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
    public ResponseEntity<Response<Void>> handleIleegalArgument( IllegalArgumentException exc ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure(exc.getMessage()));
    }

    // 400 Wrong Category
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleMessageNotReadable( HttpMessageNotReadableException exc ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure("요청 본문의 JSON 형식이나 입력값을 확인해주세요"));
    }

    // 400 Wrong id type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleTypeMismatch( MethodArgumentTypeMismatchException exc ) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.failure("요청 값의 형식이 올바르지 않습니다: "+ exc.getName()));
    }

    // 405 Method Not allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported( HttpRequestMethodNotSupportedException exc ) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).headers(exc.getHeaders())
                .body(Response.failure("지원하지 않는 HTTP 메서드입니다."));
    }

    // 500 Sever Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleUnexpectedException( Exception exc ) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Response.failure("서버 내부 오류가 발생했습니다."));
    }
}
