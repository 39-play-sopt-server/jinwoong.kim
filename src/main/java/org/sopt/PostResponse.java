package org.sopt;

import java.time.LocalDateTime;

public record PostResponse (
    long id,
    String title,
    String content,
    Category category,
    LocalDateTime createdAt,
    long viewCount
) {
    public static PostResponse from(Post post) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getCreatedAt(),
                post.getViewCount()
        );
    }
}