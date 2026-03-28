package com.jangdu.community.user.service;

import com.jangdu.community.fixture.UserFixture;
import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("ID로 조회")
    class FindById {

        @Test
        @DisplayName("존재하는 유저를 반환한다")
        void success() {
            // given
            User user = UserFixture.createUser();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            // when
            User found = userService.findById(1L);

            // then
            assertThat(found.getEmail()).isEqualTo(UserFixture.EMAIL);
        }

        @Test
        @DisplayName("존재하지 않으면 USER_NOT_FOUND 예외를 던진다")
        void failWhenNotFound() {
            // given
            given(userRepository.findById(1L)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.findById(1L))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("이메일로 조회")
    class FindByEmail {

        @Test
        @DisplayName("존재하는 유저를 반환한다")
        void success() {
            // given
            User user = UserFixture.createUser();
            given(userRepository.findByEmail(UserFixture.EMAIL)).willReturn(Optional.of(user));

            // when
            User found = userService.findByEmail(UserFixture.EMAIL);

            // then
            assertThat(found.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않으면 USER_NOT_FOUND 예외를 던진다")
        void failWhenNotFound() {
            // given
            given(userRepository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> userService.findByEmail("unknown@test.com"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }
}
