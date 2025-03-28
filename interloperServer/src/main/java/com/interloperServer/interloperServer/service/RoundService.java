package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;


@Service
public class RoundService {
    private final MessagingService messagingService;
    private final RoleService roleService;
    private final GameManagerService gameManagerService;


    public RoundService(MessagingService messagingService, RoleService roleService, GameManagerService gameManagerService) {
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
