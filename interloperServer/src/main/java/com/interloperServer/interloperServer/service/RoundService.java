package com.interloperServer.interloperServer.service;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Location;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.Round;
import com.interloperServer.interloperServer.model.RoundEndReason;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

/**
 * Service for managing round-related logic in the game.
 * <p>
 * This service handles actions such as advancing rounds, broadcasting round
 * details, managing spy's last attempt, and ending rounds for various reasons.
 */
@Service
public class RoundService {
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;
    private final GameManagerService gameManagerService;

    public RoundService(MessagingService messagingService, GameMessageFactory messageFactory,
            GameManagerService gameManagerService) {
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
        this.gameManagerService = gameManagerService;
    }

    /**
     * Advances the round for a specific game.
     * <p>
     * Ends the current round, starts the next round if available, or ends the game
     * if no more rounds are left.
     *
     * @param lobbyCode The lobby code of the game.
     */
    public void advanceRound(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);

        Round currentRound = game.getCurrentRound();

        // End the current round if it exists
        if (currentRound != null) {
            currentRound.endRound();
        }

        // Check if there are not more rounds
        if (!game.hasMoreRounds()) {
            game.getLobby().setGameActive(false);
            gameManagerService.removeGame(lobbyCode);

            // Broadcast game completion message with scores
            messagingService.broadcastMessage(game.getLobby(), messageFactory.gameComplete(game.getScoreboard()));

            return; // The game ends here
        }

        // Start the next round and broadcast its details
        game.startNextRound();
        broadcastRoundStart(game);
    }

    /**
     * Broadcasts the start of a new round to all players.
     * <p>
     * Sends round details, including roles and the first questioner, to each
     * player.
     *
     * @param game The current game.
     */
    private void broadcastRoundStart(Game game) {
        Round newRound = game.getCurrentRound();
        Location location = newRound.getLocation();

        // Randomize roles for players
        List<String> roles = randomizeRoles(location.getRoles(), game.getPlayers().size() - 1);
        int index = 0;

        // Choose a random player to be the first to ask a question
        Player firstQuestioner = chooseRandomPlayer(game.getPlayers());
        String firstQuestionerUsername = firstQuestioner != null ? firstQuestioner.getUsername() : null;

        // Send round details to each player
        for (Player player : game.getPlayers()) {
            if (!newRound.getSpy().equals(player)) {
                messagingService.sendMessage(player.getSession(), messageFactory.newRound(
                        newRound.getRoundNumber(),
                        newRound.getRoundDuration(),
                        roles.get(index),
                        firstQuestionerUsername,
                        newRound.getLocation().getName()));
                index++;
            } else {
                messagingService.sendMessage(player.getSession(), messageFactory.newRound(
                        newRound.getRoundNumber(),
                        newRound.getRoundDuration(),
                        "Spy",
                        firstQuestionerUsername));
            }

        }
    }

    /**
     * Randomizes roles for players at the start of a round.
     * <p>
     * Ensures there are enough roles for all players by duplicating and shuffling
     * roles.
     *
     * @param roles      The list of roles available for the location.
     * @param numPlayers The number of players in the game.
     * @return A randomized list of roles.
     */
    protected List<String> randomizeRoles(List<String> roles, int numPlayers) {
        Random random = new Random();
        List<String> newRoles = roles;

        // If no roles are defined, assign "Player" as the default role
        if (newRoles.size() <= 0) {
            newRoles.add("Player");
        }

        // Duplicate roles until there are enough for all players
        while (newRoles.size() < numPlayers) {
            int randomIndex = random.nextInt(roles.size());
            newRoles.add(roles.get(randomIndex));
        }

        // Shuffle the roles
        Collections.shuffle(newRoles);

        return newRoles;
    }

    /**
     * Chooses a random player from the list of players.
     *
     * @param players The list of players to choose from.
     * @return A randomly selected player, or {@code null} if the list is empty.
     */
    private Player chooseRandomPlayer(List<Player> players) {
        if (players == null || players.isEmpty()) {
            return null;
        }

        Random random = new Random();
        int randomIndex = random.nextInt(players.size());
        return players.get(randomIndex);
    }

    /**
     * Initiates the spy's final chance to guess the location after being exposed.
     * This method is called when the majority of players have voted and identified
     * the spy.
     * 
     * Broadcasts a message to all players and starts a timer for the spy's last
     * attempt.
     *
     * @param lobbyCode   The lobby code of the game.
     * @param spyUsername The username of the player identified as the spy.
     */
    public void startSpyLastAttempt(String lobbyCode, String spyUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round round = game.getCurrentRound();
        round.setSpyLastAttempt();
        int lastAttemtDuration = game.getLobby().getLobbyOptions().getSpyLastAttemptTime();

        // Broadcast the start of the spy's last attempt
        messagingService.broadcastMessage(game.getLobby(),
                messageFactory.spyLastAttempt(spyUsername, lastAttemtDuration));

        // Start a timer for the spy's last attempt
        game.startTimer(lastAttemtDuration, () -> endRoundDueToGuess(lobbyCode, spyUsername, true, false));

    }

    /**
     * Ends the round due to the spy's guess.
     * <p>
     * Determines the outcome of the round based on whether the spy guessed
     * correctly or not.
     *
     * @param lobbyCode           The lobby code of the game.
     * @param spyUsername         The username of the spy.
     * @param spyCaught           Whether the spy was caught.
     * @param spyGuessedCorrectly Whether the spy guessed the location correctly.
     */
    public void endRoundDueToGuess(String lobbyCode, String spyUsername, boolean spyCaught,
            boolean spyGuessedCorrectly) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        finalizeRound(game, RoundEndReason.SPY_GUESS, spyCaught, spyGuessedCorrectly, spyUsername);
    }

    /**
     * Ends the round due to a wrong vote majority.
     *
     * @param lobbyCode   The lobby code of the game.
     * @param spyUsername The username of the spy.
     */
    public void endRoundDueToWrongVote(String lobbyCode, String spyUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        finalizeRound(game, RoundEndReason.WRONG_VOTE, false, false, spyUsername);
    }

    /**
     * Method called when the timer has reached the round duration.
     * Ends the round, stops the timer
     * Calling this means that the spy is not caught and the spy has not guessed
     * location
     * 
     * @param lobbyCode
     * @param spyUsername
     */
    public void endRoundDueToTimeout(String lobbyCode, String spyUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        finalizeRound(game, RoundEndReason.TIMEOUT, false, false, spyUsername);
    }

    /**
     * Ends the round due to the spy disconnecting.
     * Ends the round without awarding any points
     * 
     * @param lobbyCode
     */
    public void endRoundDueToSpyDisconnect(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();
        if (currentRound == null)
            return;

        currentRound.endRound();
        game.stopTimer();

        // Broadcast end of round message
        messagingService.broadcastMessage(game.getLobby(), messageFactory.roundEnded(
                currentRound.getRoundNumber(),
                RoundEndReason.SPY_DISCONNECT.toString(),
                false,
                false,
                currentRound.getSpy().getUsername(),
                currentRound.getLocation().getName(),
                game.getScoreboard()));
    }

    /**
     * Finalizes the round by awarding points and broadcasting the end-round
     * message.
     *
     * @param game            The current game.
     * @param reason          The reason for the round ending.
     * @param spyCaught       Whether the spy was caught.
     * @param spyGuessCorrect Whether the spy guessed the location correctly.
     * @param spyUsername     The username of the spy.
     */
    private void finalizeRound(Game game, RoundEndReason reason, boolean spyCaught, boolean spyGuessCorrect,
            String spyUsername) {
        Round currentRound = game.getCurrentRound();

        if (currentRound == null) {
            return;
        }
        currentRound.endRound();
        game.stopTimer();

        // Award points based on the round outcome
        awardPoints(game, reason, spyGuessCorrect, spyUsername);

        // Broadcast end of round message
        messagingService.broadcastMessage(game.getLobby(), messageFactory.roundEnded(
                currentRound.getRoundNumber(),
                reason.toString(),
                spyCaught,
                spyGuessCorrect,
                currentRound.getSpy().getUsername(),
                currentRound.getLocation().getName(),
                game.getScoreboard()));
    }

    /**
     * Awards points to players based on the round outcome.
     *
     * @param game            The current game.
     * @param reason          The reason for the round ending.
     * @param spyGuessCorrect Whether the spy guessed the location correctly.
     * @param spyUsername     The username of the spy.
     */
    private void awardPoints(Game game, RoundEndReason reason, boolean spyGuessCorrect, String spyUsername) {
        if (spyGuessCorrect || reason == RoundEndReason.TIMEOUT || reason == RoundEndReason.WRONG_VOTE) {
            // Spy guessed the location correctly or the round ended due to timeout/wrong
            // vote
            game.updateScore(spyUsername, 1);
        }

        else {
            // Award points to all players except the spy
            for (Player p : game.getPlayers()) {
                if (!p.getUsername().equals(spyUsername)) {
                    game.updateScore(p.getUsername(), 1);
                }
            }
        }

    }

}
