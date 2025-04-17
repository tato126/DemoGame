package com.example.demo.web.dto;

import com.example.demo.domain.common.Position;
import com.example.demo.domain.player.Player;

public class PlayerDTO {
    private String id;
    private int x;
    private int y;
    private int size;


    public PlayerDTO() {
    }

    public PlayerDTO(String id, int x, int y, int size) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public static PlayerDTO fromPlayer(Player player) {
        if (player == null) {
            return null;
        }
        Position pos = player.getPosition();
        return new PlayerDTO(
                player.getId(),
                pos.x(), // record 필드 접근
                pos.y(), // record 필드 접근
                player.getSize()
        );
    }

    // Getter
    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    // Setter
    public void setId(String id) {
        this.id = id;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
