package org.sopt.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * RECORD: GitHub 이메일 조회 응답 전달
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubEmailResponse(
        String email, // 이메일 주소
        boolean primary, // 대표 이메일 여부
        boolean verified // 이메일 소유 확인 여부
) {
}

/*
[
  {
    "email": "user@example.com",
    "primary": true,
    "verified": true,
    "visibility": "private"
  }
]
 */