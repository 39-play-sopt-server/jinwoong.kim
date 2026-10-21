package org.sopt.common.exception;

public class LikeNotFoundException extends BusinessException {
    /**
     * CONSTRUCTOR: 좋아요 기록 없음 오류 코드를 전달
     */
    public LikeNotFoundException() {
        super(ErrorCode.LIKE_NOT_FOUND);
    }
}
