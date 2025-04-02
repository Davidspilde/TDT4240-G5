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

        String mostVoted = null;
        int highestVoteCount = 0;
        int majorityThreshold = (int) Math.ceil(players.size() / 2.0);

        if (votesWereCast) {
            // Count how many votes each target received
            Map<String, Integer> voteCount = new HashMap<>();

            for (String target : voteMap.values()) {
                voteCount.put(target, voteCount.getOrDefault(target, 0) + 1);
            }

            // Find most voted
            mostVoted = Collections.max(voteCount.entrySet(), Map.Entry.comparingByValue()).getKey();
            highestVoteCount = voteCount.get(mostVoted);

            // Continue round if majority is not found
            if ((highestVoteCount < majorityThreshold) || !currentRound.isVotingComplete())
                return;
        }

        currentRound.setVotingComplete();

        // Stop timer if round is over or majority is reached
        if ((highestVoteCount >= majorityThreshold) || currentRound.isVotingComplete()) {
            // Cancel the countdown timer
            Timer timer = game.getRoundTimer();
            if (timer != null) {
                timer.cancel();
                game.setRoundTimer(null);
            }

            // Mark round as done
            currentRound.setVotingComplete();
        }

        // Find real spy and check if the majority vote is for the spy
        String spyName = currentRound.getSpy().getUsername();
        boolean spyCaught = votesWereCast && mostVoted != null && mostVoted.equals(spyName);

        // Broadcast if spy is caught or not
        if (spyCaught && (highestVoteCount >= majorityThreshold)) {

            messagingService.broadcastMessage(game, Map.of(
                    "event", "spyCaught",
                    "spy", mostVoted,
                    "votes", highestVoteCount));

            for (Map.Entry<String, String> vote : voteMap.entrySet()) {
                String voter = vote.getKey();
                String target = vote.getValue();

                if (target.equals(spyName) && !voter.equals(spyName)) {
                    // Award the players who voted correctly for the spy a point
                    game.updateScore(voter, 1);
                }
            }
        } else {
            // Award a point to the spy if not caught
            game.updateScore(currentRound.getSpy().getUsername(), 1);
            messagingService.broadcastMessage(game, Map.of(
                    "event", "spyNotCaught"));
        }

        // Only deduct points if the round is over
        if (currentRound.isVotingComplete()) {
            // Deduct points from players who did not vote
            for (Player p : players) {
                if (!voteMap.containsKey(p.getUsername()) && p.equals(currentRound.getSpy())) {
                    game.updateScore(p.getUsername(), -1);
                    messagingService.sendMessage(p.getSession(), Map.of(
                            "event", "notVoted"));
                }
            }
        }

        // Reveal spy and updated scoreboard
        messagingService.broadcastMessage(game, Map.of("event", "spyReveal", "spy", spyName));

        messagingService.broadcastMessage(game, Map.of("event", "scoreboard", "scores", game.getScoreboard()));

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
        List<Player> players = game.getPlayers();

        Player spy = players.stream()
                .filter(p -> p.getUsername().equals(spyUsername))
                .findFirst()
                .orElse(null);

        // Stop if a player tries to guess location (not legal)
        if (spy == null || spy.getGameRole() != GameRole.SPY)
            return;

        // Not legal to try after the round is over
        if (currentRound.isVotingComplete())
            return;

        // Stop timer
        Timer timer = game.getRoundTimer();
        if (timer != null) {
            timer.cancel();
            game.setRoundTimer(null);
        }

        currentRound.setVotingComplete();

        // Find spy and update points
        if (currentRound.getLocation().equals(location)) {
            // Spy is correct
            messagingService.broadcastMessage(game, Map.of(
                    "event", "spyGuessCorrect",
                    "spy", spyUsername,
                    "location", location));

            game.updateScore(spyUsername, 1);
        } else {
            // Spy is incorrect
            messagingService.broadcastMessage(game, Map.of(
                    "event", "spyGuessIncorrect",
                    "spy", spyUsername,
                    "location", location));

            for (Player player : players) {
                if (!player.getUsername().equals(spyUsername) && player.getGameRole() != GameRole.SPY) {
                    game.updateScore(player.getUsername(), 1);
                }
            }
        }

    }
}
