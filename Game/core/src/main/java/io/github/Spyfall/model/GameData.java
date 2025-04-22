package io.github.Spyfall.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class GameData {
    private boolean isSpy;
    private String location;
    private String role;
    private int currentRound = 1;
    private int spyLastAttemptDuration = 45;
    private int totalRounds;
    private int timeRemaining;
    private List<Location> possibleLocations = new ArrayList<>();
    private Set<Location> greyedOutLocations = new HashSet<>();
    private boolean roundEnded;
    private String isSpyUsername;
    private HashMap<String, Integer> scoreboard;

    public boolean isRoundEnded() {
        return roundEnded;
    }

    public void setRoundEnded(boolean roundEnded) {
        this.roundEnded = roundEnded;
    }

    public String getIsSpyUsername() {
        return isSpyUsername;
    }

    public void setIsSpyUsername(String isSpyUsername) {
        this.isSpyUsername = isSpyUsername;
    }

    public HashMap<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(HashMap<String, Integer> scoreboard) {
        this.scoreboard = scoreboard;
    }
    
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
    
    public List<Location> getPossibleLocations() {
        return possibleLocations;
    }
    
    public void setPossibleLocations(List<Location> possibleLocations) {
        this.possibleLocations = possibleLocations;
    }

    public Set<Location> getGreyedOutLocations() {
        return greyedOutLocations;
    }

    public void toggleLocationGreyout(Location location) {
        if (greyedOutLocations.contains(location)) {
            greyedOutLocations.remove(location);
        } else {
            greyedOutLocations.add(location);
        }
    }

    /**
     * Clear all greyed out locations
     */
    public void clearGreyedOutLocations() {
        if (greyedOutLocations != null) {
            greyedOutLocations.clear();
        }
    }
    
    public int getSpyLastAttemptDuration() {
        return spyLastAttemptDuration;
    }
    
    public void setSpyLastAttemptDuration(int duration) {
        this.spyLastAttemptDuration = duration;
    }
}
