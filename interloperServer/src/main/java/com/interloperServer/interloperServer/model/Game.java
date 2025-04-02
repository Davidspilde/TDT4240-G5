package com.interloperServer.interloperServer.model;

import java.time.Duration;
import java.util.*;

public class Game {
    private final List<Round> rounds;
    private final Map<String, Integer> scoreboard;
    private Lobby lobby;
    private boolean isActive;
    private int currentRoundIndex;

    private int roundDuration;

    private transient Timer roundTimer;

    public Game(Lobby lobby) {
        this.lobby = lobby;
        this.rounds = new ArrayList<>();
        this.scoreboard = new HashMap<>();
        this.isActive = true;
        this.currentRoundIndex = 0;
        this.roundDuration = lobby.getLobbyOptions().getTimePerRound();

        // Initialize the scoreboard (all players start with 0 points)
        for (Player player : getPlayers()) {
            scoreboard.put(player.getUsername(), 0);
        }

        // Create all rounds
        int totalRounds = lobby.getLobbyOptions().getRoundLimit();
        for (int i = 0; i < totalRounds; i++) {
            this.rounds.add(new Round(i + 1, roundDuration));
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public int getCurrentRoundIndex() {
        return currentRoundIndex;
    }

    public Round getCurrentRound() {
        return rounds.get(currentRoundIndex);
    }

    public boolean hasMoreRounds() {
        return currentRoundIndex < rounds.size() - 1;
    }

    public int getRoundDuration() {
        return lobby.getLobbyOptions().getTimePerRound();
    }

    public List<Player> getPlayers() {
        return lobby.getPlayers();
    }

    public void startNextRound() {
        if (currentRoundIndex < rounds.size() - 1) {
            currentRoundIndex++;
        } else {
            isActive = false; // End the game after all rounds
        }
    }

    public Map<String, Integer> getScoreboard() {
        return scoreboard;
    }

    public void updateScore(String username, int points) {
        scoreboard.put(username, scoreboard.getOrDefault(username, 0) + points);
    }

    public void setRoundTimer(Timer timer) {
        this.roundTimer = timer;
    }

    public Timer getRoundTimer() {
        return this.roundTimer;
    }
}
