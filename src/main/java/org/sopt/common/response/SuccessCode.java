package org.sopt.common.response;

import org.springframework.http.HttpStatus;

/**
 * INTERFACE: 성공 응답 코드의 상태·식별자·메시지 계약을 정의합니다.
 * ErrorCode와 분리해 성공 응답에 오류 코드를 전달하지 못하도록 합니다.
 */
public interface SuccessCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
