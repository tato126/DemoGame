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
import com.example.demo.domain.projectile.ProjectileId;
import com.example.demo.domain.projectile.application.ProjectileCleanUp;
import com.example.demo.domain.projectile.application.ProjectileFind;
import com.example.demo.domain.projectile.application.ProjectileRegistry;
import com.example.demo.infrastructure.config.Canvas;
import com.example.demo.infrastructure.config.properties.EnemyProperties;
import com.example.demo.infrastructure.config.properties.SchedulingProperties;
import com.example.demo.web.websocket.WebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class GameLoopService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final WebSocketHandler webSocketHandler;
    private final CollisionService collisionService;
    private final GameService gameService;

    private final Canvas canvas;
    private final EnemyProperties enemyProperties;
    private final SchedulingProperties schedulingProperties;

    private final PlayerFind playerFind;
    private final PlayerCleanUp playerCleanUp;

    private final EnemyFind enemyFind;
    private final EnemyRegistry enemyRegistry;
    private final EnemyCleanUp enemyCleanUp;

    private final ProjectileFind projectileFind;
    private final ProjectileCleanUp projectileCleanUp;
    private final ProjectileRegistry projectileRegistry;

    private long enemyFireCounter = 0; // 발사 간격 카운터

    public GameLoopService(WebSocketHandler webSocketHandler, CollisionService collisionService, GameService gameService, Canvas canvas, EnemyProperties enemyProperties, SchedulingProperties schedulingProperties, PlayerFind playerFind, PlayerCleanUp playerCleanUp, EnemyFind enemyFind, EnemyRegistry enemyRegistry, EnemyCleanUp enemyCleanUp, ProjectileFind projectileFind, ProjectileCleanUp projectileCleanUp, ProjectileRegistry projectileRegistry) {
        this.collisionService = collisionService;
        this.gameService = gameService;
        this.canvas = canvas;
        this.enemyProperties = enemyProperties;
        this.schedulingProperties = schedulingProperties;
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

    @Scheduled(fixedRateString = "${game.scheduling.game-loop-update-rate-ms}")
    public void scheduledUpdateAllEnemies() {
        if (updateAllEnemies()) {
            webSocketHandler.broadcastGameStateUpdate();
            log.trace("[GameLoop] Broadcasting update due to enemy state change.");
        }
    }

    @Scheduled(fixedRateString = "${game.scheduling.projectile-update-rate-ms}")
    public void setSchedulingProperties() {

        if (updateAllProjectiles()) {
            webSocketHandler.broadcastGameStateUpdate();
            log.trace("[GameLoop] Broadcasting update due to projectile state change.");
        }
    }

    private boolean updateAllEnemies() {
        Collection<Enemy> currentEnemies = enemyFind.findAll();
        Player player = playerFind.findAll().stream().findFirst().orElse(null);

        if (currentEnemies == null || currentEnemies.isEmpty()) {
            return false;
        }

        boolean overallStateChange = false;
        final long enemyFireIntervalTicks = schedulingProperties.enemyFireIntervalTicks();
        enemyFireCounter++;

        List<Enemy> enemiesToProcess = new ArrayList<>(currentEnemies);

        for (Enemy currentEnemy : enemiesToProcess) {

            if (enemyFind.findById(currentEnemy.getId()).isEmpty()) {
                continue;
            }

            Enemy originalEnemy = currentEnemy; // 상태 변경 비교를 위해 원본 저장
            Enemy updateEnemy = handleEnemyMovement(currentEnemy);

            // 이동으로 인한 상태 변경 확인 및 업데이트
            if (!originalEnemy.getPosition().equals(updateEnemy.getPosition()) || !originalEnemy.getDirection().equals(updateEnemy.getDirection())) {
                enemyRegistry.addOrUpdate(updateEnemy);
                overallStateChange = true;
                log.trace("[GameLoop] Enemy {} updated movement state: {}", updateEnemy.getId(), updateEnemy);
                currentEnemy = updateEnemy;
            }

            // 발사 처리 (이동 후의 적 상태 기준)
            if (player != null && enemyFireCounter >= enemyFireIntervalTicks) {
                // TODO: player.getPosition()과 currentEnemy.getPosition()으로 방향 계산 로직 추가 필요. (Vector 계산?)
                Direction fireDirection = calculateEnemyFireDirection(updateEnemy, player); // 예시: 발사 방향 결정
                currentEnemy.fire(fireDirection);
                log.trace("[GameLoop] Enemy {} fired towards {}", updateEnemy.getId(), fireDirection);
                overallStateChange = true; // 발사 자체가 상태 변경ㅇ로 간주 (투사체 생성)
            }
        }

        if (enemyFireCounter >= enemyFireIntervalTicks) {
            enemyFireCounter = 0; // 카운터 리셋
        }
        return overallStateChange;
    }

    public Enemy handleEnemyMovement(Enemy enemy) {

        Objects.requireNonNull(enemy, "enemy must be not null");

        final int enemyMinX = enemyProperties.patrolMinX(); // 순찰 최소 x 좌표
        final int enemyMaxX = enemyProperties.patrolMaxX(); // 순찰 최대 x 좌표
        final int enemyMoveStep = enemyProperties.moveStep(); // 업데이트 당 이동 거리

        Position currentPosition = enemy.getPosition();
        Direction currentDirection = enemy.getDirection();
        Position nextPosition;
        Direction nextDirection = currentDirection;

        // 1. 다음 위치 계산
        if (currentDirection == Direction.LEFT) {
            nextPosition = currentPosition.move(-enemyMoveStep, 0);
        } else {
            nextPosition = currentPosition.move(enemyMoveStep, 0);
        }

        // 2. 경계 체크 및 방향 전환
        if (nextPosition.x() <= enemyMinX) {
            nextPosition = new Position(enemyMinX, currentPosition.y());
            nextDirection = Direction.RIGHT;
            log.trace("[GameLoop] Enemy hit minX boundary, change direction to RIGHT");
        } else if (nextPosition.x() + enemy.getEnemySize() > enemyMaxX) {
            nextPosition = new Position(enemyMaxX - enemy.getEnemySize(), currentPosition.y());
            nextDirection = Direction.LEFT;
            log.trace("[GameLoop] Enemy hit maxX boundary, change direction to LEFT");
        }
//        return enemy.toBuilder().position(nextPosition).direction(nextDirection).build(); // 예:(Builder 패턴을 사용한다면)
        return new Enemy(enemy.getId(), nextPosition, enemy.getEnemySize(), enemy.getSpeed(), nextDirection, enemy.getEquippedWeapon(), enemy.isAlive());
    }

    private Direction calculateEnemyFireDirection(Enemy enemy, Player player) {
        // 현재는 단순하게 아래로 발사, 향후 플레이어 방향으로 발사 로직 구현 가능
        // 예: if (player.getPosition().y() > enemy.getPosition.y()) return Direction.DOWN;
        return Direction.DOWN;
    }

    private boolean updateAllProjectiles() {
        // ConcurrentModificationException을 피하기 위해 컬렉션 복사 후 순회
        List<Projectile> projectileToProcess = new ArrayList<>(projectileFind.findAll());

        if (projectileToProcess.isEmpty()) {
            log.trace("[GameLoop]  ProjectileFind find all but isEmpty projectileToProcess: {}", projectileToProcess);
            return false;
        }

        Collection<Enemy> currentEnemies = enemyFind.findAll();
        Collection<Player> currentPlayers = playerFind.findAll();

        boolean overallStateChange = false;

        for (Projectile projectile : projectileToProcess) {
            ProjectileProcessingResult result = processSingleProjectile(projectile, currentEnemies, currentPlayers);

            if (result.isStateChanged()) {
                overallStateChange = true;
            }
        }
        return overallStateChange;
    }

    // 한 발사체의 이동, 경계 검사, 충돌 처리를 담당하는 내부 클래스 또는 레코드
    private record ProjectileProcessingResult(boolean isStateChanged, boolean projectileRemoved) {
    }

    private ProjectileProcessingResult processSingleProjectile(Projectile projectile, Collection<Enemy> enemies, Collection<Player> players) {
        Projectile movedProjectile = projectile.move();
        Position nextPosition = movedProjectile.getPosition();
        boolean stateChanged = false;

        // 1. 경계 검사
        if (!canvas.isWithinBounds(nextPosition, movedProjectile.getSize())) {
            projectileCleanUp.remove(projectile.getId());
            log.debug("[GameLoop] Porjectile {} removed (out of bounds at {})", projectile.getId(), nextPosition);
            stateChanged = true;
        }

        // 2. 충돌 검사 및 처리
        Object ownerId = projectile.getOwnerId();
        boolean hitSomething = false;

        if (ownerId instanceof PlayerId) { // 플레이어가 쏜 발사체
            hitSomething = checkAndHandlePlayerProjectileCollision(movedProjectile, enemies, projectile.getId());
        } else if (ownerId instanceof EnemyId) { // 적이 쏜 발사체
            hitSomething = checkAndHandleEnemyProjectileCollision(movedProjectile, players, projectile.getId());
        }

        if (hitSomething) {
            projectileCleanUp.remove(projectile.getId());
            return new ProjectileProcessingResult(true, true);
        }

        // 3. 충돌 없이 이동만 한 경우
        if (!projectile.getPosition().equals(movedProjectile.getPosition())) {
            projectileRegistry.addOrUpdate(movedProjectile);
            log.trace("[GameLoop] Projectile {} moved to {}", movedProjectile.getId(), nextPosition);
            stateChanged = true;
        }
        return new ProjectileProcessingResult(stateChanged, false);
    }

    private boolean checkAndHandlePlayerProjectileCollision(Projectile projectile, Collection<Enemy> enemies, ProjectileId projectileForLog) {
        if (enemies.isEmpty()) return false;
        List<Enemy> enemiesToCheck = new ArrayList<>(enemies); // 동시성 문제 방지 위해 복사

        for (Enemy enemy : enemiesToCheck) {
            if (collisionService.checkPlayerProjectileCollision(projectile, enemy)) {
                log.debug("[GameLoop] Player Projectile {} hit Enemy {}", projectile.getId(), enemy);
                gameService.updateEnemyAliveStatus(enemy.getId().toString(), AliveStatus.DEAD);
                enemyCleanUp.remove(enemy.getId());
                return true; // 충돌 발생
            }
        }
        return false; // 충돌 없음
    }

    private boolean checkAndHandleEnemyProjectileCollision(Projectile projectile, Collection<Player> players, ProjectileId projectileForLog) {
        if (players.isEmpty()) return false;
        List<Player> playersToCheck = new ArrayList<>(players); // 동시성 문제 방지 위해 복사

        for (Player player : playersToCheck) {
            if (collisionService.checkEnemyProjectileCollision(projectile, player)) {
                log.debug("[GameLoop] Enemy Projectile {} hit player {}", projectile.getId(), player);
                gameService.updatePlayerAliveStatus(player.getId().toString(), AliveStatus.DEAD);
                playerCleanUp.remove(player.getId());
                return true; // 충돌 발생
            }
        }
        return false; // 충돌 없음
    }
}