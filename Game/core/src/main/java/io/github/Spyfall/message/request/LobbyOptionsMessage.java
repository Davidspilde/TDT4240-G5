package io.github.Spyfall.message.request;

public class LobbyOptionsMessage extends RequestMessage {
    private int roundLimit;
    private int locationNumber;
    private int spyCount;
    private int maxPlayerCount;
    private int timePerRound;

    public LobbyOptionsMessage(String username, String lobbyCode, int roundLimit, int locationNumber,
            int maxPlayerCount, int timePerRound) {
        super("updateOptions", username, lobbyCode);
        this.roundLimit = roundLimit;
        this.locationNumber = locationNumber;
        this.spyCount = 1; // Default to 1 spy
        this.maxPlayerCount = maxPlayerCount;
        this.timePerRound = timePerRound;
    }
    
    public LobbyOptionsMessage(String username, String lobbyCode, int roundLimit, int locationNumber,
            int spyCount, int maxPlayerCount, int timePerRound) {
        super("updateOptions", username, lobbyCode);
        this.roundLimit = roundLimit;
        this.locationNumber = locationNumber;
        this.spyCount = spyCount;
        this.maxPlayerCount = maxPlayerCount;
        this.timePerRound = timePerRound;
    }
    
    public int getRoundLimit() {
        return roundLimit;
    }
    
    public void setRoundLimit(int roundLimit) {
        this.roundLimit = roundLimit;
    }
    
    public int getLocationNumber() {
        return locationNumber;
    }
    
    public void setLocationNumber(int locationNumber) {
        this.locationNumber = locationNumber;
    }
    
    public int getSpyCount() {
        return spyCount;
    }
    
    public void setSpyCount(int spyCount) {
        this.spyCount = spyCount;
    }
    
    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }
    
    public void setMaxPlayerCount(int maxPlayerCount) {
        this.maxPlayerCount = maxPlayerCount;
    }
    
    public int getTimePerRound() {
        return timePerRound;
    }
    
    public void setTimePerRound(int timePerRound) {
        this.timePerRound = timePerRound;
    }
}
