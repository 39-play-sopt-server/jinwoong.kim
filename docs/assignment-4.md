# 4차 과제: CORS 및 HTTPS

## 진행 상태

로컬에 CORS 정책, 운영 프로필과 Nginx 초기 설정을 준비했다. 서버 적용·인증서 발급·외부 HTTPS 접속은 아직 검증하지 않았다. 기존 3차 과제 브랜치에서 작업 중이므로 제출 전 3차 과제 반영 상태를 확인하고 4차 이슈 및 브랜치를 정한다.

## CORS 설계

브라우저 origin은 스킴·호스트·포트 조합이다. 프런트엔드와 API의 origin이 다르면 브라우저가 CORS 정책을 확인한다. `WebConfig`는 `/api/**`에 허용 출처와 메서드, 헤더를 적용한다. JSON 또는 Authorization 헤더를 사용하는 교차 출처 요청은 OPTIONS preflight가 발생할 수 있다.

- 개발 기본 출처: `http://localhost:3000`, `http://localhost:5173`
- 운영 출처: `CORS_ALLOWED_ORIGINS` 환경변수에 쉼표로 구분해 지정. 경로와 끝의 `/` 없이 입력한다.
- 기존 API 메서드 GET/POST/PUT/DELETE 및 HEAD/OPTIONS 허용
- 요청 헤더 Content-Type/Authorization 허용, 응답 Location 노출
- 쿠키 인증 요구사항이 없어 credentials는 허용하지 않는다.
- Swagger가 API와 같은 HTTPS origin이면 CORS 허용 목록에 별도로 추가할 필요가 없다.

CORS는 브라우저의 응답 접근 정책이며 인증·접근 제어를 대체하지 않는다. Nginx에는 CORS 헤더를 중복 설정하지 않는다.

## HTTPS 적용 순서 (EC2에서 실행)

다음은 적용 절차이며 자동 실행되지 않는다. 현재 IP와 도메인, 서버 설정을 먼저 확인한다. 설정 파일 전체나 비밀번호를 출력하지 않는다.

1. 도메인의 A 레코드를 현재 EC2 IP와 일치시킨다. AAAA 레코드가 있으면 실제 IPv6 서버를 가리키는지 함께 확인한다.
2. EC2 보안 그룹에서 HTTP 80, HTTPS 443을 허용한다. SSH는 관리자가 접속할 IP로 제한한다.
3. 기존 서버 DB 설정을 보존하고 새 코드 및 설정을 병합한 뒤 `./gradlew clean bootJar`로 빌드한다. 테스트 성공 여부는 별도로 확인한다.
4. 실행 중인 PID를 확인하고 종료한 후 아래 환경변수와 prod 프로필로 실행한다. 기존 DB 설정이 올바른 RDS를 가리키는지 먼저 확인한다.

```sh
# 실제 프런트엔드가 생기면 해당 origin으로 변경
export CORS_ALLOWED_ORIGINS='http://localhost:3000,http://localhost:5173'
nohup java -jar build/libs/assignment-1.0-SNAPSHOT.jar --spring.profiles.active=prod > app.log 2>&1 < /dev/null &
curl -I http://127.0.0.1:8080/swagger-ui/index.html
```

prod는 8080을 루프백에 바인딩하고 전달 헤더를 처리한다. 이후 직접 `공인IP:8080` 접근 대신 Nginx를 통한다.

5. Nginx와 Certbot을 설치하고 초기 설정을 적용한다. 기존 동명 설정이 있다면 먼저 보존하고 차이를 검토한다.

```sh
sudo apt update
sudo apt install -y nginx certbot python3-certbot-nginx
sudo cp deploy/nginx/assignment.conf /etc/nginx/sites-available/assignment
sudo ln -s /etc/nginx/sites-available/assignment /etc/nginx/sites-enabled/assignment
sudo nginx -t
# 검사 성공 후에만 적용
sudo systemctl reload nginx
curl -I http://jinwoong-sopt39.p-e.kr/swagger-ui/index.html
```

6. 외부에서도 HTTP 도메인 접속이 되는지 확인한 뒤 인증서를 발급한다. 이메일·약관 입력은 서버에서 직접 진행한다.

```sh
sudo certbot --nginx -d jinwoong-sopt39.p-e.kr --redirect
sudo nginx -t
sudo certbot renew --dry-run
systemctl list-timers --all | grep certbot
```

Certbot이 Nginx에 TLS 인증서 설정과 HTTP→HTTPS 리다이렉트를 추가한다. 저장소의 초기 conf는 인증서 발급 후 서버 설정을 덮어쓰는 용도로 다시 복사하지 않는다. 인증서 개인 키를 저장소에 넣지 않는다.

7. HTTPS 검증 후 보안 그룹의 외부 8080 허용 규칙을 제거한다. 자동 재시작은 별도 systemd 설정이 필요하며 이 단계에는 포함하지 않았다.

## 검증 및 PR 제출

```sh
# -k를 쓰지 않아 인증서 유효성까지 검증
curl -I https://jinwoong-sopt39.p-e.kr/swagger-ui/index.html
curl -I http://jinwoong-sopt39.p-e.kr/swagger-ui/index.html
curl -i -X OPTIONS https://jinwoong-sopt39.p-e.kr/api/v1/members \
  -H 'Origin: http://localhost:3000' \
  -H 'Access-Control-Request-Method: POST' \
  -H 'Access-Control-Request-Headers: content-type'
```

HTTPS Swagger 화면과 `/v3/api-docs` 응답, Swagger Try it out의 HTTPS 요청을 확인한다. HTTP는 HTTPS로 리다이렉트되어야 한다. 허용 origin의 preflight는 해당 origin을 반환하고 미허용 origin은 거부되어야 한다.

PR에 기재할 **검증 전 목표 주소**: `https://jinwoong-sopt39.p-e.kr/swagger-ui/index.html`. 실제 접속 검증 전에는 HTTPS 적용 완료로 기재하지 않는다. 이슈·PR 작성과 리뷰어 지정을 진행하고 실측 결과를 첨부한다.

## 키워드 정리

- CI는 변경을 통합할 때 빌드·테스트로 문제를 빨리 찾는 과정이다. CD는 검증된 변경을 배포 가능 상태로 준비하거나 자동으로 운영에 배포하는 과정이다.
- HTTPS는 HTTP를 TLS로 보호해 전송 기밀성과 무결성, 인증서를 통한 서버 신원 확인을 제공한다.
- SSL은 TLS의 전신이며 현재 HTTPS에는 TLS를 사용한다. TLS 핸드셰이크에서 인증서와 서명을 검증하고 키 교환으로 공유 키를 도출해 이후 통신을 대칭키로 암호화한다. Nginx에서 TLS를 종료하고 같은 EC2의 루프백 HTTP로 Spring에 전달한다.

## 참고 문서

- [Spring MVC CORS](https://docs.spring.io/spring-framework/reference/web/webmvc-cors.html)
- [Nginx 프록시 모듈](https://nginx.org/en/docs/http/ngx_http_proxy_module.html)
- [Ubuntu TLS 인증서](https://ubuntu.com/server/docs/how-to/security/obtain-tls-certificates/)
