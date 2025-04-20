package io.github.Spyfall.model;

import java.util.ArrayList;
import java.util.List;

public class LobbyData {
    private List<String> players = new ArrayList<>();
    private String hostPlayer;
    private int roundLimit = 3;
    private int locationCount = 10;
    private int maxPlayers = 8;
    private int timePerRound = 180; // seconds
    
    public List<String> getPlayers() {
        return players;
    }
    
    public void setPlayers(List<String> players) {
        this.players = players;
    }
    
    public void addPlayer(String player) {
        this.players.add(player);
    }

    public void removePlayer(String player){
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
