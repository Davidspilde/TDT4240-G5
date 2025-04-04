package io.github.Spyfall.message.request;

public class RequestMessage {

    protected String type;
    protected String lobbyCode;
    protected String username;

    public RequestMessage() {
    }

    public RequestMessage(String type, String username, String lobbyCode) {
        this.lobbyCode = lobbyCode;
        this.type = type;
        this.username = username;
    }
}
