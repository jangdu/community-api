package com.jangdu.community.auth.controller;

import com.jangdu.community.auth.dto.LoginRequest;
import com.jangdu.community.auth.dto.RefreshRequest;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.auth.dto.TokenResponse;
import com.jangdu.community.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<TokenResponse> signup(@Valid @RequestBody SignupRequest request) {
        TokenResponse token = authService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        TokenResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }
}
