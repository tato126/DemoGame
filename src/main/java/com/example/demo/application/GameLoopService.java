package com.example.demo.application;

import com.example.demo.application.physics.CollisionService;
import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.EnemyId;
import com.example.demo.domain.enemy.application.EnemyCleanUp;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.enemy.application.EnemyRegistry;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.player.PlayerId;
import com.example.demo.domain.common.AliveStatus;
import com.example.demo.domain.player.application.PlayerCleanUp;
import com.example.demo.domain.player.application.PlayerFind;
import com.example.demo.domain.projectile.Projectile;
import com.example.demo.domain.projectile.application.ProjectileCleanUp;
import com.example.demo.domain.projectile.application.ProjectileFind;
import com.example.demo.domain.projectile.application.ProjectileRegistry;
import com.example.demo.infrastructure.config.Canvas;
import com.example.demo.web.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class GameLoopService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final WebSocketHandler webSocketHandler;
    private final CollisionService collisionService;
    private final GameService gameService;

    private final Canvas canvas;

    private final PlayerFind playerFind;
    private final PlayerCleanUp playerCleanUp;

    private final EnemyFind enemyFind;
    private final EnemyRegistry enemyRegistry;
    private final EnemyCleanUp enemyCleanUp;

    private final ProjectileFind projectileFind;
    private final ProjectileCleanUp projectileCleanUp;
    private final ProjectileRegistry projectileRegistry;

    private static final int ENEMY_MIN_X = 50;      // 순찰 최소 x 좌표
    private static final int ENEMY_MAX_X = 330;     // 순찰 최대 x 좌표
    private static final int ENEMY_MOVE_STEP = 5;   // 업데이트 당 이동 거리
    private static final long UPDATE_RATE_MS = 100; // 업데이트 주기 (0.1초)

    public GameLoopService(WebSocketHandler webSocketHandler, CollisionService collisionService, GameService gameService, Canvas canvas, PlayerFind playerFind, PlayerCleanUp playerCleanUp, EnemyFind enemyFind, EnemyRegistry enemyRegistry, EnemyCleanUp enemyCleanUp, ProjectileFind projectileFind, ProjectileCleanUp projectileCleanUp, ProjectileRegistry projectileRegistry) {
        this.collisionService = collisionService;
        this.gameService = gameService;
        this.canvas = canvas;
        this.playerFind = playerFind;
        this.playerCleanUp = playerCleanUp;
        this.enemyFind = enemyFind;
        this.enemyRegistry = enemyRegistry;
        this.webSocketHandler = webSocketHandler;
        this.enemyCleanUp = enemyCleanUp;
        this.projectileFind = projectileFind;
        this.projectileCleanUp = projectileCleanUp;
        this.projectileRegistry = projectileRegistry;
    }

    private long enemyFireCounter = 0; // 발사 간격 카운터
    private final Long ENEMY_FIRE_INTERVAL_TICKS = 10L; // 예시: 10틱마다 발사 (1초 간격 100ms/틱)

    @Scheduled(fixedRate = UPDATE_RATE_MS)
    public void updateEnemy() {

        Collection<Enemy> currentEnemies = enemyFind.findAll();
        Player player = playerFind.findAll().stream().findFirst().orElse(null);

        if (currentEnemies == null) {
            return;
        }

        boolean stateChanged = false;

        for (Enemy currentEnemy : currentEnemies) {
            Position currentPosition = currentEnemy.getPosition();
            Direction currentDirection = currentEnemy.getDirection();
            Position nextPosition;
            Direction nextDirection = currentDirection;


            log.trace("[GameLoop] currentEnemy dir : {}", currentEnemy.getDirection());

            // 1. 다음 위치 계산
            if (currentDirection == Direction.LEFT) {
                nextPosition = currentPosition.move(-ENEMY_MOVE_STEP, 0);
            } else {
                nextPosition = currentPosition.move(ENEMY_MOVE_STEP, 0);
            }

            // 2. 경계 체크 및 방향 전환
            if (nextPosition.x() <= ENEMY_MIN_X) {
                nextPosition = new Position(ENEMY_MIN_X, currentPosition.y());
                nextDirection = Direction.RIGHT;
                log.trace("[GameLoop] Enemy hit minX boundary, change direction to RIGHT");
            } else if (nextPosition.x() + currentEnemy.getSize() >= ENEMY_MAX_X + currentEnemy.getSize()) {

                if (nextPosition.x() >= ENEMY_MAX_X) {
                    nextPosition = new Position(ENEMY_MAX_X, currentPosition.y());
                    nextDirection = Direction.LEFT;
                    log.trace("[GameLoop] Enemy hit maxX boundary, change direction to LEFT");
                }
            }
            Enemy movedEnemyState = new Enemy(currentEnemy.getId(), nextPosition, currentEnemy.getSize(), currentEnemy.getSpeed(), nextDirection, currentEnemy.getEquippedWeapon(), currentEnemy.isAlive());

            if (!currentEnemy.getPosition().equals(movedEnemyState.getPosition()) || !currentEnemy.getDirection().equals(movedEnemyState.getDirection())) {
                enemyRegistry.addOrUpdate(movedEnemyState);
                log.trace("[GameLoop] Enemy {} updated state: {}", movedEnemyState.getId(), movedEnemyState);
                stateChanged = true;
            }

            enemyFireCounter++;
            if (enemyFireCounter >= ENEMY_FIRE_INTERVAL_TICKS) {
                Direction fireDirection = Direction.DOWN; // 임시

                if (player != null) {
                    // TODO: player.getPosition()과 currentEnemy.getPosition()으로 방향 계산 로직 추가 필요. (Vector 계산?)
                    movedEnemyState.fire(fireDirection);
                    log.trace("[GameLoop] Enemy {} fired towards {}", currentEnemy.getId(), fireDirection);
                    stateChanged = true;
                }
            }

            if (enemyFireCounter >= ENEMY_FIRE_INTERVAL_TICKS) {
                enemyFireCounter = 0;
            }
        }

        if (stateChanged) {
            webSocketHandler.broadcastGameStateUpdate();
            log.trace("[GameLoop] Enemy movement caused state change, broadcasting update");
        }
    }

    @Scheduled(fixedRate = 30)
    public void updateProjectileMovement() {

        Collection<Projectile> currentProjectiles = projectileFind.findAll();

        if (currentProjectiles.isEmpty()) {
            return;
        }

        Collection<Enemy> currentEnemies = enemyFind.findAll();

        if (currentEnemies.isEmpty()) {
            log.trace("[GameLoop] currentEnemies list is null");
            return;
        }
        Collection<Player> currentPlayers = playerFind.findAll();

        if (currentPlayers.isEmpty()) {
            log.trace("[GameLoop] currentPlayers list is null");
            return;
        }

        boolean stateChanged = false;
        boolean playerDead = false; // 사망 확인 판단
        boolean enemyDead = false;

        for (Projectile currentProjectile : currentProjectiles) {
            Projectile movedProjectile = currentProjectile.move();
            Position nextPosition = movedProjectile.getPosition();

            if (!canvas.isWithinBounds(nextPosition, movedProjectile.getSize())) {
                projectileCleanUp.remove(currentProjectile.getId());
                log.debug("[GameLoop] WithInBounds Error pos:{}", currentProjectile.getId());
                stateChanged = true;
                continue;
            }

            Object ownerId = currentProjectile.getOwnerId();
            boolean hitSomething = false;

            // 무기의 사용자를 통해 발사한 발사체가 어떤 대상을 피격했는지 판단.
            if (ownerId instanceof PlayerId) {

                for (Enemy enemy : currentEnemies) {
                    if (collisionService.checkProjectileEnemyCollision(movedProjectile, enemy)) {
                        log.debug("[GameLoop] Player Projectile {} hit Enemy {}", movedProjectile.getId(), enemy.getId());
                        projectileCleanUp.remove(currentProjectile.getId());
                        gameService.updateEnemyAliveStatus(enemy.getId().toString(), AliveStatus.DEAD);
                        enemyCleanUp.remove(enemy.getId());

                        stateChanged = true;
                        hitSomething = true;
                        enemyDead = true;
                        break;
                    }
                }
            } else if (ownerId instanceof EnemyId) {

                for (Player player : currentPlayers) {

                    if (collisionService.checkProjectilePlayerCollision(movedProjectile, player)) {
                        log.debug("[GameLoop] Enemy projectile {} hit player {}", movedProjectile.getId(), player.getId());
                        projectileCleanUp.remove(movedProjectile.getId());
                        gameService.updatePlayerAliveStatus(player.getId().toString(), AliveStatus.DEAD);  // 플레이어 사망상태 업데이트
                        playerCleanUp.remove(player.getId());
                        log.debug("[GameLoop] Removed playerId: {}", player.getId());
                        stateChanged = true;
                        hitSomething = true;
                        playerDead = true; // 적의 투사체에 피격시 사망처리
                        break;
                    }
                }
            }

            if (hitSomething) {
                continue;
            }

            if (!currentProjectile.getPosition().equals(movedProjectile.getPosition())) {
                projectileRegistry.addOrUpdate(movedProjectile);
                log.trace("[GameLoop] Moved player projectile: {}", movedProjectile.getId());
                stateChanged = true;
            }
        }

        if (stateChanged) {
            webSocketHandler.broadcastGameStateUpdate();
            log.trace("[GameLoop] 적에게 빵야빵야");
            log.trace("[GameLoop] Player projectile movement caused state change, broadcasting update");
            if (playerDead) {
                log.debug("당신이 죽었습니다. PlayerID: {}", currentPlayers); // 현재의 사용자 id를 가져와야함
            } else if (enemyDead) {
                log.debug("적이 죽었습니다. EnemyID: {}", currentEnemies);
            }
        }
    }
}
