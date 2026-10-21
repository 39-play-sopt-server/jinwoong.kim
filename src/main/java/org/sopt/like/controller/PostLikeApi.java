package org.sopt.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * INTERFACE: 좋아요 추가·취소 API의 계약과 Swagger 응답 설명을 정의합니다.
 */
@Tag(name = "LIKE", description = "게시글 좋아요 API")
public interface PostLikeApi {

    /**
     * METHOD: 회원의 좋아요 추가 요청과 성공·실패 응답을 선언합니다.
     */
    @Operation(
            summary = "게시글 좋아요 추가",
            description = "게시글 ID와 회원 ID로 좋아요를 추가합니다. 요청 본문은 필요하지 않습니다. "
                    + "이미 좋아요를 누른 회원이 다시 요청하면 개수를 늘리지 않고 성공을 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 추가 성공 또는 이미 좋아요가 있는 상태"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 또는 회원 ID의 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 회원", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> like(
            @PathVariable("postId") Long postId,
            @PathVariable("memberId") Long memberId
    );

    /**
     * METHOD: 회원의 좋아요 취소 요청과 기록이 없는 경우의 오류 응답을 선언합니다.
     */
    @Operation(
            summary = "게시글 좋아요 취소",
            description = "해당 회원이 게시글에 남긴 좋아요를 삭제합니다. 요청 본문은 필요하지 않습니다. "
                    + "좋아요를 누른 기록이 없으면 404와 '좋아요를 누른 흔적이 없습니다.' 메시지를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 또는 회원 ID의 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글·회원 또는 좋아요 기록 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> unlike(
            @PathVariable("postId") Long postId,
            @PathVariable("memberId") Long memberId
    );
}
