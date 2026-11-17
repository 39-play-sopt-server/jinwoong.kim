package org.sopt.auth.service;

import io.jsonwebtoken.Claims;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.sopt.auth.domain.RefreshToken;
import org.sopt.auth.dto.TokenPair;
import org.sopt.auth.exception.AuthErrorCode;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.auth.repository.RefreshTokenRepository;
import org.sopt.common.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.sopt.member.domain.MemberRole;
import org.sopt.member.repository.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CLASS: 일반 회원과 소셜 전용 회원의 생성 및 비밀번호 로그인 공존 검증
 */
class SocialMemberCompatibilityTest {

    private final MemberRepository members = mock(MemberRepository.class);
    private final JwtProvider jwtProvider = mock(JwtProvider.class);
    private final RefreshTokenRepository refreshTokens = mock(RefreshTokenRepository.class);
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final AuthService service = new AuthService(
            members, passwordEncoder, jwtProvider, refreshTokens
    );

    @Test
    void socialMemberHasNormalizedProfileAndNoPassword() {
        Member member = Member.createSocial("  소셜 회원  ", "  social@example.com  ");

        assertAll(
                () -> assertEquals("소셜 회원", member.getName()),
                () -> assertEquals("social@example.com", member.getEmail()),
                () -> assertNull(member.getPassword()),
                () -> assertEquals(MemberRole.ROLE_USER, member.getRole()),
                () -> assertEquals(0L, member.getTokenVersion())
        );
    }

    @Test
    void socialMemberStillRequiresValidProfile() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Member.createSocial(" ", "social@example.com")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Member.createSocial("소셜 회원", null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Member.createSocial("이".repeat(101), "social@example.com")),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> Member.createSocial("소셜 회원", "a".repeat(255)))
        );
    }

    @Test
    void localMemberStillRequiresPasswordHash() {
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Member("일반 회원", "local@example.com", null)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Member("일반 회원", "local@example.com", " "))
        );
    }

    @Test
    void socialMemberCannotUsePasswordLogin() {
        when(members.findByEmail("social@example.com"))
                .thenReturn(Optional.of(Member.createSocial("소셜 회원", "social@example.com")));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.login("social@example.com", "arbitrary-password"));

        assertEquals(AuthErrorCode.INVALID_CREDENTIALS, error.getErrorCode());
        verifyNoInteractions(jwtProvider, refreshTokens);
    }

    @Test
    void localMemberCanStillLoginAndRegisterRefreshToken() {
        Member member = mock(Member.class);
        when(member.getId()).thenReturn(42L);
        when(member.getPassword()).thenReturn(passwordEncoder.encode("correct-password"));
        when(member.getTokenVersion()).thenReturn(3L);
        when(members.findByEmail("local@example.com")).thenReturn(Optional.of(member));
        when(jwtProvider.createAccessToken(42L, 3L)).thenReturn("access-token");
        when(jwtProvider.createRefreshToken(42L, 3L)).thenReturn("refresh-token");
        Claims claims = mock(Claims.class);
        when(claims.getId()).thenReturn("test-refresh-id");
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + 60000));
        when(jwtProvider.getRefreshClaims("refresh-token")).thenReturn(claims);

        TokenPair tokens = service.login(" local@example.com ", "correct-password");

        assertEquals("access-token", tokens.getAccessToken());
        assertEquals("refresh-token", tokens.getRefreshToken());
        verify(refreshTokens).save(any(RefreshToken.class));
    }

    @Test
    void wrongPasswordStillFailsForLocalMember() {
        Member member = new Member(
                "일반 회원", "local@example.com", passwordEncoder.encode("correct-password")
        );
        when(members.findByEmail("local@example.com")).thenReturn(Optional.of(member));

        BusinessException error = assertThrows(BusinessException.class,
                () -> service.login("local@example.com", "wrong-password"));

        assertEquals(AuthErrorCode.INVALID_CREDENTIALS, error.getErrorCode());
        verifyNoInteractions(jwtProvider, refreshTokens);
    }
}
