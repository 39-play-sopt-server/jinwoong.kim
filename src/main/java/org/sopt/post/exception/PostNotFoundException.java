package org.sopt.post.exception;

import org.sopt.common.exception.BusinessException;
import org.sopt.common.exception.ErrorCode;

/**
 * CLASS: 조회·수정·삭제할 게시글이 없을 때 Service에서 발생시키는 업무 예외입니다.
 */
public class PostNotFoundException extends BusinessException {

    /**
     * CONSTRUCTOR: 게시글 없음 오류 코드로 업무 예외를 생성합니다.
     */
    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND);
    }
}
