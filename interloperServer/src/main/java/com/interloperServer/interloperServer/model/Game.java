package com.interloperServer.interloperServer.model;

import java.util.*;
import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a game instance.
 * <p>
 * This class manages the state of a game, including players, rounds, scores,
 * and timers.
 * It interacts with the {@link Lobby} to manage players and game settings.
 */
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
        this.lobby.setGameActive(true);

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

    public Player getPlayer(String username) {
        return lobby.getPlayer(username);
    }

    /**
     * Retrieves a player from the game based on their WebSocketSession.
     */
    public Player getPlayerBySession(WebSocketSession session) {
        return lobby.getPlayerBySession(session);
    }

    /**
     * Starts the next round in the game.
     * <p>
     * Assigns a random spy and location for the new round.
     * Ends the game if the round limit is reached.
     */
    public void startNextRound() {
        if (!hasMoreRounds()) {
            isActive = false; // End the game after all rounds
            return;
        }

        currentRoundIndex++;
        Player newSpy = chooseRandomSpy(getPlayers());
        Location newLocation = chooseRandomLocation(lobby.getLocations());
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

    /**
     * Stops the current round timer, if one exists.
     */
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

    /**
     * Chooses a random player to be the spy for the round.
     *
     * @param players The list of players in the game.
     * @return The {@link Player} selected as the spy.
     */
    private Player chooseRandomSpy(List<Player> players) {
        Random random = new Random();
        int index = random.nextInt(0, players.size());

        return players.get(index);
    }

    /**
     * Chooses a random location for the round.
     *
     * @param locations The list of available locations.
     * @return The {@link Location} selected for the round.
     */
    private Location chooseRandomLocation(List<Location> locations) {
        Random random = new Random();
        int index = random.nextInt(0, locations.size());

        return locations.get(index);
    }
}
