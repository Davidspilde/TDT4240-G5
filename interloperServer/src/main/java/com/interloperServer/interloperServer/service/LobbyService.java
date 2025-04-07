package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.messages.LobbyOptionsMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for handling lobby-related logic.
 */
@Service
public class LobbyService {
    private final MessagingService messagingService;

    // Stores lobbies by their unique code
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public LobbyService(MessagingService messagingService) {
        this.messagingService = messagingService;
    }

    /**
     * Creates a new lobby and assigns the creator as the host.
     */
    public String createLobby(WebSocketSession session, String username) {
        String lobbyCode;

        // Ensure uniqueness of lobby code
        do {
            lobbyCode = UUID.randomUUID().toString().substring(0, 6);
        } while (lobbies.containsKey(lobbyCode));

        Player host = new Player(session, username);

        LobbyOptions options = new LobbyOptions(
                10, // roundNumber
                30, // locationNumber
                1, // spyCount
                8, // maxPlayers
                120 // roundDuration (seconds)
        );

        Lobby newLobby = new Lobby(lobbyCode, host, options);
        lobbies.put(lobbyCode, newLobby);

        messagingService.sendMessage(session, Map.of(
                "event", "lobbyCreated",
                "lobbyCode", lobbyCode,
                "host", username));
        return lobbyCode;
    }

    /**
     * Adds a player to an existing lobby.
     */
    public boolean joinLobby(WebSocketSession session, String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);

        if (lobby == null) {
            messagingService.sendMessage(session, Map.of(
                    "event", "error",
                    "message", "Lobby not found!"));
            return false;
        }

        synchronized (lobby) {
            lobby.addPlayer(new Player(session, username));
            messagingService.sendMessage(session, Map.of(
                    "event", "joinedLobby",
                    "lobbyCode", lobbyCode,
                    "host", lobby.getHost().getUsername(),
                    "username", username));
        }

        broadcastPlayerList(lobbyCode);
        return true;
    }

    /**
     * Checks if the user is the host of the lobby.
     */
    public boolean isHost(String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        return lobby != null && username.equals(lobby.getHost().getUsername());
    }

    /**
     * Removes a user by their session and updates the lobby accordingly.
     */
    public void removeUser(WebSocketSession session) {
        Lobby targetLobby = null;
        Player targetPlayer = null;

        // Find the lobby and player
        for (Lobby lobby : lobbies.values()) {
            synchronized (lobby) {
                for (Player player : lobby.getPlayers()) {
                    if (player.getSession().equals(session)) {
                        targetLobby = lobby;
                        targetPlayer = player;
                        break;
                    }
                }
            }
            if (targetLobby != null)
                break;
        }

        if (targetLobby == null || targetPlayer == null)
            return;

        synchronized (targetLobby) {
            targetLobby.removePlayer(targetPlayer);

            // Reassign host if needed
            if (targetLobby.getHost().equals(targetPlayer) && !targetLobby.getPlayers().isEmpty()) {
                Player newHost = targetLobby.getPlayers().get(0);
                targetLobby.setHost(newHost);

                for (Player p : targetLobby.getPlayers()) {
                    messagingService.sendMessage(p.getSession(), Map.of(
                            "event", "newHost",
                            "host", newHost.getUsername()));
                }
            }

            // Remove empty lobby
            if (targetLobby.getPlayers().isEmpty()) {
                lobbies.remove(targetLobby.getLobbyCode());
            }
        }
    }

    /**
     * Sends the current member list to all players in the lobby.
     */
    public void broadcastPlayerList(String lobbyCode) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        if (lobby == null)
            return;

        List<Player> players;
        synchronized (lobby) {
            players = new ArrayList<>(lobby.getPlayers()); // copy to safely iterate
        }

        List<String> usernames = players.stream().map(Player::getUsername).toList();
        for (Player player : players) {
            messagingService.sendMessage(player.getSession(), Map.of(
                    "event", "lobbyUpdate",
                    "players", usernames));
        }
    }

    /**
     * Gets the players in a lobby.
     */
    public List<Player> getPlayersInLobby(String lobbyCode) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        return (lobby != null) ? lobby.getPlayers() : new ArrayList<>();
    }

    public void updateLobbyOptions(String lobbycode, LobbyOptionsMessage newOptions) {
        LobbyOptions lobbyOptions = getLobbyFromLobbyCode(lobbycode).getLobbyOptions();

        lobbyOptions.setRoundLimit(newOptions.getRoundLimit());
        lobbyOptions.setSpyCount(newOptions.getSpyCount());
        lobbyOptions.setLocationNumber(newOptions.getRoundLimit());
        lobbyOptions.setTimePerRound(newOptions.getTimePerRound());
        lobbyOptions.setMaxPlayerCount(newOptions.getMaxPlayerCount());
    }

    public Lobby getLobbyFromLobbyCode(String lobbyCode) {
        return lobbies.get(lobbyCode);
    }
}
