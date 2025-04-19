package io.github.Spyfall.message.response;

public class ErrorMessage extends ResponseMessage{
    private String event;
    private String message;

    public ErrorMessage() {
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
} 