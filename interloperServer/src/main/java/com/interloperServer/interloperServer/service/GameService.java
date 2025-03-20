package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for handling game related logic
 */
@Service
public class GameService {
    // All active games
    private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    /**
     * Assigns roles (one Spy, rest Players) to all players in the lobby.
     * Example message: {"content": "startGame:a9b7f9", "username": "Alice"}
     * @return True if the host called the method, false if someone else did
     */
    public boolean startGame(String lobbyCode, String username, LobbyService lobbyService, WebSocketSession session) {
        if (!lobbyService.isHost(lobbyCode, username)) {
            sendMessage(session, "Only the host can start the game.");
            return false;
        }

        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);

        // Create a new game instance for this lobby
        Game game = new Game(lobbyCode, players, 5); // Example: 5 rounds
        activeGames.put(lobbyCode, game);

        assignRoles(players);
        broadcastMessage(lobbyCode, "Game started! First location: " + game.getCurrentRound().getLocation());

        return true;
    }

    /**
     * Removes a player from a game when they disconnect
     * Ends the game if the player to disconnect is the only one left
     */
    public void handlePlayerDisconnect(WebSocketSession session, String lobbyCode) {
        Game game = activeGames.get(lobbyCode);
        if (game == null) return;
    
        // Find the player who disconnected
        Player disconnectedPlayer = null;
        for (Player player : game.getPlayers()) {
            if (player.getSession().equals(session)) {
                disconnectedPlayer = player;
                break;
            }
        }
    
        if (disconnectedPlayer == null) return; // Player not found in the game
    
        // Remove player from the game
        game.getPlayers().remove(disconnectedPlayer);
        broadcastMessage(lobbyCode, disconnectedPlayer.getUsername() + " has left the game.");
    
        // If the removed player was the host, assign a new host
        if (disconnectedPlayer.getLobbyRole() == LobbyRole.HOST && !game.getPlayers().isEmpty()) {
            game.getPlayers().get(0).setLobbyRole(LobbyRole.HOST);
            broadcastMessage(lobbyCode, game.getPlayers().get(0).getUsername() + " is the new host.");
        }
    
        // If the game is now empty, end it
        if (game.getPlayers().isEmpty()) {
            endGame(lobbyCode);
        }
    }
    

    /**
     * Retrieves a game by lobby code.
     */
    public Game getGame(String lobbyCode) {
        return activeGames.get(lobbyCode);
    }

    /**
     * Retrieves all games
     */
    public Map<String, Game> getActiveGames() {
        return activeGames;
    }
    

    /**
     * Advances the round for a specific game.
     */
    public boolean advanceRound(String lobbyCode) {
        Game game = activeGames.get(lobbyCode);
        if (game == null) return false;

        game.getCurrentRound().endRound();
        game.startNextRound();

        broadcastMessage(lobbyCode, "New round started! Location: " + game.getCurrentRound().getLocation());
        return true;
    }

    /**
     * Ends a game and removes it from active games.
     */
    public void endGame(String lobbyCode) {
        activeGames.remove(lobbyCode);
        broadcastMessage(lobbyCode, "Game has ended.");
    }

    /**
     * Assigns roles to players in the game.
     */
    private void assignRoles(List<Player> players) {
        Collections.shuffle(players);
        players.get(0).setGameRole(GameRole.SPY);
        players.subList(1, players.size()).forEach(p -> p.setGameRole(GameRole.PLAYER));
    }

    /**
     * Sends a message to all players in a game.
     */
    private void broadcastMessage(String lobbyCode, String message) {
        Game game = activeGames.get(lobbyCode);
        if (game == null) return;

        for (Player player : game.getPlayers()) {
            sendMessage(player.getSession(), message);
        }
    }

    /**
     * Sends a message to a single player.
     */
    private void sendMessage(WebSocketSession session, String message) {
        try {
            session.sendMessage(new TextMessage(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
