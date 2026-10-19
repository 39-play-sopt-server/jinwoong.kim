package org.sopt.common.exception;

import org.springframework.http.HttpStatus;

/**
 * ENUM: 오류 종류별 HTTP 상태 코드와 기본 응답 메시지를 정의합니다.
 */
public enum ErrorCode {

    POST_NOT_FOUND( // 예: 없는 게시글 ID 조회
            HttpStatus.NOT_FOUND,
            "존재하지 않는 게시글입니다."
    ),

    INVALID_INPUT( // 예: 빈 제목으로 게시글 생성
            HttpStatus.BAD_REQUEST,
            "입력값이 올바르지 않습니다."
    ),

    INVALID_REQUEST_BODY( // 예: category에 "WRONG" 전달
            HttpStatus.BAD_REQUEST,
            "요청 본문의 JSON 형식이나 입력값을 확인해주세요."
    ),

    INVALID_PARAMETER_TYPE( // 예: 숫자 ID 대신 "abc" 전달
            HttpStatus.BAD_REQUEST,
            "요청 값의 형식이 올바르지 않습니다."
    ),

    METHOD_NOT_ALLOWED( // 예: 상세 조회 경로에 POST 요청
            HttpStatus.METHOD_NOT_ALLOWED,
            "지원하지 않는 HTTP 메서드입니다."
    ),
    INTERNAL_SERVER_ERROR( // 예: 처리하지 못한 서버 내부 예외
            HttpStatus.INTERNAL_SERVER_ERROR,
            "서버 내부 오류가 발생했습니다."
    ),

    MEMBER_NOT_FOUND ( // 예: 없는 회원 ID로 게시글 생성
            HttpStatus.NOT_FOUND,
                       "존재하지 않는 회원입니다."
    ),

    MEMBER_HAS_POSTS ( // 예: 작성한 게시글이 남은 회원의 탈퇴 요청
            HttpStatus.CONFLICT,
            "게시글을 먼저 삭제해주세요."
    );


    private final HttpStatus status; // 예: HttpStatus.NOT_FOUND (404)
    private final String message; // 예: "존재하지 않는 게시글입니다."

    /**
     * CONSTRUCTOR: 각 오류 상수에 사용할 HTTP 상태 코드와 기본 메시지를 설정합니다.
     */
    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    /**
     * METHOD: 오류 응답에 사용할 HTTP 상태 코드를 반환합니다.
     */
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * METHOD: 오류의 기본 응답 메시지를 반환합니다.
     */
    public String getMessage() {
        return message;
    }

}
