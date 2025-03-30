package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;

import com.interloperServer.interloperServer.model.Game;
import com.interloperServer.interloperServer.model.GameRole;
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
            messagingService.broadcastMessage(game, "gameComplete:scores: " + game.getScoreboard().toString());
            return; // The game ends here
        }

        game.startNextRound();

        if (!game.getPlayers().isEmpty()) {
            roleService.assignRoles(game);
        }

        // Send message to players about which round it is
        for (Player player : game.getPlayers()) {
            // Show location to players, but not the spy
            if (player.getGameRole() != GameRole.SPY) {
                messagingService.sendMessage(player.getSession(),
                        "round" + game.getCurrentRound().getRoundNumber() + ":location:"
                                + game.getCurrentRound().getLocation());
            } else {
                messagingService.sendMessage(player.getSession(),
                        "round" + game.getCurrentRound().getRoundNumber());
            }
        }

    }
}
