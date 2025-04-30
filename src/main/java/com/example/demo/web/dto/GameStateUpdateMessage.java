package com.example.demo.web.dto;

import java.util.List;

public class GameStateUpdateMessage {

    private String type = "gameStateUpdate"; // 타입 고정
    private List<PlayerDto> players;
    private List<EnemyDto> enemies;
    private List<ProjectileDto> projectiles;

    public GameStateUpdateMessage() {
    }

    public GameStateUpdateMessage(List<PlayerDto> players, List<EnemyDto> enemies, List<ProjectileDto> projectiles) {
        this.players = players;
        this.enemies = enemies;
        this.projectiles = projectiles;
    }


    // Getter
    public String getType() {
        return type;
    }

    public List<PlayerDto> getPlayers() {
        return players;
    }

    public List<EnemyDto> getEnemies() {
        return enemies;
    }

    public List<ProjectileDto> getProjectiles() {
        return projectiles;
    }

    // Setter
    public void setType(String type) {
        this.type = type;
    }

    public void setPlayers(List<PlayerDto> players) {
        this.players = players;
    }

    public void setEnemies(List<EnemyDto> enemies) {
        this.enemies = enemies;
    }

    public void setProjectiles(List<ProjectileDto> projectiles) {
        this.projectiles = projectiles;
    }
}
