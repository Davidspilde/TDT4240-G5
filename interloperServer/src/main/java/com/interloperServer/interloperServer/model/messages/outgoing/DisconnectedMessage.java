package com.interloperServer.interloperServer.model.messages.outgoing;

public class DisconnectedMessage extends GameMessage {
    private String lobbyCode;
    private String username;

    public DisconnectedMessage(String lobbyCode, String username) {
        super("disconnected");
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
