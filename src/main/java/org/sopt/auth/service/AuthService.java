package org.sopt.auth.service;

import lombok.RequiredArgsConstructor;

import org.sopt.auth.exception.AuthErrorCode;
import org.sopt.auth.jwt.JwtProvider;
import org.sopt.common.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.exception.MemberErrorCode;
import org.sopt.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.sopt.auth.dto.TokenPair;
import io.jsonwebtoken.Claims;
import org.sopt.auth.domain.RefreshToken;
import org.sopt.auth.repository.RefreshTokenRepository;

import io.jsonwebtoken.JwtException;
import java.time.Instant;

/**
 * CLASS: 회원가입 시 입력 확인, 이메일 중복 검사, 비밀번호 해시 처리와 저장을 담당합니다.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository members; // 예: 회원 조회와 저장에 사용하는 저장소
    private final PasswordEncoder passwordEncoder; // 예: PasswordConfig에서 등록한 BCrypt 구현체

    private final JwtProvider jwtProvider; // 로그인 성공 시 JWT를 발급하는 객체

    private final RefreshTokenRepository refreshTokens; // 허용된 Refresh Token 정보를 저장하는 저장소

    /**
     * METHOD: 이메일 중복을 검사하고 비밀번호를 해시로 변환해 회원을 저장합니다.
     */
    public MemberResponse signup(
            String name,
            String email,
            String password
    ) {
        // 1. 이메일과 비밀번호가 입력되었는지 확인합니다.
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("비밀번호는 필수입니다.");
        }

        // 2. 이메일 앞뒤 공백을 제거한 값으로 가입 여부를 확인합니다.
        String trimmedEmail = email.trim();

        if (members.existsByEmail(trimmedEmail)) {
            throw new BusinessException(
                    MemberErrorCode.EMAIL_ALREADY_EXISTS
            );
        }

        // 3. 원문 비밀번호 대신 BCrypt 해시를 저장하도록 변환합니다.
        String encodedPassword = passwordEncoder.encode(password);

        // 4. Member 생성자에서 도메인 입력 규칙을 검증한 후 저장합니다.
        Member member = new Member(
                name,
                trimmedEmail,
                encodedPassword
        );

        // 응답 DTO에는 회원 ID와 이름만 포함하므로 비밀번호 해시가 노출되지 않습니다.
        return MemberResponse.from(members.save(member));
    }

    /**
     * METHOD: 이메일과 비밀번호를 확인하고 Access Token을 반환
     * readOnly = true삭제 | 로그인에서 DB에 저장함 읽기 전용 X
     */
    public TokenPair login(String email, String password) {
        // 1. 이메일과 비밀전호가 입력되었는지 확인
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 2. 이메일에 해당하는 회원이 없으면 로그인에 실패
        Member member = members.findByEmail(email.trim())
                .orElseThrow(() ->
                        new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));

        // 3. 입력한 원문 비밀번호와 저장된 해시가 일치하는지 확인
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 4. 인증에 성공한 회원의 ID로 Access Token을 발급, 토큰 버전을 JWT에 기록
        String accessToken = jwtProvider.createAccessToken(
                member.getId(), member.getTokenVersion());
        String refreshToken = jwtProvider.createRefreshToken(
                member.getId(), member.getTokenVersion());

        // 5. Refresh Token을 검증하고 저장할 정보를 꺼냄
        Claims claims = jwtProvider.getRefreshClaims(refreshToken);

        RefreshToken savedToken = new RefreshToken(
                claims.getId(),
                member.getId(),
                claims.getExpiration().toInstant()
        );

        // 6. 서버가 허용한 Refresh Token으로 DB에 등록
        refreshTokens.save(savedToken); // refreshTokens는 DB저장소 객체.

        // 7. 저장에 성공하면 두 토큰을 Controller에 전달
        return new TokenPair(accessToken, refreshToken);
    }

    /**
     * METHOD: Refresh Token과 저장 기록을 확인하고 새 Access Token을 발급합니다.
     */
    @Transactional(readOnly = true)
    public String reissue(String refreshToken) {
        Claims claims;
        Long memberId;
        String tokenId;

        // 1. 서명, 만료, 토큰 종류와 필수 식별 정보를 확인
        try {
            claims = jwtProvider.getRefreshClaims(refreshToken);
            memberId = Long.valueOf(claims.getSubject());
            tokenId = claims.getId();

            if (tokenId == null || tokenId.isBlank()) {
                throw new IllegalArgumentException("토큰 식별자가 없습니다.");
            }
        } catch (JwtException | IllegalArgumentException e) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 서버가 허용한 토큰 기록이 있는지 확인
        RefreshToken savedToken = refreshTokens.findById(tokenId)
                .orElseThrow(() ->
                        new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));

        // 3. 저장된 회원 정보와 만료 시각을 확인
        if (!savedToken.getMemberId().equals(memberId)
                || !savedToken.getExpiresAt().isAfter(Instant.now())) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. 회원이 존재하고 Refresh Token의 버전이 현재 버전과 같은지 확인
        Member member = members.findById(memberId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN));
        Long tokenVersion = claims.get("tokenVersion", Long.class);

        if (tokenVersion == null
                || tokenVersion.longValue() != member.getTokenVersion()) {
            throw new BusinessException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 5. 현재 회원의 토큰 버전으로 새 Access Token을 발급
        return jwtProvider.createAccessToken(
                member.getId(), member.getTokenVersion()
        );
    }

    /**
     * METHOD: 해당 회원의 모든 Refresh Token 기록을 삭제해 재발급을 차단
     */
    public void logout(Long memberId) {
        // 1. 기존 Access Token과 Refresh Token의 버전을 무효화
        members.increamentTokenVersion(memberId);

        // 2. 해당 회원의 Refresh Token허용 기록을 모두 삭제
        refreshTokens.deleteByMemberId(memberId);
    }
}
