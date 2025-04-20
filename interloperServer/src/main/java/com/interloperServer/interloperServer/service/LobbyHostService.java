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

/**
 * Service for managing host-specific logic for lobbies.
 * <p>
 * This service contains functionality for hosts to manage lobby settings,
 * update locations,
 * and perform other host-related actions. It ensures that only the host can
 * make changes
 * to the lobby and broadcasts updates to all players when necessary.
 */
@Service
public class LobbyHostService {
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;

    public LobbyHostService(MessagingService messagingService, GameMessageFactory messageFactory) {
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
    }

    /**
     * Updates the lobby options.
     * <p>
     * This method allows the host to update various lobby settings such as round
     * limit,
     * number of locations, time per round, maximum player count, and spy's last
     * attempt time.
     *
     * @param lobby              The {@link Lobby} to update.
     * @param username           The username of the player attempting the update.
     * @param roundLimit         The maximum number of rounds.
     * @param locationNumber     The number of locations.
     * @param TimePerRound       The time per round in seconds.
     * @param maxPlayerCount     The maximum number of players allowed in the lobby.
     * @param spyLastAttemptTime The time allocated for the spy's last attempt.
     */
    public void updateLobbyOptions(Lobby lobby, String username, int roundLimit, int locationNumber,
            int TimePerRound,
            int maxPlayerCount, int spyLastAttemptTime) {

        // Check if the user attempting the update is the host
        if (!checkIfHost(lobby, username))
            return;

        synchronized (lobby) {
            LobbyOptions lobbyOptions = lobby.getLobbyOptions();

            // Update the lobby options with the provided values
            lobbyOptions.setRoundLimit(roundLimit);
            lobbyOptions.setLocationNumber(locationNumber);
            lobbyOptions.setTimePerRound(TimePerRound);
            lobbyOptions.setMaxPlayerCount(maxPlayerCount);
            lobbyOptions.setSpyLastAttemptTime(spyLastAttemptTime);
        }
    }

    /**
     * Sets new locations for the lobby.
     * <p>
     * This method allows the host to update the list of locations for the lobby.
     * It ensures that there is at least one location and broadcasts the changes to
     * all players.
     *
     * @param lobby     The {@link Lobby} to update.
     * @param locations The list of {@link Location} objects to set.
     * @param username  The username of the player attempting the update.
     */
    public void setLocations(Lobby lobby, List<Location> locations, String username) {
        // Check if the user attempting the update is the host
        if (!checkIfHost(lobby, username))
            return;

        // Ensure there is at least one location in the list
        if (locations.size() <= 0) {
            messagingService.sendMessage(lobby.getHost().getSession(),
                    messageFactory.error("Need to have at least one location"));
        }

        synchronized (lobby) {
            // Update the lobby's locations
            lobby.setLocations(locations);

            // Broadcast the updated locations to all players in the lobby
            messagingService.broadcastMessage(lobby, messageFactory.locationsUpdate(locations));
        }
    }

    /**
     * Sets the initial locations for the lobby.
     * <p>
     * This method loads the default locations from a JSON file and sets them for
     * the lobby.
     *
     * @param lobby The {@link Lobby} to update.
     */
    public void setInitialLocations(Lobby lobby) {
        // Reads from json file with baseLocations
        ObjectMapper mapper = new ObjectMapper();
        String path = "BaseLocations.json";
        List<Location> baseLocations;

        try (InputStream inputStream = LobbyHostService.class.getClassLoader().getResourceAsStream(path)) {
            if (inputStream == null) {
                // Handle the case where the file is not found
                throw new IllegalArgumentException("File not found: " + path);
            }
            // Deserialize the JSON file into a list of Location objects
            baseLocations = mapper.readValue(inputStream, new TypeReference<List<Location>>() {
            });

            // Ensure the list of locations is not empty
            if (baseLocations == null || baseLocations.isEmpty()) {
                throw new IllegalStateException("No locations loaded!");
            }
            System.out.println(baseLocations.get(1).getRoles());

            // Set the locations in the lobby
            lobby.setLocations(baseLocations);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if the user is the host of the lobby.
     * <p>
     * If the user is not the host, an error message is sent to the user.
     *
     * @param lobby    The {@link Lobby} to check.
     * @param username The username of the player to check.
     * @return {@code true} if the user is the host, {@code false} otherwise.
     */
    private boolean checkIfHost(Lobby lobby, String username) {
        // Check if the username matches the host's username
        if (lobby.getHost().getUsername().equals(username)) {
            return true;
        }

        // Send an error message to the user if they are not the host
        messagingService.sendMessage(lobby.getPlayer(username).getSession(),
                messageFactory.error("Only host is allowed to change lobby settings"));

        return false;
    }
}
