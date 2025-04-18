package com.interloperServer.interloperServer.model.messages.incoming;

/**
 * Represents an incoming message for casting a vote in the game.
 * <p>
 * The message contains the following field:
 * <ul>
 * <li><b>target</b>: The username of the player being voted for.</li>
 * </ul>
 * 
 * <p>
 * This class extends {@link RecieveMessage}, which includes the following
 * fields:
 * <ul>
 * <li><b>type</b>: The type of the message (e.g., "vote").</li>
 * <li><b>lobbyCode</b>: The code of the lobby associated with the message.</li>
 * <li><b>username</b>: The username of the player sending the message.</li>
 * </ul>
 */
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
