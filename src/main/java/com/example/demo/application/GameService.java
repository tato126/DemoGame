package com.example.demo.application;

import com.example.demo.application.physics.MoveValidationService;
import com.example.demo.domain.enemy.application.EnemyCleanUp;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.enemy.application.EnemyRegistry;
import com.example.demo.domain.common.AliveStatus;
import com.example.demo.domain.player.application.PlayerCleanUp;
import com.example.demo.domain.player.application.PlayerFind;
import com.example.demo.domain.player.application.PlayerRegistry;
import com.example.demo.domain.support.IdGenerator;
import com.example.demo.domain.weapon.Pistol.Pistol;
import com.example.demo.infrastructure.config.Canvas;
import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.EnemyId;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.player.PlayerId;
import com.example.demo.infrastructure.config.properties.EnemyProperties;
import com.example.demo.infrastructure.config.properties.PlayerProperties;
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

    private final PlayerProperties playerProperties;
    private final EnemyProperties enemyProperties;

    private final PlayerFind playerFind;
    private final PlayerRegistry playerRegistry;
    private final PlayerCleanUp playerCleanUp;

    private final EnemyFind enemyFind;
    private final EnemyRegistry enemyRegistry;
    private final EnemyCleanUp enemyCleanUp;

    private final Pistol defaultPistol;


    public GameService(IdGenerator idGenerator, MoveValidationService validationService, Canvas canvas, PlayerProperties playerProperties, EnemyProperties enemyProperties, PlayerFind playerFind, PlayerRegistry playerRegistry, PlayerCleanUp playerCleanUp, EnemyFind enemyFind, EnemyRegistry enemyRegistry, EnemyCleanUp enemyCleanUp, Pistol pistol) {
        this.validationService = validationService;
        this.idGenerator = idGenerator;
        this.canvas = canvas;
        this.playerProperties = playerProperties;
        this.enemyProperties = enemyProperties;
        this.playerFind = playerFind;
        this.playerRegistry = playerRegistry;
        this.playerCleanUp = playerCleanUp;
        this.enemyFind = enemyFind;
        this.enemyRegistry = enemyRegistry;
        this.enemyCleanUp = enemyCleanUp;
        this.defaultPistol = pistol;
    }

    public Player initializePlayer() {
        PlayerId newPlayerId = idGenerator.generatePlayerId();
        log.debug("[Generated] New Player ID: {}", newPlayerId);

        int initialSize = playerProperties.initialSize();
        int initialSpeed = playerProperties.initialSpeed();

        int startX = (canvas.getWidth() / 2) - (initialSize / 2);
        int startY = (canvas.getHeight() / 2) - (initialSize / 2);
        Direction initialDirection = Direction.UP;
        Position initialPosition = new Position(startX, startY); // 임시 초기 위치
        boolean isAlive = true; // 생성시에는 살아있는 상태

        Player newPlayer = new Player(newPlayerId, initialPosition, initialSize, initialSpeed, defaultPistol, initialDirection, isAlive);
        log.debug("총 객체 생성 {}", defaultPistol);

        playerRegistry.addOrUpdate(newPlayer);
        log.debug("[Service] New Player initialized and registered: {}", newPlayer);
        return newPlayer;
    }

    public void playerMove(String playerIdStr, Direction direction) {
        PlayerId playerId = PlayerId.of(playerIdStr);
        Optional<Player> playerOptional = playerFind.findById(playerId);

        if (playerOptional.isEmpty()) {
            log.warn("[Service] Received move request for non-existent player ID: {}", playerIdStr);
            return;
        }

        Player currentPlayer = playerOptional.get();

        // 다음 위치 계산
        Position currentPosition = currentPlayer.getPosition();
        int step = currentPlayer.getSpeed();

        Position nextPosition;

        try {
            nextPosition = currentPosition.moveIndirection(direction, step);
        } catch (IllegalArgumentException exception) {
            log.warn("[Service] Invalid step for player move (playerId: {}, step: {}): {}", playerIdStr, step, exception.getMessage());
            return;
        }

        // 경계 검사
        if (validationService.isPlayerMoveValid(currentPlayer, nextPosition)) {
            Player movedPlayer = currentPlayer.moveTo(nextPosition, direction);
            playerRegistry.addOrUpdate(movedPlayer);
            log.debug("[Service] Player {} processed move to {}", currentPlayer.getId(), nextPosition);
        } else {
            log.debug("[Service] Player {} move to {} is invalid.", currentPlayer.getId(), nextPosition);
            // 이동 실패 시 추가 로직 (예: 클라이언트에 알림) 필요시 추가
        }
        // 플레이어 이동 후 broadcast는 WebSocketHandler에서 호출됨
    }


    public void playerFire(String playerIdStr, Direction targetDirection) {
        log.debug("플레이어가 발사합니다.");
        PlayerId playerId = PlayerId.of(playerIdStr);
        Optional<Player> playerOptional = playerFind.findById(playerId);

        if (playerOptional.isEmpty()) {
            log.debug("[Service] Received fire request for non-existent player ID: {}", playerIdStr);
            return;
        }

        Player currentPlayer = playerOptional.get();
        currentPlayer.fire(targetDirection);
        log.debug("[Service] Player {} fired towards {}", playerId, targetDirection);
    }

    public void updatePlayerAliveStatus(String playerIdStr, AliveStatus playerStatus) {
        PlayerId playerId = PlayerId.of(playerIdStr);
        Optional<Player> playerOptional = playerFind.findById(playerId);

        if (playerOptional.isEmpty()) {
            log.debug("[Service] Check Player Received fire request for non-existent player ID: {}", playerIdStr);
            return;
        }

        Player currentPlayer = playerOptional.get();

        Player updatePlayer = currentPlayer.updateStatus(playerStatus);

        if (updatePlayer != currentPlayer || updatePlayer.isAlive() != currentPlayer.isAlive()) {
            playerRegistry.addOrUpdate(updatePlayer);
            log.trace("[Service] Current Player {} alive status {} is successfully update to {}. New Alive status", playerId, playerStatus, updatePlayer);

            // 추가적으로 게임 오버 로직 추가 가능 -> 게임 오버 메서드는 따로 생성하는 것이 직관적이여 보임
        }

        log.debug("[Service] CurrentPlayer alive status is change: {}", currentPlayer);
        log.debug("[Service] Player {} current status is {}", playerId, playerStatus);
    }

    public void removePlayer(PlayerId playerId) {
        playerCleanUp.remove(playerId);
        log.debug("[Service] Player removed: {}", playerId);
    }

    public Enemy spawnInitialEnemy() {
        if (enemyFind.findAll().isEmpty()) {
            EnemyId newEnemyId = idGenerator.generateEnemyId();
            log.debug("[Service] Spawning initial enemy with ID: {}", newEnemyId);

            int initialSize = enemyProperties.initialSize();
            int initialSpeed = enemyProperties.initialSpeed();
            int initialY = enemyProperties.initialY();
            int startX = (canvas.getWidth() / 2) - (initialSize / 2);
            Position initialPosition = new Position(startX, initialY);
            boolean isAlive = true;

            Enemy newEnemy = new Enemy(newEnemyId, initialPosition, initialSize, initialSpeed, Direction.RIGHT, defaultPistol, isAlive);
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

    public void updateEnemyAliveStatus(String enemyIdStr, AliveStatus enemyStatus) {

        EnemyId enemyId = EnemyId.of(enemyIdStr);
        Optional<Enemy> enemyOptional = enemyFind.findById(enemyId);

        if (enemyOptional.isEmpty()) {
            log.debug("[Service] Current enemyId is empty: {}", enemyIdStr);
            return;
        }

        Enemy currentEnemy = enemyOptional.get();

        Enemy updateStatusEnemy = currentEnemy.updateStatus(enemyStatus);

        if (updateStatusEnemy != currentEnemy || updateStatusEnemy.isAlive() != currentEnemy.isAlive()) {
            enemyRegistry.addOrUpdate(updateStatusEnemy);
            log.trace("[Service] Current Enemy {} alive status {} is successfully update to {}. New Alive status", enemyId, enemyStatus, updateStatusEnemy);
        }

        log.debug("[Service] Current Enemy alive status is: {}", updateStatusEnemy);
    }

    // Enemy 또한 Player 와 함께 동시 초기화.
    public void resetGame() {
        log.debug("[Service] Initiating game reset...");
        playerCleanUp.clearAll();
        enemyCleanUp.clearAll();
        log.info("[Service] Game reset complete. Players and Enemies cleared.");
    }
}
