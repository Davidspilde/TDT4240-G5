package com.interloperServer.interloperServer.model;

import org.springframework.web.socket.WebSocketSession;

/**
 * Model for a player
 * Includes the websocket session, username, lobby role, and game role
 */
public class Player {
    private WebSocketSession session;
    private final String username;
    private boolean disconnected;

    public Player(WebSocketSession session, String username) {
        this.session = session;
        this.username = username;
        this.lobbyRole = lobbyRole;
        this.gameRole = null; // Game role assigned when game starts
        this.disconnected = false; // Default is connected
    }

    public WebSocketSession getSession() { return session; }
    public void setSession(WebSocketSession session) { this.session = session; } // Allow reconnections

    public String getUsername() { return username; }

    public boolean isDisconnected() { return disconnected; }
    public void setDisconnected(boolean disconnected) { this.disconnected = disconnected; }
}
