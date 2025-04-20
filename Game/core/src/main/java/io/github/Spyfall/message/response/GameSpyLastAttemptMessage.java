package io.github.Spyfall.message.response;

public class GameSpyLastAttemptMessage extends ResponseMessage {
    private String spy;
    private int lastAttemptDuration;

    public GameSpyLastAttemptMessage() {

    }

    public int getLastAttemptDuration() {
        return lastAttemptDuration;
    }

    public String getSpy() {
        return spy;
    }

}
