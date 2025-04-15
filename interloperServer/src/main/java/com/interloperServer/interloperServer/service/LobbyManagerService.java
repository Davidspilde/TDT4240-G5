package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyOptions;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for handling lobby-related logic.
 */
@Service
public class LobbyManagerService {
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;
    private final LobbyHostService lobbyHostService;

    // Stores lobbies by their unique code
    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public LobbyManagerService(MessagingService messagingService, GameMessageFactory messageFactory,
            LobbyHostService lobbyHostService) {
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
        this.lobbyHostService = lobbyHostService;
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
                120, // roundDuration (seconds)
                45 // SpyLastAttemptDuration (seconds)

        );

        Lobby newLobby = new Lobby(lobbyCode, host, options);
        lobbies.put(lobbyCode, newLobby);

        lobbyHostService.setInitialLocations(newLobby);

        messagingService.sendMessage(session, messageFactory.lobbyCreated(lobbyCode, host.getUsername()));

        return lobbyCode;
    }

    /**
     * Adds a player to an existing lobby.
     */
    public boolean joinLobby(WebSocketSession session, String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);

        // Check for non-existent lobby
        if (lobby == null) {
            messagingService.sendMessage(session, messageFactory.error("Lobby not found!"));
            return false;
        }

        LobbyOptions options = lobby.getLobbyOptions();
        List<Player> players = lobby.getPlayers();

        // Check for full lobby
        if (options.getMaxPlayerCount() <= players.size()) {
            messagingService.sendMessage(session, messageFactory.error("Lobby is full!"));
            return false;
        }

        // Check if player is already in the lobby
        Player existingPlayer = lobby.getPlayerBySession(session);

        if (existingPlayer != null) {
            messagingService.sendMessage(session, messageFactory.error("You are already in the lobby!"));
            return false;
        }

        synchronized (lobby) {
            lobby.addPlayer(new Player(session, username));
            messagingService.sendMessage(session, messageFactory.joinedLobby(lobbyCode, lobby.getHost().getUsername()));
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
                Player found = lobby.getPlayerBySession(session);
                if (found != null) {
                    targetLobby = lobby;
                    targetPlayer = found;
                    break;
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

                messagingService.broadcastMessage(targetLobby, messageFactory.newHost(newHost.getUsername()));

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
        messagingService.broadcastMessage(lobby, messageFactory.lobbyUpdate(usernames));
    }

    /**
     * Gets the players in a lobby.
     */
    public List<Player> getPlayersInLobby(String lobbyCode) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        return (lobby != null) ? lobby.getPlayers() : new ArrayList<>();
    }

    public Lobby getLobbyFromLobbyCode(String lobbyCode) {
        return lobbies.get(lobbyCode);
    }
}
