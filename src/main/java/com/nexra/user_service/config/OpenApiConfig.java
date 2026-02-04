package com.nexra.user_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for OpenAPI (Swagger) documentation.
 * Defines the API metadata such as title, version, and description.
 *
 * Use Cases:
 * - Customizing the generated API documentation
 * - Providing clear API info for consumers via Swagger UI
 *
 * @author niteshjaitwar
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("User Service API")
                        .description("API for managing users in the Nexra application")
                        .version("1.0"));
    }
}
