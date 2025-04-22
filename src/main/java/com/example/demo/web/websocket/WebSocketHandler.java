package com.example.demo.web.websocket;

import com.example.demo.application.GameService;
import com.example.demo.domain.common.Direction;
import com.example.demo.domain.enemy.Enemy;
import com.example.demo.domain.enemy.application.EnemyFind;
import com.example.demo.domain.player.Player;
import com.example.demo.domain.player.PlayerId;
import com.example.demo.domain.player.application.PlayerFind;
import com.example.demo.web.dto.EnemyDTO;
import com.example.demo.web.dto.GameStateUpdateMessage;
import com.example.demo.web.dto.MoveMessage;
import com.example.demo.web.dto.PlayerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class WebSocketHandler extends TextWebSocketHandler {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;
    private final GameService gameService;

    private final PlayerFind playerFind;
    private final EnemyFind enemyFind;

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, PlayerId> sessionPlayerMap = new ConcurrentHashMap<>();

    public WebSocketHandler(ObjectMapper objectMapper, GameService gameService, PlayerFind playerFind, EnemyFind enemyFind) {
        this.objectMapper = objectMapper;
        this.gameService = gameService;
        this.playerFind = playerFind;
        this.enemyFind = enemyFind;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.debug("WebSocket connection established: {}", session.getId());

        gameService.resetGame();

        Player newPlayer = gameService.initializeOrGetPlayer();
        sessionPlayerMap.put(session.getId(), newPlayer.getId());

        gameService.spawnInitialEnemy();

        sendFullGameState(session);

        broadcastGameStateUpdate();
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

    private void sendFullGameState(WebSocketSession session) throws IOException {

        List<PlayerDTO> playerDTOs = playerFind.findAll().stream()
                .map(PlayerDTO::fromPlayer)
                .toList();

        List<EnemyDTO> enemyDTOs = enemyFind.findAll().stream()
                .map(EnemyDTO::fromEnemy)
                .toList();

        GameStateUpdateMessage gameStateUpdateMessage = new GameStateUpdateMessage(playerDTOs, enemyDTOs);
        String messageJson = objectMapper.writeValueAsString(gameStateUpdateMessage);

        if (session.isOpen()) {
            session.sendMessage(new TextMessage(messageJson));
            log.debug("[Send] Full game state to {}: {}", session.getId(), messageJson);
        }
    }

    public void broadcastGameStateUpdate() {
        List<PlayerDTO> playerDTOs = playerFind.findAll().stream()
                .map(PlayerDTO::fromPlayer)
                .toList();

        List<EnemyDTO> enemyDTOs = enemyFind.findAll().stream()
                .map(EnemyDTO::fromEnemy)
                .toList();

        GameStateUpdateMessage gameStateUpdateMessage = new GameStateUpdateMessage(playerDTOs, enemyDTOs);

        try {
            String messageJson = objectMapper.writeValueAsString(gameStateUpdateMessage);
            log.debug("Broadcasting game state update ({} players, {} enemies)", playerDTOs.size(), enemyDTOs.size());

            for (WebSocketSession s : sessions.values()) {
                if (s.isOpen()) {
                    try {
                        s.sendMessage(new TextMessage(messageJson));
                    } catch (IOException exception) {
                        log.error("Failed to send message to session {}: {}", s.getId(), exception.getMessage());
                        sessions.remove(s.getId());
                        sessionPlayerMap.remove(s.getId());
                    }
                } else {
                    sessions.remove(s.getId());
                    sessionPlayerMap.remove(s.getId());
                }
            }
        } catch (IOException exception) {
            log.error("Error broadcasting game state update", exception);
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
        PlayerId removedPlayerId = sessionPlayerMap.remove(session.getId());
        sessions.remove(session.getId());
        if (removedPlayerId != null) {
            gameService.removePlayer(removedPlayerId);
            log.debug("Player {} removed due to WebSocket connectiong closed: {} with status {}", removedPlayerId, session.getId(), status);
            broadcastGameStateUpdate();
        }
        log.debug("WebSocket connection closed: {} with status {}", session.getId(), status);
    }
}
