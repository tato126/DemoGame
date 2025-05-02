package com.example.demo.application;

import com.example.demo.application.physics.CollisionService;
import com.example.demo.application.physics.MoveValidationService;
import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.application.EnemyCleanUp;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.enemy.application.EnemyRegistry;
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
    private final Canvas canvas;

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

    public GameLoopService(WebSocketHandler webSocketHandler, CollisionService collisionService, Canvas canvas, EnemyFind enemyFind, EnemyRegistry enemyRegistry, EnemyCleanUp enemyCleanUp, ProjectileFind projectileFind, ProjectileCleanUp projectileCleanUp, ProjectileRegistry projectileRegistry) {
        this.collisionService = collisionService;
        this.canvas = canvas;
        this.enemyFind = enemyFind;
        this.enemyRegistry = enemyRegistry;
        this.webSocketHandler = webSocketHandler;
        this.enemyCleanUp = enemyCleanUp;
        this.projectileFind = projectileFind;
        this.projectileCleanUp = projectileCleanUp;
        this.projectileRegistry = projectileRegistry;
    }

    @Scheduled(fixedRate = UPDATE_RATE_MS)
    public void updateEnemyMovement() {

        Collection<Enemy> currentEnemies = enemyFind.findAll();

        if (currentEnemies == null) {
            return;
        }

        boolean stateChanged = false;

        for (Enemy currentEnemy : currentEnemies) {
            Position currentPosition = currentEnemy.getPosition();
            Direction currentDirection = currentEnemy.getDirection();
            Position nextPosition;
            Direction nextDirection = currentDirection;

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
                log.debug("[Position Change] Enemy hit minX boundary, change direction to RIGHT");
            } else if (nextPosition.x() + currentEnemy.getSize() >= ENEMY_MAX_X + currentEnemy.getSize()) {

                if (nextPosition.x() >= ENEMY_MAX_X) {
                    nextPosition = new Position(ENEMY_MAX_X, currentPosition.y());
                    nextDirection = Direction.LEFT;
                    log.debug("[Position Change] Enemy hit maxX boundary, change direction to LEFT");
                }
            }
            Enemy movedEnemyState = new Enemy(currentEnemy.getId(), nextPosition, currentEnemy.getSize(), nextDirection);


            if (!currentEnemy.getPosition().equals(movedEnemyState.getPosition()) || !currentEnemy.getDirection().equals(movedEnemyState.getDirection())) {
                enemyRegistry.addOrUpdate(movedEnemyState);
                log.trace("[Update] Enemy {} updated state: {}", movedEnemyState.getId(), movedEnemyState);
                stateChanged = true;
            }
        }

        if (stateChanged) {
            webSocketHandler.broadcastGameStateUpdate();
            log.debug("[GameLoop] Enemy movement caused state change, broadcasting update");
        }
    }

    @Scheduled(fixedRate = 30)
    public void updateProjectileMovement() {

        Collection<Projectile> currentProjectiles = projectileFind.findAll();

        if (currentProjectiles.isEmpty()) {
            return;
        }

        Collection<Enemy> currentEnemies = enemyFind.findAll();

        if (currentProjectiles.isEmpty()) {
            log.debug("[Empty]Projectile List is Empty!");
            return;
        }

        boolean stateChange = false;

        for (Projectile currentProjectile : currentProjectiles) {
            Projectile movedProjectile = currentProjectile.move();
            Position nextPosition = movedProjectile.getPosition();

            if (!canvas.isWithinBounds(nextPosition, movedProjectile.getSize())) {
                projectileCleanUp.remove(currentProjectile.getId());
                log.debug("[GameLoop] WithInBounds Error pos:{}", currentProjectile.getId());
                stateChange = true;
                continue;
            }

            boolean hitEnemy = false;

            for (Enemy currentEnemy : currentEnemies) {

                if (collisionService.checkProjectileEnemyCollision(movedProjectile, currentEnemy)) {
                    log.debug("[Projectile Collision] Projectile {} hit Enemy {}", movedProjectile.getId(), currentEnemy.getId());

                    projectileCleanUp.remove(movedProjectile.getId());
                    enemyCleanUp.remove(currentEnemy.getId());

                    stateChange = true;
                    hitEnemy = true;
                    break;
                }
            }

            if (hitEnemy) {
                continue;
            }

            if (!currentProjectile.getPosition().equals(movedProjectile.getPosition())) {
                projectileRegistry.addOrUpdate(movedProjectile);
                log.trace("[Projectile] Moved projectile: {}", movedProjectile.getId());
                stateChange = true;
            }
        }

        if (stateChange) {
            webSocketHandler.broadcastGameStateUpdate();
            log.debug("[GameLoop] 빵야빵야");
            log.debug("[GameLoop] Projectile movement caused state change, broadcasting update");
        }
    }
}
