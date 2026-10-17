package org.sopt.post.dto;

import org.sopt.post.domain.Category;

/**
 * RECORD: 게시글 생성 요청의 제목·본문·카테고리를 전달합니다.
 */
public record CreatePostRequest (
    String title, // 예: "게시글 제목"
    String content, // 예: "게시글 본문"
    Category category // 예: Category.GENERAL
) {

}
