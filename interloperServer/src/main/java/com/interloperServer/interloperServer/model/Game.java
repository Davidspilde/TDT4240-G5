package com.interloperServer.interloperServer.model;

import java.util.*;

public class Game {
    private final String lobbyCode;
    private final List<Player> players;
    private final List<Round> rounds;
    private final Map<String, Integer> scoreboard; 
    private boolean isActive;
    private int currentRoundIndex;

    public Game(String lobbyCode, List<Player> players, int totalRounds) {
        this.lobbyCode = lobbyCode;
        this.players = new ArrayList<>(players);
        this.rounds = new ArrayList<>();
        this.scoreboard = new HashMap<>();
        this.isActive = true;
        this.currentRoundIndex = 0;

        // Initialize the scoreboard (all players start with 0 points)
        for (Player player : players) {
            scoreboard.put(player.getUsername(), 0);
        }

        // Create all rounds
        for (int i = 0; i < totalRounds; i++) {
            this.rounds.add(new Round(i + 1));
        }
    }

    public String getLobbyCode() { return lobbyCode; }
    public boolean isActive() { return isActive; }
    public int getCurrentRoundIndex() { return currentRoundIndex; }
    public Round getCurrentRound() { return rounds.get(currentRoundIndex); }
    public List<Player> getPlayers() { return players; }
    public Map<String, Integer> getScoreboard() { return scoreboard; }

    public void startNextRound() {
        if (currentRoundIndex < rounds.size() - 1) {
            currentRoundIndex++;
        } else {
            isActive = false; // End the game after all rounds
        }
    }

    public void updateScore(String username, int points) {
        scoreboard.put(username, scoreboard.getOrDefault(username, 0) + points);
    }
}
