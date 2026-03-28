package com.jangdu.community.user.service;

import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.global.storage.StorageService;
import com.jangdu.community.user.dto.UpdateProfileRequest;
import com.jangdu.community.user.dto.UserResponse;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final StorageService storageService;

    private static final String AVATAR_DIRECTORY = "avatars";

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    public UserResponse getMyInfo(Long userId) {
        User user = findById(userId);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findById(userId);
        user.updateProfile(request.getNickname(), request.getBio());
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse updateAvatar(Long userId, MultipartFile file) {
        User user = findById(userId);
        String oldAvatarUrl = user.getAvatarUrl();

        String newAvatarUrl = storageService.upload(file, AVATAR_DIRECTORY);

        try {
            user.updateAvatarUrl(newAvatarUrl);
            userRepository.flush();
        } catch (Exception e) {
            storageService.delete(newAvatarUrl);
            throw e;
        }

        if (oldAvatarUrl != null) {
            try {
                storageService.delete(oldAvatarUrl);
            } catch (Exception e) {
                log.warn("Failed to delete old avatar: {}", oldAvatarUrl, e);
            }
        }

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse deleteAvatar(Long userId) {
        User user = findById(userId);

        if (user.getAvatarUrl() != null) {
            String avatarUrl = user.getAvatarUrl();
            user.updateAvatarUrl(null);

            try {
                storageService.delete(avatarUrl);
            } catch (Exception e) {
                log.warn("Failed to delete avatar from storage: {}", avatarUrl, e);
            }
        }

        return UserResponse.from(user);
    }
}
