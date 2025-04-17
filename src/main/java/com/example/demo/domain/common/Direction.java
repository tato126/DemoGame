package com.example.demo.domain.common;

public enum Direction {
    UP, DOWN, LEFT, RIGHT;

    public Position move(Position position, int step) {
        return switch (this) {
            case UP -> position.move(0, -step);     // Y 감소
            case DOWN -> position.move(0, step);    // Y 증가
            case LEFT -> position.move(-step, 0);   // X 감소
            case RIGHT -> position.move(step, 0);   // X 증가
        };
    }
}
