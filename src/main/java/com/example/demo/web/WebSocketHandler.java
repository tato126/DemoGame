package com.example.demo.web;

import com.example.demo.core.GameService;
import com.example.demo.core.GameState;
import com.example.demo.core.user.domain.Direction;
import com.example.demo.core.user.domain.Player;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final GameService gameService;
    private final GameState gameState; // GameState 초기화

    // 연결된 세션들 관리 (간단 버전, 멀티플레이어 시 개선 필요)
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();


    public WebSocketHandler(ObjectMapper objectMapper, GameService gameService, GameState gameState) {
        this.objectMapper = objectMapper;
        this.gameService = gameService;
        this.gameState = gameState; // GameState 초기화
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.debug("WebSocket connection established: {}", session.getId());

        Player player = gameService.initializeOrGetPlayer();

        // 최초 접속 시 현재 게임 상태 전송
        sendGameStateUpdate(session, player);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        log.debug("Received message from {}: {}", session.getId(), payload);

        try {
            Map<String, String> messageMap = objectMapper.readValue(payload, Map.class);
            String type = messageMap.get("type");

            if ("move".equals(type)) {
                String playerId = messageMap.get("playerId"); // 클라이언트가 자신의 ID를 보낼도록 함
                String directionStr = messageMap.get("direction");
                Direction direction = Direction.valueOf(directionStr.toUpperCase()); // 문자열을 Enum으로 변환


                // GameService 호출하여 이동 처리
                gameService.processPlayerMove(playerId, direction);

                // 변경된 게임 상태를 모든 클라이언트에게 브로드캐스트
                broadcastGameStateUpdate();
            } else {
                log.debug("Unknown message type received: {}", type);
            }
        } catch (Exception exception) {
            log.error("Error handling message: {}", payload, exception);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.put(session.getId(), session);
        log.debug("WebSocket connection closed: {} with status {}", session.getId(), status);
    }

    private void sendGameStateUpdate(WebSocketSession session, Player playerToSend) throws IOException {
        // Player 객체가 null이 아닐 때만 메시지 생성 및 전송
        if (playerToSend != null) {
            // DTO를 사용하는 것이 좋지만, 간단히 Map으로 구성
            Map<String, Object> playerState = Map.of(
                    "id", playerToSend.getId(), // playerToSend 에서 직접 ID를 가져옴
                    "x", playerToSend.getPosition().x(),
                    "y", playerToSend.getPosition().y(),
                    "size", playerToSend.getSize()
            );

            Map<String, Object> gameStateMessage = Map.of(
                    "type", "gameStateUpdate", "player", playerState
                    // 다른 게임 요소 상태 추가 가능
            );
            String messageJson = objectMapper.writeValueAsString(gameStateMessage);
            session.sendMessage(new TextMessage(messageJson));
            log.debug("Sent gameStateUpdate to {}: {}", session.getId(), messageJson);
        }
    }

    private void broadcastGameStateUpdate() {
        Player currentPlayer = gameState.getPlayer();
        if (currentPlayer != null) {
            Map<String, Object> playerState = Map.of(
                    "id", currentPlayer.getId(),
                    "x", currentPlayer.getPosition().x(),
                    "y", currentPlayer.getPosition().y(),
                    "size", currentPlayer.getSize()
            );
            Map<String, Object> gameStateMessage = Map.of(
                    "type", "gameStateUpdate",
                    "player", playerState
            );
            try {
                String messageJson = objectMapper.writeValueAsString(gameStateMessage);
                log.debug("Broadcasting gameStateUpdate: {}", messageJson);
                for (WebSocketSession session : sessions.values()) {
                    if (session.isOpen()) {
                        session.sendMessage(new TextMessage(messageJson));
                    }
                }
            } catch (IOException exception) {
                log.error("Error broadcasting game state update", exception);
            }
        }
    }
}
