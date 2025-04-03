package io.github.Spyfall.models.messages;

public class CreateLobbyMessage {
    private String username;
    private String type;

    public CreateLobbyMessage(String username) {

        // the type for this message will always be createLobby
        this.type = "createLobby";
        this.username = username;
    }
}
