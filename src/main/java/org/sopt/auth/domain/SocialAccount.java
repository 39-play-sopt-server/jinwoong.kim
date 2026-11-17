package org.sopt.auth.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.sopt.member.domain.Member;

/**
 * CLASS: 소셜 플랫폼 계정과 우리 회원의 연결 정보 저장
 */
@Entity
@Table(
        name = "social_account",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"provider", "provider_user_id"}
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SocialAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 연결 기록의 기본키

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SocialProvider provider; // 소셜 로그인 제공자

    @Column(name = "provider_user_id", nullable = false, length = 100)
    private String providerUserId; // 플랫폼에서 제공한 사용자 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 연결된 우리 서비스 회원

    /**
     * CONSTRUCTOR: 제공자와 플랫폼 사용자 ID를 우리 회원에 연결
     */
    public SocialAccount(
            SocialProvider provider,
            String providerUserId,
            Member member
    ) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.member = member;
    }
}
