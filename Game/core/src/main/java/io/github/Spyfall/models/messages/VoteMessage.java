package io.github.Spyfall.models.messages;

public class VoteMessage extends Message {
    private String target;

    public VoteMessage(String target) {
        this.target = target;
    }

}
