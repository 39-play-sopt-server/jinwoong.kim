package org.sopt.post.domain;

import jakarta.persistence.*;
import org.sopt.common.domain.BaseEntity;
import org.sopt.member.domain.Member;

/**
 * CLASS: 게시글의 제목 본문 카테고리를 저장하고 작성자 회원과 연결합니다.
 */
@Entity
@Table(name = "post")
public class Post extends BaseEntity {
    @Column(nullable = false, length = 255)
    private String title; // 예: "첫 번째 게시글"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // 예: "게시글 본문입니다."

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Category category; // 예: Category.GENERAL

    @Column(nullable = false)
    private Long viewCount = 0L; // 예: 상세 조회 전 0L, 조회 후 1L

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 예: 1L

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 예: DB에서 조회한 작성자 Member객체


    /**
     * CONSTRUCTOR: 제목 본문 카테고리 작성자를 검증하고 게시글의 초기 상태를 설정
     */
    protected Post() {
    }

    public Post(
            String title,
            String content,
            Category category,
            Member member
    ) {

        validateTitle(title);
        validateContent(content);

        if (category == null || member == null) {
            throw new IllegalArgumentException("카테고리와 작성자는 필수입니다.");
        }

        this.title = title;
        this.content = content;
        this.category = category;
        this.member = member;
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
        if (title.length() > 255) { throw new IllegalArgumentException("제목은 255자 이하여야합니다."); }
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

    /**
     * METHOD: 게시글이 연결된 작성자 회원을 반환합니다.
     */
    public Member getMember() { return member; }

}
