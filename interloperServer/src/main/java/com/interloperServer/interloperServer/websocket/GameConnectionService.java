package com.interloperServer.interloperServer.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.service.GameManagerService;
import com.interloperServer.interloperServer.service.GameService;
import com.interloperServer.interloperServer.service.LobbyManagerService;

@Service
public class GameConnectionService {
    private LobbyManagerService lobbyManager;
    private GameManagerService gameManager;
    private GameService gameService;

    public GameConnectionService(LobbyManagerService lobbyManager, GameManagerService gameManager,
            GameService gameService) {
        this.lobbyManager = lobbyManager;
        this.gameManager = gameManager;
        this.gameService = gameService;
    }

    public void onConnect(WebSocketSession session) {
        System.out.println("WebSocket connected: " + session.getId());
    }

    public void onDisconnect(WebSocketSession session) {

        // Check if the user was in a game
        for (String lobbyCode : gameManager.getAllGameCodes()) {
            gameService.handlePlayerDisconnect(session, lobbyCode);
        }
        lobbyManager.removeUser(session);

    }
}
