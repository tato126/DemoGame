package com.example.demo.domain.enemy;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;

import java.util.Objects;

public class Enemy {

    private final EnemyId id;
    private final Position position;
    private final int size;
    private final Direction direction;

    public Enemy(EnemyId id, Position position, int size, Direction direction) {
        this.id = Objects.requireNonNull(id, "[Enemy] Id must be not null");
        this.position = Objects.requireNonNull(position, "[Enemy] Position must be not null");
        this.size = size;
        this.direction = direction;
    }

    public Enemy(EnemyId id, Position position, int size) {
        this(id, position, size, Direction.RIGHT);
    }

    public Enemy moveTo(Position position) {
        return new Enemy(this.id, position, this.size, this.direction);
    }

    public Enemy changeDirection(Direction newDirection) {
        return new Enemy(this.id, this.position, this.size, newDirection);
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
