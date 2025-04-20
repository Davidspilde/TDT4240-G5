package io.github.Spyfall.message.response;

import java.util.HashMap;

public class GameRoundEndedMessage extends ResponseMessage {
    private String spy;
    private HashMap<String, Integer> scoreboard;
    // new
    private String location;
    private int roundNumber;
    private String reason;
    private boolean spyCaught;
    private boolean spyGuessCorrect;

    public GameRoundEndedMessage() {

    }

    public HashMap<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(HashMap<String, Integer> scoreboard) {
        this.scoreboard = scoreboard;
    }

    public String getSpy() {
        return spy;
    }

    public void setSpy(String spy) {
        this.spy = spy;
    }

    // new
    public String getLocation() {
        return location;
    }
    
    public int getRoundNumber() {
        return roundNumber;
    }

    public String getReason() {
        return reason;
    }

    public boolean isSpyCaught() {
        return spyCaught;
    }

    public boolean isSpyGuessCorrect() {
        return spyGuessCorrect;
    }
}
