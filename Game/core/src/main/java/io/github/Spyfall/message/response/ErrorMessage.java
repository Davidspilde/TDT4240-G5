package io.github.Spyfall.message.response;

public class ErrorMessage extends ResponseMessage {
    private String message;

    public ErrorMessage() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
