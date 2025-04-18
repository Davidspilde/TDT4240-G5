package com.interloperServer.interloperServer.service.messagingServices;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.interloperServer.interloperServer.model.Location;
import com.interloperServer.interloperServer.model.messages.outgoing.*;

/**
 * Factory class for creating various game-related messages.
 * <p>
 * This class provides methods to construct instances of different message types
 * used for communication between the server and clients.
 * <p>
 * The factory supports creating messages for events such as:
 * <ul>
 * <li>Error messages</li>
 * <li>Game start and end notifications</li>
 * <li>Round updates</li>
 * <li>Lobby updates</li>
 * <li>Spy's last attempt</li>
 * <li>Round end summaries</li>
 * </ul>
 * 
 * <p>
 * Each method returns a specific message object, simplifying the creation and
 * management of messages.
 */
@Component
public class GameMessageFactory {

    /**
     * Creates an error message.
     *
     * @param message The error description.
     * @return An {@link ErrorMessage} instance.
     */
    public ErrorMessage error(String message) {
        return new ErrorMessage(message);
    }

    /**
     * Creates a message indicating the game has started.
     *
     * @return A {@link GameMessage} with the event "gameStarted".
     */
    public GameMessage gameStarted() {
        return new GameMessage("gameStarted");
    }

    /**
     * Creates a message indicating the game has ended.
     *
     * @return A {@link GameMessage} with the event "gameEnded".
     */
    public GameMessage gameEnded() {
        return new GameMessage("gameEnded");
    }

    /**
     * Creates a message for starting a new round.
     *
     * @param number          The round number.
     * @param duration        The duration of the round in seconds.
     * @param role            The role assigned to the player.
     * @param firstQuestioner The username of the first questioner.
     * @param location        The location for the round (optional, null for the
     *                        spy).
     * @return A {@link NewRoundMessage} instance.
     */
    public NewRoundMessage newRound(int number, int duration, String role, String firstQuestioner, String location) {
        return new NewRoundMessage(number, duration, role, firstQuestioner, location);
    }

    /**
     * Creates a message for starting a new round without specifying a location.
     *
     * @param number          The round number.
     * @param duration        The duration of the round in seconds.
     * @param role            The role assigned to the player.
     * @param firstQuestioner The username of the first questioner.
     * @return A {@link NewRoundMessage} instance.
     */
    public NewRoundMessage newRound(int number, int duration, String role, String firstQuestioner) {
        return new NewRoundMessage(number, duration, role, firstQuestioner);
    }

    /**
     * Creates a message indicating that a lobby has been created.
     *
     * @param lobbyCode The code of the newly created lobby.
     * @param host      The username of the host of the lobby.
     * @return A {@link LobbyCreatedMessage} instance.
     */
    public LobbyCreatedMessage lobbyCreated(String lobbyCode, String host) {
        return new LobbyCreatedMessage(lobbyCode, host);
    }

    /**
     * Creates a message indicating that a player has joined a lobby.
     *
     * @param lobbyCode The code of the lobby the player joined.
     * @param host      The username of the host of the lobby.
     * @return A {@link JoinedLobbyMessage} instance.
     */
    public JoinedLobbyMessage joinedLobby(String lobbyCode, String host) {
        return new JoinedLobbyMessage(lobbyCode, host);
    }

    /**
     * Creates a message indicating that a new host has been assigned to the lobby.
     *
     * @param host The username of the new host.
     * @return A {@link NewHostMessage} instance.
     */
    public NewHostMessage newHost(String host) {
        return new NewHostMessage(host);
    }

    /**
     * Creates a message to update the list of players in the lobby.
     *
     * @param players A list of usernames of players currently in the lobby.
     * @return A {@link LobbyUpdateMessage} instance.
     */
    public LobbyUpdateMessage lobbyUpdate(List<String> players) {
        return new LobbyUpdateMessage(players);
    }

    /**
     * Creates a message indicating that the game has been completed.
     *
     * @param scoreboard A map where the keys are player usernames and the values
     *                   are their scores.
     * @return A {@link GameCompleteMessage} instance.
     */
    public GameCompleteMessage gameComplete(Map<String, Integer> scoreboard) {
        return new GameCompleteMessage(scoreboard);
    }

    /**
     * Creates a message for the spy's last attempt.
     *
     * @param spyUsername The username of the spy.
     * @param duration    The duration of the spy's last attempt in seconds.
     * @return A {@link SpyLastAttemptMessage} instance.
     */
    public SpyLastAttemptMessage spyLastAttempt(String spyUsername, int duration) {
        return new SpyLastAttemptMessage(spyUsername, duration);

    }

    /**
     * Creates a message indicating that a vote has been cast.
     *
     * @return A {@link GameMessage} with the event "voted".
     */
    public GameMessage voted() {
        return new GameMessage("voted");
    }

    /**
     * Creates a message to update the list of locations in the lobby.
     *
     * @param locations A list of {@link Location} objects representing the updated
     *                  locations.
     * @return A {@link LobbyLocationsUpdateMessage} instance.
     */
    public LobbyLocationsUpdateMessage locationsUpdate(List<Location> locations) {
        return new LobbyLocationsUpdateMessage(locations);
    }

    /**
     * Creates a message indicating that a round has ended.
     *
     * @param roundNumber     The number of the round that has ended.
     * @param reason          The reason why the round ended (e.g., VOTES,
     *                        SPY_GUESS,
     *                        TIMEOUT).
     * @param spyCaught       A boolean indicating whether the spy was caught.
     * @param spyGuessCorrect A boolean indicating whether the spy's guess of the
     *                        location was correct.
     * @param spy             The username of the spy for the round.
     * @param location        The location for the round.
     * @param scoreboard      A map where the keys are player usernames and the
     *                        values are their scores.
     * @return A {@link RoundEndedMessage} instance.
     */
    public RoundEndedMessage roundEnded(
            int roundNumber,
            String reason,
            boolean spyCaught,
            boolean spyGuessCorrect,
            String spy,
            String location,
            Map<String, Integer> scoreboard) {

        return new RoundEndedMessage(roundNumber, reason, spyCaught, spyGuessCorrect, spy, location, scoreboard);
    }

}
