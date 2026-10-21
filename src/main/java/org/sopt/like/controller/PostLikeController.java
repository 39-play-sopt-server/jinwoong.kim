package org.sopt.like.controller;

import org.sopt.common.response.ApiResponse;
import org.sopt.like.service.PostLikeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 좋아요 추가 취소 HTTP요청을 Service에 위임
 */
@RestController
@RequestMapping(value = "/api/v1/posts/{postId}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostLikeController implements PostLikeApi {
    private final PostLikeService service; // 예: Spring이 주입한 Service 객체

    /**
     * CONSTRUCTOR: 업무 처리에 필요한 저장소 또는 Service를 생성자로 주입
     */
    public PostLikeController(PostLikeService service) {
        this.service = service;
    }

    /**
     * METHOD: 좋아요 요청을 Service에 전달하고 성공 응답을 반환
     */
    @Override
    @PutMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> like(@PathVariable("postId") Long postId, @PathVariable("memberId") Long memberId) {
        service.like(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(LikeSuccessCode.LIKE_CREATED, null));
    }

    /**
     * METHOD: 좋아요 취소 요청을 Service에 전달하고 성공 응답을 반환
     */
    @Override
    @DeleteMapping("/{memberId}")
    public ResponseEntity<ApiResponse<Void>> unlike(@PathVariable("postId") Long postId, @PathVariable("memberId") Long memberId) {
        service.unlike(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(LikeSuccessCode.LIKE_DELETED, null));
    }

}
