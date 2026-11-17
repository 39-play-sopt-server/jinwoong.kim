package org.sopt.auth.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.domain.SocialAccount;
import org.sopt.auth.dto.SocialUserInfo;
import org.sopt.auth.exception.AuthErrorCode;
import org.sopt.auth.repository.SocialAccountRepository;
import org.sopt.common.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.sopt.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CLASS: 소셜 계정에 연결된 회원 조회와 신규 소셜 회원 가입 담당
 */
@Service
@Transactional
@RequiredArgsConstructor
public class SocialMemberService {

    private final MemberRepository members; // 우리 서비스 회원 저장소
    private final SocialAccountRepository socialAccounts; // 소셜 계정 연결

    /**
     * METHOD: 기존 소셜 연결 조회 또는 신규 회원과 연결 정보 생성
     */
    public Member findOrCreate(SocialUserInfo userInfo) {
        // 1. 제공자와 플랫폼 사용자 ID로 기존 연결 조회
        Optional<SocialAccount> socialAccount =
                socialAccounts.findByProviderAndProviderUserId(
                        userInfo.provider(),
                        userInfo.providerUserId()
                );

        if (socialAccount.isPresent()) {
            return socialAccount.get().getMember();
        }

        // 2. 동일 이메일의 기존 계정에 자동 연결하지 않고 가입 거절
        String email = userInfo.email().trim();

        if(members.existsByEmail(email)) {
            throw new BusinessException(AuthErrorCode.SOCIAL_EMAIL_CONFLICT);
        }

        // 3. 비밀번호 없는 소셜 회원 생성 및 저장
        Member member = members.save(
                Member.createSocial(userInfo.name(), email)
        );

        // 4. 플랫폼 계정과 우리 회원의 연결 정보 저장
        socialAccounts.save(new SocialAccount(
                userInfo.provider(),
                userInfo.providerUserId(),
                member
        ));

        return member;
    }
}
