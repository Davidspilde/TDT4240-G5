package io.github.Spyfall.message.response;

public class LobbyJoinedMessage extends ResponseMessage {
    private String lobbyCode;
    private String host;

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

}
