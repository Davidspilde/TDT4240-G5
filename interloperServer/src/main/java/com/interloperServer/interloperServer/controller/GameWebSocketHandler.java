package com.interloperServer.interloperServer.controller;

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
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ChatMessage receivedMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
        String content = receivedMessage.getContent();

        // Creates a new lobby with the creator as host
        // Example message: {"content": "createLobby:Alice"}
        // Example response: Lobby Created! Code: a48465 (Host: Alice)
        if (content.startsWith("createLobby:")) {
            String username = content.split(":")[1];
            String lobbyCode = lobbyService.createLobby(session, username);
            session.sendMessage(new TextMessage("Lobby Created! Code: " + lobbyCode + " (Host: " + username + ")"));
            return;
        }

        // Tries to join an existing lobby if it exists
        // Example message: {"content": "joinLobby:a48465:Bob"}
        // Example response: Joined Lobby: a48465. Host: Alice
        if (content.startsWith("joinLobby:")) {
            String[] parts = content.split(":");
            String lobbyCode = parts[1];
            String username = parts[2];

            boolean success = lobbyService.joinLobby(session, lobbyCode, username);
            if (success) {
                session.sendMessage(new TextMessage("Joined Lobby: " + lobbyCode + ". Host: " + lobbyService.getLobbyHost(lobbyCode).getUsername()));
            } else {
                session.sendMessage(new TextMessage("Lobby Not Found!"));
            }
            return;
        }

        // Starts the game if sender is host
        // Example message: {"content": "startGame:a9b7f9", "username": "Alice"}
        // Example response: Game started!
        if (content.startsWith("startGame:")) {
            String lobbyCode = content.split(":")[1];
            String receivedUsername = receivedMessage.getUsername();
        
            System.out.println("Received username for starting game: " + receivedUsername);
            System.out.println("Expected host: " + lobbyService.getLobbyHost(lobbyCode).getUsername());
        
            boolean gameStarted = gameService.startGame(lobbyCode, receivedUsername, lobbyService);
            if (gameStarted) {
                session.sendMessage(new TextMessage("Game started!"));
            } else {
                session.sendMessage(new TextMessage("Only the host can start the game."));
            }
            return;
        }
        
    }

    // Removes player from lobbies when the connection is closed
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        lobbyService.removeUser(session);
    }
}
