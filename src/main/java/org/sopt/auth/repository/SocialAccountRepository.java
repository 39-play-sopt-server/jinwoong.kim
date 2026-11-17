package org.sopt.auth.repository;

import java.util.Optional;
import org.sopt.auth.domain.SocialAccount;
import org.sopt.auth.domain.SocialProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;

/**
 * INTERFACE: 소셜 계정 연결 정보의 저장, 조회, 삭제 담당
 */
public interface SocialAccountRepository
        extends JpaRepository<SocialAccount, Long> {

    /**
     * METHOD: 제공자와 플랫폼 사용자 ID로 소셜 계정 연결 조회
     */
    @EntityGraph(attributePaths = "member")
    Optional<SocialAccount> findByProviderAndProviderUserId(
            SocialProvider provider,
            String providerUserId
    );

    /**
     * METHOD: 회원에게 연결된 모든 소셜 계정 기록 삭제
     */
    void deleteByMember_Id(Long memberId);
}
