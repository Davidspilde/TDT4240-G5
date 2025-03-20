package com.interloperServer.interloperServer.controller;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.ChatMessage;
import com.interloperServer.interloperServer.service.GameService;
import com.interloperServer.interloperServer.service.LobbyService;

/**
 * Receives all messages sent to /ws/game and delegates to the appropriate services
 */
@Component
public class GameWebSocketHandler extends TextWebSocketHandler {
    private final GameService gameService;
    private final LobbyService lobbyService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public GameWebSocketHandler(GameService gameService, LobbyService lobbyService) {
        this.gameService = gameService;
        this.lobbyService = lobbyService;
    }

    /**
     * Reads message and delegates to service
     */
    @Override
    protected void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) throws Exception {
        ChatMessage receivedMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        String content = receivedMessage.getContent();

        if (content.startsWith("createLobby:")) {
            String username = content.split(":")[1].trim();
            lobbyService.createLobby(session, username);
            return;
        }

        if (content.startsWith("joinLobby:")) {
            String[] parts = content.split(":");
            String lobbyCode = parts[1].trim();
            String username = parts[2].trim();
            lobbyService.joinLobby(session, lobbyCode, username);
            return;
        }

        if (content.startsWith("startGame:")) {
            String lobbyCode = content.split(":")[1].trim();
            String receivedUsername = receivedMessage.getUsername();
            gameService.startGame(lobbyCode, receivedUsername, lobbyService, session);
            return;
        }        
    }

    /**
     * Removes player from lobby and game when they disconnect
     */
    @Override
    public void afterConnectionClosed(@NonNull WebSocketSession session, @NonNull CloseStatus status) {
        lobbyService.removeUser(session);

        // Check if the user was in a game
        for (String lobbyCode : gameService.getActiveGames().keySet()) {
            gameService.handlePlayerDisconnect(session, lobbyCode);
        }
    }

}
