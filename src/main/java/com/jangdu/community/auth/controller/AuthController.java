package com.jangdu.community.auth.controller;

import com.jangdu.community.auth.dto.LoginRequest;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.auth.dto.TokenResponse;
import com.jangdu.community.auth.jwt.CookieProvider;
import com.jangdu.community.auth.service.AuthService;
import com.jangdu.community.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieProvider cookieProvider;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<TokenResponse>> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse token = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, cookieProvider.createRefreshTokenCookie(token.getRefreshToken()).toString())
                .body(ApiResponse.created(token));
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieProvider.createRefreshTokenCookie(token.getRefreshToken()).toString())
                .body(ApiResponse.success(token));
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(@CookieValue(name = "refresh_token") String refreshToken) {
        TokenResponse token = authService.refresh(refreshToken);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieProvider.createRefreshTokenCookie(token.getRefreshToken()).toString())
                .body(ApiResponse.success(token));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieProvider.deleteRefreshTokenCookie().toString())
                .body(ApiResponse.success());
    }
}
