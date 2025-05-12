package com.example.demo.domain.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameCalculationUtils {

    private static final Logger log = LoggerFactory.getLogger(GameCalculationUtils.class);

    private GameCalculationUtils() {
        throw new IllegalStateException("[GameCalculationUtils] Utility class");
    }

    // 투사체의 발사 위치 조정 메서드.
    // 발사 주체 방향의 가운데에서 발사됨
    public static Position calculateProjectileStartPosition(Position entityPosition, int entitySize, int projectileSize, Direction facingDirection, Object entityIdForLog) {

        int startX = entityPosition.x(); // 발사 주체의 x 좌표
        int startY = entityPosition.y(); // 발사 주체의 y 좌표

        int entityCenterX = entityPosition.x() + entitySize / 2; // 발사 주체 X 좌표의 가운데
        int entityCenterY = entityPosition.y() + entitySize / 2; // 발사 주체의 y 좌표의 가운데

        switch (facingDirection) {
            case UP:
                startX = entityCenterX - projectileSize / 2;
                startY = entityPosition.y() - projectileSize; // 캐릭터 상단에서 총알 크기만큼 위로
                break;
            case DOWN:
                startX = entityCenterX - projectileSize / 2;
                startY = entityPosition.y() + entitySize;     // 캐릭터 하단
                break;
            case LEFT:
                startX = entityPosition.x() - projectileSize; // 캐릭터 좌측에서 총알 크기만큼 왼쪽으로
                startY = entityCenterY - projectileSize / 2;
                break;
            case RIGHT:
                startX = entityPosition.x() + entitySize;     // 캐릭터 우측
                startY = entityCenterY - projectileSize / 2;
                break;
        }
        log.trace("Calculated projectile start position: ({}, {}) for entity {} facing {}", startX, startY, entityIdForLog, facingDirection);
        return new Position(startX, startY);
    }
}
