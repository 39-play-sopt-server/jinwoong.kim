package org.sopt.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * INTERFACE: 인증된 회원의 좋아요 추가·취소 API 계약과 Swagger 응답 정의
 */
@Tag(name = "LIKE", description = "게시글 좋아요 API")
public interface PostLikeApi {

    /**
     * METHOD: 인증된 회원의 좋아요 추가 요청과 성공·실패 응답 선언
     */
    @Operation(
            summary = "게시글 좋아요 추가",
            description = "게시글 ID와 인증된 회원 정보로 좋아요 추가. 요청 본문 불필요. "
                    + "이미 좋아요를 누른 회원의 중복 요청은 개수 증가 없이 성공 응답 반환"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 추가 성공 또는 이미 좋아요가 있는 상태"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 토큰 누락 또는 유효하지 않은 토큰", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "CSRF 검증 실패", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글 또는 회원", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> like(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal Long memberId
    );

    /**
     * METHOD: 인증된 회원 본인의 좋아요 취소와 기록 부재 시 오류 응답 선언
     */
    @Operation(
            summary = "게시글 좋아요 취소",
            description = "인증된 회원 본인의 좋아요 취소. 요청 본문 불필요. "
                    + "본인의 좋아요 기록이 없으면 404와 '좋아요를 누른 흔적이 없습니다.' 메시지 반환"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 ID 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 토큰 누락 또는 유효하지 않은 토큰", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "CSRF 검증 실패", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 게시글·회원 또는 좋아요 기록 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "예상하지 못한 서버 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> unlike(
            @PathVariable("postId") Long postId,
            @AuthenticationPrincipal Long memberId
    );
}
