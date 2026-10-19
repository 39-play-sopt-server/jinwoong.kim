package org.sopt.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

/**
 * CLASS: 생성 시각을 공통으로 관리하고 상속받는 엔터티의 테이블에 매핑합니다.
 */
@MappedSuperclass
public abstract class BaseEntity {
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt; // 예: 2026-10-19T12:00:00; 최초 저장 전에 설정

    /**
     * CONTRUCTOR: 상속받는 엔터티가 사용할 기본 생성자 제공
     */
    protected BaseEntity() {}

    /**
     * METHOD: 최초 저장 전에 생성 시각이 비어 있으면 현재 시각을 기록
     */
    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * METHOD: 저장된 생성 시각을 반환
     * @return createdAt
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

