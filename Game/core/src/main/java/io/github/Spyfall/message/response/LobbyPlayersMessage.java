package io.github.Spyfall.message.response;

import java.util.List;

public class LobbyPlayersMessage extends ResponseMessage {
    private List<String> players;

    public LobbyPlayersMessage() {

    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

}
