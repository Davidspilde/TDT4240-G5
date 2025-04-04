package io.github.Spyfall.message.response;

public class GameCompleteMessage extends ResponseMessage {
    private String scoreboard;

    public GameCompleteMessage() {

    }

    public String getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(String scoreboard) {
        this.scoreboard = scoreboard;
    }

}
