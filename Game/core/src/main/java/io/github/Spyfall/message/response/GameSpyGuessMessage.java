package io.github.Spyfall.message.response;

public class GameSpyGuessMessage extends ResponseMessage {
    private String spy;
    private String location;

    public String getSpy() {
        return spy;
    }

    public void setSpy(String spy) {
        this.spy = spy;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
