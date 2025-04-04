package io.github.Spyfall.message.response;

public class LobbyNewHostMessage extends ResponseMessage {
    private String host;

    public LobbyNewHostMessage() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

}
