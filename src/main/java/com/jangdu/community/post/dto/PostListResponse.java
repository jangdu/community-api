package com.jangdu.community.post.dto;

import com.jangdu.community.post.entity.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListResponse {

    private final Long id;
    private final String categoryName;
    private final String categorySlug;
    private final String authorNickname;
    private final String title;
    private final int viewCount;
    private final LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .id(post.getId())
                .categoryName(post.getCategory().getName())
                .categorySlug(post.getCategory().getSlug())
                .authorNickname(post.getAuthor().getNickname())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .createdAt(post.getCreatedAt())
                .build();
    }
}
