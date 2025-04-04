package io.github.Spyfall.message.request;

public class VoteMessage extends RequestMessage {
    private String target;

    public VoteMessage(String type, String username, String target, String lobbyCode) {
        super(type, username, lobbyCode);
        this.target = target;
    }
}
