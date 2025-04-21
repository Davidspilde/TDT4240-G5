package com.interloperServer.interloperServer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

/**
 * Represents a lobby where players can join, interact, and prepare for a game.
 * <p>
 * This class manages the state of the lobby, including players, host, game
 * settings,
 * and available locations. It provides methods for adding, removing, and
 * retrieving players.
 */
public class Lobby {

    private Player host;
    private Boolean gameActive;

    private LobbyOptions lobbyOptions;
    private String lobbyCode;
    private List<Player> players;
    private List<Location> locations;

    public Lobby(String lobbyCode, Player host, LobbyOptions lobbyOptions) {
        this.lobbyCode = lobbyCode;
        this.host = host;
        this.lobbyOptions = lobbyOptions;
        this.players = Collections.synchronizedList(new ArrayList<Player>(Arrays.asList(host)));
        this.gameActive = false;
    }

    public String getLobbyCode() {
        return lobbyCode;
    }

    public void setLobbyCode(String lobbyCode) {
        this.lobbyCode = lobbyCode;
    }

    public Player getHost() {
        return host;
    }

    public synchronized void setHost(Player host) {
        this.host = host;
    }

    public List<Location> getLocations() {
        return locations;
    }

    public synchronized void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public synchronized void addLocation(Location location) {
        locations.add(location);
    }

    public void removeLocation(Location location) {
        locations.remove(location);
    }

    public Boolean getGameActive() {
        return gameActive;
    }

    public void setGameActive(Boolean gameActive) {
        this.gameActive = gameActive;
    }

    public LobbyOptions getLobbyOptions() {
        return lobbyOptions;
    }

    public synchronized void setLobbyOptions(LobbyOptions options) {
        this.lobbyOptions = options;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getPlayer(String username) {
        for (Player player : this.players) {
            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        // Return null if no player is found
        return null;
    }

    /**
     * Retrieves a player from the lobby based on their WebSocketSession.
     */
    public Player getPlayerBySession(WebSocketSession session) {
        for (Player player : this.players) {
            WebSocketSession playerSession = player.getSession();
            // Skip this player if their session is null
            if (playerSession != null && playerSession.equals(session)) {
                return player;
            }
        }
        // Return null if no player is found
        return null;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

}
