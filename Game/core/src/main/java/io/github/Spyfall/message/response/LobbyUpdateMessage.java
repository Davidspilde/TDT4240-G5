package io.github.Spyfall.message.response;

import java.util.List;

public class LobbyUpdateMessage extends ResponseMessage {
    private List<String> players;

    public LobbyUpdateMessage() {
    }

    public List<String> getPlayers() {
        return players;
    }

}
