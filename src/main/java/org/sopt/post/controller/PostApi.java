package org.sopt.post.controller;

import org.sopt.common.response.Response;
import org.sopt.post.domain.Category;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * INTERFACE: 게시글 API의 메서드 계약과 Swagger 문서 설명을 정의합니다.
 */
public interface PostApi {

    /**
     * METHOD: 게시글 생성 API의 요청 타입과 성공 응답 타입을 선언합니다.
     */
    @Operation(
            summary = "게시글 생성",
            description = "제목, 본문, 카테고리를 입력받아 게시글을 생성합니다."
    )
    ResponseEntity<Response<Void>> createPost(CreatePostRequest request);

    /**
     * METHOD: 페이지 번호와 크기로 목록을 조회하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 목록 조회",
            description = "선택한 카테고리의 게시글을 ID 오름차순으로 페이지 조회합니다. "
                    + "category를 생략하면 전체 카테고리를 조회합니다. "
                    + "page는 0부터 시작하며 기본값은 page=0, size=5입니다. "
                    + "조회수는 증가하지 않습니다."
    )
    ResponseEntity<Response<List<PostResponse>>> readPostList(int page, int size, Category category);

    /**
     * METHOD: ID로 상세 조회하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 상세 조회",
            description = "ID에 해당하는 게시글을 조회하고 조회수를 1 증가시킵니다."
    )
    ResponseEntity<Response<PostResponse>> readPost(@PathVariable("postId") long id);

    /**
     * METHOD: ID와 수정 데이터로 게시글을 수정하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 수정",
            description = "ID에 해당하는 게시글의 제목과 본문을 수정합니다. 생략하거나 공백인 항목은 기존 값을 유지합니다."
    )
    ResponseEntity<Response<Void>> updatePost(@PathVariable("postId") long id, @RequestBody UpdatePostRequest request);

    /**
     * METHOD: ID로 게시글을 삭제하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 삭제",
            description = "ID에 해당하는 게시글을 삭제합니다."
    )
    ResponseEntity<Response<Void>> deletePost(@PathVariable("postId") long id);
}
