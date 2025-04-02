package com.example.demo.action;

import com.example.demo.domain.GameState;
import com.example.demo.domain.Position;
import com.example.demo.gameObject.Dot;
import com.example.demo.gameObject.Player;
import com.example.demo.gameObject.obstacle.ObstacleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GoalReachTest {

    private GameActionHandler handler;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        handler = new GameActionHandler();
        gameState = new GameState(new ObstacleFactory());
    }

    @Test
    void reachingGoal_shouldSetReachedGoalFlag() {

        // dot 바로 왼쪽 위치에 플레이어 이동
        Dot dot = gameState.getDot();
        Position leftOfDot = new Position(gameState.getDotX() - 10, gameState.getDotY());
        gameState.applyPlayerMovement(new Player(leftOfDot));

        handler.move(gameState, "right");

        assertTrue(gameState.isReachedGoal(), "reachedGoal 플래그가 true여야 해요.");
    }

    @Test
    void reachingGoal_shouldIncreaseScore() {
        Dot dot = gameState.getDot();
        Position leftOfDot = new Position(gameState.getDotX() - 10, gameState.getDotY());
        gameState.applyPlayerMovement(new Player(leftOfDot));

        int beforeScore = gameState.getScore();
        handler.move(gameState, "right");

        assertTrue(gameState.getScore() > beforeScore, "점수가 증가해야 해요.");
    }

    @Test
    void reachingGoal_shouldGenerateNewDot() {
        Dot dot = gameState.getDot();
        Position leftOfDot = new Position(gameState.getDotX() - 10, gameState.getDotY());
        gameState.applyPlayerMovement(new Player(leftOfDot));

        Position beforeDotPosition = dot.getPosition();
        handler.move(gameState, "right");

        Position afterDosPosition = gameState.getDot().getPosition();
        assertNotEquals(beforeDotPosition, afterDosPosition, "dot 위치가 변경되어야 해요.");
    }
}
