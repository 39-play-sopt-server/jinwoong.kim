package org.sopt.post.controller;

import org.sopt.common.response.Response;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;
import org.sopt.post.service.PostService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostController implements PostApi {

    // 심화 view 없애기
    // 콘솔 입출력을 담당하는 View
    // private final PostView view;
    // 게시글 처리를 담당하는 Service
    private final PostService service;

    // CONSTRUCTOR: 입출력을 담당하는 View와 게시글 처리를 담당하는 Service를 전달받습니다.
    public PostController(PostService service)
    {
        this.service = service;
    }

    // METHODS -----------------
    // METHOD: 게시글 작성
//    @Operation(
//            summary = "게시글 생성",
//            description = "제목, 본문, 카테고리를 입력받아 게시글을 생성합니다."
//    )
    @Override
    @PostMapping
    public ResponseEntity<Response<Void>> createPost(@RequestBody CreatePostRequest request) {
        service.createPost(request.title(), request.content(), request.category());
        // 201 created
        return ResponseEntity.status(HttpStatus.CREATED).body(Response.success("게시글이 작성되었습니다.", null));
    }

    // 심화 view 없애기
    // METHOD: 게시글 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<Response<List<PostResponse>>> readPostList() {
        return ResponseEntity.ok(
                Response.success(
                "게시글 목록을 조회했습니다.",
                service.readPostList()
                )
        );
    }

    // 심화 view 없애기
    // METHOD: ID로 게시글 단건 조회
    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<Response<PostResponse>> readPost(@PathVariable("postId") long id){
        PostResponse post = service.readPost(id);

        return ResponseEntity.ok(Response.success("게시글을 조회했습니다.", post));
    }

    // 심화 view 없애기
    // METHOD: 게시글 수정
    // 입력하지 않은 항목은 기존 값을 유지합니다.
    @Override
    @PutMapping("/{postId}")
    public ResponseEntity<Response<Void>> updatePost(
            @PathVariable("postId") long id,
            @RequestBody UpdatePostRequest request
    ) {
        service.updatePost(id, request.title(), request.content());
        return ResponseEntity.ok(Response.success("ID:" + id +" 게시글이 수정되었습니다.", null));

    }

    // METHOD: ID로 게시글 삭제
    @Override
    @DeleteMapping("/{postId}")
    public ResponseEntity<Response<Void>> deletePost(@PathVariable("postId") long id) {
        service.deletePost(id);
        return ResponseEntity.status(HttpStatus.OK).body(Response.success("ID:" + id + " 게시글이 삭제되었습니다.", null));
    }
}
