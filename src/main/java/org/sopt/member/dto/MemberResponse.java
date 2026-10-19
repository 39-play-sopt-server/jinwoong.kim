package org.sopt.member.dto;

import org.sopt.member.domain.Member;

/**
 * RECORD: 저장된 회원의 ID와 이름을 응답으로 전달
 */
public record MemberResponse(
        Long id, // 예: 1L
        String name // 예: 진웅
) {
    /**
     * METHOD: 회원 엔티티의 ID와 이름을 응답 DTO로 복사
     */
    public static MemberResponse from(Member member) {
        return new MemberResponse(member.getId(), member.getName());
    }

}
