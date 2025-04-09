package com.interloperServer.interloperServer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.Round;
import com.interloperServer.interloperServer.model.RoundEndReason;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

@Service
public class RoundService {
    private final MessagingService messagingService;
    private final GameManagerService gameManagerService;

    public RoundService(MessagingService messagingService,
            GameManagerService gameManagerService) {
        this.messagingService = messagingService;
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
            messagingService.broadcastMessage(game.getLobby(), Map.of(
                    "event", "gameComplete",
                    "scoreboard", game.getScoreboard()));
            return; // The game ends here
        }

        game.startNextRound();
        broadcastRoundStart(game);
    }

    /*
     * Broadcast new-round data
     * Send message to players about which round it is and round duration
     */
    private void broadcastRoundStart(Game game) {
        Round newRound = game.getCurrentRound();

        for (Player player : game.getPlayers()) {
            Map<String, Object> roundMessage = new HashMap<>();
            roundMessage.put("event", "newRound");
            roundMessage.put("roundNumber", newRound.getRoundNumber());
            roundMessage.put("roundDuration", newRound.getRoundDuration());

            if (!newRound.getSpy().equals(player)) {
                roundMessage.put("role", "Player");
                roundMessage.put("location", newRound.getLocation());
            } else {
                roundMessage.put("role", "Spy");
            }

            messagingService.sendMessage(player.getSession(), roundMessage);
        }
    }

    /**
     * Method called when a majority of votes has been reachd.
     * Ends the round, stops the timer
     * Calling this means that a majority is reached, which may or may not be the
     * spy
     * Spy gets point if majority is not for spy
     * Players get points if majority is for spy
     * 
     * @param lobbyCode
     * @param spyCaught
     * @param spyUsername
     */
    public void endRoundDueToVotes(String lobbyCode, boolean spyCaught, String spyUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        finalizeRound(game, RoundEndReason.VOTES, spyCaught, false, spyUsername);
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
     */
    public void endRoundDueToSpyGuess(String lobbyCode, String spyUsername, boolean spyGuessedCorrectly) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        finalizeRound(game, RoundEndReason.SPY_GUESS, false, spyGuessedCorrectly, spyUsername);
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
        currentRound.setVotingComplete();
        game.stopTimer();

        // Award points
        awardPoints(game, spyCaught, spyGuessCorrect, spyUsername);

        // Broadcast end of round message
        Map<String, Object> message = new HashMap<>();
        message.put("event", "roundEnded");
        message.put("roundNumber", currentRound.getRoundNumber());
        message.put("reason", reason.toString());
        message.put("spyCaught", spyCaught);
        message.put("spyGuessCorrect", spyGuessCorrect);
        message.put("spy", (spyUsername != null) ? spyUsername : currentRound.getSpy().getUsername());
        message.put("location", currentRound.getLocation());
        message.put("scoreboard", game.getScoreboard());

        messagingService.broadcastMessage(game.getLobby(), message);
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
    private void awardPoints(Game game, boolean spyCaught, boolean spyGuessCorrect, String spyUsername) {
        if (spyGuessCorrect) {
            // Spy guessed location correctly
            game.updateScore(spyUsername, 1);
        }

        if (spyCaught) {
            // Everyone except spy gets 1 point
            for (Player p : game.getPlayers()) {
                if (!p.getUsername().equals(spyUsername)) {
                    game.updateScore(p.getUsername(), 1);
                }
            }
        }

        if (!spyCaught && !spyGuessCorrect) {
            // If we get here, spy wasn't caught, spy didn't guess or guessed incorrectly
            game.updateScore(spyUsername, 1);
        }
    }

}
