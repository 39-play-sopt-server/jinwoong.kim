package org.sopt.common.exception;

import org.springframework.http.HttpStatus;

/**
 * INTERFACE: 공통·도메인별 오류 코드가 제공해야 할 정보를 정의합니다.
 */
public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
