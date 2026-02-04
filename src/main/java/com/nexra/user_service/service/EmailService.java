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
    public void sendEmail(String to, String subject, String otp) {
        log.info("EmailService -> sendEmail() Sending email to: {}", to);
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(generateHtmlTemplate(otp), true); // true = HTML

            javaMailSender.send(message);
            log.info("EmailService -> sendEmail() Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("EmailService -> sendEmail() Failed to send email", e);
        }
    }

    private String generateHtmlTemplate(String otp) {
        return "<html>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px;'>" +
                "  <div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; padding: 20px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1);'>"
                +
                "    <h2 style='color: #333333; text-align: center;'>Nexra - Secure Verification</h2>" +
                "    <p style='color: #555555; font-size: 16px;'>Hello,</p>" +
                "    <p style='color: #555555; font-size: 16px;'>Your One-Time Password (OTP) for verification is:</p>"
                +
                "    <div style='text-align: center; margin: 20px 0;'>" +
                "      <span style='font-size: 24px; font-weight: bold; color: #007bff; letter-spacing: 5px; background-color: #e6f7ff; padding: 10px 20px; border-radius: 5px;'>"
                + otp + "</span>" +
                "    </div>" +
                "    <p style='color: #555555; font-size: 16px;'>This OTP is valid for 5 minutes. Please do not share this code with anyone.</p>"
                +
                "    <p style='color: #555555; font-size: 14px; margin-top: 20px;'>If you did not request this, please ignore this email.</p>"
                +
                "    <div style='text-align: center; margin-top: 30px; font-size: 12px; color: #999999;'>" +
                "      &copy; 2026 Nexra Inc. All rights reserved." +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";
    }
}
