package org.sopt.common.exception;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sopt.common.response.ApiResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * CLASS: Spring 기본 예외의 HTTP 상태·헤더를 보존하면서 공통 API 오류 응답을 제공합니다.
 */
@NullMarked
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * METHOD: 도메인 업무 예외의 상태·코드·메시지를 응답합니다.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException exception) {
        ErrorCode code = exception.getErrorCode();
        return ResponseEntity.status(code.getStatus()).body(ApiResponse.failure(code, exception.getMessage()));
    }

    /**
     * METHOD: 엔티티·Service 입력값 검증 실패를 400으로 응답합니다.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(ApiResponse.failure(CommonErrorCode.INVALID_INPUT, exception.getMessage()));
    }

    /**
     * METHOD: FK·고유 키 등 DB 제약 위반을 409로 응답합니다.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleIntegrity(DataIntegrityViolationException exception) {
        return ResponseEntity.status(CommonErrorCode.DATA_INTEGRITY_VIOLATION.getStatus())
                .body(ApiResponse.failure(CommonErrorCode.DATA_INTEGRITY_VIOLATION));
    }

    /**
     * METHOD: Spring이 판단한 400·404·405·415 등의 상태와 Allow 헤더를 유지합니다.
     * 지원하지 않는 Accept는 본문을 생략해 JSON 변환 실패가 다시 발생하지 않게 합니다.
     */
    @Override
    protected @Nullable ResponseEntity<Object> handleExceptionInternal(
            Exception exception, @Nullable Object body, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        if (status.value() == 406) {
            return new ResponseEntity<>(null, headers, status);
        }

        CommonErrorCode code = frameworkCode(exception, status);
        String message = code.getMessage();
        if (exception instanceof MethodArgumentTypeMismatchException mismatch) {
            message += " 항목: " + mismatch.getName();
        }
        if (status.is5xxServerError()) {
            log.error("Spring 요청 처리 중 서버 오류가 발생했습니다.", exception);
        }
        return super.handleExceptionInternal(exception, ApiResponse.failure(code, message), headers, status, request);
    }

    /**
     * METHOD: 프레임워크 예외의 상태를 공통 식별 코드로 연결합니다.
     */
    private CommonErrorCode frameworkCode(Exception exception, HttpStatusCode status) {
        if (exception instanceof HttpMessageNotReadableException) {
            return CommonErrorCode.INVALID_REQUEST_BODY;
        }
        if (exception instanceof MethodArgumentTypeMismatchException) {
            return CommonErrorCode.INVALID_PARAMETER_TYPE;
        }
        return switch (status.value()) {
            case 404 -> CommonErrorCode.RESOURCE_NOT_FOUND;
            case 405 -> CommonErrorCode.METHOD_NOT_ALLOWED;
            case 415 -> CommonErrorCode.UNSUPPORTED_MEDIA_TYPE;
            default -> status.is5xxServerError()
                    ? CommonErrorCode.INTERNAL_SERVER_ERROR : CommonErrorCode.INVALID_REQUEST;
        };
    }

    /**
     * METHOD: 예상하지 못한 예외만 500으로 응답하고 원인 스택을 로그에 남깁니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception exception) {
        log.error("처리하지 못한 예외가 발생했습니다.", exception);
        return ResponseEntity.internalServerError().body(ApiResponse.failure(CommonErrorCode.INTERNAL_SERVER_ERROR));
    }
}
