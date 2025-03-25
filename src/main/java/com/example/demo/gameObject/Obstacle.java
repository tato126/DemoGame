package com.example.demo.gameObject;

import com.example.demo.domain.Position;

/**
 * 장애물(Obstacle) 클래스.
 *
 * @author chan
 */
public class Obstacle {

    private final Position position;
    private final int size = 10;

    public Obstacle(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public int getX() {
        return position.getX();
    }

    public int getY() {
        return position.getY();
    }

    @Override
    public String toString() {
        return "Obstacle{" +
                "position=" + position +
                ", size=" + size +
                '}';
    }
}
