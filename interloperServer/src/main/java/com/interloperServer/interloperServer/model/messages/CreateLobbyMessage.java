package com.interloperServer.interloperServer.model.messages;

public class CreateLobbyMessage {
    private String type;
    private String username;

    public CreateLobbyMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
