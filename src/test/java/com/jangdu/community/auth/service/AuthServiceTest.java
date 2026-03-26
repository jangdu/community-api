package com.jangdu.community.auth.service;

import com.jangdu.community.auth.dto.LoginRequest;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.auth.dto.TokenResponse;
import com.jangdu.community.auth.jwt.JwtProvider;
import com.jangdu.community.fixture.UserFixture;
import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.user.entity.Role;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Nested
    @DisplayName("회원가입")
    class Signup {

        @Test
        @DisplayName("성공 시 토큰을 발급한다")
        void success() {
            // given
            SignupRequest request = UserFixture.createSignupRequest();

            given(userRepository.existsByEmail(anyString())).willReturn(false);
            given(passwordEncoder.encode(anyString())).willReturn(UserFixture.ENCODED_PASSWORD);
            given(userRepository.save(any(User.class))).willAnswer(invocation -> {
                User saved = invocation.getArgument(0);
                ReflectionTestUtils.setField(saved, "id", 1L);
                return saved;
            });
            given(jwtProvider.createAccessToken(any(), anyString(), any(Role.class))).willReturn("accessToken");
            given(jwtProvider.createRefreshToken(any(), anyString(), any(Role.class))).willReturn("refreshToken");

            // when
            TokenResponse response = authService.signup(request);

            // then
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
            verify(userRepository).save(any(User.class));
            verify(refreshTokenService).save(any(), anyString());
        }

        @Test
        @DisplayName("이미 존재하는 이메일이면 DUPLICATE_EMAIL 예외를 던진다")
        void failWhenDuplicateEmail() {
            // given
            SignupRequest request = UserFixture.createSignupRequest();
            given(userRepository.existsByEmail(anyString())).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    @Nested
    @DisplayName("로그인")
    class Login {

        @Test
        @DisplayName("성공 시 토큰을 발급한다")
        void success() {
            // given
            LoginRequest request = UserFixture.createLoginRequest();
            User user = UserFixture.createUser();

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
            given(jwtProvider.createAccessToken(any(), anyString(), any(Role.class))).willReturn("accessToken");
            given(jwtProvider.createRefreshToken(any(), anyString(), any(Role.class))).willReturn("refreshToken");

            // when
            TokenResponse response = authService.login(request);

            // then
            assertThat(response.getAccessToken()).isEqualTo("accessToken");
            verify(refreshTokenService).save(any(), anyString());
        }

        @Test
        @DisplayName("존재하지 않는 이메일이면 USER_NOT_FOUND 예외를 던진다")
        void failWhenUserNotFound() {
            // given
            LoginRequest request = UserFixture.createLoginRequest();
            given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }

        @Test
        @DisplayName("비밀번호가 틀리면 INVALID_PASSWORD 예외를 던진다")
        void failWhenInvalidPassword() {
            // given
            LoginRequest request = UserFixture.createLoginRequest();
            User user = UserFixture.createUser();

            given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_PASSWORD);
        }
    }

    @Nested
    @DisplayName("토큰 재발급")
    class Refresh {

        @Test
        @DisplayName("성공 시 새로운 토큰 쌍을 발급한다")
        void success() {
            // given
            String refreshToken = "validRefreshToken";
            User user = UserFixture.createUser();

            given(jwtProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtProvider.getUserId(refreshToken)).willReturn(1L);
            given(refreshTokenService.find(1L)).willReturn(refreshToken);
            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(jwtProvider.createAccessToken(any(), anyString(), any(Role.class))).willReturn("newAccessToken");
            given(jwtProvider.createRefreshToken(any(), anyString(), any(Role.class))).willReturn("newRefreshToken");

            // when
            TokenResponse response = authService.refresh(refreshToken);

            // then
            assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
            verify(refreshTokenService).save(any(), anyString());
        }

        @Test
        @DisplayName("유효하지 않은 토큰이면 INVALID_REFRESH_TOKEN 예외를 던진다")
        void failWhenInvalidToken() {
            // given
            given(jwtProvider.validateToken(anyString())).willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.refresh("invalidToken"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        @Test
        @DisplayName("Redis에 저장된 토큰과 다르면 INVALID_REFRESH_TOKEN 예외를 던진다")
        void failWhenTokenMismatch() {
            // given
            given(jwtProvider.validateToken(anyString())).willReturn(true);
            given(jwtProvider.getUserId(anyString())).willReturn(1L);
            given(refreshTokenService.find(1L)).willReturn("differentToken");

            // when & then
            assertThatThrownBy(() -> authService.refresh("mismatchToken"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    @Nested
    @DisplayName("로그아웃")
    class Logout {

        @Test
        @DisplayName("성공 시 Redis에서 토큰을 삭제한다")
        void success() {
            // when
            authService.logout(1L);

            // then
            verify(refreshTokenService).delete(1L);
        }
    }
}
