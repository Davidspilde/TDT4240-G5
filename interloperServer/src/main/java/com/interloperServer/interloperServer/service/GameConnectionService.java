package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

/**
 * Service for managing WebSocket connections for the game.
 * <p>
 * This service handles events related to WebSocket connections, such as when a
 * player connects or disconnects.
 * It interacts with the {@link GameService} to manage game-related actions
 * during these events.
 */
@Service
public class GameConnectionService {
    private GameService gameService;

    public GameConnectionService(GameService gameService) {
        this.gameService = gameService;
    }

    /**
     * Handles the event when a WebSocket connection is established.
     * <p>
     * Logs the connection event.
     *
     * @param session The {@link WebSocketSession} associated with the client.
     */
    public void onConnect(WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

    /**
     * Handles the event when a WebSocket connection is closed.
     * <p>
     * Retrieves the username and lobby code from the session attributes and
     * notifies the {@link GameService}
     * to handle the player's disconnection.
     *
     * @param session The {@link WebSocketSession} associated with the client.
     */
    public void onDisconnect(WebSocketSession session) {
        // Try to get username and lobby code from the session attributes
        String username = (String) session.getAttributes().get("username");
        String lobbyCode = (String) session.getAttributes().get("lobbyCode");

        System.out.println("Disconnect: username=" + username + ", lobbyCode=" + lobbyCode);

        // Check if the user was in a game
        if (lobbyCode != null) {
            gameService.handlePlayerDisconnect(session, lobbyCode);
        }
    }
}
