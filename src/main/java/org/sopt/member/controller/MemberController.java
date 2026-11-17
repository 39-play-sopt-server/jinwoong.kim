package org.sopt.member.controller;

import org.sopt.common.response.ApiResponse;
import org.sopt.member.service.MemberService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.cookie.RefreshTokenCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

/**
 * CLASS: 회원 삭제 HTTP 요청을 Service에 위임하고 응답을 반환
 */
@RestController
@RequestMapping(value = "/api/v1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class MemberController implements MemberApi {
    private final MemberService service; // 예: Spring이 주입한 Service 객체
    private final RefreshTokenCookie refreshTokenCookie; // 탈퇴 후 Refresh

    /**
     * METHOD: 회원 삭제를 Service에 위임하고 성공 응답을 반환
     */
    @Override
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> delete(
            @AuthenticationPrincipal Long memberId
    ) {
        // 1. 요청 주소의 ID 대신 인증된 회원의 ID로 탈퇴를 처리
        service.delete(memberId);

        // 2. 탈퇴에 성공하면 현재 클라이언트의 쿠키를 삭제
        return ResponseEntity
                .ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.clear())
                .body(ApiResponse.<Void>success(
                        MemberSuccessCode.MEMBER_DELETED,
                        null
                ));
    }
}

