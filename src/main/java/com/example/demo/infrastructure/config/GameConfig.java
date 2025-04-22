package com.example.demo.infrastructure.config;

import com.example.demo.infrastructure.config.properties.CanvasProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public Canvas canvas(CanvasProperties properties) {
        return new Canvas(properties.width(), properties.height());
    }
}
