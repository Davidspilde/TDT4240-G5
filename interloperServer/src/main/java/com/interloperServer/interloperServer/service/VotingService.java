package com.interloperServer.interloperServer.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.Round;

@Service
public class VotingService {
    private final MessagingService messagingService;
    private final GameManagerService gameManagerService;
    private final RoundService roundService;

    public VotingService(MessagingService messagingService, GameManagerService gameManagerService,
            RoundService roundService) {
        this.messagingService = messagingService;
        this.gameManagerService = gameManagerService;
        this.roundService = roundService;
    }

    /**
     * Casts a vote for the spy from a user to another user for the current round
     * 
     * @param lobbyCode the lobby for the game
     * @param voter     the username of the player voting
     * @param target    the username of the player being voted for
     */
    public void castVote(String lobbyCode, String voterUsername, String targetUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();

        if (currentRound.isVotingComplete())
            return;

        List<Player> players = game.getPlayers();

        Player voter = players.stream()
                .filter(p -> p.getUsername().equals(voterUsername))
                .findFirst()
                .orElse(null);

        // Check for invalid target
        if (players.stream().noneMatch(p -> p.getUsername().equals(targetUsername))) {
            if (voter != null) {
                messagingService.sendMessage(voter.getSession(), Map.of(
                        "event", "invalidVote",
                        "message", "Invalid vote. " + targetUsername + " is not in the game."));
            }
            return;
        }

        // Don't register vote if the voter doesn't exist
        if (voter == null) {
            return;
        }

        // Register vote
        currentRound.castVote(voterUsername, targetUsername);

        // Notify voter of successful vote
        messagingService.sendMessage(voter.getSession(), Map.of(
                "event", "voted",
                "voter", voterUsername));

        // Look for majority after every vote
        evaluateVotes(lobbyCode);
    }

    /**
     * Calculates the most voted player
     * 
     * @param voteMap map of votes (username -> username)
     * @return the name of the most voted player
     */
    private String getMostVotedPlayer(Map<String, String> voteMap) {
        Map<String, Integer> voteCount = new HashMap<>();
        for (String target : voteMap.values()) {
            voteCount.put(target, voteCount.getOrDefault(target, 0) + 1);
        }
        return Collections.max(voteCount.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    /**
     * Returns how many votes the given player received.
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
     * Checks if a vote count has reached majority
     * 
     * @param highestVoteCount  the highest number of votes
     * @param majorityThreshold the amount of votes needed to end the round
     * @return true or false
     */
    private boolean hasMajority(int highestVoteCount, int majorityThreshold) {
        return highestVoteCount >= majorityThreshold;
    }

    /**
     * Checks if the players have managed to vote out the spy
     * 
     * @param lobbyCode the lobby having the vote
     */
    public void evaluateVotes(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();
        // If round is already complete, do nothing
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

        // Otherwise we have a majority
        String spyUsername = currentRound.getSpy().getUsername();
        boolean spyCaught = mostVoted.equals(spyUsername);

        // Delegate awarding points and broadcasting
        roundService.endRoundDueToVotes(lobbyCode, spyCaught, spyUsername);
    }

    /**
     * The spy guesses a location
     * 
     * @param lobbyCode   the lobby for the game
     * @param spyUsername the username of the spy
     * @param location    the location being guessed
     */
    public void castSpyGuess(String lobbyCode, String spyUsername, String location) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();

        // Not legal to guess after round is over
        if (currentRound.isVotingComplete()) {
            return;
        }

        Player spy = currentRound.getSpy();

        // Not legal to guess location if the one guessing is not the spy
        if (!spy.getUsername().equals(spyUsername)) {
            return;
        }

        // Check if guess is correct
        boolean spyGuessedCorrectly = currentRound.getLocation().equals(location);

        roundService.endRoundDueToSpyGuess(lobbyCode, spyUsername, spyGuessedCorrectly);
    }

}
