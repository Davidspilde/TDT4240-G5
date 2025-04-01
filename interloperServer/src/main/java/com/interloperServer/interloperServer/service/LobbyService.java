package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyRole;
import com.interloperServer.interloperServer.model.Player;

import java.time.Duration;
import java.util.*;

/**
 * Class for handling lobby related logic
 */
@Service
public class LobbyService {
    private final MessagingService messagingService;

    // Stores lobbies and their participants
    private final List<Lobby> lobbies = new ArrayList<Lobby>();

    public LobbyService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    /**
     * Creates a new lobby and assigns the creator as the host.
     * Example message: {"content": "createLobby:Alice"}
     * Example response: Lobby Created! Code: a48465 (Host: Alice)
     * 
     * @return The newly created lobby code
     */
    public String createLobby(WebSocketSession session, String username) {
        String lobbyCode;

        // Ensure uniqueness of lobby code
        do {
            lobbyCode = UUID.randomUUID().toString().substring(0, 6);
        } while (getLobbyFromLobbyCode(lobbyCode).equals(null));

        Player host = new Player(session, username, LobbyRole.HOST);

        // Baseoptions which gets applies when first creating a lobby
        LobbyOptions options = new LobbyOptions(10, 30, 1, 8, Duration.ofMinutes(10));

        Lobby newLobby = new Lobby(lobbyCode, host, options);
        lobbies.add(newLobby);

        messagingService.sendMessage(session, "Lobby Created! Code: " + lobbyCode + " (Host: " + username + ")");
        return lobbyCode;
    }

    /**
     * Adds a player to a lobby.
     * Example message: {"content": "joinLobby:a48465:Bob"}
     * Example response: Joined Lobby: a48465. Host: Alice
     * 
     * @return True if the lobby exists, false if not
     */
    public boolean joinLobby(WebSocketSession session, String lobbyCode, String username) {

        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);

        if (lobby.equals(null)) {
            messagingService.sendMessage(session, "Lobby Not Found!");
            return false;
        }

        lobby.addPlayer(new Player(session, username, LobbyRole.PLAYER));
        messagingService.sendMessage(session,
                "Joined Lobby: " + lobbyCode + ". Host: " + lobby.getHost());

        // Notify all users in the lobby
        broadcastPlayerList(lobbyCode);
        return true;
    }

    /**
     * Ensures only the host can start the game.
     */
    public boolean isHost(String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        if (lobby.equals(null) && !lobby.getHost().getUsername().equals(username)) {
            return false;
        }
        return true;
    }

    /**
     * Removes a player when they disconnect.
     * If the host leaves, a new host is assigned.
     */
    public void removeUser(WebSocketSession session) {
        for (int i = 0; i < lobbies.size(); i++) {
            Lobby lobby = lobbies.get(i);
            Player player = null;

            for (int p = 0; p < lobby.getPlayers().size(); p++) {
                Player tempPlayer = lobby.getPlayers().get(p);
                if (tempPlayer.getSession().equals(session)) {
                    player = tempPlayer;
                }
            }

            if (player.equals(null)) {
                return;
            }

            lobby.removePlayer(player);

            // If the host left, assign a new host
            if (lobby.getHost().equals(player)) {
                lobby.setHost(lobby.getPlayers().get(0));
            }

            // Remove empty lobbies
            if (lobby.getPlayers().isEmpty()) {
                lobbies.remove(lobby);
            }
        }
        ;
    }

    /**
     * Sends the current members of a lobby to every member in that lobby
     * 
     * @param lobbyCode
     */
    public void broadcastPlayerList(String lobbyCode) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);

        if (lobby.equals(null)) {
            return;
        }

        List<Player> players = lobby.getPlayers();
        List<String> usernames = players.stream().map(Player::getUsername).toList();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(usernames);

            for (Player player : players) {
                messagingService.sendMessage(player.getSession(), "Lobby Update: " + message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Lobby getLobbyFromLobbyCode(String lobbyCode) {

        for (int i = 0; i < lobbies.size(); i++) {
            if (lobbies.get(i).getLobbyCode().equals(lobbyCode)) {
                return lobbies.get(i);
            }
        }
        return null;
    }

}
