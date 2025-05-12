package com.example.demo.web.dto;

import com.example.demo.domain.common.Direction;
import com.example.demo.domain.common.Position;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.weapon.Weapon;

public class PlayerDto {

    private String id;
    private int x;
    private int y;
    private int size;
    private int speed;
    private Weapon weapon;
    private Direction direction;


    public PlayerDto() {
    }

    public PlayerDto(String id, int x, int y, int size, int speed, Weapon weapon, Direction direction) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
        this.speed = speed;
        this.weapon = weapon;
        this.direction = direction;
    }


    public static PlayerDto fromPlayer(Player player) {
        if (player == null) {
            return null;
        }
        Position pos = player.getPosition();

        return new PlayerDto(
                player.getId().toString(),
                pos.x(),
                pos.y(),
                player.getPlayerSize(),
                player.getSpeed(),
                player.getEquippedWeapon(),
                player.getDirection()
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

    public int getSpeed() {
        return speed;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public Direction getDirection() {
        return direction;
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

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
