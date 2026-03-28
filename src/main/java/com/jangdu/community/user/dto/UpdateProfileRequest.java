package com.jangdu.community.user.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @Size(min = 2, max = 30, message = "닉네임은 2~30자여야 합니다")
    private String nickname;

    @Size(max = 200, message = "자기소개는 200자 이내여야 합니다")
    private String bio;
}
