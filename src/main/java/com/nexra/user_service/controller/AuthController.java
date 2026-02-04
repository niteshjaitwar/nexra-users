package com.nexra.user_service.controller;

import com.nexra.user_service.model.AuthRequest;
import com.nexra.user_service.model.AuthResponse;
import com.nexra.user_service.model.ForgotPasswordRequest;
import com.nexra.user_service.model.RefreshTokenRequest;
import com.nexra.user_service.model.ResetPasswordRequest;
import com.nexra.user_service.model.UserDTO;
import com.nexra.user_service.model.VerifyEmailRequest;
import com.nexra.user_service.security.CustomUserDetailsService;
import com.nexra.user_service.security.JwtService;
import com.nexra.user_service.event.KafkaProducer;
import com.nexra.user_service.event.UserEvent;
import com.nexra.user_service.service.OtpService;
import com.nexra.user_service.service.TokenService;
import com.nexra.user_service.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related requests.
 * Provides endpoints for registration, login, verification, and password
 * management.
 *
 * Use Cases:
 * - Registering and verifying users via OTP
 * - Authenticating users and issuing JWT tokens
 * - Handling password resets and token refreshes
 * - User logout
 *
 * @author niteshjaitwar
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    // private final EmailService emailService; // Replaced by Kafka
    private final KafkaProducer kafkaProducer; // Injected
    private final OtpService otpService;
    private final TokenService tokenService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserDTO userDTO) {
        UserDTO registeredUser = userService.registerUser(userDTO);

        // Generate OTP
        String otp = otpService.generateOtp(registeredUser.getEmail());

        // Publish Event via Kafka
        kafkaProducer.sendEvent(UserEvent.builder()
                .eventType("REGISTRATION")
                .email(registeredUser.getEmail())
                .payload(otp)
                .build());

        return ResponseEntity.ok("User registered successfully. Please check your email for verification OTP.");
    }

    @PostMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestBody VerifyEmailRequest request) {
        if (otpService.validateOtp(request.getEmail(), request.getOtp())) {
            userService.enableUser(request.getEmail());
            return ResponseEntity.ok("Email verified successfully. You can now login.");
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // Ensure user is enabled (AuthenticationManager checks this if entity
        // implements isEnabled correctly)
        // Since User entity implements UserDetails.isEnabled() mapped to DB field,
        // standard auth provider checks it.

        String jwtToken = jwtService.generateToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        // Save refresh token
        tokenService.saveRefreshToken(userDetails.getUsername(), refreshToken, 604800000); // 7 days hardcoded for now,
                                                                                           // ideally from prop
                                                                                           // properties

        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .build());
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String username = tokenService.getUsernameFromRefreshToken(refreshToken);

        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtService.generateToken(userDetails);
            return ResponseEntity.ok(AuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // Rotate? For now keep same.
                    .build());
        }
        return ResponseEntity.badRequest().build(); // Invalid refresh token
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            // Blacklist token. Need expiration time.
            // Extract from token? Or just set max Access token TTL default (e.g. 1 day).
            // Better to extract from token if possible, else default.
            tokenService.blacklistToken(token, 86400000); // Default 1 day blacklist
            return ResponseEntity.ok("Logged out successfully");
        }
        return ResponseEntity.badRequest().body("Invalid token");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        // Generate OTP
        String otp = otpService.generateOtp(request.getEmail());
        // Publish Event via Kafka
        kafkaProducer.sendEvent(UserEvent.builder()
                .eventType("FORGOT_PASSWORD")
                .email(request.getEmail())
                .payload(otp)
                .build());
        return ResponseEntity.ok("Password reset OTP sent to email.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody ResetPasswordRequest request) {
        if (otpService.validateOtp(request.getEmail(), request.getOtp())) {
            userService.updatePassword(request.getEmail(), request.getNewPassword());
            return ResponseEntity.ok("Password reset successfully. Please login with new password.");
        }
        return ResponseEntity.badRequest().body("Invalid or expired OTP");
    }
}
