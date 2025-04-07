package io.github.Spyfall.message.response;

public class LobbyJoinedMessage extends ResponseMessage {
    private String lobbyCode;
    private String host;
    private String username;

    public LobbyJoinedMessage() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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
        return "LobbyJoinedMessage{" +
                "lobbyCode='" + lobbyCode + '\'' +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
