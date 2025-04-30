package com.example.demo.domain.projectile;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;

import java.util.Objects;

public class Projectile {

    private final ProjectileId id;
    private final Object ownerId;
    private final Position position;
    private final Direction direction;
    private final int size;
    private final int speed;

    public Projectile(ProjectileId id, Object ownerId, Position position, Direction direction, int size, int speed) {
        this.id = Objects.requireNonNull(id, "[Projectile] Id must be not null.");
        this.ownerId = Objects.requireNonNull(ownerId);
        this.position = Objects.requireNonNull(position, "[Projectile] Position must be not null.");
        this.direction = Objects.requireNonNull(direction);
        this.size = size;
        this.speed = speed;
    }

    public Projectile move() {
        Position nextPosition = direction.move(this.position, this.speed); // speed 만큼 이동한다? 다소 이상함
        return new Projectile(id, ownerId, nextPosition, direction, size, speed);
    }

    public ProjectileId getId() {
        return id;
    }

    public Object getOwnerId() {
        return ownerId;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return speed;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) return true;

        if (obj == null || getClass() != obj.getClass()) return false;

        Projectile projectile = (Projectile) obj;

        return id.equals(projectile.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Projectile{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", position=" + position +
                ", direction=" + direction +
                ", size=" + size +
                ", speed=" + speed +
                '}';
    }
}
