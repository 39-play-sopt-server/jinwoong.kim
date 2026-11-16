package org.sopt.auth.dto;

/**
 * RECORD: 로그인 요청의 이메일과 원문 비밀번호를 전달
 */
public record LoginRequest (
    String email,
    String password
) {}
