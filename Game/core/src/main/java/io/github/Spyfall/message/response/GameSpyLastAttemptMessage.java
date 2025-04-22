package io.github.Spyfall.message.response;

public class GameSpyLastAttemptMessage extends ResponseMessage {
    private String spyUsername;
    private int lastAttemptDuration;

    public GameSpyLastAttemptMessage() {

    }

    public int getLastAttemptDuration() {
        return lastAttemptDuration;
    }

    public String getSpyUsername() {
        return spyUsername;
    }

}
