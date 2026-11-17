package org.sopt.auth.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.sopt.auth.controller.AuthSuccessCode;
import org.sopt.auth.cookie.RefreshTokenCookie;
import org.sopt.auth.dto.TokenPair;
import org.sopt.auth.exception.AuthErrorCode;
import org.sopt.auth.service.AuthService;
import org.sopt.common.exception.BusinessException;
import org.sopt.common.response.ApiResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * CLASS: OAuth2 로그인 결과의 JWT 응답과 임시 인증 상태 정리 담당
 */
@Component
@RequiredArgsConstructor
public class OAuth2LoginHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final AuthService authService; // 공통 JWT 발급과 Refresh Token 저장 담당
    private final RefreshTokenCookie refreshTokenCookie; // Refresh Token 쿠키 생성 담당
    private final JsonMapper jsonMapper; // 공통 응답 JSON 변환 담당

    /**
     * METHOD: 소셜 인증 성공 후 우리 서비스 JWT와 쿠키 응답
     */
    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
        try {
            OAuth2User user = (OAuth2User) authentication.getPrincipal();
            Long memberId = user.getAttribute("memberId");

            if (memberId == null) {
                writeFailure(response, AuthErrorCode.SOCIAL_LOGIN_FAILED);
                return;
            }

            TokenPair tokens = authService.loginSocial(memberId);
            prepareResponse(response);
            response.setStatus(HttpServletResponse.SC_OK);
            response.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken());
            response.addHeader(HttpHeaders.SET_COOKIE,
                    refreshTokenCookie.create(tokens.getRefreshToken()));
            response.getWriter().write(jsonMapper.writeValueAsString(
                    ApiResponse.<Void>success(AuthSuccessCode.LOGIN_SUCCESS, null)));
        } catch (BusinessException e) {
            writeFailure(response, AuthErrorCode.SOCIAL_LOGIN_FAILED);
        } finally {
            clearTemporaryAuthentication(request);
        }
    }

    /**
     * METHOD: 인증 거절과 계정 충돌을 공통 JSON 오류로 응답
     */
    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception
    ) throws IOException {
        try {
            AuthErrorCode error = AuthErrorCode.SOCIAL_LOGIN_FAILED;

            if (exception instanceof OAuth2AuthenticationException oauthException
                    && AuthErrorCode.SOCIAL_EMAIL_CONFLICT.getCode()
                    .equals(oauthException.getError().getErrorCode())) {
                error = AuthErrorCode.SOCIAL_EMAIL_CONFLICT;
            }

            writeFailure(response, error);
        } finally {
            clearTemporaryAuthentication(request);
        }
    }

    /**
     * METHOD: 인증 정보의 캐시와 외부 전송 방지용 응답 설정
     */
    private void prepareResponse(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setHeader("Referrer-Policy", "no-referrer");
    }

    /**
     * METHOD: 내부 예외 상세 대신 정해진 오류 코드와 메시지 응답
     */
    private void writeFailure(HttpServletResponse response, AuthErrorCode error) throws IOException {
        prepareResponse(response);
        response.setStatus(error.getStatus().value());
        response.getWriter().write(jsonMapper.writeValueAsString(ApiResponse.failure(error)));
    }

    /**
     * METHOD: OAuth state와 GitHub 토큰을 담은 임시 세션 및 인증 정보 정리
     */
    private void clearTemporaryAuthentication(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
    }
}
