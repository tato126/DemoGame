package com.example.demo.domain.player;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.GameCalculationUtils;
import com.example.demo.domain.common.AliveStatus;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.weapon.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Player {

    private final Logger log = LoggerFactory.getLogger(getClass());

    //     private final String name;
    private final PlayerId id;
    private final Position position;
    private final int playerSize;
    private final int speed;
    private final Weapon equippedWeapon;
    private final Direction direction;
    private final boolean isAlive;

    public Player(PlayerId id, Position position, int playerSize, int speed, Weapon initialWeapon, Direction direction, boolean isAlive) {
        this.id = Objects.requireNonNull(id, "[Player] Id must be not null");
        this.position = Objects.requireNonNull(position, "[Player] Position must be not null");
        this.playerSize = playerSize;
        this.speed = speed;
        this.equippedWeapon = Objects.requireNonNull(initialWeapon, "[Player] Initial weapon must be not null");
        this.direction = Objects.requireNonNull(direction, "[Player] Initial weapon must be not null");
        this.isAlive = isAlive;
    }

    public Player updateStatus(AliveStatus newStatus) {
        Objects.requireNonNull(newStatus, "[Player] New PlayerStatus must be not null");

        boolean newIsAlive = this.isAlive;

        switch (newStatus) {
            case ALIVE:
                newIsAlive = true;
                break;
            case DEAD:
                newIsAlive = false;
                break;
            case INVINCIBLE: // 현재는 다루지 않음
                newIsAlive = true;
                break;
        }

        if (this.isAlive != newIsAlive) {
            return new Player(this.id, this.position, this.playerSize, this.speed, this.equippedWeapon, this.direction, newIsAlive);
        }
        return this;
    }

    public Player moveTo(Position position, Direction newFacingDirection) {
        log.debug("현재 방향: {} , 새로운 방향: {}", this.direction, newFacingDirection);
        return new Player(this.id, position, this.playerSize, this.speed, this.equippedWeapon, newFacingDirection, this.isAlive);
    }

    // 차후 이동하지 않고 방향만 바꿀 때 * 지금은 사용하지 않음*
    public Player moveTo(Direction newDirection) {
        return new Player(this.id, this.position, this.playerSize, this.speed, this.equippedWeapon, newDirection, this.isAlive);
    }

    public void fire(Direction projectileDirection) {
        if (equippedWeapon != null) {
            log.debug("현재 무기는 null이 아닙니다.");

            int projectileSize = 5; // 임시 실제로는 장착 중인 무기의 투사체 크기를 가져와야 함

            Position projectileStartPosition = GameCalculationUtils.calculateProjectileStartPosition(this.position, this.playerSize, projectileSize, this.direction, this.id);

            equippedWeapon.shoot(this.id, projectileStartPosition, projectileDirection);
        } else {
            log.debug("Player {} has no weapon equipped!", id);
        }
    }

    public Player equipWeapon(Weapon newWeapon) {
        Objects.requireNonNull(newWeapon);
        log.debug("Player {} equipped {}", id, newWeapon.getClass().getSimpleName());
        return new Player(this.id, position, this.playerSize, this.speed, newWeapon, this.direction, this.isAlive);
    }

    // Getter
    public PlayerId getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public int getPlayerSize() {
        return playerSize;
    }

    public int getSpeed() {
        return speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
    }

    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Player player = (Player) obj;
        return id.equals(player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", position=" + position +
                ", playerSize=" + playerSize +
                ", speed=" + speed +
                ", equippedWeapon=" + equippedWeapon +
                ", direction=" + direction +
                ", isAlive=" + isAlive +
                '}';
    }
}
