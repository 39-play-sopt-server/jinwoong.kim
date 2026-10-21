package org.sopt.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.sopt.common.response.ApiResponse;
import org.sopt.member.dto.CreateMemberRequest;
import org.sopt.member.dto.MemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * INTERFACE: 회원 생성·삭제 계약과 Swagger 문서를 정의합니다.
 */
@Tag(name = "MEMBER", description = "회원 API")
public interface MemberApi {
    /**
     * METHOD: 회원 생성 요청과 생성 ID·이름 응답을 선언합니다.
     */
    @Operation(summary = "회원 생성", description = "이름으로 회원을 등록합니다. data에 ID·이름을, Location에 리소스 URI를 반환합니다. 이름 중복은 허용하며 로그인 API는 아닙니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "회원 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이름 누락·공백·100자 초과 또는 JSON 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<MemberResponse>> create(@RequestBody CreateMemberRequest request);

    /**
     * METHOD: 회원 삭제 요청과 작성글·좋아요가 남은 경우의 실패 응답을 선언합니다.
     */
    @Operation(summary = "회원 삭제", description = "작성글과 좋아요가 남아 있으면 409로 거절합니다. 회원이 없으면 404를 반환합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "406", description = "Accept에서 JSON 응답을 허용하지 않음. 응답 본문 없음", content = @Content),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "회원 ID 형식 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 회원", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "작성글·좋아요가 남아 있거나 DB 제약조건 충돌", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiResponse.class)))
    })
    ResponseEntity<ApiResponse<Void>> delete(@PathVariable("memberId") Long id);
}
