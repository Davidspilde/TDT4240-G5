package io.github.Spyfall.message.response;

import java.util.Map;

public class GameRoundEndedMessage extends ResponseMessage {
    private String spy;
    private Map<String, Integer> scoreboard;

    public GameRoundEndedMessage() {

    }

    public Map<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Map<String, Integer> scoreboard) {
        this.scoreboard = scoreboard;
    }

    public String getSpy() {
        return spy;
    }

    public void setSpy(String spy) {
        this.spy = spy;
    }
}
