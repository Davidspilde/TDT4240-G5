package io.github.Spyfall.model;

import java.util.ArrayList;
import java.util.List;

public class LobbyData {
    // Initializes with base values
    private List<String> players = new ArrayList<>();
    private String hostPlayer;
    private int roundLimit = 6;
    private int locationCount;
    private int maxPlayers = 8;
    private int timePerRound = 600;
    private int locationLimit = 30;
    private int spyLastAttemptTime = 45;

    public int getLocationLimit() {
        return locationLimit;
    }

    public void setLocationLimit(int locationLimit) {
        this.locationLimit = locationLimit;
    }

    public int getSpyLastAttemptTime() {
        return spyLastAttemptTime;
    }

    public void setSpyLastAttemptTime(int spyLastAttemptTime) {
        this.spyLastAttemptTime = spyLastAttemptTime;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String player) {
        this.players.add(player);
    }

    public void removePlayer(String player) {
        this.players.remove(player);
    }

    public String getHostPlayer() {
        return hostPlayer;
    }

    public void setHostPlayer(String hostPlayer) {
        this.hostPlayer = hostPlayer;
    }

    public int getRoundLimit() {
        return roundLimit;
    }

    public void setRoundLimit(int roundLimit) {
        this.roundLimit = roundLimit;
    }

    public int getLocationCount() {
        return locationCount;
    }

    public void setLocationCount(int locationCount) {
        this.locationCount = locationCount;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public int getTimePerRound() {
        return timePerRound;
    }

    public void setTimePerRound(int timePerRound) {
        this.timePerRound = timePerRound;
    }
}
