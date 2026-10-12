package org.sopt.post.controller;

import org.sopt.common.response.Response;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface PostApi {

    @Operation(
            summary = "게시글 생성",
            description = "제목, 본문, 카테고리를 입력받아 게시글을 생성합니다."
    )
    ResponseEntity<Response<Void>> createPost(CreatePostRequest request);

    @Operation(
            summary = "게시글 목록 조회",
            description = "전체 게시글을 ID 오름차순으로 조회합니다. 조회수는 증가하지 않습니다."
    )
    ResponseEntity<Response<List<PostResponse>>> readPostList();

    @Operation(
            summary = "게시글 상세 조회",
            description = "ID에 해당하는 게시글을 조회하고 조회수를 1 증가시킵니다."
    )
    ResponseEntity<Response<PostResponse>> readPost(@PathVariable("postId") long id);

    @Operation(
            summary = "게시글 수정",
            description = "ID에 해당하는 게시글의 제목과 본문을 수정합니다. 두 항목 모두 입력해야 합니다."
    )
    ResponseEntity<Response<Void>> updatePost(@PathVariable("postId") long id, @RequestBody UpdatePostRequest request);

    @Operation(
            summary = "게시글 삭제",
            description = "ID에 해당하는 게시글을 삭제합니다."
    )
    ResponseEntity<Response<Void>> deletePost(@PathVariable("postId") long id);
}
