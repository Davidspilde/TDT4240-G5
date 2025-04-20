package com.interloperServer.interloperServer.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a single round in the game.
 * <p>
 * This class manages the state of a round, including the spy, location,
 * duration,
 * votes, and the current round state. It provides methods for managing votes,
 * transitioning round states, and determining if the round is active or voting
 * is complete.
 */
public class Round {

    private final int roundNumber;
    private Location location;
    private Player spy;
    private int roundDuration;
    private RoundState roundState;

    private final Map<String, String> votes = new HashMap<>(); // voterUsername -> targetUsername

    public Round(int roundNumber, int roundDuration, Player spy, Location location) {
        this.spy = spy;
        this.roundNumber = roundNumber;
        this.location = location;
        this.roundDuration = roundDuration;
        this.roundState = RoundState.NORMAL;
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

    public RoundState getRoundState() {
        return roundState;
    }

    public boolean isActive() {
        if (roundState == RoundState.ENDED) {
            return false;
        }
        return true;
    }

    public void endRound() {
        roundState = RoundState.ENDED;
    }

    public void setSpyLastAttempt() {
        roundState = RoundState.SPY_LAST_ATTEMPT;
    }

    public boolean isVotingComplete() {
        if (roundState != RoundState.NORMAL) {
            return true;
        }
        return false;
    }

    /**
     * Cast a vote from one player to another
     */
    public void castVote(String voterUsername, String targetUsername) {
        votes.put(voterUsername, targetUsername);
    }

    public Map<String, String> getVotes() {
        return votes;
    }

    public void clearVotes() {
        votes.clear();
    }

    public int getRoundDuration() {
        return roundDuration;
    }

}
