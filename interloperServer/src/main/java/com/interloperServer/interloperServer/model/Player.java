package com.interloperServer.interloperServer.model;

import org.springframework.web.socket.WebSocketSession;

/**
 * Model for a player
 * Includes the websocket session, username, lobby role, and game role
 */
public class Player {
    private final WebSocketSession session;
    private final String username;
    private LobbyRole lobbyRole;
    private GameRole gameRole;

    public Player(WebSocketSession session, String username, LobbyRole lobbyRole) {
        this.session = session;
        this.username = username;
        this.lobbyRole = lobbyRole;
        this.gameRole = null; // Game role assigned when game starts
    }

    public WebSocketSession getSession() { return session; }
    public String getUsername() { return username; }
    public LobbyRole getLobbyRole() { return lobbyRole; }
    public void setLobbyRole(LobbyRole lobbyRole) { this.lobbyRole = lobbyRole; }

    public GameRole getGameRole() { return gameRole; }
    public void setGameRole(GameRole gameRole) { this.gameRole = gameRole; }
}
