package com.example.demo.application;

import com.example.demo.application.physics.MoveValidationService;
import com.example.demo.domain.enemy.application.EnemyCleanUp;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.enemy.application.EnemyRegistry;
import com.example.demo.domain.player.application.PlayerCleanUp;
import com.example.demo.domain.player.application.PlayerFind;
import com.example.demo.domain.player.application.PlayerRegistry;
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
    private final IdGenerator idGenerator;
    private final MoveValidationService validationService;
    private final Canvas canvas;

    private final PlayerFind playerFind;
    private final PlayerRegistry playerRegistry;
    private final PlayerCleanUp playerCleanUp;

    private final EnemyFind enemyFind;
    private final EnemyRegistry enemyRegistry;
    private final EnemyCleanUp enemyCleanUp;

    public GameService(IdGenerator idGenerator, MoveValidationService validationService, Canvas canvas, PlayerFind playerFind, PlayerRegistry playerRegistry, PlayerCleanUp playerCleanUp, EnemyFind enemyFind, EnemyRegistry enemyRegistry, EnemyCleanUp enemyCleanUp) {
        this.validationService = validationService;
        this.idGenerator = idGenerator;
        this.canvas = canvas;
        this.playerFind = playerFind;
        this.playerRegistry = playerRegistry;
        this.playerCleanUp = playerCleanUp;
        this.enemyFind = enemyFind;
        this.enemyRegistry = enemyRegistry;
        this.enemyCleanUp = enemyCleanUp;
    }

    public void processPlayerMove(String playerIdStr, Direction direction) {
        PlayerId playerId = PlayerId.of(playerIdStr);
        Optional<Player> playerOptional = playerFind.byId(playerId);

        if (playerOptional.isEmpty()) {
            log.warn("[Service] Received move request for non-existent player ID: {}", playerIdStr);
            return;
        }

        Player currentPlayer = playerOptional.get();

        // 다음 위치 계산
        Position currentPosition = currentPlayer.getPosition();
        int step = currentPlayer.getSize();
        Position nextPosition = direction.move(currentPosition, step);

        // 경계 검사
        if (validationService.isPlayerMoveValid(currentPlayer, nextPosition)) {
            Player movedPlayer = currentPlayer.moveTo(nextPosition);
            playerRegistry.addOrUpdate(movedPlayer);
            log.debug("[Service] Player {} processed move to {}", currentPlayer.getId(), nextPosition);
        } else {
            log.debug("[Service] Player {} move to {} is invalid.", currentPlayer.getId(), nextPosition);
            // 이동 실패 시 추가 로직 (예: 클라이언트에 알림) 필요시 추가
        }
        // 플레이어 이동 후 broadcast는 WebSocketHandler에서 호출됨
    }

    public Player initializeOrGetPlayer() {
        PlayerId newPlayerId = idGenerator.generatePlayerId();
        log.debug("[Generated] New Player ID: {}", newPlayerId);

        int initialSize = 20;
        int startX = (canvas.getWidth() / 2) - (initialSize / 2);
        int startY = (canvas.getHeight() / 2) - (initialSize / 2);
        Position initialPosition = new Position(startX, startY); // 임시 초기 위치

        Player newPlayer = new Player(newPlayerId, initialPosition, initialSize);


        playerRegistry.addOrUpdate(newPlayer);
        log.debug("[Service] New Player initialized and registered: {}", newPlayer);
        return newPlayer;
    }
    public Enemy spawnInitialEnemy() {
        if (enemyFind.findAll().isEmpty()) {
            EnemyId newEnemyId = idGenerator.generateEnemyId();
            log.debug("[Service] Spawning initial enemy with ID: {}", newEnemyId);

            int initialSize = 20;
            int initialY = 50;
            int startX = (canvas.getWidth() / 2) - (initialSize / 2);
            Position initialPosition = new Position(startX, initialY);

            Enemy newEnemy = new Enemy(newEnemyId, initialPosition, initialSize);
            enemyRegistry.addOrUpdate(newEnemy);
            log.info("[Service] Initial enemy spawned and registered: {}", newEnemyId);
            return newEnemy;
        } else {
            log.debug("[Service] Initial enemy already exists. Skipping spawn.");
            // 기존 적 중 하나를 반환하거나 null 반환 (게임 규칙에 따라)
            // 첫 번째 적을 반환하는 것이 의미 없을 수 있으므로 null 반환 고려
            return enemyFind.findAll().stream().findFirst().orElse(null);
        }
    }

    public void removePlayer(PlayerId playerId) {
        playerCleanUp.remove(playerId);
        log.debug("[Service] Player removed: {}", playerId);
    }

    // Enemy 또한 Player 와 함께 동시 초기화.
    public void resetGame() {
        log.debug("[Service] Initiating game reset...");
        playerCleanUp.clearAll();
        enemyCleanUp.clearAll();
        log.info("[Service] Game reset complete. Players and Enemies cleared.");
    }
}
