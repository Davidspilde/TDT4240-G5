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
     */
    public void advanceRound(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);

        Round currentRound = game.getCurrentRound();

        if (currentRound != null) {
            game.getCurrentRound().endRound();
        }

        // Check if there are more rounds
        if (!game.hasMoreRounds()) {
            // Send game completion message with scores
            messagingService.broadcastMessage(game.getLobby(), messageFactory.gameComplete(game.getScoreboard()));

            return; // The game ends here
        }

        game.startNextRound();
        broadcastRoundStart(game);
    }

    /*
     * Broadcast new-round data
     * Send message to players about which round it is and details about the round
     */
    private void broadcastRoundStart(Game game) {
        Round newRound = game.getCurrentRound();
        Location location = newRound.getLocation();

        List<String> roles = randomizeRoles(location.getRoles(), game.getPlayers().size() - 1);
        int index = 0;

        for (Player player : game.getPlayers()) {

            if (!newRound.getSpy().equals(player)) {
                messagingService.sendMessage(player.getSession(), messageFactory.newRound(
                        newRound.getRoundNumber(),
                        newRound.getRoundDuration(),
                        roles.get(index),
                        newRound.getLocation().getName()));
                index++;

            } else {
                messagingService.sendMessage(player.getSession(), messageFactory.newRound(
                        newRound.getRoundNumber(),
                        newRound.getRoundDuration(),
                        "Spy"));
            }

        }
    }

    /*
     *
     * adds duplicates of roles if there are not enough for players, also shuffles
     * the roles
     */
    protected List<String> randomizeRoles(List<String> roles, int numPlayers) {
        Random random = new Random();
        List<String> newRoles = roles;

        while (newRoles.size() < numPlayers) {
            int randomIndex = random.nextInt(roles.size());
            newRoles.add(roles.get(randomIndex));
        }
        Collections.shuffle(newRoles);

        return newRoles;
    }

    /**
     * Initiates the spy's final chance to guess the location after being exposed.
     * This method is called when the majority of players have voted and identified
     * the spy.
     * It performs the following actions:
     *
     * @param lobbyCode   the code of the lobby in which the game is taking place
     * @param spyUsername the username of the player identified as the spy
     */
    public void startSpyLastAttempt(String lobbyCode, String spyUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        int lastAttemtDuration = 30; // This could be a lobbyoption, so this is temporary
        Round round = game.getCurrentRound();
        round.setSpyLastAttempt();

        // Sends message that the spy has been revealed and the lastAttempt timer has
        // started
        messagingService.broadcastMessage(game.getLobby(),
                messageFactory.spyLastAttempt(spyUsername, lastAttemtDuration));

        // Starts a new timer where the spy looses if there is a timeout
        game.startTimer(lastAttemtDuration, () -> endRoundDueToGuess(lobbyCode, spyUsername, true, false));

    }

    /**
     * Method called when the spy has guessed.
     * Ends the round, stops the timer
     * Calling this means that the spy has guessed location
     * Spy gets a point if guessing correctly
     * 
     * @param lobbyCode
     * @param spyUsername
     * @param spyGuessedCorrectly
     * @param spyCaught
     */
    public void endRoundDueToGuess(String lobbyCode, String spyUsername, boolean spyCaught,
            boolean spyGuessedCorrectly) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        RoundEndReason reason;

        if (spyGuessedCorrectly) {
            reason = RoundEndReason.SPY_GUESS;
        } else {
            reason = RoundEndReason.VOTES;
        }

        finalizeRound(game, reason, spyCaught, spyGuessedCorrectly, spyUsername);
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
     * Calls award points and then broadcast the end round message
     * 
     * @param game            the current game
     * @param reason          the reason for the round to end
     * @param spyCaught       if the spy was caught or not
     * @param spyGuessCorrect if the spy guessed the location
     * @param spyUsername     name of the spy
     */
    private void finalizeRound(Game game, RoundEndReason reason, boolean spyCaught, boolean spyGuessCorrect,
            String spyUsername) {
        Round currentRound = game.getCurrentRound();

        if (currentRound == null) {
            return;
        }
        currentRound.endRound();
        game.stopTimer();

        // Award points
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
     * Award points to the correct users based on if the spy was caught or not and
     * if the spy guessed corectly
     * 
     * @param game            the current game
     * @param spyCaught       if the spy was caught or not
     * @param spyGuessCorrect if the spy guessed the location
     * @param spyUsername     name of the spy
     */
    private void awardPoints(Game game, RoundEndReason reason, boolean spyGuessCorrect, String spyUsername) {
        if (spyGuessCorrect || reason == RoundEndReason.TIMEOUT) {
            // Spy guessed location correctly or there was a timeout
            game.updateScore(spyUsername, 1);
        }

        else {
            // Everyone except spy gets 1 point
            for (Player p : game.getPlayers()) {
                if (!p.getUsername().equals(spyUsername)) {
                    game.updateScore(p.getUsername(), 1);
                }
            }
        }

    }

}
