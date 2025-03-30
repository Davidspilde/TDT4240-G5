package com.interloperServer.interloperServer.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.GameRole;
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
     * @param game           the game having a vote
     * @param voterUsername  the user voting
     * @param targetUsername the user being voted for
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
                messagingService.sendMessage(voter.getSession(),
                        "Invalid vote. " + targetUsername + " is not in the game.");
            }
            return;
        }

        // Don't register vote if the voter doesn't exist
        if (voter == null) {
            return;

        }

        // Register vote
        currentRound.castVote(voterUsername, targetUsername);

        messagingService.broadcastMessage(game, voterUsername + ":voted");

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

        // Find real spy and check if the majority vote is for the spy
        String spyName = players.stream()
                .filter(p -> p.getGameRole() == GameRole.SPY)
                .map(Player::getUsername)
                .findFirst()
                .orElse("Unknown");

        boolean spyCaught = votesWereCast && mostVoted != null && mostVoted.equals(spyName);

        // Broadcast if spy is caught or not
        if (spyCaught && (highestVoteCount >= majorityThreshold)) {
            messagingService.broadcastMessage(game, "spyCaught:" + mostVoted);
            for (Map.Entry<String, String> vote : voteMap.entrySet()) {
                String voter = vote.getKey();
                String target = vote.getValue();

                if (target.equals(spyName) && !voter.equals(spyName)) {
                    // Award the players who voted correctly for the spy a point
                    game.updateScore(voter, 1);
                }
            }
        } else {
            messagingService.broadcastMessage(game, "spyNotCaught");
            for (Player p : players) {
                if (p.getGameRole() == GameRole.SPY) {
                    // Award a point to the spy if not caught
                    game.updateScore(p.getUsername(), 1);
                    break;
                }
            }
        }

        // Only deduct points if the round is over
        if (currentRound.isVotingComplete()) {
            // Deduct points from players who did not vote
            for (Player p : players) {
                if (!voteMap.containsKey(p.getUsername()) && p.getGameRole() != GameRole.SPY) {
                    game.updateScore(p.getUsername(), -1);
                    messagingService.sendMessage(p.getSession(), "You did not vote and lost 1 point");
                }
            }
        }

        // Reveal spy and updated scoreboard
        messagingService.broadcastMessage(game, "spy:" + spyName);
        messagingService.broadcastMessage(game, "scoreboard:" + game.getScoreboard());

        // Mark round as done
        currentRound.setVotingComplete();
    }
}
