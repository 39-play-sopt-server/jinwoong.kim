package org.sopt.auth.oauth;

import org.sopt.auth.dto.SocialUserInfo;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;

/**
 * INTERFACE: 플랫폼별 사용자 정보를 공통 형식으로 변환하는 계약
 */
public interface SocialUserInfoExtractor {

    /**
     * METHOD: 해당 로그인 제공자의 처리 가능 여부 확인
     */
    boolean supports(String registrationId);

    /**
     * METHOD: 플랫폼 사용자 정보 조회,검증 및 공통 정보 변환
     */
    SocialUserInfo extract(
            OAuth2UserRequest request, // 로그인 제공자 설정과 GitHub API 호출용 AccessToken
            OAuth2User user // Spring Security가 GitHub에서 조회한 사용자 정보
    );

    /*
     * GitHub 응답 → GithubUserInfoExtractor ─┐
     *                                       ├→ SocialUserInfo
     * 다른 플랫폼 → 해당 플랫폼 Extractor ─-----─┘
     *                                             ↓
                                         SocialMemberService
     */
}
