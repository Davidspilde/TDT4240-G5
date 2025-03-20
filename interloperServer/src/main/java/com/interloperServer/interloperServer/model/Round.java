package com.interloperServer.interloperServer.model;

import java.util.HashMap;
import java.util.Map;

public class Round {
    private final int roundNumber;
    private final String location;
    private boolean isActive;

    private final Map<String, Integer> votes = new HashMap<>(); // Tracks votes (username -> count)

    public Round(int roundNumber) {
        this.roundNumber = roundNumber;
        this.location = generateRandomLocation();
        this.isActive = true;
    }

    public int getRoundNumber() { return roundNumber; }
    public String getLocation() { return location; }
    public boolean isActive() { return isActive; }

    public void endRound() { this.isActive = false; }

    // Voting Methods
    public void castVote(String targetUsername) {
        votes.put(targetUsername, votes.getOrDefault(targetUsername, 0) + 1);
    }

    public Map<String, Integer> getVotes() {
        return votes;
    }
    
    public void clearVotes() {
        votes.clear();
    }

    private String generateRandomLocation() {
        String[] locations = {"Restaurant", "Museum", "Beach", "Space Station", "Jungle"};
        return locations[new java.util.Random().nextInt(locations.length)];
    }

}
