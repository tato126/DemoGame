package com.example.demo.domain.common;

public record Position(int x, int y) {

    public Position move(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

}
