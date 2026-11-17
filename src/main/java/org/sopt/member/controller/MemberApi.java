package org.sopt.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


/**
 * INTERFACE: 회원 삭제 계약과 Swagger 문서를 정의합니다.
 */
@Tag(name = "MEMBER", description = "회원 API")
public interface MemberApi {
    /**
     * METHOD: 회원 삭제 요청과 작성글·좋아요가 남은 경우의 실패 응답을 선언합니다.
     */
    @Operation(summary = "본인 회원 탈퇴", description = "작성글과 좋아요가 남아 있으면 409로 거절합니다. 회원이 없으면 404를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 ID 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "작성글·좋아요가 남아 있거나 DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 토큰이 없거나 유효하지 않음", content =  @Content),
    })
    ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal Long memberId);

    // AuthenticationPrincipal로 바꾼이유:
    // @PathVariable은 사용자가 URL에 지정한 값을 가져오고, @AuthenticationPrincipal은 서버가 인증한 현재 사용자의 정보를 가져와서
}
