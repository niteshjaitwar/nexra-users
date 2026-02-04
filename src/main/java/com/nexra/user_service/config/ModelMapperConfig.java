package com.nexra.user_service.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for ModelMapper.
 * Initializes the ModelMapper bean with a LOOSE matching strategy to handle
 * flexible DTO-to-Entity mappings.
 *
 * Use Cases:
 * - Injecting ModelMapper into services
 * - Centralized configuration for object mapping behavior
 *
 * @author niteshjaitwar
 */
@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper;
    }
}
