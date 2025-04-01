package com.example.demo.gameObject;

import com.example.demo.domain.Position;

/**
 * 유저 클래스.
 *
 * @author chan
 */
public class Player {

    private final Position position;

    int step = 10; // 유저의 이동 거리.
    int size = 10; // 유저의 크기.
    int hp = 3; // 유저의 체력.

    public Player(Position position) {
        this.position = position;
    }

    public Player(Position position, int hp) {
        this.position = position;
        this.hp = hp;
    }


    public Player move(String direction) {

        int dx = 0;
        int dy = 0;

        switch (direction) {
            case "up" -> dy = -step;
            case "down" -> dy = step;
            case "left" -> dx = -step;
            case "right" -> dx = step;
        }

        return new Player(position.move(dx, dy), hp);
    }

    public void reduceHp() {
        if (hp > 0) {
            hp--;
        }
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public int getHp() {
        return hp;
    }
}
