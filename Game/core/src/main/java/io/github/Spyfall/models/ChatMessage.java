package io.github.Spyfall.models;

public class ChatMessage {

    private String content;
    private String username;

    public ChatMessage() {
    }

    public ChatMessage(String content, String username) {
        this.content = content;
        this.username = username;
    }

    // Optional: getters and setters (depending on Gson config)
}
