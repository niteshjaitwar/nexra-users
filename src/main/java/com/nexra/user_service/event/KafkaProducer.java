package com.nexra.user_service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * Producer for publishing user-related events to Kafka.
 *
 * Use Cases:
 * - Decoupling email sending from validation logic
 * - Scalable event distribution
 *
 * @author niteshjaitwar
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private static final String TOPIC = "user-events";

    public void sendEvent(UserEvent event) {
        log.info("KafkaProducer -> sendEvent() Publishing event: type={}, email={}", event.getEventType(),
                event.getEmail());
        kafkaTemplate.send(TOPIC, event);
    }
}
