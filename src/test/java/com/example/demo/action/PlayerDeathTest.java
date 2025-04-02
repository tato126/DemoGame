package com.example.demo.action;

import com.example.demo.domain.GameState;
import com.example.demo.domain.Position;
import com.example.demo.gameObject.Player;
import com.example.demo.gameObject.obstacle.Obstacle;
import com.example.demo.gameObject.obstacle.ObstacleFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerDeathTest {

    private GameActionHandler handler;
    private GameState gameState;

    @BeforeEach
    void setUp() {
        handler = new GameActionHandler();
        gameState = new GameState(new ObstacleFactory());
    }

    @Test
    void collision_playerHpReduce() {

        Player weakPlayer = new Player(new Position(100, 100), 1);
        gameState.applyPlayerMovement(weakPlayer);

        gameState.getObstacles().add(new Obstacle(new Position(110, 100)));

        handler.move(gameState, "right");

        assertTrue(gameState.isGameOver(), "플레이어가 죽으면 gameOver가 true여야 해요");
        assertEquals(0, gameState.getPlayer().getHp());
    }

    @Test
    void collision_playerNotDie() {

        Player player = new Player(new Position(100, 100), 3);
        gameState.applyPlayerMovement(player);

        gameState.getObstacles().add(new Obstacle(new Position(110, 100)));

        handler.move(gameState, "right");

        assertFalse(gameState.isGameOver(), "HP가 0이 아니면 gameOver는 false여야 해요.");
        assertEquals(2, gameState.getPlayer().getHp(), "HP가 2여야 해요.");

    }
}
