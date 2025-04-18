package com.example.demo.core.user.domain.player;

import com.example.demo.core.Position;

import java.util.Objects;

public class Player {

    private final String id;
    private final Position position;
    private final int size;


    public Player(String id, Position position, int size) {
        this.id = Objects.requireNonNull(id, "[Player] Id must be not null");
        this.position = Objects.requireNonNull(position, "[Player] Position must be not null");
        this.size = size;
    }

    public Player moveTo(Position position) {
        return new Player(this.id, position, this.size);
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
                "id='" + id + '\'' +
                ", position=" + position +
                ", size=" + size +
                '}';
    }
}
