package com.interloperServer.interloperServer.service;

import java.io.InputStream;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interloperServer.interloperServer.model.Lobby;
import com.interloperServer.interloperServer.model.LobbyOptions;
import com.interloperServer.interloperServer.model.Location;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

/*Contains all Host-only related logic
 *
 * Might rename this so to be host generally rather than only lobby, since host should be able to end game.
 * */

@Service
public class LobbyHostService {
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;

    public LobbyHostService(MessagingService messagingService, GameMessageFactory messageFactory) {
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
    }

    public void updateLobbyOptions(Lobby lobby, String username, int roundLimit, int locationNumber,
            int TimePerRound,
            int maxPlayerCount, int spyLastAttemptTime) {

        // Check if user is host
        if (!checkIfHost(lobby, username))
            return;

        synchronized (lobby) {
            LobbyOptions lobbyOptions = lobby.getLobbyOptions();

            lobbyOptions.setRoundLimit(roundLimit);
            lobbyOptions.setLocationNumber(locationNumber);
            lobbyOptions.setTimePerRound(TimePerRound);
            lobbyOptions.setMaxPlayerCount(maxPlayerCount);
            lobbyOptions.setSpyLastAttemptTime(spyLastAttemptTime);
        }
    }

    // Sets new locations for a lobby
    public void setLocations(Lobby lobby, List<Location> locations, String username) {
        // Check if user is host
        if (!checkIfHost(lobby, username))
            return;

        // check if there is at least 1 location
        if (locations.size() <= 0) {
            messagingService.sendMessage(lobby.getHost().getSession(),
                    messageFactory.error("Need to have at least on location"));
        }

        synchronized (lobby) {
            lobby.setLocations(locations);

            // broadcasts the changes to all players
            messagingService.broadcastMessage(lobby, messageFactory.locationsUpdate(locations));
        }
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

    // checks if user is host, sends error if not
    private boolean checkIfHost(Lobby lobby, String username) {
        if (lobby.getHost().getUsername().equals(username)) {
            return true;
        }

        messagingService.sendMessage(lobby.getPlayer(username).getSession(),
                messageFactory.error("Only host is allowed to change lobby settings"));

        return false;
    }
}
