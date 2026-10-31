package org.sopt.post.dto;

import org.sopt.post.domain.Category;

/**
 * RECORD: 게시글 생성에 필요한 제목,본문,카테고리와 작성자 ID를 전달
 */
public record CreatePostRequest (
    String title, // 예: "게시글 제목"
    String content, // 예: "게시글 본문"
    Category category, // 예: Category.GENERAL
    Long memberId // 예: DB에 저장된 화원 ID: 1L
) {}
