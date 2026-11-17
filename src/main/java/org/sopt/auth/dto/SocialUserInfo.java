package org.sopt.auth.dto;

import org.sopt.auth.domain.SocialProvider;

/**
 * RECORD: 소셜 플랫폼에서 확인한 사용자 정보를 공통 형식으로 전달
 */
public record SocialUserInfo(
        SocialProvider provider,
        String providerUserId,
        String name,
        String email
) {
}
