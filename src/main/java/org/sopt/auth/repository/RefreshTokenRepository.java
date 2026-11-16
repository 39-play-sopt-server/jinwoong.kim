package org.sopt.auth.repository;

import org.sopt.auth.domain.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository
        extends JpaRepository<RefreshToken, String> {

    /**
     * METHOD: 특정 회원에게 발급된 Refresh Token 기록을 모두 삭제
     */
    void deleteByMemberId(Long memberId);
}
