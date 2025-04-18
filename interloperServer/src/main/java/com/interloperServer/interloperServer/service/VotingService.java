package com.interloperServer.interloperServer.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.Round;
import com.interloperServer.interloperServer.model.RoundState;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

/**
 * Service for managing voting logic in the game.
 * <p>
 * This service handles voting-related actions, such as casting votes,
 * evaluating votes,
 * and processing the spy's guess. It interacts with other services to manage
 * game state
 * and send messages to players.
 */
@Service
public class VotingService {
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;
    private final GameManagerService gameManagerService;
    private final RoundService roundService;

    public VotingService(MessagingService messagingService, GameMessageFactory messageFactory,
            GameManagerService gameManagerService,
            RoundService roundService) {
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
        this.gameManagerService = gameManagerService;
        this.roundService = roundService;
    }

    /**
     * Casts a vote for the spy from one player to another during the current round.
     *
     * @param lobbyCode      The lobby code of the game.
     * @param voterUsername  The username of the player casting the vote.
     * @param targetUsername The username of the player being voted for.
     */
    public void castVote(String lobbyCode, String voterUsername, String targetUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();

        // If voting is already complete, do nothing
        if (currentRound.isVotingComplete())
            return;

        Player voter = game.getPlayer(voterUsername);

        // Check if the target player exists
        if (game.getPlayer(targetUsername) == null) {
            if (voter != null) {
                messagingService.sendMessage(voter.getSession(),
                        messageFactory.error("Invalid Vote: " + targetUsername + " is not in the game."));
            }
            return;
        }

        // Prevent self-voting
        if (voter != null && voterUsername.equals(targetUsername)) {
            messagingService.sendMessage(voter.getSession(), "Invalid Vote. Cannot vote for yourself.");
        }

        // If the voter does not exist, do nothing
        if (voter == null) {
            return;
        }

        // Register the vote
        currentRound.castVote(voterUsername, targetUsername);

        // Notify the voter of a successful vote
        messagingService.sendMessage(voter.getSession(), messageFactory.voted());

        // Evaluate votes after every new vote
        evaluateVotes(lobbyCode);
    }

    /**
     * Determines the player with the most votes.
     *
     * @param voteMap A map of votes (voter -> target).
     * @return The username of the most voted player.
     */
    private String getMostVotedPlayer(Map<String, String> voteMap) {
        Map<String, Integer> voteCount = new HashMap<>();
        for (String target : voteMap.values()) {
            voteCount.put(target, voteCount.getOrDefault(target, 0) + 1);
        }
        return Collections.max(voteCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * Counts the number of votes a specific player received.
     *
     * @param voteMap  A map of votes (voter -> target).
     * @param username The username of the player to count votes for.
     * @return The number of votes the player received.
     */
    private int getVoteCountForPlayer(Map<String, String> voteMap, String username) {
        int count = 0;
        for (String voteTarget : voteMap.values()) {
            if (voteTarget.equals(username)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks if a vote count has reached the majority threshold.
     *
     * @param highestVoteCount  The highest number of votes received by a player.
     * @param majorityThreshold The number of votes needed for a majority.
     * @return {@code true} if the majority threshold is reached, {@code false}
     *         otherwise.
     */
    private boolean hasMajority(int highestVoteCount, int majorityThreshold) {
        return highestVoteCount >= majorityThreshold;
    }

    /**
     * Evaluates the votes to determine if the spy has been caught.
     * <p>
     * If a majority is reached, the round ends, and the spy either gets a last
     * attempt
     * to guess the location or the round ends due to a wrong vote.
     *
     * @param lobbyCode The lobby code of the game.
     */
    public void evaluateVotes(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();
        if (currentRound == null) {
            return;
        }

        // If voting is already complete, do nothing
        if (currentRound.isVotingComplete())
            return;

        Map<String, String> voteMap = currentRound.getVotes();
        List<Player> players = game.getPlayers();

        boolean votesWereCast = !voteMap.isEmpty();
        // If no votes cast, do nothing (just keep waiting for votes)
        if (!votesWereCast)
            return;

        String mostVoted = getMostVotedPlayer(voteMap);
        int highestVoteCount = getVoteCountForPlayer(voteMap, mostVoted);
        int majorityThreshold = (int) Math.ceil(players.size() / 2.0);

        // If no majority, do not end the round
        if (!hasMajority(highestVoteCount, majorityThreshold)) {
            return;
        }

        // Determine if the spy was caught
        String spyUsername = currentRound.getSpy().getUsername();
        boolean spyCaught = mostVoted.equals(spyUsername);

        // The spy gets one last attempt to guess the right location if caught
        if (spyCaught) {
            // Start the spy's last attempt to guess the location
            roundService.startSpyLastAttempt(lobbyCode, spyUsername);
            return;
        } else {
            // End the round due to a wrong vote
            roundService.endRoundDueToWrongVote(lobbyCode, spyUsername);
        }

    }

    /**
     * Processes the spy's guess of the location.
     *
     * @param lobbyCode   The lobby code of the game.
     * @param spyUsername The username of the spy making the guess.
     * @param location    The location being guessed.
     */
    public void castSpyGuess(String lobbyCode, String spyUsername, String location) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();

        // If the round is not active, the spy cannot guess
        if (!currentRound.isActive()) {
            return;
        }

        Player spy = currentRound.getSpy();

        // If the player guessing is not the spy, do nothing
        if (!spy.getUsername().equals(spyUsername)) {
            return;
        }

        // Check if the spy's guess is correct
        boolean spyGuessedCorrectly = currentRound.getLocation().getName().equals(location);

        // Determine if the spy was caught during voting
        boolean caught = currentRound.getRoundState() != RoundState.NORMAL;

        // End the round based on the spy's guess
        roundService.endRoundDueToGuess(lobbyCode, spyUsername, caught, spyGuessedCorrectly);
    }

}
