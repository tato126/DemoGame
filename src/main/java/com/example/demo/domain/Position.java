package com.example.demo.domain;

public class Position {

    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Position move(int oldX, int oldY) {
        return new Position(x + oldX, y + oldY);
    }

    public boolean isWithinBounds(int width, int height, int size) {
        return x >= 0 && y >= 0 &&
                x + size <= width &&
                y + size <= height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Position position)) {
            return false;
        }

        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return 31 * x + y;
    }

    @Override
    public String toString() {
        return "Position [Position x = %s, Position y = %s]".formatted(x, y);
    }
}
