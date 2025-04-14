package com.example.demo.core;

import com.example.demo.core.user.domain.Direction;
import com.example.demo.core.user.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GameState gameState;
    private final Canvas canvas;

    public GameService(GameState gameState, Canvas canvas) {
        this.gameState = gameState;
        this.canvas = canvas;
    }

    public void processPlayerMove(String playerId, Direction direction) {
        Player currentPlayer = gameState.getPlayer();
        if (currentPlayer == null) {
            log.debug("[Service] No player found int GameState.");
            return;
        }

        // TODO: playerId 검증 로직 추가 (현재는 단일 플레이어라 생략)

        // 다음 위치 계산
        Position currentPosition = currentPlayer.getPosition();
        int step = currentPlayer.getSize();
        Position nextPosition = direction.move(currentPosition, step);

        // 경계 검사
        if (canvas.isWithinBounds(nextPosition, currentPlayer.getSize())) {
            Player movedPlayer = currentPlayer.moveTo(nextPosition);
            gameState.updatePlayer(movedPlayer);
            log.debug("[Service] Player {} processed move to {}", movedPlayer.getId(), nextPosition);
        } else {
            log.debug("[Service] Move blocked by boundary for player {}: {} to {}", playerId, direction, nextPosition);
        }
    }

    // -- 게임 상태 조회 및 리셋 메서드 --
    public Optional<GameState> getGameState() {
        return Optional.of(gameState);
    }

    public void resetGame(String playerId) {
        Player initialPlayer = new Player(playerId, new Position(50, 50), 20);
        gameState.reset(initialPlayer);
        log.debug("Reset Game requested for player {}", playerId);
    }
}
