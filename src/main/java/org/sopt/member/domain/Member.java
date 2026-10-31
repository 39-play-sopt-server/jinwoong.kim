package org.sopt.member.domain;

import jakarta.persistence.*;
import org.sopt.common.domain.BaseEntity;

/**
 * CLASS: 회원의 이름과 식별자를 저장하고 이름의 입력 규칙을 검증
 */
@Entity
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1L; 저장 전에는 null

    @Column(nullable = false, length = 100)
    private String name; // 예: "진웅"

    /**
     * CONSTRUCTOR: JPA가 회원 객체를 생성할 수 있도록 기본 생성자 제공
     */
    protected Member() {}

    /**
     * CONTRUCTOR: 이름의 공백과 길이를 검증하고 앞뒤 공백즐 제거해 저장
     */
    public Member(String name) {
        if(name == null || name.isBlank()) {
            throw new IllegalArgumentException("이름은 필수입니다.");
        }
        String trimmed = name.trim();
        if(trimmed.length() > 100) {
            throw new IllegalArgumentException("이름은 100자 이하여야 합니다.");
        }
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
}
