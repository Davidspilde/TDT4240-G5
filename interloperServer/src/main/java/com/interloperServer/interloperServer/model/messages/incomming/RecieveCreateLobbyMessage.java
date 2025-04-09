package com.interloperServer.interloperServer.model.messages.incomming;

public class RecieveCreateLobbyMessage {
    private String type;
    private String username;

    public RecieveCreateLobbyMessage() {
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
