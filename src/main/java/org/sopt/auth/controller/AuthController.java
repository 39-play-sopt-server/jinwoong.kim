package org.sopt.auth.controller;

import java.net.URI;

import lombok.RequiredArgsConstructor;
import org.sopt.auth.dto.LoginRequest;
import org.sopt.auth.dto.SignupRequest;
import org.sopt.auth.service.AuthService;
import org.sopt.common.response.ApiResponse;
import org.sopt.member.controller.MemberSuccessCode;
import org.sopt.member.dto.MemberResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.sopt.auth.cookie.RefreshTokenCookie;
import org.sopt.auth.dto.TokenPair;

/**
 * CLASS: 회원가입 HTTP 요청을 AuthService에 위임하고 응답을 반환합니다.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService; // 예: Spring이 주입한 회원가입 업무 처리 객체

    private final RefreshTokenCookie refreshTokenCookie; // Refresh Token 을 전달할 쿠키 생성 객체

    /**
     * METHOD: 가입 정보를 전달하고 비밀번호를 제외한 회원 정보와 201 응답을 반환합니다.
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<MemberResponse>> signup(
            @RequestBody SignupRequest request
    ) {
        MemberResponse member = authService.signup(
                request.name(),
                request.email(),
                request.password()
        );

        // 생성된 회원의 URI를 Location 헤더에 담고 공통 응답 형식으로 반환합니다.
        return ResponseEntity
                .created(URI.create("/api/v1/members/" + member.id()))
                .body(ApiResponse.success(
                        MemberSuccessCode.MEMBER_CREATED,
                        member
                ));
    }

    /**
     * METHOD: 로그인 성공 시 Access Token을 Authorization 응답 헤더로 전달
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Void>> login(
            @RequestBody LoginRequest request
    ) {
        // 1. 이메일과 비밀번호를 검증하고 두토큰을 발급받음
        TokenPair tokens = authService.login(
                request.email(),
                request.password()
        );

        // 2. Refresh Token을 담을 쿠키 문자열
        String cookie = refreshTokenCookie.create(tokens.getRefreshToken());

        // 3. Access token 과 Refresh Token 쿠키를 응답 헤더에 담음
        return ResponseEntity
                .ok()
                .header(HttpHeaders.AUTHORIZATION,
                        "Bearer " + tokens.getAccessToken()
                )
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(ApiResponse.<Void>success(
                        AuthSuccessCode.LOGIN_SUCCESS,
                        null
                ));

        /*
         * 응답 헤더:
         * Authorization: Bearer {Access Token}
         * Set-Cookie: refreshToken={Refresh Token}; ...
         *
         * 응답 본문:
         * {
         *   "success": true,
         *   "code": "LOGIN_SUCCESS",
         *   "message": "로그인에 성공했습니다.",
         *   "data": null
         * }
         */
    }
}