package com.interloperServer.interloperServer.model.messages.incoming;

/**
 * Represents a generic incoming message received by the server.
 * <p>
 * This class contains common fields that are shared across different types of
 * incoming messages:
 * <ul>
 * <li><b>type</b>: The type of the message (e.g., "createLobby",
 * "updateLobbyOptions").</li>
 * <li><b>lobbyCode</b>: The code of the lobby associated with the message.</li>
 * <li><b>username</b>: The username of the player sending the message.</li>
 * </ul>
 * 
 * <p>
 * This class serves as a base class for more specific message types, which
 * extend this class to include additional fields.
 */
public class ReceiveMessage {
    private String type;
    private String lobbyCode;
    private String username;

    public ReceiveMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
