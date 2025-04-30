package com.example.demo.web.dto;

public class ShotMessage {

    private String type;
    private String playerId;
    private String direction;

    // 기본 생성자
    public ShotMessage() {
    }

    // Getter
    public String getType() {
        return type;
    }

    public String getPlayerId() {
        return playerId;
    }

    public String getDirection() {
        return direction;
    }

    // Setter
    public void setType(String type) {
        this.type = type;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
