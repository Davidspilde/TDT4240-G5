package com.interloperServer.interloperServer.service;

import java.time.Duration;

public class LobbyOptions {

    private int roundLimit;
    private int locationNumber;
    private int spyCount;
    private int maxPlayerCount;
    private int timePerRound;

    public LobbyOptions(int roundLimit, int locationNumber, int spyCount, int maxPlayerCount, int timePerRound) {
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
