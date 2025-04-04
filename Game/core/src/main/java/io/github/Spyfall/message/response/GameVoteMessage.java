package io.github.Spyfall.message.response;

public class GameVoteMessage extends ResponseMessage {
    private String voted;

    public GameVoteMessage() {

    }

    public String getVoted() {
        return voted;
    }

    public void setVoted(String voted) {
        this.voted = voted;
    }
}
