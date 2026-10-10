package org.sopt;

// 게시글 작성 시각에 사용합니다.
import java.time.LocalDateTime;

public class Post {
    private String title;
    private String content;
    private final Category category;
    private final LocalDateTime createdAt;
    // 심화: 게시글을 구분하는 고유 ID입니다.
    private final long id;

    // CONSTRUCTOR: 게시글 생성에 필요한 값을 전달받습니다.
    public Post(
            String title,
            String content,
            Category category,
            long id
    ) {

        // 모든 입력을 검증한 뒤 필드를 설정합니다.
        // 심화: 고유 ID는 양수만 허용합니다.
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

    // ------------------------------------
    // GETTER: 제목 조회
    public String getTitle() {return this.title; }

    // GETTER: 본문 조회
    public String getContent() {return this.content; }

    // GETTER: 카테고리 조회
    public Category getCategory() {return category; }

    // GETTER: 작성 시각 조회
    public LocalDateTime getCreatedAt() {return createdAt; }

    // 심화 GETTER: 고유 ID 조회
    public long getId() {
        return id;
    }


    // METHOD: 제목과 본문을 모두 검증한 뒤 함께 수정합니다.
    public void update(String title, String content) {
        validateTitle(title);
        validateContent(content);

        this.title = title;
        this.content = content;
    }

    // 입력값 검증
    // METHOD: 제목의 빈 값과 공백 검사
    private void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("제목은 비어 있을 수 없습니다");
        }
    }

    // METHOD: 본문의 빈 값과 공백 검사
    private void validateContent(String content) {
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("본문은 비어 있을 수 없습니다");
        }
    }
}
