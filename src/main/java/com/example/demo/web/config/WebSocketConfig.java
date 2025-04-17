package com.example.demo.web.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private WebSocketHandler gameWebSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 클라이언트가 접속할 웹소켓 엔드포인트("/game-ws")와 처리할 핸들러 지정
        registry.addHandler(gameWebSocketHandler, "/game-ws")
                .setAllowedOrigins("*"); // 개발 편의상 모든 출처 허용 (배포 시 특정 출처 지정)
    }
}
