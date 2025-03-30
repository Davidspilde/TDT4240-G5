package com.interloperServer.interloperServer.model.messages;

public class VoteMessage extends Message {
    private String target;

    public VoteMessage() {
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
