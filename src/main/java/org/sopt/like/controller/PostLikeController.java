package org.sopt.like.controller;

import org.sopt.common.response.Response;
import org.sopt.like.service.PostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 좋아요 추가 취소 HTTP요청을 Service에 위임
 */
@RestController
@RequestMapping("/api/v1/posts/{postId}/likes")
public class PostLikeController {
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
    @PutMapping("/{memberId}")
    public ResponseEntity<Response<Void>> like(@PathVariable("postId") Long postId, @PathVariable("memberId") Long memberId) {
        service.like(postId, memberId);
        return ResponseEntity.ok(Response.success("PostId:"+ postId + " 에 좋아요를 눌렀습니다", null));
    }

    /**
     * METHOD: 좋아요 취소 요청을 Service에 전달하고 성공 응답을 반환
     */
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Response<Void>> unlike(@PathVariable("postId") Long postId, @PathVariable("memberId") Long memberId) {
        service.unlike(postId, memberId);
        return ResponseEntity.ok(Response.success("PostId:"+ postId + "에 좋아요를 취소했습니다", null));
    }



}
