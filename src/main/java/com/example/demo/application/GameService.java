package com.example.demo.application;

import com.example.demo.domain.support.IdGenerator;
import com.example.demo.infrastructure.config.Canvas;
import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.EnemyId;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.player.PlayerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final GameState gameState;
    private final Canvas canvas;
    private final IdGenerator idGenerator;

    public GameService(GameState gameState, Canvas canvas, IdGenerator idGenerator) {
        this.gameState = gameState;
        this.canvas = canvas;
        this.idGenerator = idGenerator;
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

    // 현재는 Enemy 객체가 화면 생성이 되는지만 확인
    public void processEnemyMove(String enemyId) {
//        Enemy currentEnemy = gameState.getEnemy();

    }

    public Player initializeOrGetPlayer() {
        if (gameState.getPlayer() == null) { // GameState에 플레이어가 없는 경우
            PlayerId newPlayerId = idGenerator.generatePlayerId();
            log.debug("[Generated] New Player ID: {}", newPlayerId);
            int initialSize = 20;

            int startX = (canvas.getWidth() / 2) - (initialSize / 2);
            int startY = (canvas.getHeight() / 2) - (initialSize / 2);

            Position initialPosition = new Position(startX, startY); // 임시 초기 위치
            Player newPlayer = new Player(newPlayerId.toString(), initialPosition, initialSize);
            gameState.updatePlayer(newPlayer);
            return newPlayer;
        } else {
            // 이미 플레이어가 있으면 기존 플레이어 반환 (싱글 플레이어)
            return gameState.getPlayer();
        }
    }

    public Enemy spawnInitialEnemy() {
        if (gameState.getEnemy() == null) { // GameState에 Enemy가 없는 경우
            EnemyId newEnemyId = idGenerator.generateEnemyId();
            log.debug("[Generated] New Enemy ID: {}", newEnemyId);

            int initialSize = 20;

            int startX = (canvas.getWidth() / 2) - (initialSize / 2);
            int startY = 50;

            Position initialPosition = new Position(startX, startY); // 임시 초기 위치

            Enemy newEnemy = new Enemy(newEnemyId.toString(), initialPosition, initialSize);
            gameState.updateEnemy(newEnemy);
            return newEnemy;
        } else {
            // 이미 Enemy 객체가 있으면 기존 Enemy 반환
            // 차후 플레이 양상에 따라서 변환해야할 수도 있음
            return gameState.getEnemy();
        }
    }

    // -- 게임 상태 조회 및 리셋 메서드 --
    public Optional<GameState> getGameState() {
        return Optional.of(gameState);
    }

    // Enemy 또한 Player 와 함께 동시 초기화.
    public void resetGame(String playerId, String enemyId) {
        Player initialPlayer = new Player(playerId, new Position(50, 50), 20); // 임시
        Enemy initialEnemy = new Enemy(enemyId, new Position(100, 100), 20); // 임시
        gameState.reset(initialPlayer, initialEnemy);
        log.debug("[Reset] Game requested for player {}", playerId);
    }
}
