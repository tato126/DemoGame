package com.example.demo.domain.enemy;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.weapon.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Enemy {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final EnemyId id;
    private final Position position;
    private final int size;
    private final Direction direction;
    private Weapon defaultWeapon;

    public Enemy(EnemyId id, Position position, int size, Direction direction, Weapon defaultWeapon) {
        this.id = Objects.requireNonNull(id, "[Enemy] Id must not be null.");
        this.position = Objects.requireNonNull(position, "[Enemy] Position must not be null.");
        this.size = size;
        this.direction = direction;
        this.defaultWeapon = defaultWeapon;
    }

    public Enemy moveTo(Position position) {
        return new Enemy(this.id, position, this.size, this.direction, this.defaultWeapon);
    }

    public void fire(Direction targetDirection) {
        if (defaultWeapon != null) {
            defaultWeapon.shoot(this.id, this.position, targetDirection);
            log.debug("[Enemy] Enemy defaultWeapon shoot! : id {}, position {}, targetDir {}", this.id, this.position, targetDirection);
        } else {
            log.debug("[Enemy] Enemy defaultWeapon is null");
        }
    }

    public void equipWeapon(Weapon newWeapon) {
        this.defaultWeapon = Objects.requireNonNull(newWeapon);
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

    public Weapon getDefaultWeapon() {
        return defaultWeapon;
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
