package com.example.demo.domain.enemy;

import com.example.demo.domain.common.Position;

import java.util.Objects;

public class Enemy {

    private final String id;
    private final Position position;
    private final int size;

    public Enemy(String id, Position position, int size) {
        this.id = Objects.requireNonNull(id, "[Enemy] Id must be not null");
        this.position = Objects.requireNonNull(position, "[Enemy] Position must be not null");
        this.size = size;
    }

    public Enemy moveTo(Position position) {
        return new Enemy(this.id, position, this.size);
    }

    // Getter
    public String getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
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
                '}';
    }
}
