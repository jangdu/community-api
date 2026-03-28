package com.jangdu.community.auth.service;

import com.jangdu.community.auth.dto.LoginRequest;
import com.jangdu.community.auth.dto.SignupRequest;
import com.jangdu.community.auth.dto.TokenResponse;
import com.jangdu.community.auth.jwt.JwtProvider;
import com.jangdu.community.global.exception.BusinessException;
import com.jangdu.community.global.exception.ErrorCode;
import com.jangdu.community.user.entity.User;
import com.jangdu.community.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RefreshTokenService refreshTokenService;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        validateDuplicateEmail(request.getEmail());

        User user = User.create(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname()
        );

        userRepository.save(user);
        log.info("User signed up: email={}", request.getEmail());

        return issueTokens(user);
    }

    @Transactional
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!user.isActive()) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        validatePassword(request.getPassword(), user.getPassword());
        user.updateLastLoginAt();
        log.info("User logged in: userId={}", user.getId());

        return issueTokens(user);
    }

    public TokenResponse refresh(String refreshToken) {
        validateRefreshToken(refreshToken);

        Long userId = jwtProvider.getUserId(refreshToken);
        validateStoredRefreshToken(userId, refreshToken);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return issueTokens(user);
    }

    public void logout(Long userId) {
        refreshTokenService.delete(userId);
        log.info("User logged out: userId={}", userId);
    }

    private void validateDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void validatePassword(String rawPassword, String encodedPassword) {
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private void validateStoredRefreshToken(Long userId, String refreshToken) {
        String savedToken = refreshTokenService.find(userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }
    }

    private TokenResponse issueTokens(User user) {
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getEmail(), user.getRole());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getEmail(), user.getRole());

        refreshTokenService.save(user.getId(), refreshToken);

        return TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
