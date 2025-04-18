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
 * Service for managing lobby-related logic.
 * <p>
 * This service handles creating, joining, leaving, and managing lobbies. It
 * ensures
 * proper synchronization and broadcasts updates to players when necessary.
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
     *
     * @param session  The WebSocket session of the host.
     * @param username The username of the host.
     * @return The unique code of the newly created lobby.
     */
    public String createLobby(WebSocketSession session, String username) {
        String lobbyCode;

        // Ensure uniqueness of lobby code
        do {
            lobbyCode = UUID.randomUUID().toString().substring(0, 6);
        } while (lobbies.containsKey(lobbyCode));

        Player host = new Player(session, username);

        // Set session attributes for reconnection purposes
        session.getAttributes().put("username", username);
        session.getAttributes().put("lobbyCode", lobbyCode);

        LobbyOptions options = new LobbyOptions(
                10, // roundNumber
                30, // locationNumber
                8, // maxPlayers
                10, // roundDuration (seconds)
                45 // SpyLastAttemptDuration (seconds)

        );

        Lobby newLobby = new Lobby(lobbyCode, host, options);
        lobbies.put(lobbyCode, newLobby);

        // Initialize default locations for the lobby
        lobbyHostService.setInitialLocations(newLobby);

        // Notify the host about the lobby creation
        messagingService.sendMessage(session, messageFactory.lobbyCreated(lobbyCode, host.getUsername()));

        // Send the locations for the lobby
        messagingService.sendMessage(session, messageFactory.locationsUpdate(newLobby.getLocations()));

        return lobbyCode;
    }

    /**
     * Adds a player to an existing lobby.
     *
     * @param session   The WebSocket session of the player.
     * @param lobbyCode The code of the lobby to join.
     * @param username  The username of the player.
     * @return {@code true} if the player successfully joined the lobby,
     *         {@code false} otherwise.
     */
    public boolean joinLobby(WebSocketSession session, String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);

        // Check for non-existent lobby
        if (lobby == null) {
            messagingService.sendMessage(session, messageFactory.error("Lobby not found!"));
            return false;
        }

        // Look for a player with the same username who is disconnected
        Player existingPlayer = lobby.getPlayer(username);
        if (existingPlayer != null && existingPlayer.isDisconnected()) {
            // Handle reconnection
            existingPlayer.cancelDisconnectRemoval();
            existingPlayer.setSession(session);

            // Set session attributes for reconnection purposes
            session.getAttributes().put("username", username);
            session.getAttributes().put("lobbyCode", lobbyCode);

            messagingService.sendMessage(session, messageFactory.joinedLobby(lobbyCode, lobby.getHost().getUsername()));
            broadcastPlayerList(lobbyCode);
            return true;
        }

        // Check if the game is already running
        if (lobby.getGameActive()) {
            messagingService.sendMessage(session, messageFactory.error("Cannot join lobby when game started"));
            return false;
        }

        LobbyOptions options = lobby.getLobbyOptions();
        List<Player> players = lobby.getPlayers();

        // Check for full lobby
        if (options.getMaxPlayerCount() <= players.size()) {
            messagingService.sendMessage(session, messageFactory.error("Lobby is full!"));
            return false;
        }

        // Check if the username is already taken
        if (existingPlayer != null && !existingPlayer.isDisconnected()) {
            messagingService.sendMessage(session, messageFactory.error("Username is taken!"));
            return false;
        }

        // Add the player to the lobby
        synchronized (lobby) {
            lobby.addPlayer(new Player(session, username));
            messagingService.sendMessage(session, messageFactory.joinedLobby(lobbyCode, lobby.getHost().getUsername()));

            // sends the locations for the lobby
            messagingService.sendMessage(session, messageFactory.locationsUpdate(lobby.getLocations()));
        }

        // Set session attributes for reconnection purposes
        session.getAttributes().put("username", username);
        session.getAttributes().put("lobbyCode", lobbyCode);

        broadcastPlayerList(lobbyCode);
        return true;
    }

    /**
     * Removes a player from a lobby.
     *
     * @param session   The WebSocket session of the player.
     * @param lobbyCode The code of the lobby to leave.
     * @param username  The username of the player.
     * @return {@code true} if the player successfully left the lobby, {@code false}
     *         otherwise.
     */
    public boolean leaveLobby(WebSocketSession session, String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);

        // Check for non-existent lobby
        if (lobby == null) {
            messagingService.sendMessage(session, messageFactory.error("Lobby not found!"));
            return false;
        }

        // Prevent leaving if the game is active
        if (lobby.getGameActive()) {
            messagingService.sendMessage(session, messageFactory.error("Cannot leave while game is active"));
            return false;
        }

        removeUser(session);
        return true;
    }

    /**
     * Checks if the user is the host of the lobby.
     *
     * @param lobbyCode The code of the lobby.
     * @param username  The username of the player.
     * @return {@code true} if the user is the host, {@code false} otherwise.
     */
    public boolean isHost(String lobbyCode, String username) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        return lobby != null && username.equals(lobby.getHost().getUsername());
    }

    /**
     * Removes a user by their session and updates the lobby accordingly.
     *
     * @param session The WebSocket session of the player to remove.
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

        // Broadcasts changes in the lobby
        broadcastPlayerList(targetLobby.getLobbyCode());
    }

    /**
     * Sends the current member list to all players in the lobby.
     *
     * @param lobbyCode The code of the lobby.
     */
    public void broadcastPlayerList(String lobbyCode) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        if (lobby == null)
            return;

        List<Player> players;
        synchronized (lobby) {
            players = new ArrayList<>(lobby.getPlayers()); // Copy to safely iterate
        }

        List<String> usernames = players.stream().map(Player::getUsername).toList();
        messagingService.broadcastMessage(lobby, messageFactory.lobbyUpdate(usernames));
    }

    /**
     * Gets the players in a lobby.
     *
     * @param lobbyCode The code of the lobby.
     * @return A list of players in the lobby, or an empty list if the lobby does
     *         not exist.
     */
    public List<Player> getPlayersInLobby(String lobbyCode) {
        Lobby lobby = getLobbyFromLobbyCode(lobbyCode);
        return (lobby != null) ? lobby.getPlayers() : new ArrayList<>();
    }

    /**
     * Retrieves a lobby by its code.
     *
     * @param lobbyCode The code of the lobby.
     * @return The {@link Lobby} associated with the code, or {@code null} if not
     *         found.
     */
    public Lobby getLobbyFromLobbyCode(String lobbyCode) {
        return lobbies.get(lobbyCode);
    }
}
