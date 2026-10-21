package org.sopt.like.exception;

import org.sopt.common.exception.BusinessException;

/**
 * CLASS: 해당 회원의 좋아요 기록이 없을 때 발생하는 도메인 예외입니다.
 */
public class LikeNotFoundException extends BusinessException {
    /**
     * CONSTRUCTOR: 좋아요 기록 없음 오류 코드를 전달
     */
    public LikeNotFoundException() {
        super(LikeErrorCode.LIKE_NOT_FOUND);
    }
}
