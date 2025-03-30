package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.*;

import java.util.*;

/**
 * Service for handling game related logic
 */
@Service
public class GameService {
    private final GameManagerService gameManagerService;
    private final VotingService votingService;
    private final RoundService roundService;
    private final RoleService roleService;
    private final MessagingService messagingService;

    // All active games
    // private final Map<String, Game> activeGames = new ConcurrentHashMap<>();

    /**
     * Initializes the game service with its dependent services
     * 
     * @param votingService
     * @param roundService
     * @param roleService
     */
    public GameService(VotingService votingService, RoundService roundService, RoleService roleService,
            MessagingService messagingService, GameManagerService gameManagerService) {
        this.votingService = votingService;
        this.roundService = roundService;
        this.roleService = roleService;
        this.messagingService = messagingService;
        this.gameManagerService = gameManagerService;
    }

    /**
     * Assigns roles (one Spy, rest Players) to all players in the lobby.
     * Example message: {"content": "startGame:a9b7f9", "username": "Alice"}
     * 
     * @return True if the host called the method, false if someone else did
     */
    public boolean startGame(String lobbyCode, String username, LobbyService lobbyService, WebSocketSession session) {
        if (!lobbyService.isHost(lobbyCode, username)) {
            messagingService.sendMessage(session, "Only the host can start the game.");
            return false;
        }

        List<Player> players = lobbyService.getPlayersInLobby(lobbyCode);

        // Create a new game instance for this lobby
        Game game = new Game(lobbyCode, players, 5, 10); // Example: 5 rounds, 20 seconds per round
        gameManagerService.storeGame(lobbyCode, game);

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

        // Start voting countdown for the first round
        startRoundCountdown(lobbyCode);

        return true;
    }

    /**
     * Removes a player from a game when they disconnect
     * Ends the game if the player to disconnect is the only one left
     */
    public void handlePlayerDisconnect(WebSocketSession session, String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        // Find the player who disconnected
        Player disconnectedPlayer = null;
        for (Player player : game.getPlayers()) {
            if (player.getSession().equals(session)) {
                disconnectedPlayer = player;
                break;
            }
        }

        if (disconnectedPlayer == null)
            return; // Player not found in the game

        // Remove player from the game
        game.getPlayers().remove(disconnectedPlayer);
        messagingService.broadcastMessage(game, "left:" + disconnectedPlayer.getUsername());

        // If the removed player was the host, assign a new host
        if (disconnectedPlayer.getLobbyRole() == LobbyRole.HOST && !game.getPlayers().isEmpty()) {
            game.getPlayers().get(0).setLobbyRole(LobbyRole.HOST);
            messagingService.broadcastMessage(game,
                    "newHost:" + game.getPlayers().get(0).getUsername());
        }

        // If the game is now empty, end it
        if (game.getPlayers().isEmpty()) {
            gameManagerService.removeGame(lobbyCode); // Remove game when empty
            messagingService.broadcastMessage(game, "Game has ended.");
        }
    }

    /**
     * Start counting down the round
     * 
     * @param lobbyCode
     */
    public void startRoundCountdown(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        int roundDuration = game.getCurrentRound().getRoundDuration();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                beginEndOfRound(lobbyCode);
            }
        }, roundDuration * 1000);
    }

    /**
     * Casts a vote for the spy from a user to another user for the current round
     * 
     * @param lobbyCode the lobby for the game
     * @param voter     the username of the player voting
     * @param target    the username of the player being voted for
     */
    public void castVote(String lobbyCode, String voter, String target) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        votingService.castVote(lobbyCode, voter, target);
    }

    /**
     * Advance round if voting is done
     * 
     * @param lobbyCode
     */
    public void checkVotingAndAdvance(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();
        if (currentRound.isVotingComplete()) {
            advanceRound(lobbyCode);
        } else {
            messagingService.broadcastMessage(game, "Voting is not complete yet!");
        }
    }

    /**
     * Advances the game to the next round
     */
    public void advanceRound(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        roundService.advanceRound(lobbyCode);

        // Start round countdown
        startRoundCountdown(lobbyCode);
    }

    /**
     * Shows the scoreboard after the round is complete
     * 
     * @param lobbyCode
     */
    public void beginEndOfRound(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        messagingService.broadcastMessage(game, "End of round");

        // Mark voting as complete
        game.getCurrentRound().setVotingComplete();

        // Evaluate all votes, give and deduct points accordingly
        votingService.evaluateVotes(lobbyCode);

        // ⬇Notify users that the round has ended
        messagingService.broadcastMessage(game, "End of round. Spy was: " +
                game.getPlayers().stream()
                        .filter(p -> p.getGameRole() == GameRole.SPY)
                        .map(Player::getUsername)
                        .findFirst()
                        .orElse("Unknown"));
    }

    /**
     * Ends a game and removes it from active games.
     */
    public void endGame(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        gameManagerService.removeGame(lobbyCode);
        messagingService.broadcastMessage(game, "Game has ended.");
    }

}
