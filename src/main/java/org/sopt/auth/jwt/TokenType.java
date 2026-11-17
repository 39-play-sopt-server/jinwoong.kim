package org.sopt.auth.jwt;

/**
 * ENUM: JWT의 사용 목적을 Access Token 과 Refresh Token으로 구분
 */
public enum TokenType {

    ACCESS, // 일반 API 접근에 사용하는 토큰

    REFRESH // ACCESS Token 재발급에 사용하는 토큰
}
