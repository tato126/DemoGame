package com.example.demo.domain.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameCalculationUtils {

    private static final Logger log = LoggerFactory.getLogger(GameCalculationUtils.class);

    private GameCalculationUtils() {
        throw new IllegalStateException("[GameCalculationUtils] Utility class");
    }

    public static Position calculateProjectileStartPosition(Position entityPosition, int entitySize, int projectileSize, Direction facingDirection, Object entityIdForLog) {

        int startX = entityPosition.x();
        int startY = entityPosition.y();

        int entityCenterX = entityPosition.x() + entitySize / 2;
        int entityCenterY = entityPosition.y() + entitySize / 2;

        switch (facingDirection) {
            case UP:
                startX = entityCenterX - projectileSize / 2;
                startY = entityPosition.y() - projectileSize;
                break;
            case DOWN:
                startX = entityCenterX - projectileSize / 2;
                startY = entityPosition.y() + entitySize;
                break;
            case LEFT:
                startX = entityPosition.x() - projectileSize;
                startY = entityCenterY - projectileSize / 2;
                break;
            case RIGHT:
                startX = entityPosition.x() + entitySize;
                startY = entityCenterY - projectileSize / 2;
                break;
        }
        log.trace("Calculated projectile start position: ({}, {}) for entity {} facing {}", startX, startY, entityIdForLog, facingDirection);
        return new Position(startX, startY);
    }
}
