package org.sopt.member.exception;

import org.sopt.common.exception.BusinessException;
import org.sopt.common.exception.ErrorCode;

/**
 * CLASS: 존재하지 않는 회원을 조회했을 떄 회원 없음 오류 코드를 전달
 */
public class MemberNotFoundException extends BusinessException {
    /**
     * CONTRUSTOR: 필요한 초기 상태를 가지는 오브젝트 생성
     */
    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND);
    }
}