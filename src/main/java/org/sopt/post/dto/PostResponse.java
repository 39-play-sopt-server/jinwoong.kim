package org.sopt.post.dto;

import org.sopt.post.domain.Category;
import org.sopt.post.domain.Post;

import java.time.LocalDateTime;

/**
 * RECORD: 저장된 Post 객체 대신 게시글의 현재 값을 복사해 전달하는 응답 DTO입니다.
 */
public record PostResponse (
    long id, // 예: 1L
    String title, // 예: "게시글 제목"
    String content, // 예: "게시글 본문"
    Category category, // 예: Category.GENERAL
    LocalDateTime createdAt, // 예: 2026-10-12T12:00:00
    long viewCount, // 예: 3L
    Long memberId,
    String authorName,
    long likeCount // 예: 3L
) {
    /**
     * METHOD: Service 트랜잭션 안에서 게시글과 작성자 및 좋아요 수를 응답 DTO로 복사합니다.
     */
    public static PostResponse from(Post post, long likeCount) {
        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCategory(),
                post.getCreatedAt(),
                post.getViewCount(),
                post.getMember().getId(),
                post.getMember().getName(),
                likeCount
        );
    }
}
