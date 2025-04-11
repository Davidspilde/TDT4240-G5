package com.interloperServer.interloperServer.model.messages.outgoing;

public class ReconnectedMessage extends GameMessage {
    private String lobbyCode;
    private String username;

    public ReconnectedMessage(String lobbyCode, String username) {
        super("reconnected");
        this.lobbyCode = lobbyCode;
        this.username = username;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public String getUsername() {
        return username;
    }

}
