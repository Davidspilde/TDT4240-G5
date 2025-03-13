package com.interloperServer.interloperServer.model;

/**
 * Model for a chat message
 * Includes message content, lobby code, and username
 */
public class ChatMessage {
    private String content;
    private String lobbyCode;
    private String username;

    public ChatMessage() {}

    public ChatMessage(String content, String lobbyCode, String username) {
        this.content = content;
        this.lobbyCode = lobbyCode;
        this.username = username;
    }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getLobbyCode() { return lobbyCode; }
    public void setLobbyCode(String lobbyCode) { this.lobbyCode = lobbyCode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}

