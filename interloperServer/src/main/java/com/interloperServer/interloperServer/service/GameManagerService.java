package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.Game;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameManagerService {
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    /**
     * Retrieves a game by lobby code.
     */
    public Game getGame(String lobbyCode) {
        return activeGames.get(lobbyCode);
    }

    public void storeGame(String lobbyCode, Game game) {
        activeGames.put(lobbyCode, game);
    }

    public void removeGame(String lobbyCode) {
        activeGames.remove(lobbyCode);
    }

    public boolean hasGame(String lobbyCode) {
        return activeGames.containsKey(lobbyCode);
    }

     /**
     * Retrieves all games
     */
    public Map<String, Game> getActiveGames() {
        return activeGames;
    }

    public Set<String> getAllGameCodes() {
        return activeGames.keySet();
    }
}
