package org.sopt.auth.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * CLASS: 서버에서 사용을 허용한 Refresh Token의 정보를 저장
 */
@Entity
@Table(name = "refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class RefreshToken {

    @Id
    @Column(length = 36)
    private String tokenId; // JWT의 jti에 저장한 UUID

    @Column(nullable = false)
    private Long memberId; // 토큰을 발급받은 회원 ID

    @Column(nullable = false)
    private Instant expiresAt; // Refresh Token의 만료 시각
}


/*
| 필드 | 저장하는 내용 |
|---|---|
| tokenId | 어떤 Refresh Token인지 |
| memberId | 누구에게 발급한 토큰인지 |
| expiresAt | 언제 만료되는지 |
 */
