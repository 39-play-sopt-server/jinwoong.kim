package org.sopt.week1.POST;

public class Post {
    private String title;
    private String content;

    public Post(String title, String content) {
        this.title = title;
        this.content = content;
    }

    // METHOD: GET title
    public String getTitle() {return this.title; }

    // METHOD: GET content
    public String getContent() {return this.content; }

    // METHOD: UPDATE title
    public void updateTitle(String title) {
        this.title = title;
    }

    // METHOD: UPDATE content
    public void updateContent(String content) {
        this.content = content;
    }
}