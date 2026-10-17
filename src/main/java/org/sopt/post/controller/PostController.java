package org.sopt.post.controller;

import org.sopt.common.response.Response;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;
import org.sopt.post.service.PostService;
import org.sopt.post.domain.Category;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CLASS: 게시글 HTTP 요청을 받아 Service에 위임하고 성공 응답을 반환합니다.
 */
@RestController
@RequestMapping("/api/v1/posts")
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
    public ResponseEntity<Response<Void>> createPost(@RequestBody CreatePostRequest request) {
        service.createPost(request.title(), request.content(), request.category());

        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success("게시글이 작성되었습니다.", null));
    }

    /**
     * METHOD: page와 size 쿼리를 받아 해당 페이지의 게시글 목록을 200 응답으로 반환합니다.
     */
    @Override
    @GetMapping
    public ResponseEntity<Response<List<PostResponse>>> readPostList(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "5") int size,
            @RequestParam(name = "category", required = false) Category category
    ) {
        return ResponseEntity.ok(
                Response.success(
                "게시글 목록을 조회했습니다.",
                service.readPostList(page, size, category)
                )
        );
    }

    /**
     * METHOD: 경로의 게시글 ID로 상세 조회를 요청하고, 증가한 조회수를 포함한 DTO를 200 응답으로 반환합니다.
     */
    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> readPost(@PathVariable("postId") long id){
        PostResponse post = service.readPost(id);

        return ResponseEntity.ok(Response.success("게시글을 조회했습니다.", post));
    }

    /**
     * METHOD: 경로의 ID와 요청 본문을 Service에 전달해 수정하고 200 성공 응답을 반환합니다.
     */
    @Override
    @PutMapping("/{postId}")
    public ResponseEntity<Response<Void>> updatePost(
            @PathVariable("postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        service.updatePost(id, request.title(), request.content());
        return ResponseEntity.ok(Response.success("ID:" + id +" 게시글이 수정되었습니다.", null));

    }

    /**
     * METHOD: 경로의 ID에 해당하는 게시글 삭제를 요청하고 200 성공 응답을 반환합니다.
     */
    @Override
    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable("postId") long id) {
        service.deletePost(id);
        return ResponseEntity.status(HttpStatus.OK).body(Response.success("ID:" + id + " 게시글이 삭제되었습니다.", null));
    }
}
