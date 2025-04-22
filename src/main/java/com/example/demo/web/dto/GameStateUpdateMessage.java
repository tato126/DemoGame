package com.example.demo.web.dto;

import com.example.demo.domain.player.Player;

import java.util.List;

public class GameStateUpdateMessage {

    private String type = "gameStateUpdate"; // 타입 고정
    private List<PlayerDTO> players;
    private List<EnemyDTO> enemies;

    public GameStateUpdateMessage() {
    }

    public GameStateUpdateMessage(List<PlayerDTO> players, List<EnemyDTO> enemies) {
        this.players = players;
        this.enemies = enemies;
    }


    // Getter
    public String getType() {
        return type;
    }

    public List<PlayerDTO> getPlayers() {
        return players;
    }

    public List<EnemyDTO> getEnemies() {
        return enemies;
    }

    // Setter
    public void setType(String type) {
        this.type = type;
    }

    public void setPlayers(List<PlayerDTO> players) {
        this.players = players;
    }

    public void setEnemies(List<EnemyDTO> enemies) {
        this.enemies = enemies;
    }
}
