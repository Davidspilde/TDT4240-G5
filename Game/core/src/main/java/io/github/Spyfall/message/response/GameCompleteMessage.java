package io.github.Spyfall.message.response;

import java.util.HashMap;

public class GameCompleteMessage extends ResponseMessage {
    private HashMap<String, Integer> scoreboard;

    public GameCompleteMessage() {

    }

    public HashMap<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(HashMap<String, Integer> scoreboard) {
        this.scoreboard = scoreboard;
    }

}
