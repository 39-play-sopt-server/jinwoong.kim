package org.sopt.auth.oauth;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.dto.SocialUserInfo;
import org.sopt.auth.service.SocialMemberService;
import org.sopt.common.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

/**
 * CLASS: 플랫폼 사용자 조회와 우리 서비스 회원 연결 담당
 */
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final List<SocialUserInfoExtractor> extractors; // 플랫폼별 정보 처리 객체
    private final SocialMemberService socialMembers; // 소셜 회원 조회·가입 담당

    private final DefaultOAuth2UserService delegate =
            new DefaultOAuth2UserService(); // 기본 사용자 정보 조회 담당

    /**
     * METHOD: 플랫폼 정보 조회 후 우리 회원 ID와 권한으로 인증 사용자 구성
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest request) {
        // 1. 로그인 제공자 확인
        String registrationId =
                request.getClientRegistration().getRegistrationId();

        // 2. 해당 플랫폼을 지원하는 처리 객체 선택
        SocialUserInfoExtractor extractor = extractors.stream()
                .filter(candidate -> candidate.supports(registrationId))
                .findFirst()
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        new OAuth2Error("unsupported_provider"),
                        "지원하지 않는 로그인 제공자입니다."
                ));

        // 3. 기본 사용자 정보 조회 후 공통 형식으로 변환
        OAuth2User platformUser = delegate.loadUser(request);
        SocialUserInfo userInfo = extractor.extract(request, platformUser);

        // 4. 연결된 회원 조회 또는 신규 소셜 회원 가입
        Member member;

        try {
            member = socialMembers.findOrCreate(userInfo);
        } catch (BusinessException e) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(e.getErrorCode().getCode()),
                    e.getMessage(),
                    e
            );
        } catch (DataIntegrityViolationException e) {
            // 동시 가입 등의 DB 제약 충돌을 OAuth 실패 응답으로 변환
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("social_registration_failed"),
                    "소셜 회원 등록에 실패했습니다. 다시 시도해 주세요.",
                    e
            );
        }

        // 5. 우리 회원 ID와 DB 권한을 OAuth2 인증 정보로 전달
        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(member.getRole().name())),
                Map.of("memberId", member.getId()),
                "memberId"
        );
    }
}
