package com.example.demo.application;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.enemy.application.EnemyRegistry;
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

    private final EnemyFind enemyFind;
    private final EnemyRegistry enemyRegistry;

    private static final int ENEMY_MIN_X = 50;      // 순찰 최소 x 좌표
    private static final int ENEMY_MAX_X = 330;     // 순찰 최대 x 좌표
    private static final int ENEMY_MOVE_STEP = 5;   // 업데이트 당 이동 거리
    private static final long UPDATE_RATE_MS = 100; // 업데이트 주기 (0.1초)

    public GameLoopService(WebSocketHandler webSocketHandler, EnemyFind enemyFind, EnemyRegistry enemyRegistry) {
        this.enemyFind = enemyFind;
        this.enemyRegistry = enemyRegistry;
        this.webSocketHandler = webSocketHandler;
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

            // 상태 변경시 GameState 업데이트 및 BroadCast
            if (!currentEnemy.getPosition().equals(movedEnemyState.getPosition()) || !currentEnemy.getDirection().equals(movedEnemyState.getDirection())) {
                enemyRegistry.addOrUpdate(movedEnemyState);
                log.debug("[Update] Enemy {} updated state: {}", movedEnemyState.getId(), movedEnemyState);
                stateChanged = true;
            }
        }

        if (stateChanged) {
            webSocketHandler.broadcastGameStateUpdate();
            log.debug("[GameLoop] Enemy movement caused state change, broadcasting update");
        }
    }
}
