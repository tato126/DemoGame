package com.example.demo.dto;

import java.util.List;

public class GameStateUpdateMessage {

    private String type = "gameStateUpdate"; // 타입 고정
    private PlayerDTO player;

    // 난중에 다른 게임 요소 (enemies, item 등) 리스트 추가 가능
    private EnemyDTO enemy;

    public GameStateUpdateMessage() {
    }

    public GameStateUpdateMessage(PlayerDTO player, EnemyDTO enemy) {
        this.player = player;
        this.enemy = enemy;
    }

    // Getter
    public String getType() {
        return type;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public EnemyDTO getEnemy() {
        return enemy;
    }

    // Setter
    public void setType(String type) {
        this.type = type;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }

    public void setEnemy(EnemyDTO enemy) {
        this.enemy = enemy;
    }
}
