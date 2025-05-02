package com.example.demo.application.physics;

import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.projectile.Projectile;
import com.example.demo.infrastructure.config.Canvas;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MoveValidationService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final Canvas canvas;
    private final CollisionService collisionService;
    private final EnemyFind enemyFind;

    public MoveValidationService(Canvas canvas, CollisionService collisionService, EnemyFind enemyFind) {
        this.enemyFind = enemyFind;
        this.canvas = canvas;
        this.collisionService = collisionService;
    }

    public boolean isPlayerMoveValid(Player player, Position nextPosition) {
        // 경계 검사
        if (!canvas.isWithinBounds(nextPosition, player.getSize())) {
            log.debug("[Validation] Player Move blocked by boundary: {} to {}", player.getPosition(), nextPosition);
            return false;
        }
        // 다른 객체와 충돌 검사
        Collection<Enemy> currentEnemies = enemyFind.findAll();
        if (!currentEnemies.isEmpty()) {
            Player playerNextPosition = player.moveTo(nextPosition);
            for (Enemy currentEnemy : currentEnemies) {
                if (collisionService.checkPlayerEnemyCollision(playerNextPosition, currentEnemy)) {
                    log.debug("[Validation] Player move blocked by Enemy collision: {} to {}", playerNextPosition, currentEnemy.getPosition());
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isProjectileMoveValid(Projectile projectile, Position nextPosition) {

        if (!canvas.isWithinBounds(nextPosition, projectile.getSize())) {
            log.debug("[Validation] Projectile Move blocked by boundary: {} to {}", projectile.getPosition(), nextPosition);
            return false;
        }

        Collection<Enemy> currentEnemies = enemyFind.findAll();
        if (!currentEnemies.isEmpty()) {
            Projectile projectileNextPos = projectile.move();
            for (Enemy currentEnemy : currentEnemies) {
                if (collisionService.checkProjectileEnemyCollision(projectileNextPos, currentEnemy)) {
                    log.debug("[Validation] Projectile move blocked by Enemy collision: {} to {}", projectileNextPos, currentEnemy.getPosition());
                    return false;
                }
            }
        }
        return true;
    }
}
