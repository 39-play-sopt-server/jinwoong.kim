package org.sopt.like.controller;

import lombok.RequiredArgsConstructor;
import org.sopt.common.response.ApiResponse;
import org.sopt.like.service.PostLikeService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 인증된 회원의 좋아요 추가·취소 요청을 Service에 위임
 */
@RestController
@RequestMapping(value = "/api/v1/posts/{postId}/likes", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PostLikeController implements PostLikeApi {
    private final PostLikeService service; // 예: Spring이 주입한 Service 객체

    /**
     * METHOD: 인증된 회원의 좋아요 추가와 성공 응답 반환
     */
    @Override
    @PutMapping
    public ResponseEntity<ApiResponse<Void>> like(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal Long memberId
    ) {
        service.like(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(LikeSuccessCode.LIKE_CREATED, null));
    }

    /**
     * METHOD: 인증된 회원 본인의 좋아요 취소와 성공 응답 반환
     */
    @Override
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> unlike(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal Long memberId
    ) {
        service.unlike(postId, memberId);
        return ResponseEntity.ok(ApiResponse.success(LikeSuccessCode.LIKE_DELETED, null));
    }

}
