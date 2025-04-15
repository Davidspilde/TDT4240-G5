package com.interloperServer.interloperServer.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyOptions;
import com.interloperServer.interloperServer.model.Location;

/*Contains all Host-only related logic
 *
 * Might rename this so to be host generally rather than only lobby, since host should be able to end game.
 * */

@Service
public class LobbyHostService {

    public LobbyHostService() {
    }

    public void updateLobbyOptions(Lobby lobby, int roundLimit, int spyCount, int locationNumber, int TimePerRound,
            int maxPlayerCount, int spyLastAttemptTime) {
        LobbyOptions lobbyOptions = lobby.getLobbyOptions();

        lobbyOptions.setRoundLimit(roundLimit);
        lobbyOptions.setSpyCount(spyCount);
        lobbyOptions.setLocationNumber(locationNumber);
        lobbyOptions.setTimePerRound(TimePerRound);
        lobbyOptions.setMaxPlayerCount(maxPlayerCount);
        lobbyOptions.setSpyLastAttemptTime(spyLastAttemptTime);
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
            if (baseLocations == null || baseLocations.isEmpty()) {
                throw new IllegalStateException("No locations loaded!");
            }
            System.out.println(baseLocations.get(1).getRoles());

            lobby.setLocations(baseLocations);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
