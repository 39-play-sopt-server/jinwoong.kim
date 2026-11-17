package org.sopt.member.domain;

import jakarta.persistence.*;
import org.sopt.common.domain.BaseEntity;
import lombok.Getter;

/**
 * CLASS: 회원 식별자, 이름, 이메일과 비밀번호 해시를 저장하고 가입 정보의 입력 규칙을 검증합니다.
 */
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1L; 저장 전에는 null

    @Column(nullable = false, length = 100)
    private String name; // 예: "진웅"

    @Column(nullable = false, unique = true, length = 254)
    private String email; // 예: "kjw@kjw.com"

    @Column(length = 255)
    private String password; // 일반 회원은 비밀번호 해시, 소셜 전용 회원은 null

    @Getter
    @Column(nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private long tokenVersion = 0L; // 기존 토큰의 사용 가능 여부를 판단하는 회원별 버전

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20,
            columnDefinition = "VARCHAR(20) DEFAULT 'ROLE_USER'"
    )
    private MemberRole role = MemberRole.ROLE_USER; // 회원가입 시 기본 권한

    /**
     * CONSTRUCTOR: JPA가 회원 객체를 생성할 수 있도록 기본 생성자 제공
     */
    protected Member() {}

    /**
     * CONSTRUCTOR: 이름과 이메일의 공통 입력 검증 및 초기화
     */
    private Member(String name, String email) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > 100) {
            throw new IllegalArgumentException("이름은 100자 이하여야 합니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        String trimmedEmail = email.trim();
        if (trimmedEmail.length() > 254) {
            throw new IllegalArgumentException("이메일은 254자 이하여야 합니다.");
        }

        this.name = trimmedName;
        this.email = trimmedEmail;
    }

    /**
     * CONSTRUCTOR: 일반 회원의 프로필과 비밀번호 해시 초기화
     */
    public Member(String name, String email, String encodedPassword) {
        this(name, email);

        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("해싱된 비밀번호는 필수입니다.");
        }

        this.password = encodedPassword;
    }

    /**
     * METHOD: 비밀번호 없는 소셜 전용 회원 생성
     */
    public static Member createSocial(String name, String email) {
        return new Member(name, email);
    }

    /**
     * METHOD: 회원의 고유 식별자를 반환합니다.
     */
    public Long getId() { return id; }
    /**
     * METHOD: 회원의 이름을 반환합니다.
     */
    public String getName() { return name; }

    /**
     * METHOD: 회원의 이메일을 반환합니다.
     */
    public String getEmail() { return email; }

    /**
     * METHOD: 로그인 시 비밀번호 대조에 사용할 저장된 해시를 반환합니다.
     */
    public String getPassword() { return password; }
}
