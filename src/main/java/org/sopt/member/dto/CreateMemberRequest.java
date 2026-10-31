package org.sopt.member.dto;

/**
 * RECORD: 회원 생성 요청의 이름을 전달
 */
public record CreateMemberRequest(
        String name // 예: "진웅"
) {}
