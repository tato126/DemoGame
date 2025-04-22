package com.example.demo.infrastructure.config;

import com.example.demo.domain.common.Position;
import org.springframework.stereotype.Component;

@Component
public class Canvas {

    private final int width;
    private final int height;


    public Canvas(int width, int height) {
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
