# 6차 과제 검증 이미지

단계별 구현 과정에서 Postman으로 확인한 결과. 원본 변경 없이 선별하여 복사했음.
최종 구현의 상태 변경 요청에는 CSRF 쿠키와 `X-XSRF-TOKEN` 헤더도 필요함.
캡처의 Authorization 탭만으로 전체 요청 헤더를 판단할 수 없음.

| 이미지 | 확인 내용 | 원본 파일명 |
| --- | --- | --- |
| `refresh-reissue-success.png` | Bearer Token 없이 Refresh Token 쿠키로 재발급 성공 | `200_TOKEN_REISSUED_NOAUTH.png` |
| `refresh-reissue-invalid-401.png` | 유효한 Refresh Token이 없는 재발급 요청 거절 | `401_INVALID_REFRESH_TOKEN_1.png` |
| `logout-success.png` | 로그아웃 성공 | `200_LOGOUT_SUCCESS.png` |
| `logout-old-access-401.png` | 로그아웃 이전 Access Token으로 게시글 조회 시 401 | `401_afterLogout.png` |
| `withdraw-success.png` | 인증된 본인 회원 탈퇴 성공 | `200_MEMBERS_DELETED.png` |

비밀번호나 토큰 문자열이 보이는 캡처는 공개 자료에서 제외했음.
회원 탈퇴 후 인증 실패 캡처는 게시글 조회와 다른 HTTP 메서드로 촬영되어 제외했음.
실제 GitHub 승인 완료 및 운영 배포 결과를 증명하는 이미지는 포함하지 않았음.
