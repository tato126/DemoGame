package com.example.demo.web.dto;

public class MoveMessage {

    private String type;
    private String playerId;
    private String direction;

    // 기본 생성자 (Jackson 파싱 시 필요할 수 있음)
    public MoveMessage() {}

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
