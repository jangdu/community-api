package com.jangdu.community.post.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePostRequest {

    private Long categoryId;

    @Size(max = 100, message = "제목은 100자 이내여야 합니다")
    private String title;

    private String content;
}
