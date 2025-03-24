package com.example.demo.service;

import com.example.demo.domain.GameState;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameState gameState;

    public GameService(GameState gameState) {
        this.gameState = gameState;
    }

    public GameState move(String direction) {
        gameState.move(direction);
        return gameState;
    }

    public GameState getGameState() {
        return gameState;
    }
}
