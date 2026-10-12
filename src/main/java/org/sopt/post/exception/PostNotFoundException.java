package org.sopt.post.exception;

import org.sopt.common.exception.BusinessException;
import org.sopt.common.exception.ErrorCode;

// extends사용이유: 실행 중 발생하는 예외의 한 종류를 직접 정의.
public class PostNotFoundException extends BusinessException {

    public PostNotFoundException() {
        super(ErrorCode.POST_NOT_FOUND); //super: 부모 클랫의 생성자에 예외 메세지 전달
    }
}
