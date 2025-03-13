package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.LobbyRole;
import com.interloperServer.interloperServer.model.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for handling lobby related logic
 */
@Service
public class LobbyService {
    // Stores lobbies and their participants (LobbyCode -> Players)
    private final Map<String, List<Player>> lobbies = new ConcurrentHashMap<>();


    /**
     * Creates a new lobby and assigns the creator as the host.
     * Example message: {"content": "createLobby:Alice"}
     * Example response: Lobby Created! Code: a48465 (Host: Alice)
     * @return The newly created lobby code
     */
    public String createLobby(WebSocketSession session, String username) {
        String lobbyCode = UUID.randomUUID().toString().substring(0, 6);
        Player host = new Player(session, username, LobbyRole.HOST);
        lobbies.put(lobbyCode, new ArrayList<>(List.of(host))); // Store as a list of Players
        return lobbyCode;
    }

    /**
     * Adds a player to a lobby.
     * Example message: {"content": "joinLobby:a48465:Bob"}
     * Example response: Joined Lobby: a48465. Host: Alice
     * @return True if the lobby exists, false if not
     */
    public boolean joinLobby(WebSocketSession session, String lobbyCode, String username) {
        if (!lobbies.containsKey(lobbyCode)) {
            return false;
        }
        lobbies.get(lobbyCode).add(new Player(session, username, LobbyRole.PLAYER));
        return true;
    }

    /**
     * Gets the host of a given lobby.
     */
    public Player getLobbyHost(String lobbyCode) {
        return lobbies.get(lobbyCode).stream()
            .filter(player -> player.getLobbyRole() == LobbyRole.HOST)
            .findFirst()
            .orElse(null);
    }

    /**
     * Ensures only the host can start the game.
     */
    public boolean isHost(String lobbyCode, String username) {
        return getLobbyHost(lobbyCode) != null && getLobbyHost(lobbyCode).getUsername().equals(username);
    }

    /**
     * Removes a player when they disconnect.
     * If the host leaves, a new host is assigned.
     */
    public void removeUser(WebSocketSession session) {
        lobbies.forEach((code, players) -> {
            players.removeIf(player -> player.getSession().equals(session));

            // If the host left, assign a new host
            if (players.stream().noneMatch(p -> p.getLobbyRole() == LobbyRole.HOST) && !players.isEmpty()) {
                players.get(0).setLobbyRole(LobbyRole.HOST);
            }

            // Remove empty lobbies
            if (players.isEmpty()) {
                lobbies.remove(code);
            }
        });
    }

    public List<Player> getPlayersInLobby(String lobbyCode) {
        return lobbies.getOrDefault(lobbyCode, new ArrayList<>());
    }
}
