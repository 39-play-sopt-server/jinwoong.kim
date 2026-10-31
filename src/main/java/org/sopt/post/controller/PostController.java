package org.sopt.post.controller;

import java.net.URI;
import org.sopt.common.response.ApiResponse;
import org.sopt.post.domain.Category;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostIdResponse;
import org.sopt.post.dto.PostPageResponse;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;
import org.sopt.post.service.PostService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 게시글 HTTP 요청을 받아 Service에 위임하고 성공 응답을 반환합니다.
 */
@RestController
@RequestMapping(value = "/api/v1/posts", produces = MediaType.APPLICATION_JSON_VALUE)
public class PostController implements PostApi {

    private final PostService service; // 예: 생성자로 주입받은 PostService 객체

    /**
     * CONSTRUCTOR: 게시글 요청 처리를 위임할 Service를 생성자로 주입받습니다.
     */
    public PostController(PostService service)
    {
        this.service = service;
    }

    /**
     * METHOD: 요청 본문의 데이터로 게시글을 생성하고 201 성공 응답을 반환합니다.
     */
    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<PostIdResponse>> createPost(@RequestBody CreatePostRequest request) {
        Long id = service.createPost(request.title(), request.content(), request.category(), request.memberId());
        return ResponseEntity.created(URI.create("/api/v1/posts/" + id))
                .body(ApiResponse.success(PostSuccessCode.POST_CREATED, new PostIdResponse(id)));
    }

    /**
     * METHOD: page와 size 쿼리를 받아 해당 페이지의 게시글 목록을 200 응답으로 반환합니다.
     */
    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<PostPageResponse>> readPostList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "category", required = false) Category category,
            @RequestParam(name = "memberId", required = false) Long memberId,
            @RequestParam(name = "title", required = false) String title
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                PostSuccessCode.POST_LIST_READ,
                service.readPostList(page, size, category, memberId, title)
                )
        );
    }

    /**
     * METHOD: 경로의 게시글 ID로 상세 조회를 요청하고, 증가한 조회수를 포함한 DTO를 200 응답으로 반환합니다.
     */
    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> readPost(@PathVariable("postId") long id){
        PostResponse post = service.readPost(id);

        return ResponseEntity.ok(ApiResponse.success(PostSuccessCode.POST_READ, post));
    }

    /**
     * METHOD: 경로의 ID와 요청 본문을 Service에 전달해 수정하고 200 성공 응답을 반환합니다.
     */
    @Override
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostIdResponse>> updatePost(
            @PathVariable("postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        service.updatePost(id, request.title(), request.content());
        return ResponseEntity.ok(ApiResponse.success(PostSuccessCode.POST_UPDATED, new PostIdResponse(id)));

    }

    /**
     * METHOD: 경로의 ID에 해당하는 게시글 삭제를 요청하고 200 성공 응답을 반환합니다.
     */
    @Override
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostIdResponse>> deletePost(@PathVariable("postId") long id) {
        service.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success(PostSuccessCode.POST_DELETED, new PostIdResponse(id)));
    }
}
