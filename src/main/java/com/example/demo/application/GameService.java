package com.example.demo.application;

import com.example.demo.application.physics.MoveValidationService;
import com.example.demo.domain.enemy.application.EnemyCleanUp;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.enemy.application.EnemyRegistry;
import com.example.demo.domain.player.application.PlayerCleanUp;
import com.example.demo.domain.player.application.PlayerFind;
import com.example.demo.domain.player.application.PlayerRegistry;
import com.example.demo.domain.support.IdGenerator;
import com.example.demo.domain.weapon.Pistol.Pistol;
import com.example.demo.domain.weapon.Weapon;
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

    private final Pistol defaultPistol;


    public GameService(IdGenerator idGenerator, MoveValidationService validationService, Canvas canvas, PlayerFind playerFind, PlayerRegistry playerRegistry, PlayerCleanUp playerCleanUp, EnemyFind enemyFind, EnemyRegistry enemyRegistry, EnemyCleanUp enemyCleanUp, Pistol pistol) {
        this.validationService = validationService;
        this.idGenerator = idGenerator;
        this.canvas = canvas;
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

        int initialSize = 20;
        int startX = (canvas.getWidth() / 2) - (initialSize / 2);
        int startY = (canvas.getHeight() / 2) - (initialSize / 2);
        Position initialPosition = new Position(startX, startY); // 임시 초기 위치

        Player newPlayer = new Player(newPlayerId, initialPosition, initialSize, defaultPistol);
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

    public Enemy spawnInitialEnemy() {
        if (enemyFind.findAll().isEmpty()) {
            EnemyId newEnemyId = idGenerator.generateEnemyId();
            log.debug("[Service] Spawning initial enemy with ID: {}", newEnemyId);

            int initialSize = 20;
            int initialY = 50;
            int startX = (canvas.getWidth() / 2) - (initialSize / 2);
            Position initialPosition = new Position(startX, initialY);

            Enemy newEnemy = new Enemy(newEnemyId, initialPosition, initialSize, Direction.RIGHT, defaultPistol);
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

    // Not used now
//    public void enemyFire(String enemyIdStr, Direction targetDirection) {
//        log.debug("적이 발사합니다.");
//        EnemyId enemyId = EnemyId.of(enemyIdStr);
//        Optional<Enemy> enemyOptional = enemyFind.findById(enemyId);
//
//        if (enemyOptional.isEmpty()) {
//            log.debug("[Service] Error: Received fire request for non-existent Enemy ID: {}", enemyIdStr);
//            return;
//        }
//
//        Enemy currentEnemy = enemyOptional.get();
//        currentEnemy.fire(targetDirection);
//        log.debug("[Service] Enemy {} fired towards {}", enemyId, targetDirection);
//    }

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
