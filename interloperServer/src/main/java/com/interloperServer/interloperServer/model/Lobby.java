package com.interloperServer.interloperServer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

public class Lobby {

    private Player host;
    private LobbyOptions lobbyOptions;
    private String lobbyCode;
    private List<Player> players;

    public Lobby(String lobbyCode, Player host, LobbyOptions lobbyOptions) {
        this.lobbyCode = lobbyCode;
        this.host = host;
        this.lobbyOptions = lobbyOptions;
        this.players = Collections.synchronizedList(new ArrayList<Player>(Arrays.asList(host)));
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

    public LobbyOptions getLobbyOptions() {
        return lobbyOptions;
    }

    public synchronized void setLobbyOptions(LobbyOptions options) {
        this.lobbyOptions = options;
    }

    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Retrieves a player from the lobby based on their username.
     *
     * @param username The username of the player to retrieve.
     * @return The Player object if found, or null if no player with the given
     *         username exists.
     */
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
     *
     * @param session The WebSocketSession of the player to retrieve.
     * @return The Player object if found, or null if no player with the given
     *         session exists.
     */
    public Player getPlayerBySession(WebSocketSession session) {
        for (Player player : this.players) {
            if (player.getSession().equals(session)) {
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
