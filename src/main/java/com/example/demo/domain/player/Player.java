package com.example.demo.domain.player;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.weapon.Weapon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Player {

    private final Logger log = LoggerFactory.getLogger(getClass());

    // private final String name;
    private final PlayerId id;
    private final Position position;
    private final int size;
    private Weapon equippedWeapon;

    public Player(PlayerId id, Position position, int size, Weapon initialWeapon) {
        this.id = Objects.requireNonNull(id, "[Player] Id must be not null");
        this.position = Objects.requireNonNull(position, "[Player] Position must be not null");
        this.size = size;
        this.equippedWeapon = Objects.requireNonNull(initialWeapon, "[Player] Initial weapon must be not null");
    }

    public Player moveTo(Position position) {
        return new Player(this.id, position, this.size, this.equippedWeapon);
    }

    public void fire(Direction targetDirection) {
        if (equippedWeapon != null) {
            log.debug("현재 무기는 null이 아닙니다.");
            equippedWeapon.shoot(this.id, this.position, targetDirection);
        } else {
            log.debug("Player {} has no weapon equipped!", id);
        }
    }

    public void equipWeapon(Weapon newWeapon) {
        this.equippedWeapon = Objects.requireNonNull(newWeapon);
        log.debug("Player {} equipped {}", id, newWeapon.getClass().getSimpleName());
    }

    // Getter
    public PlayerId getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public Weapon getEquippedWeapon() {
        return equippedWeapon;
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
                ", size=" + size +
                ", equippedWeapon=" + equippedWeapon +
                '}';
    }
}
