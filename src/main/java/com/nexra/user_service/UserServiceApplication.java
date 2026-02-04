package com.nexra.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main entry point for the User Service application.
 * Bootstraps the Spring Boot context and enables generic JPA auditing features.
 *
 * Use Cases:
 * - Starting the application server
 * - Initializing component scanning and auto-configuration
 *
 * @author niteshjaitwar
 */
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
