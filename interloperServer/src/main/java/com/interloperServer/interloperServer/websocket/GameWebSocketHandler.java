package com.interloperServer.interloperServer.websocket;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final MessageDispatcher dispatcher;

    public GameWebSocketHandler(MessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        JsonNode root = new ObjectMapper().readTree(message.getPayload());
        dispatcher.dispatch(root, session);
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        // keep as-is
    }
}
