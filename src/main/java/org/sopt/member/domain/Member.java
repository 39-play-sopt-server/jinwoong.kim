package org.sopt.member.domain;

import jakarta.persistence.*;
import org.sopt.common.domain.BaseEntity;

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

    @Column(nullable = false, length = 255)
    private String password; // 원문이 아닌 BCrypt 비밀번호 해시
    /**
     * CONSTRUCTOR: JPA가 회원 객체를 생성할 수 있도록 기본 생성자 제공
     */
    protected Member() {}

    /**
     * CONSTRUCTOR: 이름·이메일의 공백과 길이 및 비밀번호 해시의 존재를 검증해 저장합니다.
     */
    public Member(String name, String email, String encodedPassword) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }

        String trimmed = name.trim();
        if(trimmed.length() > 100) {
            throw new IllegalArgumentException("이름은 100자 이하여야 합니다.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("이메일은 필수입니다.");
        }

        String trimmedEmail = email.trim();
        if (trimmedEmail.length() > 254) {
            throw new IllegalArgumentException("이메일은 254자 이하여야 합니다");
        }

        if (encodedPassword == null || encodedPassword.isBlank()) {
            throw new IllegalArgumentException("해싱된 비밀번호는 필수입니다.");
        }

        this.email = trimmedEmail;
        this.password = encodedPassword;
        this.name = trimmed;
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
