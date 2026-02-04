package com.nexra.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Service for managing token lifecycle using Redis.
 * Handles token storage, blacklisting, and validation.
 *
 * Use Cases:
 * - Storing Refresh Tokens with TTL
 * - Blacklisting Access Tokens on Logout
 * - Verifying if a token is valid/blacklisted
 *
 * @author niteshjaitwar
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    private static final String BLACKLIST_PREFIX = "blacklist:";

    public void saveRefreshToken(String username, String token, long expirationMs) {
        log.info("TokenService -> saveRefreshToken() Saving refresh token for user: {}", username);
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + token,
                username,
                expirationMs,
                TimeUnit.MILLISECONDS);
    }

    public void blacklistToken(String token, long expirationMs) {
        log.info("TokenService -> blacklistToken() Blacklisting token");
        long ttl = Math.max(0, expirationMs); // Ensure non-negative
        redisTemplate.opsForValue().set(
                BLACKLIST_PREFIX + token,
                "blacklisted",
                ttl,
                TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + token));
    }

    public String getUsernameFromRefreshToken(String token) {
        Object username = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + token);
        return username != null ? username.toString() : null;
    }

    public void deleteRefreshToken(String token) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + token);
    }
}
