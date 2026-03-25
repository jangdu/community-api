package com.jangdu.community.auth.controller;

import com.jangdu.community.auth.dto.LoginRequest;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.auth.service.AuthService;
import com.jangdu.community.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        User user = authService.signup(request);

        return ResponseEntity.ok(Map.of(
                "message", "회원가입 성공",
                "userId", user.getId()
        ));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        User user = authService.login(request);

        return ResponseEntity.ok(Map.of(
                "message", "로그인 성공",
                "userId", user.getId()
        ));
    }
}
