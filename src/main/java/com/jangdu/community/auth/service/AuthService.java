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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getNickname())
                .build();

        userRepository.save(user);

        return createTokenResponse(user);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        return createTokenResponse(user);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userId = jwtProvider.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return createTokenResponse(user);
    }

    private TokenResponse createTokenResponse(User user) {
        return TokenResponse.builder()
                .accessToken(jwtProvider.createAccessToken(user.getId(), user.getEmail()))
                .refreshToken(jwtProvider.createRefreshToken(user.getId(), user.getEmail()))
                .build();
    }
}
