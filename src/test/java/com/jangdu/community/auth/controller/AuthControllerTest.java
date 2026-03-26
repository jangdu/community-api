package com.jangdu.community.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.auth.dto.TokenResponse;
import com.jangdu.community.auth.jwt.CookieProvider;
import com.jangdu.community.auth.jwt.JwtProperties;
import com.jangdu.community.auth.jwt.JwtProvider;
import com.jangdu.community.auth.service.AuthService;
import com.jangdu.community.config.TestSecurityConfig;
import com.jangdu.community.fixture.UserFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtProvider jwtProvider;

    @MockitoBean
    private CookieProvider cookieProvider;

    @MockitoBean
    private JwtProperties jwtProperties;

    @Nested
    @DisplayName("POST /api/auth/signup")
    class SignupApi {

        @Test
        @DisplayName("201 Created - 회원가입 성공 시 accessToken만 body에 내려준다")
        void success() throws Exception {
            // given
            SignupRequest request = UserFixture.createSignupRequest();
            TokenResponse tokenResponse = TokenResponse.builder()
                    .accessToken("accessToken")
                    .refreshToken("refreshToken")
                    .build();

            given(authService.signup(any())).willReturn(tokenResponse);
            given(cookieProvider.createRefreshTokenCookie(any()))
                    .willReturn(org.springframework.http.ResponseCookie.from("refresh_token", "refreshToken").build());

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.status").value(201))
                    .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
                    .andExpect(jsonPath("$.data.refreshToken").doesNotExist())
                    .andExpect(header().exists("Set-Cookie"));
        }

        @Test
        @DisplayName("400 Bad Request - 이메일 형식이 잘못되면 에러를 반환한다")
        void invalidEmail() throws Exception {
            // given
            SignupRequest request = new SignupRequest("invalid", "Test1234!", "테스트");

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"))
                    .andExpect(jsonPath("$.errors").isArray());
        }

        @Test
        @DisplayName("400 Bad Request - 비밀번호 강도가 부족하면 에러를 반환한다")
        void weakPassword() throws Exception {
            // given
            SignupRequest request = new SignupRequest("test@test.com", "weak", "테스트");

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"));
        }

        @Test
        @DisplayName("400 Bad Request - 닉네임이 빈 값이면 에러를 반환한다")
        void emptyNickname() throws Exception {
            // given
            SignupRequest request = new SignupRequest("test@test.com", "Test1234!", "");

            // when & then
            mockMvc.perform(post("/api/auth/signup")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value("C001"));
        }
    }

    @Nested
    @DisplayName("POST /api/auth/logout")
    class LogoutApi {

        @Test
        @DisplayName("401 Unauthorized - 인증 없이 로그아웃하면 거부한다")
        void unauthorized() throws Exception {
            mockMvc.perform(post("/api/auth/logout"))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value("C003"));
        }
    }
}
