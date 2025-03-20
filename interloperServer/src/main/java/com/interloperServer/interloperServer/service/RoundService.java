package com.interloperServer.interloperServer.service;
import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;


@Service
public class RoundService {
    private final MessagingService messagingService;
    private final RoleService roleService;

    public RoundService(MessagingService messagingService, RoleService roleService) {
        this.messagingService = messagingService;
        this.roleService = roleService;
    }

    /**
     * Advances the round for a specific game.
     */
    public void advanceRound(Game game) {
        game.getCurrentRound().endRound();

        // Check if there are more rounds
        if (!game.hasMoreRounds()) {
            messagingService.broadcastMessage(game, "All rounds are completed! Final Scores: " + game.getScoreboard().toString());
            return; // The game ends here
        }

        game.startNextRound();

        if (!game.getPlayers().isEmpty()) {
            roleService.assignRoles(game);
        }

        messagingService.broadcastMessage(game, "New round started! Location: " + game.getCurrentRound().getLocation());
    }
}
