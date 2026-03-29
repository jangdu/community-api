package com.jangdu.community.post.controller;

import com.jangdu.community.global.common.ApiResponse;
import com.jangdu.community.post.dto.*;
import com.jangdu.community.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Post", description = "게시글 API")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @Operation(summary = "게시글 작성")
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> create(
            Authentication authentication,
            @Valid @RequestBody CreatePostRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        PostResponse response = postService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }

    @Operation(summary = "게시글 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<PostListResponse>>> getList(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<PostListResponse> response = postService.getList(categoryId, keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> getDetail(@PathVariable Long id) {
        PostResponse response = postService.getDetail(id);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 수정")
    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<PostResponse>> update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody UpdatePostRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        PostResponse response = postService.update(userId, id, request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "게시글 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            Authentication authentication,
            @PathVariable Long id) {
        Long userId = (Long) authentication.getPrincipal();
        postService.delete(userId, id);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
