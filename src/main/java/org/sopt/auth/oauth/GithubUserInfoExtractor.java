package org.sopt.auth.oauth;
import java.util.Arrays;
import org.sopt.auth.domain.SocialProvider;
import org.sopt.auth.dto.GithubEmailResponse;
import org.sopt.auth.dto.SocialUserInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * CLASS: GitHub 사용자 정보 조회, 검증 및 공통 정보 변환
 */
@Component
public class GithubUserInfoExtractor implements SocialUserInfoExtractor {

    private final RestClient githubRestClient; // GitHub API 호출용 클라이언트

    /**
     * CONSTRUCTOR: GitHub 전용 HTTP 클라이언트 주입
     */
    public GithubUserInfoExtractor(
            @Qualifier("githubRestClient") RestClient githubRestClient
    ) {
        this.githubRestClient = githubRestClient;
    }

    /**
     * METHOD: GitHub 로그인 처리 가능 여부 확인
     */
    @Override
    public boolean supports(String registrationId) {
        return "github".equals(registrationId);
    }

    /**
     * METHOD: GitHub 사용자 식별 정보와 검증된 이메일 반환
     */
    @Override
    public SocialUserInfo extract(
            OAuth2UserRequest request,
            OAuth2User user
    ) {
        // 1. GitHub 사용자 고유 ID 확인
        Number githubId = user.getAttribute("id");

        if (githubId == null || githubId.longValue() <= 0) {
            throw failure("GitHub 사용자 ID를 확인할 수 없습니다.");
        }

        // 2. 이름이 없으면 GitHub 로그인 이름 사용
        String name = user.getAttribute("name");

        if (name == null || name.isBlank()) {
            name = user.getAttribute("login");
        }

        if (name == null || name.isBlank()) {
            throw failure("GitHub 사용자 이름을 확인할 수 없습니다.");
        }

        name = name.trim();

        // 우리 회원 이름의 길이 제한에 맞춰 저장
        if (name.length() > 100) {
            name = name.substring(0, 100);
        }

        // 3. GitHub API용 토큰으로 검증된 대표 이메일 조회
        String email = findVerifiedEmail(
                request.getAccessToken().getTokenValue()
        );

        return new SocialUserInfo(
                SocialProvider.GITHUB,
                githubId.toString(),
                name,
                email
        );
    }

    /**
     * METHOD: GitHub 이메일 목록에서 검증된 대표 이메일 선택
     */
    private String findVerifiedEmail(String accessToken) {
        GithubEmailResponse[] emails;

        try {
            emails = githubRestClient.get()
                    .uri("/user/emails?per_page=100")
                    .headers(headers -> headers.setBearerAuth(accessToken))
                    .retrieve()
                    .body(GithubEmailResponse[].class);
        } catch (RestClientException e) {
            throw failure("GitHub 이메일 조회에 실패했습니다.");
        }

        if (emails == null) {
            throw failure("GitHub 이메일 정보가 없습니다.");
        }

        return Arrays.stream(emails)
                .filter(email -> email != null)
                .filter(email -> email.primary() && email.verified())
                .map(GithubEmailResponse::email)
                .filter(email -> email != null && !email.isBlank())
                .map(String::trim)
                .filter(email -> email.length() <= 254)
                .findFirst()
                .orElseThrow(() ->
                        failure("GitHub의 검증된 대표 이메일이 필요합니다."));
    }

    /**
     * METHOD: OAuth2 로그인 처리용 예외 생성
     */
    private OAuth2AuthenticationException failure(String message) {
        return new OAuth2AuthenticationException(
                new OAuth2Error("invalid_user_info"),
                message
        );
    }
}
