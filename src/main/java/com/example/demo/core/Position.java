package com.example.demo.core;

public record Position(int x, int y) {

    public Position move(int dx, int dy) {
        return new Position(this.x + dx, this.y + dy);
    }

    // 필요시 음수 좌표 등을 막는 생성자 유효성 검자 추가
}
