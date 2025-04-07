package com.interloperServer.interloperServer.model;

import java.io.Console;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.interloperServer.interloperServer.service.LobbyOptions;

public class Lobby {

    private Player host;
    private LobbyOptions lobbyOptions;
    private String lobbyCode;
    private List<Player> players;
    private List<Location> locations;

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

    public List<Location> getLocations() {
        return locations;
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
    }

    public void addLocation(Location location) {
        locations.add(location);
    }

    public void removeLocation(Location location) {
        locations.remove(location);
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
