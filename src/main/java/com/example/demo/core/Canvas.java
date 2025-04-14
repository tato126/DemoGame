package com.example.demo.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Canvas {

    private final int width;
    private final int height;


    public Canvas(@Value("${game.canvas.width:400}") int width, @Value("${game.canvas.height:300}") int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    // 경계 체크 메서드
    public boolean isWithinBounds(Position position, int objectSize) {
        return position.x() >= 0 && position.x() + objectSize <= width &&
                position.y() >= 0 && position.y() + objectSize <= height;
    }
}
