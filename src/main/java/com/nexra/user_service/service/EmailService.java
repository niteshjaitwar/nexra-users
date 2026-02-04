package com.nexra.user_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Service for sending emails.
 * Handles sending OTPs, welcome emails, etc.
 *
 * Use Cases:
 * - Sending OTP for registration/verification
 * - Password reset links
 *
 * @author niteshjaitwar
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Async
    public void sendEmail(String to, String subject, String body) {
        log.info("EmailService -> sendEmail() Sending email to: {}", to);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML

            javaMailSender.send(message);
            log.info("EmailService -> sendEmail() Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("EmailService -> sendEmail() Failed to send email", e);
            // In production, might want to throw exception or handle retry
        }
    }
}
