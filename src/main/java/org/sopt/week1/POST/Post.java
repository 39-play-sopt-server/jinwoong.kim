package org.sopt;

public class Post {
    String title;
    String content;

    public Post(String title, String content) {
        validate(title, content)
        this.title = title;
        this.content = content;
    }

    // METHOD: UBDATE title & content
    public void update(String title, String content) {
        validate(title, content);
        this.title = title; // 제목수정
        this.content = content // 내용수정
    }

    // METHOD: GET title
    public void getTitle() {return this.title; }

    // METHOD: GET content
    public void getTitle() {return this.content; }

    // METHOD: validating title and content
    private void validate(String title, String content) {
        if (title == null) throw new IllegalArgumentException("제목을 입력하세요!")
        if (title == null) throw new IllewgalArgumentException("내용을 입력하세요!")
    }
}