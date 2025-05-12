package com.example.demo.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.scheduling")
public record SchedulingProperties(long gameLoopUpdateRateMs, long projectileUpdateRateMs,
                                   long enemyFireIntervalTicks) {
}
