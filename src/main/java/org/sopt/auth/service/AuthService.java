package org.sopt.auth.service;

import org.sopt.common.exception.BusinessException;
import org.sopt.member.domain.Member;
import org.sopt.member.dto.MemberResponse;
import org.sopt.member.exception.MemberErrorCode;
import org.sopt.member.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CLASS: 회원가입 시 입력 확인, 이메일 중복 검사, 비밀번호 해시 처리와 저장을 담당합니다.
 */
@Service
@Transactional
public class AuthService {

    private final MemberRepository members; // 예: 회원 조회와 저장에 사용하는 저장소
    private final PasswordEncoder passwordEncoder; // 예: PasswordConfig에서 등록한 BCrypt 구현체

    /**
     * CONSTRUCTOR: 회원 저장소와 비밀번호 해시 처리 객체를 생성자로 주입받습니다.
     */
    public AuthService(
            MemberRepository members,
            PasswordEncoder passwordEncoder
    ) {
        this.members = members;
        this.passwordEncoder = passwordEncoder;
    }

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
}
