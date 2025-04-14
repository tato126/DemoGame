package com.example.demo.web;

import com.example.demo.core.GameService;
import com.example.demo.core.GameState;
import com.example.demo.core.user.domain.Direction;
import com.example.demo.core.user.domain.Player;
import com.example.demo.dto.GameStateUpdateMessage;
import com.example.demo.dto.MoveMessage;
import com.example.demo.dto.PlayerDTO;
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
            MoveMessage moveMessage = objectMapper.readValue(payload, MoveMessage.class);
            String type = moveMessage.getType();

            if ("move".equals(type)) {
                String playerId = moveMessage.getPlayerId(); // 클라이언트가 자신의 ID를 보낼도록 함
                String directionStr = moveMessage.getDirection();

                if (playerId == null || directionStr == null) {
                    log.warn("Invalid move message received (missing fields): {}", payload);
                    sendErrorMessage(session, "Move message requires 'playerId' and 'direction'.");
                    return;
                }

                Direction direction;

                try {
                    direction = Direction.valueOf(directionStr.toUpperCase());
                } catch (IllegalArgumentException exception) {
                    log.warn("Invalid direction value received: {}", directionStr);
                    sendErrorMessage(session, "Invalid direction value: " + directionStr);
                    return;
                }

                // GameService 호출하여 이동 처리
                // TODO: session과 playerId를 매핑하여 권한 검증 로직 추가 고려
                gameService.processPlayerMove(playerId, direction);

                // 변경된 게임 상태를 모든 클라이언트에게 브로드캐스트
                broadcastGameStateUpdate();

            } else {
                log.debug("Unknown message type received: {}", type);
                sendErrorMessage(session, "Unknown message type: " + type);
            }
        } catch (IOException exception) {
            log.error("Error handling message: {}", payload, exception);
            sendErrorMessage(session, "Error processing message: " + exception.getMessage());
        } catch (Exception exception) {
            log.error("Unexpected error handling message: {}", payload, exception);
            sendErrorMessage(session, "An unexpected error occurred.");
        }
    }

    private void sendGameStateUpdate(WebSocketSession session, Player playerToSend) throws IOException {
        PlayerDTO playerDTO = PlayerDTO.fromPlayer(playerToSend);
        // Player 객체가 null이 아닐 때만 메시지 생성 및 전송
        if (playerToSend != null) {
            GameStateUpdateMessage gameStateMessage = new GameStateUpdateMessage(playerDTO);
            String messageJson = objectMapper.writeValueAsString(gameStateMessage);
            session.sendMessage(new TextMessage(messageJson));
            log.debug("Send gameStateUpdate to {}: {}", session.getId(), messageJson);
        } else {
            log.warn("Player object is null, cannot send game state update to session: {}", session.getId());
        }
    }

    private void broadcastGameStateUpdate() {
        Player currentPlayer = gameState.getPlayer();
        PlayerDTO playerDTO = PlayerDTO.fromPlayer(currentPlayer);

        if (playerDTO != null) {
            GameStateUpdateMessage gameStateUpdateMessage = new GameStateUpdateMessage(playerDTO);

            try {
                String messageJson = objectMapper.writeValueAsString(gameStateUpdateMessage);
                log.debug("Broadcasting gameStateUpdate: {}", messageJson);
                for (WebSocketSession s : sessions.values()) {
                    if (s.isOpen()) {
                        s.sendMessage(new TextMessage(messageJson));
                    }
                }
            } catch (IOException exception) {
                log.error("Error broadcasting game state update", exception);
            }
        }
    }

    // 클라이언트에게 에러 메시지를 보내는 메서드
    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            // 간단한 에러 메시지 형식 정의
            Map<String, String> errorPayload = Map.of("type", "error", "message", errorMessage);
            String errorJson = objectMapper.writeValueAsString(errorPayload);
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(errorJson));
            }
        } catch (IOException exception) {
            log.error("Failed to send error message to session {}: {}", session.getId(), errorMessage, exception);
        }
    }


    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session.getId());
        log.debug("WebSocket connection closed: {} with status {}", session.getId(), status);
    }
}
