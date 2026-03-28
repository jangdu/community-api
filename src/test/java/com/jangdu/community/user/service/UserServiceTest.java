package com.jangdu.community.user.service;

import com.jangdu.community.fixture.UserFixture;
import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.global.storage.StorageService;
import com.jangdu.community.user.dto.UserResponse;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StorageService storageService;

    @Nested
    @DisplayName("ID로 조회")
    class FindById {

        @Test
        @DisplayName("존재하는 유저를 반환한다")
        void success() {
            User user = UserFixture.createUser();
            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            User found = userService.findById(1L);

            assertThat(found.getEmail()).isEqualTo(UserFixture.EMAIL);
        }

        @Test
        @DisplayName("존재하지 않으면 USER_NOT_FOUND 예외를 던진다")
        void failWhenNotFound() {
            given(userRepository.findById(1L)).willReturn(Optional.empty());

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
            User user = UserFixture.createUser();
            given(userRepository.findByEmail(UserFixture.EMAIL)).willReturn(Optional.of(user));

            User found = userService.findByEmail(UserFixture.EMAIL);

            assertThat(found.getId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않으면 USER_NOT_FOUND 예외를 던진다")
        void failWhenNotFound() {
            given(userRepository.findByEmail("unknown@test.com")).willReturn(Optional.empty());

            assertThatThrownBy(() -> userService.findByEmail("unknown@test.com"))
                    .isInstanceOf(BusinessException.class)
                    .extracting(e -> ((BusinessException) e).getErrorCode())
                    .isEqualTo(ErrorCode.USER_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("아바타 업로드")
    class UpdateAvatar {

        @Test
        @DisplayName("새 아바타를 업로드한다")
        void success() {
            User user = UserFixture.createUser();
            MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "image".getBytes());

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(storageService.upload(any(), anyString())).willReturn("https://s3.com/avatars/new.png");

            UserResponse response = userService.updateAvatar(1L, file);

            assertThat(response.getAvatarUrl()).isEqualTo("https://s3.com/avatars/new.png");
            verify(storageService).upload(any(), anyString());
        }

        @Test
        @DisplayName("기존 아바타가 있으면 삭제 후 새로 업로드한다")
        void deleteOldAvatar() {
            User user = UserFixture.createUser();
            ReflectionTestUtils.setField(user, "avatarUrl", "https://s3.com/avatars/old.png");
            MockMultipartFile file = new MockMultipartFile("file", "avatar.png", "image/png", "image".getBytes());

            given(userRepository.findById(1L)).willReturn(Optional.of(user));
            given(storageService.upload(any(), anyString())).willReturn("https://s3.com/avatars/new.png");

            userService.updateAvatar(1L, file);

            verify(storageService).delete("https://s3.com/avatars/old.png");
        }
    }

    @Nested
    @DisplayName("아바타 삭제")
    class DeleteAvatar {

        @Test
        @DisplayName("아바타를 삭제한다")
        void success() {
            User user = UserFixture.createUser();
            ReflectionTestUtils.setField(user, "avatarUrl", "https://s3.com/avatars/old.png");

            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            UserResponse response = userService.deleteAvatar(1L);

            assertThat(response.getAvatarUrl()).isNull();
            verify(storageService).delete("https://s3.com/avatars/old.png");
        }

        @Test
        @DisplayName("아바타가 없으면 삭제를 시도하지 않는다")
        void skipWhenNoAvatar() {
            User user = UserFixture.createUser();

            given(userRepository.findById(1L)).willReturn(Optional.of(user));

            userService.deleteAvatar(1L);

            verify(storageService, never()).delete(anyString());
        }
    }
}
