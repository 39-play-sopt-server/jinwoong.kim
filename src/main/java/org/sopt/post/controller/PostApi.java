package org.sopt.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.common.response.ApiResponse;
import org.sopt.post.domain.Category;
import org.sopt.post.dto.CreatePostRequest;
import org.sopt.post.dto.PostIdResponse;
import org.sopt.post.dto.PostPageResponse;
import org.sopt.post.dto.PostResponse;
import org.sopt.post.dto.UpdatePostRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * INTERFACE: 게시글 API의 메서드 계약과 Swagger 문서 설명을 정의합니다.
 */
@Tag(name = "POST", description = "게시글 API")
public interface PostApi {

    /**
     * METHOD: 게시글 생성 API의 요청 타입과 성공 응답 타입을 선언합니다.
     */
    @Operation(
            summary = "게시글 생성",
            description = "제목, 본문, 카테고리, 작성자 ID를 입력받아 게시글을 생성합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "게시글 생성 성공. data.id와 Location 헤더에 생성 리소스 정보 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "필수 입력값 누락, 제목·본문·카테고리·작성자 ID 오류 또는 잘못된 JSON", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 작성자", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<PostIdResponse>> createPost(CreatePostRequest request);

    /**
     * METHOD: 페이지 번호와 크기로 목록을 조회하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 목록 조회",
            description = "선택한 카테고리의 게시글을 생성일 내림차순(같은 시각이면 ID 내림차순)으로 페이지 조회합니다. "
                    + "category, memberId, title은 선택조건이며 함께 전달하면 AND로 검색합니다. "
                    + "page는 0부터 시작하며 기본값은 page=0, size=5입니다. "
                    + "조회수는 증가하지 않습니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공. 검색 결과가 없거나 페이지 범위를 벗어나면 빈 목록 반환"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "page·size 범위 오류, 작성자 ID 오류 또는 검색 조건의 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<PostPageResponse>> readPostList(int page, int size, Category category, Long memberId, String title);

    /**
     * METHOD: ID로 상세 조회하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 상세 조회",
            description = "ID에 해당하는 게시글을 조회하고 조회수를 1 증가시킵니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<PostResponse>> readPost(@PathVariable("postId") long id);

    /**
     * METHOD: ID와 수정 데이터로 게시글을 수정하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 수정",
            description = "ID에 해당하는 게시글의 제목과 본문을 수정합니다. 생략하거나 공백인 항목은 기존 값을 유지합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식 오류, 제목 길이 초과 또는 잘못된 JSON", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<PostIdResponse>> updatePost(@PathVariable("postId") long id, @RequestBody UpdatePostRequest request);

    /**
     * METHOD: ID로 게시글을 삭제하는 API의 계약을 선언합니다.
     */
    @Operation(
            summary = "게시글 삭제",
            description = "ID에 해당하는 게시글을 삭제합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글과 연결된 좋아요 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<PostIdResponse>> deletePost(@PathVariable("postId") long id);
}
