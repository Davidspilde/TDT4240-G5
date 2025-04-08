package com.interloperServer.interloperServer.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;
import com.interloperServer.interloperServer.model.Round;

@Service
public class VotingService {
    private final MessagingService messagingService;
    private final GameManagerService gameManagerService;

    public VotingService(MessagingService messagingService, GameManagerService gameManagerService) {
        this.messagingService = messagingService;
        this.gameManagerService = gameManagerService;
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
     * Stops the current round
     * 
     * @param currentRound
     * @param game
     */
    private void completeRound(Round currentRound, Game game) {
        currentRound.setVotingComplete();
        Timer timer = game.getRoundTimer();
        if (timer != null) {
            timer.cancel();
            game.setRoundTimer(null);
        }
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

        Map<String, String> voteMap = currentRound.getVotes();

        List<Player> players = game.getPlayers();

        boolean votesWereCast = !voteMap.isEmpty();

        if (!votesWereCast && !currentRound.isVotingComplete()) {
            return;
        }

        String mostVoted = null;

        int highestVoteCount = 0;
        int majorityThreshold = (int) Math.ceil(players.size() / 2.0);

        if (votesWereCast) {
            // Count how many votes each target received
            mostVoted = getMostVotedPlayer(voteMap);
            highestVoteCount = getVoteCountForPlayer(voteMap, mostVoted);

            // Continue round if majority is not found and the round is not over
            if (!hasMajority(highestVoteCount, majorityThreshold) && !currentRound.isVotingComplete())
                return;
        }

        // Stop timer and mark round as done
        completeRound(currentRound, game);

        // Find real spy and check if the majority vote is for the spy
        String spyName = currentRound.getSpy().getUsername();
        boolean spyCaught = votesWereCast && mostVoted != null && mostVoted.equals(spyName);

        // Award points based on votes
        if (spyCaught) {
            // Award points to every player who is not the spy
            for (Player p : players) {
                if (!spyName.equals(p.getUsername())) {

                    game.updateScore(p.getUsername(), 1);
                }
            }

        } else {
            // Award a point to the spy if not caught
            game.updateScore(currentRound.getSpy().getUsername(), 1);
        }

        // Broadcast end of round message
        Map<String, Object> endRoundMessage = new HashMap<>();
        endRoundMessage.put("event", "roundEnded");
        endRoundMessage.put("spyCaught", spyCaught);
        endRoundMessage.put("spy", spyName);
        endRoundMessage.put("location", currentRound.getLocation());
        endRoundMessage.put("scoreboard", game.getScoreboard());

        messagingService.broadcastMessage(game, endRoundMessage);
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

        // Not legal to try after the round is over
        if (currentRound.isVotingComplete()) {
            return;
        }

        Player spy = currentRound.getSpy();

        // Stop if a player tries to guess location (not legal)
        if (!spy.getUsername().equals(spyUsername)) {
            return;
        }

        // Stop timer
        completeRound(currentRound, game);

        boolean spyCorrectGuess = currentRound.getLocation().equals(location);

        // Find spy and update points
        if (spyCorrectGuess) {
            // Give spy a point
            game.updateScore(spyUsername, 1);
        } else {
            // Give all players except spy a point
            List<Player> players = game.getPlayers();

            for (Player p : players) {
                if (!p.getUsername().equals(spyUsername)) {
                    game.updateScore(p.getUsername(), 1);
                }
            }
        }

        // Broadcast end of round message
        Map<String, Object> endRoundMessage = new HashMap<>();
        endRoundMessage.put("event", "roundEnded");
        endRoundMessage.put("spyCaught", false);
        endRoundMessage.put("spy", spyUsername);
        endRoundMessage.put("location", location);
        endRoundMessage.put("spyGuess", spyCorrectGuess);
        endRoundMessage.put("scoreboard", game.getScoreboard());

        messagingService.broadcastMessage(game, endRoundMessage);
    }
}
