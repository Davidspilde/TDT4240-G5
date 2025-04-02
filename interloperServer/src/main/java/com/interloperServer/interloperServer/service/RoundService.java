package com.interloperServer.interloperServer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;

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

        game.getCurrentRound().endRound();

        // Check if there are more rounds
        if (!game.hasMoreRounds()) {
            // Send game completion message with scores
            messagingService.broadcastMessage(game, Map.of(
                    "event", "gameComplete",
                    "scores", game.getScoreboard()));
            return; // The game ends here
        }

        game.startNextRound();
        // Send message to players about which round it is and round duration
        for (Player player : game.getPlayers()) {
            Map<String, Object> roundMessage = new HashMap<>();
            roundMessage.put("event", "newRound");
            roundMessage.put("roundNumber", game.getCurrentRound().getRoundNumber());

            // Show location to players, but not the spy
            if (!game.getCurrentRound().getSpy().equals(player)) {
                roundMessage.put("role", "Player");
                roundMessage.put("location", game.getCurrentRound().getLocation());
            } else {

                roundMessage.put("role", "Spy");
            }

            messagingService.sendMessage(player.getSession(), roundMessage);
        }

    }

    private Player chooseRandomSpy(List<Player> players) {
        Random random = new Random();
        int index = random.nextInt(0, players.size() - 1);

        return players.get(index);

    }
}
