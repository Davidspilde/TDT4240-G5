package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class GameConnectionService {
    private GameService gameService;

    public GameConnectionService(GameService gameService) {
        this.gameService = gameService;
    }

    public void onConnect(WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

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
