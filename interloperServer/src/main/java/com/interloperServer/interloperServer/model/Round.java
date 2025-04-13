package com.interloperServer.interloperServer.model;

import java.util.HashMap;
import java.util.Map;

public class Round {
    private final int roundNumber;
    private Location location;
    private boolean isActive;
    private Player spy;
    private int roundDuration;
    private boolean votingComplete;

    private final Map<String, String> votes = new HashMap<>(); // voterUsername -> targetUsername

    public Round(int roundNumber, int roundDuration, Player spy, Location location) {
        this.spy = spy;
        this.roundNumber = roundNumber;
        this.isActive = true;
        this.location = location;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;

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

    public int getRoundDuration() {
        return roundDuration;
    }

}
