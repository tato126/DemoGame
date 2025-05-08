package com.example.demo.domain.enemy;

import com.example.demo.domain.common.AliveStatus;
import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.GameCalculationUtils;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.weapon.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Enemy {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EnemyId id;
    private final Position position;
    private final int size;
    private final int speed;
    private Weapon equippedWeapon;
    private final Direction direction;
    private final boolean isAlive;

    public Enemy(EnemyId id, Position position, int size, int speed, Direction direction, Weapon equippedWeapon, boolean isAlive) {
        this.id = Objects.requireNonNull(id, "[Enemy] Id must not be null.");
        this.position = Objects.requireNonNull(position, "[Enemy] Position must not be null.");
        this.size = size;
        this.speed = speed;
        this.direction = direction;
        this.equippedWeapon = equippedWeapon;
        this.isAlive = isAlive;
    }

    public Enemy updateStatus(AliveStatus newStatus) {

        Objects.requireNonNull(newStatus, "[Enemy] Alive Status must be not null");

        boolean newIsAlive = this.isAlive;

        switch (newStatus) {

            case ALIVE:
                newIsAlive = true;
                break;
            case DEAD:
                newIsAlive = false;
                break;
            case INVINCIBLE: // 지금은 다루지 않음
                newIsAlive = true;
                break;
        }

        if (this.isAlive != newIsAlive) {
            return new Enemy(this.id, this.position, this.size, this.speed, this.direction, this.equippedWeapon, newIsAlive);
        }

        return this;
    }

    public Enemy moveTo(Position position) {
        return new Enemy(this.id, position, this.size, this.speed, this.direction, this.equippedWeapon, this.isAlive);
    }

    public void fire(Direction projectileDirection) {
        if (equippedWeapon != null) {

            int projectileSize = 5; // 임시

            Position projectileStartPosition = GameCalculationUtils.calculateProjectileStartPosition(this.position, this.size, projectileSize, projectileDirection, this.id);

            equippedWeapon.shoot(this.id, projectileStartPosition, projectileDirection);
            log.debug("[Enemy] Enemy equippedWeapon shoot! : id {}, position {}, targetDir {}", this.id, this.position, projectileDirection);
        } else {
            log.debug("[Enemy] Enemy equippedWeapon is null");
        }
    }

    public void equipWeapon(Weapon newWeapon) {
        this.equippedWeapon = Objects.requireNonNull(newWeapon);
        log.debug("[Enemy] Enemy {} equipped {}", id, newWeapon.getClass().getSimpleName());
    }

    // Getter
    public EnemyId getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public Direction getDirection() {
        return direction;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public int getSpeed() {
        return speed;
    }

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Enemy enemy = (Enemy) obj;
        return id.equals(enemy.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Enemy{" +
                "id='" + id + '\'' +
                ", position=" + position +
                ", size=" + size +
                ", direction=" + direction +
                '}';
    }
}
