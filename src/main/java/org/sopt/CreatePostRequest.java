package org.sopt;

public record CreatePostRequest (
    String title,
    String content,
    Category category
) {

}