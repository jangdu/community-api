package com.jangdu.community.auth.service;

import com.jangdu.community.auth.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtProperties jwtProperties;

    private static final String KEY_PREFIX = "refresh_token:";

    public void save(Long userId, String refreshToken) {
        String key = KEY_PREFIX + userId;
        redisTemplate.opsForValue().set(
                key,
                refreshToken,
                jwtProperties.getRefreshTokenExpiry(),
                TimeUnit.MILLISECONDS
        );
    }

    public String find(Long userId) {
        String key = KEY_PREFIX + userId;
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(Long userId) {
        String key = KEY_PREFIX + userId;
        redisTemplate.delete(key);
    }
}
