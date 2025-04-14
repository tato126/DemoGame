package com.example.demo.web;

import com.example.demo.core.Canvas;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public Canvas canvas(@Value("${game.canvas.width:400}") int width,
                         @Value("${game.canvas.height:300}") int height) {
        return new Canvas(width, height);
    }
}
