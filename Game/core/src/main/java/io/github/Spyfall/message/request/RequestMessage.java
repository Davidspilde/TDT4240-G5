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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "RequestMessage{type='" + type + "', username='" + username + 
               "', lobbyCode='" + lobbyCode + "'}";
    }
}
