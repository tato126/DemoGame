package com.example.demo.dto;

public class GameStateUpdateMessage {

    private String type = "gameStateUpdate"; // 타입 고정
    private PlayerDTO player;

    // 난중에 다른 게임 요소 (enemies, item 등) 리스트 추가 가능
    // private List<EnemyDTO> enemiss;

    public GameStateUpdateMessage() {
    }

    public GameStateUpdateMessage(PlayerDTO player) {
        this.player = player;
    }

    // Getter
    public String getType() {
        return type;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    // Setter
    public void setType(String type) {
        this.type = type;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }
}
