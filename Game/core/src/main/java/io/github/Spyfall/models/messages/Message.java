package io.github.Spyfall.models.messages;

public class Message {

    private String type;
    private String lobbyCode;
    private String username;

    public Message() {
    }

    public Message(String type, String username, String lobbyCode) {
        this.lobbyCode = lobbyCode;
        this.type = type;
        this.username = username;
    }
}
