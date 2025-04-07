package io.github.Spyfall.model;

import java.util.ArrayList;
import java.util.List;

public class GameData {
    private boolean isSpy;
    private String location;
    private String role;
    private int currentRound = 1;
    private int totalRounds;
    private int timeRemaining;
    private List<String> possibleLocations = new ArrayList<>();
    
    public boolean isSpy() {
        return isSpy;
    }
    
    public void setSpy(boolean isSpy) {
        this.isSpy = isSpy;
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
    
    public int getCurrentRound() {
        return currentRound;
    }
    
    public void setCurrentRound(int currentRound) {
        this.currentRound = currentRound;
    }
    
    public int getTotalRounds() {
        return totalRounds;
    }
    
    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }
    
    public int getTimeRemaining() {
        return timeRemaining;
    }
    
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
    
    public List<String> getPossibleLocations() {
        return possibleLocations;
    }
    
    public void setPossibleLocations(List<String> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }
}
