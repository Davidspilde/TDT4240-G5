package com.interloperServer.interloperServer.websocket;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.service.GameManagerService;
import com.interloperServer.interloperServer.service.GameService;
import com.interloperServer.interloperServer.service.LobbyService;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final MessageDispatcher dispatcher;
    private final LobbyService lobbyService;
    private final GameService gameService;
    private final GameManagerService gameManagerService;

    public GameWebSocketHandler(MessageDispatcher dispatcher, LobbyService lobbyService, GameService gameService,
            GameManagerService gameManagerService) {
        this.dispatcher = dispatcher;
        this.lobbyService = lobbyService;
        this.gameService = gameService;
        this.gameManagerService = gameManagerService;
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
        lobbyService.removeUser(session);

        // Check if the user was in a game
        for (String lobbyCode : gameManagerService.getAllGameCodes()) {
            gameService.handlePlayerDisconnect(session, lobbyCode);
        }
    }
}
