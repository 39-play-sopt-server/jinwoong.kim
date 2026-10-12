package org.sopt.post.domain;

import java.time.LocalDateTime;

/**
 * CLASS: 게시글의 상태와 생성·수정 시 지켜야 하는 검증 규칙을 관리합니다.
 */
public class Post {
    private String title; // 예: "첫 번째 게시글"
    private String content; // 예: "게시글 본문입니다."
    private final Category category; // 예: Category.GENERAL
    private final LocalDateTime createdAt; // 예: 2026-10-12T12:00:00

    private final long id; // 예: 1L

    private long viewCount = 0L; // 예: 상세 조회 전 0L, 조회 후 1L

    /**
     * CONSTRUCTOR: ID·제목·본문·카테고리를 검증하고, 작성 시각과 초기 조회수를 가진 게시글을 생성합니다.
     */
    public Post(
            String title,
            String content,
            Category category,
            long id
    ) {

        if (id <= 0) {
            throw new IllegalArgumentException("게시글 ID는 양수여야 합니다.");
        }
        validateContent(content);
        validateTitle(title);

        if (category == null) {
            throw new IllegalArgumentException("카테고리를 선택해주세요.");
        }

        this.title = title;
        this.content = content;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.id = id;
    }

    /**
     * METHOD: 현재 제목을 반환합니다.
     */
    public String getTitle() {return this.title; }

    /**
     * METHOD: 현재 본문을 반환합니다.
     */
    public String getContent() {return this.content; }

    /**
     * METHOD: 게시글의 카테고리를 반환합니다.
     */
    public Category getCategory() {return category; }

    /**
     * METHOD: 게시글이 생성된 시각을 반환합니다.
     */
    public LocalDateTime getCreatedAt() {return createdAt; }

    /**
     * METHOD: 게시글의 고유 ID를 반환합니다.
     */
    public long getId() {
        return id;
    }

    /**
     * METHOD: 생략되거나 공백인 항목은 기존 값으로 대체하고, 두 항목을 모두 검증한 뒤 수정합니다.
     */
    public synchronized void update(String title, String content) {
        String updatedTitle = (title == null || title.isBlank()) ? this.title : title;
        String updatedContent = (content == null || content.isBlank()) ? this.content : content;

        validateTitle(updatedTitle);
        validateContent(updatedContent);

        this.title = updatedTitle;
        this.content = updatedContent;
    }

    /**
     * METHOD: 제목이 null이거나 공백이면 입력 오류를 발생시킵니다.
     */
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 비어 있을 수 없습니다");
        }
    }

    /**
     * METHOD: 본문이 null이거나 공백이면 입력 오류를 발생시킵니다.
     */
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("본문은 비어 있을 수 없습니다");
        }
    }

    /**
     * METHOD: 현재 조회수를 반환하며, 조회수를 증가시키지는 않습니다.
     */
    public long getViewCount() {
        return viewCount;
    }

    /**
     * METHOD: 상세 조회 시 게시글의 조회수를 1 증가시킵니다.
     * synchronized:
     */
    public synchronized void increaseViewCount() {
        viewCount++;
    }

}
