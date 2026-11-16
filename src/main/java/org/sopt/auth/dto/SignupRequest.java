package org.sopt.auth.dto;

/**
 * RECORD: 회원가입 요청의 이름, 이메일, 원문 비밀번호를 전달합니다.
 */
public record SignupRequest (
        String name, // 예: "진웅"
        String email, // 예: "kjw@kjw.com"
        String password // 해시 전 원문 비밀번호; AuthService에서 해시로 변환
) {}
