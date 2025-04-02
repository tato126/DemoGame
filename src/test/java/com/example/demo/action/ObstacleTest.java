package com.example.demo.action;

import com.example.demo.domain.GameState;
import com.example.demo.domain.Position;
import com.example.demo.gameObject.Player;
import com.example.demo.gameObject.obstacle.Obstacle;
import com.example.demo.gameObject.obstacle.ObstacleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ObstacleTest {

    private GameActionHandler actionHandler;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        actionHandler = new GameActionHandler();
        gameState = new GameState(new ObstacleFactory());

        // 플레이어 위치
        Player player = new Player(new Position(100, 100));
        gameState.applyPlayerMovement(player);

        // 장애물 추가 (오른쪽 이동 시 충돌)
        Obstacle obstacle = new Obstacle(new Position(110, 100));
        gameState.getObstacles().add(obstacle);
    }

    @Test
    void collision_shouldPreventMovement() {
        Position initialPosition = gameState.getPlayer().getPosition();

        actionHandler.move(gameState, "right");

        assertEquals(initialPosition.getX(), gameState.getPlayerX());
        assertEquals(initialPosition.getY(), gameState.getPlayerY());
    }

    @Test
    void collision_shouldReducePlayerHp() {
        int initialHp = gameState.getPlayer().getHp();

        actionHandler.move(gameState, "right");

        assertEquals(initialHp - 1, gameState.getPlayer().getHp());
    }

    @Test
    void collision_shouldSetCollisionFlagTrue() {
        actionHandler.move(gameState, "right");

        assertTrue(gameState.getCollision());
    }
}
