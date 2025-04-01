package com.interloperServer.interloperServer.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.service.LobbyOptions;

public class Lobby {

    private Player host;
    private LobbyOptions lobbyOptions;
    private String lobbyCode;
    private List<Player> players;

    public Lobby(String lobbyCode, Player host, LobbyOptions lobbyOptions) {
        this.lobbyCode = lobbyCode;
        this.host = host;
        this.lobbyOptions = lobbyOptions;
        this.players = new ArrayList<Player>(Arrays.asList(host));
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

    public void setHost(Player host) {
        this.host = host;
    }

    public LobbyOptions getLobbyOptions() {
        return lobbyOptions;
    }

    public void setLobbyOptions(LobbyOptions options) {
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
