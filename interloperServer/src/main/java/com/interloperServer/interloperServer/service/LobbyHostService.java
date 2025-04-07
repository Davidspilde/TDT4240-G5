package com.interloperServer.interloperServer.service;

import com.interloperServer.interloperServer.model.messages.LobbyOptionsMessage;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyOptions;
import com.interloperServer.interloperServer.model.Location;

/*Contains all Host-only related logiContains all Host-only related logicc*/
@Service
public class LobbyHostService {

    public LobbyHostService() {
    }

    public void updateLobbyOptions(Lobby lobby, LobbyOptionsMessage newOptions) {
        LobbyOptions lobbyOptions = lobby.getLobbyOptions();

        lobbyOptions.setRoundLimit(newOptions.getRoundLimit());
        lobbyOptions.setSpyCount(newOptions.getSpyCount());
        lobbyOptions.setLocationNumber(newOptions.getRoundLimit());
        lobbyOptions.setTimePerRound(newOptions.getTimePerRound());
        lobbyOptions.setMaxPlayerCount(newOptions.getMaxPlayerCount());
    }

    public void setLocations(Lobby lobby, List<Location> locations) {
        lobby.setLocations(locations);
    }

    public void setInitialLocations(Lobby lobby) {

        // Reads from json file with baseLocations
        ObjectMapper mapper = new ObjectMapper();
        String path = "BaseLocations.json";
        List<Location> baseLocations;
        try (InputStream inputStream = LobbyHostService.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("File not found: " + path);
            }
            baseLocations = mapper.readValue(inputStream, new TypeReference<List<Location>>() {
            });

            lobby.setLocations(baseLocations);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
