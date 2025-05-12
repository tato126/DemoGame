package com.example.demo.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.enemy")
public record EnemyProperties(int initialSize, int initialSpeed, int initialY, int patrolMinX, int patrolMaxX,
                              int moveStep) {
}
