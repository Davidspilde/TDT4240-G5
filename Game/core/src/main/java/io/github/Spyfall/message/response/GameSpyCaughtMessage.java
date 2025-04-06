package io.github.Spyfall.message.response;

public class GameSpyCaughtMessage {
    private String spy;
    private int votes;

    public GameSpyCaughtMessage() {

    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public String getSpy() {
        return spy;
    }

    public void setSpy(String spy) {
        this.spy = spy;
    }
}
