package com.nexra.user_service.event;

import com.nexra.user_service.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Consumer for processing user events.
 *
 * Use Cases:
 * - Sending emails asynchronously upon receiving Kafka events
 *
 * @author niteshjaitwar
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class UserEventListener {

    private final EmailService emailService;

    @KafkaListener(topics = "user-events", groupId = "user-service-group")
    public void consume(UserEvent event) {
        log.info("UserEventListener -> consume() Received event: {}", event);

        if ("REGISTRATION".equals(event.getEventType())) {
            emailService.sendEmail(
                    event.getEmail(),
                    "Welcome & Verify Email",
                    event.getPayload()); // Payload is OTP
        } else if ("FORGOT_PASSWORD".equals(event.getEventType())) {
            emailService.sendEmail(
                    event.getEmail(),
                    "Reset Password",
                    event.getPayload()); // Payload is OTP
        }
    }
}
