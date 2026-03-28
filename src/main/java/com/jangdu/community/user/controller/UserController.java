package com.jangdu.community.user.controller;

import com.jangdu.community.global.common.ApiResponse;
import com.jangdu.community.user.dto.UpdateProfileRequest;
import com.jangdu.community.user.dto.UserResponse;
import com.jangdu.community.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "User", description = "유저 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "내 정보 조회")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getMe(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.getMyInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "프로필 수정")
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfile(
            Authentication authentication,
            @Valid @RequestBody UpdateProfileRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "아바타 업로드")
    @PatchMapping(value = "/me/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserResponse>> uploadAvatar(
            Authentication authentication,
            @RequestParam("file") MultipartFile file) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.updateAvatar(userId, file);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "아바타 삭제")
    @DeleteMapping("/me/avatar")
    public ResponseEntity<ApiResponse<UserResponse>> deleteAvatar(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        UserResponse response = userService.deleteAvatar(userId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
