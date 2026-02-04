package com.nexra.user_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * Service for managing OTPs (One Time Passwords).
 * Stores OTPs in Redis with expiration.
 *
 * Use Cases:
 * - Generating and validating registration/verification OTPs
 *
 * @author niteshjaitwar
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OtpService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String OTP_PREFIX = "otp:";
    private static final long OTP_EXPIRATION_MINUTES = 5;

    public String generateOtp(String key) {
        log.info("OtpService -> generateOtp() Generating OTP for key: {}", key);
        SecureRandom random = new SecureRandom();
        int otp = 100000 + random.nextInt(900000); // 6 digit OTP
        String otpString = String.valueOf(otp);

        redisTemplate.opsForValue().set(
                OTP_PREFIX + key,
                otpString,
                OTP_EXPIRATION_MINUTES,
                TimeUnit.MINUTES);
        log.info("OtpService -> generateOtp() Generated OTP for key {}: {}", key, otpString);
        return otpString;
    }

    public boolean validateOtp(String key, String otp) {
        log.info("OtpService -> validateOtp() Validating OTP for key: {}", key);
        Object storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + key);

        if (storedOtp != null && storedOtp.toString().equals(otp)) {
            redisTemplate.delete(OTP_PREFIX + key); // Invalidate after use
            return true;
        }
        return false;
    }
}
