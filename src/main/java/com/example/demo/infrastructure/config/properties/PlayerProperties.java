package com.example.demo.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.player")
public record PlayerProperties(int initialSize, int initialSpeed) {
}
