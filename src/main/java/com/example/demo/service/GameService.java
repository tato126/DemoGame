package com.example.demo.service;

import com.example.demo.action.GameActionHandler;
import com.example.demo.domain.GameState;
import com.example.demo.dto.GameStateDto;
import com.example.demo.dto.ObstacleDto;
import com.example.demo.exit.DefaultExitHandler;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GameService {

    private final GameState gameState;
    private final GameActionHandler actionHandler;
    private final DefaultExitHandler exitHandler;

    public GameService(GameState gameState, GameActionHandler actionHandler, DefaultExitHandler exitHandler) {
        this.gameState = gameState;
        this.actionHandler = actionHandler;
        this.exitHandler = exitHandler;
    }

    public GameStateDto move(String direction) {
        actionHandler.move(gameState, direction);
        return convertToDto(gameState);
    }

    public GameStateDto getGameState() {
        if (gameState.isGameOver()) {
            gameState.reset();
        }

        return convertToDto(gameState);
    }

    public GameStateDto convertToDto(GameState state) {
        List<ObstacleDto> obstacleDtos = state.getObstacles().stream()
                .map(ob -> new ObstacleDto(ob.getX(), ob.getY()))
                .toList();

        return new GameStateDto(
                state.getPlayerX(),
                state.getPlayerY(),
                state.getDotX(),
                state.getDotY(),
                state.getScore(),
                state.isReachedGoal(),
                state.getCollision(),
                obstacleDtos,
                state.getPlayer().getHp(),
                state.isGameOver()
        );
    }

    public void resetGame() {
        gameState.reset();
    }

    public void exitGame() {
        exitHandler.exit(0);
    }
}
