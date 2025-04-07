package com.example.demo.service;

import com.example.demo.action.GameActionHandler;
import com.example.demo.domain.GameState;
import com.example.demo.exit.DefaultExitHandler;
import com.example.demo.gameObject.obstacle.ObstacleFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GameServiceTest {

    @DisplayName("시스템이 종료되어야 한다.")
    @Test
    void exitGame_shouldCallExitHandler() {
        // given
        GameState state = new GameState(new ObstacleFactory());
        GameActionHandler actionHandler = mock(GameActionHandler.class);
        DefaultExitHandler mockHandler = mock(DefaultExitHandler.class);
        GameService service = new GameService(state, actionHandler, mockHandler);

        // when
        service.exitGame();

        // then
        verify(mockHandler).exit(0);
    }

}