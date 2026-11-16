package org.sopt.auth.controller;

import java.net.URI;
import org.sopt.auth.dto.SignupRequest;
import org.sopt.auth.service.AuthService;
import org.sopt.common.response.ApiResponse;
import org.sopt.member.controller.MemberSuccessCode;
import org.sopt.member.dto.MemberResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CLASS: 회원가입 HTTP 요청을 AuthService에 위임하고 응답을 반환합니다.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService; // 예: Spring이 주입한 회원가입 업무 처리 객체

    /**
     * CONSTRUCTOR: 회원가입을 처리할 AuthService를 생성자로 주입받습니다.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

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
}