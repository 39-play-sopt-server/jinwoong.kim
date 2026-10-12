package org.sopt.post.dto;

/**
 * RECORD: 게시글 수정 요청을 전달합니다. 생략하거나 공백인 항목은 기존 값을 유지합니다.
 */
public record UpdatePostRequest (
    String title, // 예: "수정한 제목"; 생략·공백이면 기존 값 유지
    String content // 예: "수정한 본문"; 생략·공백이면 기존 값 유지
) {

}
