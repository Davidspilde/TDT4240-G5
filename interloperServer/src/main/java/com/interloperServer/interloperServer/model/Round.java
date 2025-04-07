package com.interloperServer.interloperServer.model;

import java.util.HashMap;
import java.util.Map;

public class Round {
    private final int roundNumber;
    private final String location;
    private boolean isActive;
    private Player spy;
    private int roundDuration;
    private boolean votingComplete;

    private final Map<String, String> votes = new HashMap<>(); // voterUsername -> targetUsername

    public Round(int roundNumber, int roundDuration, Player spy) {
        this.spy = spy;
        this.roundNumber = roundNumber;
        this.location = generateRandomLocation();
        this.isActive = true;
        this.roundDuration = roundDuration;
        this.votingComplete = false;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public Player getSpy() {
        return spy;
    }

    public void setSpy(Player spy) {
        this.spy = spy;
    }

    public String getLocation() {
        return location;
    }

    public boolean isActive() {
        return isActive;
    }

    public void endRound() {
        this.isActive = false;
    }

    // Cast a vote from one player to another
    public void castVote(String voterUsername, String targetUsername) {
        votes.put(voterUsername, targetUsername);
    }

    public Map<String, String> getVotes() {
        return votes;
    }

    public void clearVotes() {
        votes.clear();
    }

    public void setVotingComplete() {
        votingComplete = true;
    }

    public boolean isVotingComplete() {
        return votingComplete;
    }

    // Create the location for this round
    private String generateRandomLocation() {
        String[] locations = { "Restaurant", "Museum", "Beach", "Space Station", "Jungle" };
        return locations[new java.util.Random().nextInt(locations.length)];
    }

    public int getRoundDuration() {
        return roundDuration;
    }

}
