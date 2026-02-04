package com.nexra.user_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceApplicationTests {

	@org.springframework.boot.test.mock.mockito.MockBean
	private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;

	@org.springframework.boot.test.mock.mockito.MockBean
	private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

	@org.springframework.boot.test.mock.mockito.MockBean
	private org.springframework.mail.javamail.JavaMailSender javaMailSender;

	@Test
	void contextLoads() {
	}

}
