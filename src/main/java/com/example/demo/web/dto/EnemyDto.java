package com.example.demo.web.dto;

import com.example.demo.domain.common.Position;
import com.example.demo.domain.enemy.Enemy;

public class EnemyDto {

    private String id;
    private int x;
    private int y;
    private int size;


    public EnemyDto(String id, int x, int y, int size) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public static EnemyDto fromEnemy(Enemy enemy) {
        if (enemy == null) {
            return null;
        }

        Position pos = enemy.getPosition();
        return new EnemyDto(enemy.getId().toString(),
                pos.x(),
                pos.y(),
                enemy.getSize()
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
