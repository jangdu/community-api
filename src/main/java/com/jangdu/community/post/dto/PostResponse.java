package com.jangdu.community.post.dto;

import com.jangdu.community.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponse {

    private final Long id;
    private final String categoryName;
    private final String categorySlug;
    private final AuthorInfo author;
    private final String title;
    private final String content;
    private final int viewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .categoryName(post.getCategory().getName())
                .categorySlug(post.getCategory().getSlug())
                .author(AuthorInfo.builder()
                        .id(post.getAuthor().getId())
                        .nickname(post.getAuthor().getNickname())
                        .avatarUrl(post.getAuthor().getAvatarUrl())
                        .build())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    @Getter
    @Builder
    public static class AuthorInfo {
        private final Long id;
        private final String nickname;
        private final String avatarUrl;
    }
}
