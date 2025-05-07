package com.example.demo.domain.common;

import java.util.Objects;

public record Position(int x, int y) {

    public Position move(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    public Position moveIndirection(Direction direction, int step) {
        Objects.requireNonNull(direction, "Direction cannot be null");

        if (step <= 0) {
            throw new IllegalStateException("Step must be a positive(step <= 0) integer, but was " + step);
        }

        return switch (direction) {
            case UP -> this.move(0, -step);
            case DOWN -> this.move(0, step);
            case LEFT -> this.move(-step, 0);
            case RIGHT -> this.move(step, 0);
        };
    }
}
