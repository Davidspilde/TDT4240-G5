package io.github.Spyfall.models.messages;

public class Message {

    protected String type;
    protected String lobbyCode;
    protected String username;

    public Message() {
    }

    public Message(String type, String username, String lobbyCode) {
        this.lobbyCode = lobbyCode;
        this.type = type;
        this.username = username;
    }
}
