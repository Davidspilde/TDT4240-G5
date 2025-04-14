package com.interloperServer.interloperServer.model;

import java.util.*;

import org.springframework.web.socket.WebSocketSession;

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

    /**
     * Retrieves a player from the game based on their username.
     *
     * @param username The username of the player to retrieve.
     * @return The Player object if found, or null if no player with the given
     *         username exists.
     */
    public Player getPlayer(String username) {
        return lobby.getPlayer(username);
    }

    /**
     * Retrieves a player from the game based on their WebSocketSession.
     *
     * @param session The WebSocketSession of the player to retrieve.
     * @return The Player object if found, or null if no player with the given
     *         session exists.
     */
    public Player getPlayerBySession(WebSocketSession session) {
        return lobby.getPlayerBySession(session);
    }

    public void startNextRound() {
        if (!hasMoreRounds()) {
            isActive = false; // End the game after all rounds
            return;
        }

        currentRoundIndex++;
        Player newSpy = chooseRandomSpy(getPlayers());
        Location newLocation = chooseRandomLocation(lobby.getLocations());// might change this to game if we decide to
                                                                          // have locations here too
        int timePerRound = lobby.getLobbyOptions().getTimePerRound();

        Round newRound = new Round(currentRoundIndex, timePerRound, newSpy, newLocation);

        currentRound = newRound;

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

    /**
     * Starts a timer that executes the given task after the specified duration.
     *
     * @param durationInSeconds The duration in seconds before the task is executed.
     * @param task              The task to execute when the timer expires.
     */
    public void startTimer(int durationInSeconds, Runnable task) {
        stopTimer(); // Stop any existing timer to avoid conflicts

        Timer timer = new Timer();
        this.roundTimer = timer;

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Execute provided task
                task.run();
            }
        }, durationInSeconds * 1000);
    }

    private Player chooseRandomSpy(List<Player> players) {
        Random random = new Random();
        int index = random.nextInt(0, players.size());

        return players.get(index);
    }

    private Location chooseRandomLocation(List<Location> locations) {
        Random random = new Random();
        int index = random.nextInt(0, locations.size());

        return locations.get(index);
    }
}
