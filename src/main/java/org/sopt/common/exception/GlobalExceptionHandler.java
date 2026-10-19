package org.sopt.common.exception;

import org.sopt.common.response.Response;
import org.sopt.post.domain.Category;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.springframework.http.converter.HttpMessageNotReadableException;

import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import org.springframework.web.HttpRequestMethodNotSupportedException;

/**
 * CLASS: 업무 예외와 요청 처리 중 발생한 예외를 공통 HTTP 오류 응답으로 변환합니다.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class); // 예: 예상하지 못한 예외의 메시지와 스택 트레이스 기록

    /**
     * METHOD: 업무 예외에 지정된 ErrorCode의 상태 코드와 예외 메시지로 응답합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<Void>> handleBusinessException( BusinessException exc) {
        ErrorCode errorCode = exc.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(exc.getMessage()));
    }

    /**
     * METHOD: 입력값 검증 실패를 400으로 응답하고, 구체적인 검증 메시지를 유지합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response<Void>> handleIllegalArgument( IllegalArgumentException exc ) {
        ErrorCode errorCode = ErrorCode.INVALID_INPUT;
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(exc.getMessage()));

    }

    /**
     * METHOD: 잘못된 JSON이나 본문 값의 변환 실패를 400으로 응답합니다.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Response<Void>> handleMessageNotReadable( HttpMessageNotReadableException exc ) {
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_BODY;
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(errorCode.getMessage()));
    }

    /**
     * METHOD: 경로·쿼리 매개변수의 타입 변환 실패를 400으로 응답하며, 해당 항목 이름을 제공합니다.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Response<Void>> handleTypeMismatch( MethodArgumentTypeMismatchException exc ) {
        ErrorCode errorCode = ErrorCode.INVALID_PARAMETER_TYPE;
        return ResponseEntity.status(errorCode.getStatus())
                .body(Response.failure(errorCode.getMessage() + " 항목: " + exc.getName()));
    }

    /**
     * METHOD: 지원하지 않는 HTTP 메서드를 405로 응답하고, Allow 등 예외의 응답 헤더를 유지합니다.
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Response<Void>> handleMethodNotSupported( HttpRequestMethodNotSupportedException exc ) {
        ErrorCode errorCode = ErrorCode.METHOD_NOT_ALLOWED;
        return ResponseEntity.status(errorCode.getStatus()).headers(exc.getHeaders())
                .body(Response.failure(errorCode.getMessage()));
    }

    /**
     * METHOD: 예상하지 못한 예외는 로그에 기록하고, 내부 오류 정보를 노출하지 않는 500 응답을 반환합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response<Void>> handleUnexpectedException( Exception exc ) {
        log.error("처리하지 못한 예외가 발생했습니다.", exc);
        ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(errorCode.getStatus()).body(Response.failure(errorCode.getMessage()));
    }

    /**
     * METHOD: DB 제약조건 충돌을 409 응답으로 변환
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Response<Void>> handleIntergrity(DataIntegrityViolationException exc) {
        return ResponseEntity.status(409).body(Response.failure("데이터 제약조건과 충돌합니다."));
    }
}
