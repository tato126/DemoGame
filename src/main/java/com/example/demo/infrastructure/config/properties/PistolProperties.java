package com.example.demo.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.weapon.pistol")
public record PistolProperties(int size, int speed) {
}
