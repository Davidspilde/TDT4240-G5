package com.interloperServer.interloperServer.model;

import java.util.*;

public class Game {
    private final Map<String, Integer> scoreboard;
    private Lobby lobby;
    private boolean isActive;
    private int currentRoundIndex;
    private int roundLimit;
    private Round currentRound;

    private transient Timer roundTimer;

    public Game(Lobby lobby) {
        this.lobby = lobby;
        this.scoreboard = new HashMap<>();
        this.isActive = true;
        this.currentRoundIndex = 0;
        this.roundLimit = lobby.getLobbyOptions().getRoundLimit();

        // Initialize the scoreboard (all players start with 0 points)
        for (Player player : getPlayers()) {
            scoreboard.put(player.getUsername(), 0);
        }

        startNextRound();

    }

    public boolean isActive() {
        return isActive;
    }

    public int getCurrentRoundIndex() {
        return currentRoundIndex;
    }

    public Lobby getLobby() {
        return lobby;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public boolean hasMoreRounds() {
        return currentRoundIndex < roundLimit;
    }

    public int getRoundDuration() {
        return lobby.getLobbyOptions().getTimePerRound();
    }

    public List<Player> getPlayers() {
        return lobby.getPlayers();
    }

    public void startNextRound() {
        if (hasMoreRounds()) {
            currentRoundIndex++;
            Round newRound = new Round(currentRoundIndex, lobby.getLobbyOptions().getTimePerRound(),
                    chooseRandomSpy(getPlayers()));
            currentRound = newRound;
            return;
        }

        isActive = false; // End the game after all rounds

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

    // Stop existing timer if there is one
    public void stopTimer() {
        if (roundTimer != null) {
            roundTimer.cancel();
            setRoundTimer(null);
        }
    }

    private Player chooseRandomSpy(List<Player> players) {
        Random random = new Random();
        int index = random.nextInt(0, players.size() - 1);

        return players.get(index);
    }
}
