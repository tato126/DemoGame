package com.example.demo.application.physics;

import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.player.Player;
import org.springframework.stereotype.Service;

@Service
public class CollisionService {

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

        return collisionX && collisionY;
    }
}
