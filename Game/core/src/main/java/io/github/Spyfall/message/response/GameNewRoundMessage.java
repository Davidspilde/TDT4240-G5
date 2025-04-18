package io.github.Spyfall.message.response;

public class GameNewRoundMessage extends ResponseMessage {
    private int roundNumber;
    private int roundDuration;
    private String location;
    private String role;
    private String firstQuestioner;

    public GameNewRoundMessage() {

    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public String getFirstQuestioner() {
        return this.firstQuestioner;
    }

    public void setRoundNumber(int roundNumber) {
        this.roundNumber = roundNumber;
    }

    public int getRoundDuration() {
        return roundDuration;
    }

    public void setRoundDuration(int roundDuration) {
        this.roundDuration = roundDuration;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
