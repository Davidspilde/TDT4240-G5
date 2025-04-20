package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Game;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for managing active games.
 * <p>
 * This service provides methods to store, retrieve, and remove games by their
 * lobby codes.
 * It uses a thread-safe {@link ConcurrentHashMap} to manage active games.
 */
@Service
public class GameManagerService {
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    /**
     * Retrieves a game by its lobby code.
     *
     * @param lobbyCode The lobby code of the game to retrieve.
     * @return The {@link Game} associated with the given lobby code, or
     *         {@code null} if no game exists.
     */
    public Game getGame(String lobbyCode) {
        return activeGames.get(lobbyCode);
    }

    /**
     * Stores a game with the given lobby code.
     *
     * @param lobbyCode The lobby code of the game.
     * @param game      The {@link Game} to store.
     */
    public void storeGame(String lobbyCode, Game game) {
        activeGames.put(lobbyCode, game);
    }

    /**
     * Removes a game by its lobby code.
     *
     * @param lobbyCode The lobby code of the game to remove.
     */
    public void removeGame(String lobbyCode) {
        activeGames.remove(lobbyCode);
    }

    /**
     * Checks if a game exists for the given lobby code.
     *
     * @param lobbyCode The lobby code to check.
     * @return {@code true} if a game exists for the given lobby code, {@code false}
     *         otherwise.
     */
    public boolean hasGame(String lobbyCode) {
        return activeGames.containsKey(lobbyCode);
    }

    /**
     * Retrieves all active games.
     *
     * @return A {@link Map} of lobby codes to their corresponding {@link Game}
     *         instances.
     */
    public Map<String, Game> getActiveGames() {
        return activeGames;
    }

    /**
     * Retrieves all active game lobby codes.
     *
     * @return A {@link Set} of all lobby codes for active games.
     */
    public Set<String> getAllGameCodes() {
        return activeGames.keySet();
    }
}
