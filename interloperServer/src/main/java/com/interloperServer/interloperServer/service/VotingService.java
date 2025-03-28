package com.interloperServer.interloperServer.service;
import java.util.Collections;
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


    public VotingService(MessagingService messagingService, GameManagerService gameManagerService){
        this.messagingService = messagingService;
        this.gameManagerService = gameManagerService;
    }

    public void startVotingPhase(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null) return;

        game.getCurrentRound().clearVotes(); // Reset votes
        messagingService.broadcastMessage(game, "vote-phase");
    }
    
    /**
     * Casts a vote for the spy from a user to another user for the current round
     * @param game the game having a vote
     * @param voterUsername the user voting
     * @param targetUsername the user being voted for
     */
    public void castVote(String lobbyCode, String voterUsername, String targetUsername) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null) return;
    
        Round currentRound = game.getCurrentRound();
        List<Player> players = game.getPlayers();
    
        if (players.stream().noneMatch(p -> p.getUsername().equals(targetUsername))) {
            messagingService.broadcastMessage(game, "Invalid vote. " + targetUsername + " is not in the game.");
            return;
        }
    
        // Register the vote in the current round
        currentRound.castVote(targetUsername);
        messagingService.broadcastMessage(game, voterUsername + " voted for " + targetUsername + " as the Spy.");
    
        // Check if all players have voted
        if (currentRound.getVotes().size() == players.size()) {
            evaluateVotes(lobbyCode);
        }
    }

    /**
     * Checks if the players have managed to vote out the spy
     * @param lobbyCode the lobby having the vote
     */
    public void evaluateVotes(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null) return;
    
        Round currentRound = game.getCurrentRound();
        Map<String, Integer> votes = currentRound.getVotes();
        List<Player> players = game.getPlayers();
        
        if (votes.isEmpty()) {
            messagingService.broadcastMessage(game, "No votes were cast. Moving to the next round.");
            currentRound.setVotingComplete();
            return;
        }
    
        // Determine the player with the most votes
        String mostVoted = Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
        int highestVoteCount = votes.get(mostVoted);
        
        // Check if the highest vote count is a majority (more than 50% of players)
        int totalPlayers = players.size();
        int majorityThreshold = (totalPlayers / 2) + 1; // More than half
    
        boolean spyCaught = false;
    
        if (highestVoteCount >= majorityThreshold) {
            // Check if the most voted player is the Spy
            spyCaught = players.stream()
                .filter(p -> p.getUsername().equals(mostVoted))
                .anyMatch(p -> p.getGameRole() == GameRole.SPY);
        }
    
        // Award points
        if (spyCaught) {
            messagingService.broadcastMessage(game, "The players voted correctly! " + mostVoted + " was the Spy!");
            for (Player player : players) {
                if (player.getGameRole() != GameRole.SPY) {
                    game.updateScore(player.getUsername(), 1);
                }
            }
        } else {
            messagingService.broadcastMessage(game, "The players voted incorrectly. The Spy was not caught!");
            for (Player player : players) {
                if (player.getGameRole() == GameRole.SPY) {
                    game.updateScore(player.getUsername(), 1);
                    break;
                }
            }
        }
    
        // Display updated scores
        messagingService.broadcastMessage(game, "Current Scores: " + game.getScoreboard().toString());
    
        // Move to the next round
        currentRound.setVotingComplete();
        currentRound.clearVotes(); // Reset votes for the next round
    }
}
