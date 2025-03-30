package com.interloperServer.interloperServer.model;

import java.util.*;

public class Game {
    private final String lobbyCode;
    private final List<Player> players;
    private final List<Round> rounds;
    private final Map<String, Integer> scoreboard;
    private boolean isActive;
    private int currentRoundIndex;

    private int roundDuration;

    private transient Timer roundTimer;

    public Game(String lobbyCode, List<Player> players, int totalRounds, int roundDuration) {
        this.lobbyCode = lobbyCode;
        this.players = new ArrayList<>(players);
        this.rounds = new ArrayList<>();
        this.scoreboard = new HashMap<>();
        this.isActive = true;
        this.currentRoundIndex = 0;
        this.roundDuration = roundDuration;

        // Initialize the scoreboard (all players start with 0 points)
        for (Player player : players) {
            scoreboard.put(player.getUsername(), 0);
        }

        // Create all rounds
        for (int i = 0; i < totalRounds; i++) {
            this.rounds.add(new Round(i + 1, roundDuration));
        }
    }

    public String getLobbyCode() {
        return lobbyCode;
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
        return roundDuration;
    }

    public List<Player> getPlayers() {
        return players;
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
