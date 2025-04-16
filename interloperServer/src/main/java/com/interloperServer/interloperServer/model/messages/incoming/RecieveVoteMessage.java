package com.interloperServer.interloperServer.model.messages.incoming;

public class RecieveVoteMessage extends RecieveMessage {
    private String target;

    public RecieveVoteMessage() {
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
