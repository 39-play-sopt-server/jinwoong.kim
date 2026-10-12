package org.sopt;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.HttpRequestMethodNotSupportedException;

@RestControllerAdvice // Controller에서 발생한 예외를 공통으로 처리
public class GlobalExceptionHandler {

    // Logger
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 이 메서드가 처리할 예외 종류를 지정
    // 404 Error Not Exist
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException( BusinessException exc) {
        ErrorCode errorCode = exc.getErrorCode();
        // NOT_FOUND: HTTP 상태 코드404, Response.failure(...): 기존 공통응답 형식 유지
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(exc.getMessage()));
    }

    // 400 Wrong Input
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Void>> handleIllegalArgument( IllegalArgumentException exc ) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(exc.getMessage()));
        // exc.getMessage 사용이유: 구체적인 에러 메세지 유지
    }

    // 400 Wrong Category
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleMessageNotReadable( HttpMessageNotReadableException exc ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(errorCode.getMessage()));
    }

    // 400 Wrong id type
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleTypeMismatch( MethodArgumentTypeMismatchException exc ) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_TYPE;
        return ResponseEntity.status(errorCode.getStatus())
                .body(Response.failure(errorCode.getMessage() + " 항목: " + exc.getName()));
    }

    // 405 Method Not allowed
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported( HttpRequestMethodNotSupportedException exc ) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getStatus()).headers(exc.getHeaders())
                .body(Response.failure(errorCode.getMessage()));
    }

    // 500 Sever Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleUnexpectedException( Exception exc ) {
        log.error("처리하지 못한 예외가 발생했습니다.", exc);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(errorCode.getMessage()));
    }
}
