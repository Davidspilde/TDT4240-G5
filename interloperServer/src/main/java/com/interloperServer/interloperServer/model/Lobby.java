package com.interloperServer.interloperServer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

}
