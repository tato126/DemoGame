package com.example.demo.dto;

/**
 * 장애물 DTO 클래스.
 *
 * @author chan
 */
public class ObstacleDto {

    private int x;
    private int y;

    public ObstacleDto(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
