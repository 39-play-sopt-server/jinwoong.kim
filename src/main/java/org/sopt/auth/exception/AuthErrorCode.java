package org.sopt.auth.exception;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.sopt.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;

/**
 * ENUM: 인증 관련 오류의 HTTP 상태, 식별 코드와 메시지를 관리
 */
@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(
            HttpStatus.UNAUTHORIZED,
        "이메일 또는 비밀번호가 올바르지 않습니다."
    ),

    INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED,
        "유효하지 않은 Refresh Token입니다."
    );

    private final HttpStatus status; // 예: 인증 실패 시 401
    private final String message; // 예: 클라이언트에 전달할 오류 메시지

    /**
     * METHOD: 오류 메시지와 구분되는 식별 코드를 반환
     */
    @Override
    public String getCode() {
        return name();
    }
}
