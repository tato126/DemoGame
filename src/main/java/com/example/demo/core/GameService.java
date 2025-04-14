package com.example.demo.core;

import com.example.demo.core.user.domain.Direction;
import com.example.demo.core.user.domain.Player;
import com.example.demo.core.user.domain.PlayerId;
import com.example.demo.core.user.domain.PlayerIdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GameState gameState;
    private final Canvas canvas;
    private final PlayerIdGenerator playerIdGenerator;

    public GameService(GameState gameState, Canvas canvas, PlayerIdGenerator playerIdGenerator) {
        this.gameState = gameState;
        this.canvas = canvas;
        this.playerIdGenerator = playerIdGenerator;
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

    public Player initializeOrGetPlayer() {
        if (gameState.getPlayer() == null) { // GameState에 플레이어가 없는 경우
            PlayerId newPlayerId = playerIdGenerator.generateId();
            log.debug("Generated new Player ID: {}", newPlayerId);
            Position initialPosition = new Position(50, 50); // 초기 위치
            int initialSize = 20;                                  // 초기 크기
            Player newPlayer = new Player(newPlayerId.toString(), initialPosition, initialSize);
            gameState.updatePlayer(newPlayer);
            return newPlayer;
        } else {
            // 이미 플레이어가 있으면 기존 플레이어 반환 (싱글 플레이어)
            return gameState.getPlayer();
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
