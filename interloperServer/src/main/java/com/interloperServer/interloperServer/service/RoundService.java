package com.interloperServer.interloperServer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.Player;

@Service
public class RoundService {
    private final MessagingService messagingService;
    private final RoleService roleService;
    private final GameManagerService gameManagerService;

    public RoundService(MessagingService messagingService, RoleService roleService,
            GameManagerService gameManagerService) {
        this.messagingService = messagingService;
        this.roleService = roleService;
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

        if (!game.getPlayers().isEmpty()) {
            roleService.assignRoles(game);
        }

        // Send message to players about which round it is
        for (Player player : game.getPlayers()) {
            Map<String, Object> roundMessage = new HashMap<>();
            roundMessage.put("event", "newRound");
            roundMessage.put("roundNumber", game.getCurrentRound().getRoundNumber());
            roundMessage.put("role", player.getGameRole().toString());

            // Show location to players, but not the spy
            if (player.getGameRole() != GameRole.SPY) {
                roundMessage.put("location", game.getCurrentRound().getLocation());
            }

            messagingService.sendMessage(player.getSession(), roundMessage);
        }

    }
}
