package com.jangdu.community.auth.jwt;

import com.jangdu.community.user.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties();
        properties.setSecret("test-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm");
        properties.setAccessTokenExpiry(1800000);
        properties.setRefreshTokenExpiry(604800000);

        jwtProvider = new JwtProvider(properties);
        jwtProvider.init();
    }

    @Nested
    @DisplayName("토큰 생성")
    class CreateToken {

        @Test
        @DisplayName("Access Token을 생성한다")
        void createAccessToken() {
            String token = jwtProvider.createAccessToken(1L, "test@test.com", Role.USER);

            assertThat(token).isNotNull();
            assertThat(jwtProvider.validateToken(token)).isTrue();
            assertThat(jwtProvider.getUserId(token)).isEqualTo(1L);
            assertThat(jwtProvider.getRole(token)).isEqualTo("USER");
        }

        @Test
        @DisplayName("Refresh Token을 생성한다")
        void createRefreshToken() {
            String token = jwtProvider.createRefreshToken(1L, "test@test.com", Role.USER);

            assertThat(token).isNotNull();
            assertThat(jwtProvider.validateToken(token)).isTrue();
        }
    }

    @Nested
    @DisplayName("토큰 검증")
    class ValidateToken {

        @Test
        @DisplayName("유효한 토큰이면 true를 반환한다")
        void validToken() {
            String token = jwtProvider.createAccessToken(1L, "test@test.com", Role.USER);

            assertThat(jwtProvider.validateToken(token)).isTrue();
        }

        @Test
        @DisplayName("잘못된 토큰이면 false를 반환한다")
        void invalidToken() {
            assertThat(jwtProvider.validateToken("invalid.token.here")).isFalse();
        }

        @Test
        @DisplayName("만료된 토큰이면 false를 반환한다")
        void expiredToken() {
            JwtProperties expiredProperties = new JwtProperties();
            expiredProperties.setSecret("test-secret-key-must-be-at-least-256-bits-long-for-hs256-algorithm");
            expiredProperties.setAccessTokenExpiry(0);
            expiredProperties.setRefreshTokenExpiry(0);

            JwtProvider expiredProvider = new JwtProvider(expiredProperties);
            expiredProvider.init();

            String token = expiredProvider.createAccessToken(1L, "test@test.com", Role.USER);

            assertThat(jwtProvider.validateToken(token)).isFalse();
        }
    }

    @Nested
    @DisplayName("토큰에서 정보 추출")
    class ExtractClaims {

        @Test
        @DisplayName("userId를 추출한다")
        void getUserId() {
            String token = jwtProvider.createAccessToken(42L, "test@test.com", Role.USER);

            assertThat(jwtProvider.getUserId(token)).isEqualTo(42L);
        }

        @Test
        @DisplayName("role을 추출한다")
        void getRole() {
            String token = jwtProvider.createAccessToken(1L, "test@test.com", Role.ADMIN);

            assertThat(jwtProvider.getRole(token)).isEqualTo("ADMIN");
        }
    }
}
