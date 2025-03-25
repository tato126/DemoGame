package com.example.demo.rule;

import com.example.demo.domain.Position;
import com.example.demo.gameObject.Obstacle;

import java.util.List;

/**
 * 게임 플레이시 조건 판별 전용 클래스.
 *
 * @author chan
 */
public class GameRuleEvaluator {

    /**
     * 장애물과 충돌 유무 판단 메서드.
     *
     * @param playerNextPosition 플레이어의 다음 위치.
     * @param obstacles          장애물 리스트.
     * @param size               객체 크기.
     * @return 충돌 여부.
     */
    public boolean isColliding(Position playerNextPosition, List<Obstacle> obstacles, int size) {
        for (Obstacle obstacle : obstacles) {
            Position obstaclePosition = obstacle.getPosition();

            boolean overlap =
                    playerNextPosition.getX() < obstaclePosition.getX() + size &&
                            playerNextPosition.getX() + size > obstaclePosition.getX() &&
                            playerNextPosition.getY() < obstaclePosition.getY() + size &&
                            playerNextPosition.getY() + size > obstaclePosition.getY();

            if (overlap) {
                return true;
            }
        }

        return false;
    }

    /**
     * 플레이어의 목표점 도달 여부 판단 메서드.
     *
     * @param playerPosition 플레이어 위치.
     * @param dotPosition    목표 위치
     * @return 도달 여부.
     */
    public boolean isReachedGoal(Position playerPosition, Position dotPosition) {
        return playerPosition.equals(dotPosition);
    }
}
