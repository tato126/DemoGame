package com.example.demo.dto;


import java.util.List;

/**
 * 플레이어의 좌표, 목표점의 좌표 등의 각종 DTO 클래스.
 *
 * @author chan
 */
public class GameStateDto {

    private int playerX;
    private int playerY;
    private int dotX;
    private int dotY;
    private int score;
    private boolean reachedGoal;
    private boolean collision;

    private List<ObstacleDto> obstacles;


    public GameStateDto(int playerX, int playerY, int dotX, int dotY,
                        int score, boolean reachedGoal, boolean collision,
                        List<ObstacleDto> obstacles) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.dotX = dotX;
        this.dotY = dotY;
        this.score = score;
        this.reachedGoal = reachedGoal;
        this.collision = collision;
        this.obstacles = obstacles;
    }

    public int getPlayerX() {
        return playerX;
    }

    public int getPlayerY() {
        return playerY;
    }

    public int getDotX() {
        return dotX;
    }

    public int getDotY() {
        return dotY;
    }

    public int getScore() {
        return score;
    }

    public boolean isReachedGoal() {
        return reachedGoal;
    }

    public boolean isCollision() {
        return collision;
    }

    public List<ObstacleDto> getObstacles() {
        return obstacles;
    }
}
