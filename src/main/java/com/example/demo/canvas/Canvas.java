package com.example.demo.canvas;

import com.example.demo.domain.Position;

/**
 * 게임 화면 클래스.
 *
 * @author chan
 */
public class Canvas {

    int width = 300;
    int height = 300;

    public boolean isWithinBounds(Position position, int size) {
        int x = position.getX();
        int y = position.getY();
        return x >= 0 && y >= 0 && x + size <= width && y + size <= height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
