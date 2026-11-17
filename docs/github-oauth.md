# GitHub 소셜 로그인

## 로그인 시작

- 로컬: `http://localhost:8080/oauth2/authorization/github`
- 운영: `https://jinwoong-sopt39.p-e.kr/oauth2/authorization/github`
- 콜백: 각 서버 주소의 `/login/oauth2/code/github`

GitHub OAuth 앱에 콜백 등록 후 환경별 `.env`에 `GITHUB_CLIENT_ID`,
`GITHUB_CLIENT_SECRET`, `GITHUB_REDIRECT_URI` 설정 필요.
로컬 HTTP에서는 `APP_COOKIE_SECURE=false`, 운영 HTTPS에서는 기본값 `true` 사용.

## 처리 흐름

1. Spring Security가 OAuth state를 생성하고 임시 세션에 저장
2. GitHub 승인 후 콜백의 state 검증 및 인가 코드 교환
3. GitHub 프로필과 검증된 대표 이메일 조회
4. 제공자와 사용자 고유 ID로 기존 연결 조회 또는 신규 소셜 회원 가입
5. 우리 서비스의 Access Token과 Refresh Token 발급·저장
6. 응답 후 임시 세션과 OAuth 인증 정보 정리

로그인 성공 응답은 기존 API와 동일하게 `Authorization: Bearer ...`,
`Set-Cookie: refreshToken=...` 헤더와 `LOGIN_SUCCESS` JSON 본문 사용.
JWT를 URL의 쿼리나 fragment에 포함하지 않음.
별도 프론트엔드 없이 확인할 때는 브라우저 개발자 도구의 콜백 응답 헤더 확인.

일반 API는 임시 OAuth 세션만으로 접근할 수 없으며 우리 Access Token 필요.
OAuth state 왕복용 쿠키는 `HttpOnly`, `SameSite=Lax` 사용.
Refresh Token 쿠키는 기존의 `HttpOnly`, `SameSite=Strict` 유지.
변경 API 호출에는 CSRF 토큰도 필요하며 로그인 후 `/api/v1/auth/csrf` 재조회 권장.

## 기존 계정과 공존

- 소셜 전용 회원의 `password`는 NULL이며 비밀번호 로그인 거절
- GitHub 비밀번호와 GitHub Access Token은 회원 테이블에 저장하지 않음
- 처음 연결하는 소셜 계정의 이메일이 기존 회원과 같으면 409 `SOCIAL_EMAIL_CONFLICT`
- 이메일만 같다는 이유로 기존 회원에 자동 연결하지 않음
- 기존 연결은 이메일이 아닌 `provider + provider_user_id` 기준으로 재사용
- 탈퇴 시 Refresh Token과 소셜 연결 기록을 먼저 삭제하고 회원 삭제

## 기존 DB 변경

`ddl-auto: update`만으로 기존 password 컬럼의 NOT NULL 제약이 바뀌지 않을 수 있음.
기존 로컬 DB에는 적용 완료. **운영 RDS는 배포 전 대상 DB 확인 후 별도 적용 필요.**

```sql
ALTER TABLE member MODIFY COLUMN password VARCHAR(255) NULL;
```

원본: `docs/sql/006_social_member_password_nullable.sql`.
기존 비밀번호 해시 데이터는 유지되며 일반 회원 생성자의 비밀번호 필수 검증도 유지.

## 검증 범위

- `OAuth2LoginFlowTest`: 실제 HTTP 제공자 대역과 MySQL 테스트 DB를 사용한 8개 시나리오
- 로그인 성공, JWT/쿠키 발급과 재발급, 임시 세션 인증 차단
- state 누락·불일치·재사용, 사용자 동의 취소 거절
- 기존 이메일 충돌 시 계정 보존, 기존 소셜 연결 재사용
- 미검증 이메일과 이메일 API 500 응답 시 회원 생성 차단
- 전체 테스트 32개와 `clean build` 통과

대역의 token/user/emails 응답은 아래 GitHub 공식 계약을 바탕으로 구성했음.
Postman 로그인이 없어 공개된 제공자 소유 mock 검색은 수행하지 않았음.
테스트 통과는 대역과의 통합 검증이며 실제 GitHub 앱 설정·동의 화면 검증을 대신하지 않음.
실제 GitHub에서의 로그인과 운영 HTTPS/RDS 적용은 별도 확인 대상.

- https://docs.github.com/en/apps/oauth-apps/building-oauth-apps/authorizing-oauth-apps
- https://docs.github.com/en/rest/users/users#get-the-authenticated-user
- https://docs.github.com/en/rest/users/emails#list-email-addresses-for-the-authenticated-user
- https://docs.spring.io/spring-security/reference/servlet/oauth2/login/advanced.html
