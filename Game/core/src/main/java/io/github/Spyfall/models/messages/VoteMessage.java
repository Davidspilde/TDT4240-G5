package io.github.Spyfall.models.messages;

public class VoteMessage extends Message {
    private String target;

    public VoteMessage(String type, String username, String target, String lobbyCode) {
        super(type, username, lobbyCode);
        this.target = target;
    }
}
