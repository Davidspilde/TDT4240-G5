package com.interloperServer.interloperServer.websocket.handlers;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketMessageHandler<T> {
    String getType(); // e.g. "createLobby", "vote", etc.

    // which message class that should be used
    Class<T> getMessageClass();

    void handle(T message, WebSocketSession session);
}
