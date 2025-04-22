package com.example.demo.infrastructure.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "game.canvas")
public record CanvasProperties(int width, int height) {


}
