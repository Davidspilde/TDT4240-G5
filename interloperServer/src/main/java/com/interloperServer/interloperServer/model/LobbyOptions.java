package com.interloperServer.interloperServer.model;

public class LobbyOptions {

    private int roundLimit;
    private int locationNumber;
    private int maxPlayerCount;
    private int timePerRound;
    private int spyLastAttemptTime;

    public LobbyOptions(int roundLimit, int locationNumber, int maxPlayerCount, int timePerRound,
            int spyLastAttemptTime) {
        this.roundLimit = roundLimit;
        this.locationNumber = locationNumber;
        this.maxPlayerCount = maxPlayerCount;
        this.timePerRound = timePerRound;
        this.spyLastAttemptTime = spyLastAttemptTime;
    }

    public int getSpyLastAttemptTime() {
        return spyLastAttemptTime;
    }

    public void setSpyLastAttemptTime(int spyLastAttemptTime) {
        this.spyLastAttemptTime = spyLastAttemptTime;
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
