package com.interloperServer.interloperServer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.interloperServer.interloperServer.websocket.GameWebSocketHandler;

/**
 * Configuration class for WebSocket support.
 * <p>
 * This class enables WebSocket functionality and registers WebSocket handlers
 * for specific endpoints.
 * <p>
 * Connect using: <code>ws://localhost:8080/ws/game</code>
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final GameWebSocketHandler gameWebSocketHandler;

    public WebSocketConfig(GameWebSocketHandler gameWebSocketHandler) {
        this.gameWebSocketHandler = gameWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(gameWebSocketHandler, "/ws/game").setAllowedOrigins("*");
    }
}
