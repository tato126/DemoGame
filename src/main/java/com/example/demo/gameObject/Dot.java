package com.example.demo.gameObject;

import com.example.demo.domain.Position;

/**
 * 목적지(Target) 점 클래스
 *
 * @author chan
 */
public class Dot {

    private final Position position;

    public Dot(Position position) {
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }
}
