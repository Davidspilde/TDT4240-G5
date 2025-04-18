package com.interloperServer.interloperServer.model.messages.incoming;

/**
 * Represents an incoming message for creating a lobby.
 * <p>
 * The message contains the following fields:
 * <ul>
 * <li><b>type</b>: The type of the message (e.g., "createLobby").</li>
 * <li><b>username</b>: The username of the player creating the lobby.</li>
 * </ul>
 */
public class RecieveCreateLobbyMessage {
    private String type;
    private String username;

    public RecieveCreateLobbyMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
