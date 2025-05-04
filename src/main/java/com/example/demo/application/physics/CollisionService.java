package com.example.demo.application.physics;

import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.projectile.Projectile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CollisionService {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // Player & Enemy Collision
    public boolean checkPlayerEnemyCollision(Player player, Enemy enemy) {

        // null check
        if (player == null || enemy == null) {
            return false;
        }

        // 위치와 크기 가져오기
        Position playerPosition = player.getPosition();
        int playerSize = player.getSize();

        Position enemyPosition = enemy.getPosition();
        int enemySize = enemy.getSize();

        // 플레이어 경계
        int playerLeft = playerPosition.x();
        int playerRight = playerPosition.x() + playerSize;
        int playerTop = playerPosition.y();
        int playerBottom = playerPosition.y() + playerSize;

        // 적 경계
        int enemyLeft = enemyPosition.x();
        int enemyRight = enemyPosition.x() + enemySize;
        int enemyTop = enemyPosition.y();
        int enemyBottom = enemyPosition.y() + enemySize;

        // 충돌 검사
        boolean collisionX = playerRight > enemyLeft && playerLeft < enemyRight;
        boolean collisionY = playerBottom > enemyTop && playerTop < enemyBottom;

        log.debug("[Collision Service] check Player & Enemy collisionX : {}, collisionY: {}", collisionX, collisionY);
        return collisionX && collisionY;
    }

    // Projectile & Enemy Collision
    public boolean checkProjectileEnemyCollision(Projectile projectile, Enemy enemy) {

        if (projectile == null || enemy == null) {
            return false;
        }

        // 위치/크기 검사
        Position projectilePos = projectile.getPosition();
        int projectileSize = projectile.getSize();

        Position enemyPos = enemy.getPosition();
        int enemySize = enemy.getSize();

        // 투사체 경계
        int proLeft = projectilePos.x(); // 왼쪽 경계
        int proRight = projectilePos.x() + projectileSize; // 오른쪽 경계
        int proTop = projectilePos.y(); // 상단 경계
        int proBottom = projectilePos.y() + projectileSize; // 하단 경계

        // 적 경계
        int enemyLeft = enemyPos.x();
        int enemyRight = enemyPos.x() + enemySize;
        int enemyTop = enemyPos.y();
        int enemyBottom = enemyPos.y() + enemySize;

        // 충돌 검사
        boolean collisionX = proLeft < enemyRight && proRight > enemyLeft;
        boolean collisionY = proTop < enemyBottom && proBottom > enemyTop;

        log.debug("[Collision Service] check Projectile & Enemy collisionX : {}, collisionY: {}", collisionX, collisionY);
        return collisionX && collisionY;
    }

    // Projectile & Player Collision
    public boolean checkProjectilePlayerCollision(Projectile projectile, Player player) {

        if (projectile == null || player == null) {
            return false;
        }

        // 위치/크기 검사
        Position projectilePos = projectile.getPosition();
        int projectileSize = projectile.getSize();

        Position playerPos = player.getPosition();
        int playerSize = player.getSize();

        // 투사체 경계
        int proLeft = projectilePos.x(); // 왼쪽 경계
        int proRight = projectilePos.x() + projectileSize; // 오른쪽 경계
        int proTop = projectilePos.y(); // 상단 경계
        int proBottom = projectilePos.y() + projectileSize; // 하단 경계

        // 적 경계
        int playerLeft = playerPos.x();
        int playerRight = playerPos.x() + playerSize;
        int playerTop = playerPos.y();
        int playerBottom = playerPos.y() + playerSize;

        // 충돌 검사
        boolean collisionX = proLeft < playerRight && proRight > playerLeft;
        boolean collisionY = proTop < playerBottom && proBottom > playerTop;

        log.debug("[Collision Service] check Projectile & player collisionX : {}, collisionY: {}", collisionX, collisionY);
        return collisionX && collisionY;
    }
}
