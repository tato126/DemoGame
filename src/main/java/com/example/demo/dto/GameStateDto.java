package com.example.demo.dto;


import java.util.List;

/**
 * 플레이어의 좌표, 목표점의 좌표 등의 각종 DTO 클래스.
 *
 * @author chan
 */
public class GameStateDto {

    private final int playerX;
    private final int playerY;
    private final int dotX;
    private final int dotY;
    private final int score;
    private final boolean reachedGoal;
    private final boolean collision;

    private final int hp;
    private final boolean gameOver;

    private final List<ObstacleDto> obstacles;


    public GameStateDto(int playerX, int playerY, int dotX, int dotY,
                        int score, boolean reachedGoal, boolean collision,
                        List<ObstacleDto> obstacles, int hp, boolean gameOver) {
        this.playerX = playerX;
        this.playerY = playerY;
        this.dotX = dotX;
        this.dotY = dotY;
        this.score = score;
        this.reachedGoal = reachedGoal;
        this.collision = collision;
        this.obstacles = obstacles;
        this.hp = hp;
        this.gameOver = gameOver;
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

    public int getHp() {
        return hp;
    }

    public boolean isGameOver() {
        return gameOver;
    }
}
