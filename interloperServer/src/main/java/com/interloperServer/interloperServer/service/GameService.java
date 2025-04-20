package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

/**
 * Service for handling game-related logic.
 * <p>
 * This service manages the lifecycle of a game, including starting games,
 * handling player disconnections,
 * advancing rounds, and ending games. It interacts with other services to
 * manage game state and send messages.
 */
@Service
public class GameService {
    private final GameManagerService gameManagerService;
    private final VotingService votingService;
    private final RoundService roundService;
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;
    private final LobbyManagerService lobbyManager;

    public GameService(VotingService votingService, RoundService roundService,
            MessagingService messagingService, GameMessageFactory messageFactory, GameManagerService gameManagerService,

            LobbyManagerService lobbyManager) {
        this.lobbyManager = lobbyManager;
        this.votingService = votingService;
        this.roundService = roundService;
        this.messagingService = messagingService;
        this.messageFactory = messageFactory;
        this.gameManagerService = gameManagerService;
    }

    /**
     * Starts a new game in the specified lobby.
     * <p>
     * Assigns roles to players, initializes the game, and starts the first round.
     *
     * @param username  The username of the player attempting to start the game.
     * @param lobbyCode The code of the lobby where the game is being started.
     * @param session   The WebSocket session of the player.
     * @return {@code true} if the game was successfully started, {@code false}
     *         otherwise.
     */
    public boolean startGame(String username, String lobbyCode, WebSocketSession session) {
        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(lobbyCode);

        if (lobby == null) {
            messagingService.sendMessage(session, messageFactory.error("Lobby doesn't exist."));
            return false;
        }
        // Prevent starting a game if one is already active
        if (lobby.getGameActive()) {
            messagingService.sendMessage(session, messageFactory.error("Active game is already in session"));
            return false;
        }

        // Only the host can start the game
        if (!lobby.getHost().getUsername().equals(username)) {
            messagingService.sendMessage(session, messageFactory.error("Only the host can start the game"));
            return false;
        }

        // Ensure there are enough players to start the game
        if (lobby.getPlayers().size() < 3) {
            messagingService.sendMessage(session, messageFactory.error("Too few players to start the game."));
            return false;
        }

        // Create a new game instance for this lobby
        Game game = new Game(lobby);
        gameManagerService.storeGame(lobby.getLobbyCode(), game);

        // Notify players that the game has started
        messagingService.broadcastMessage(lobby, messageFactory.gameStarted());

        // Start the first round
        roundService.advanceRound(lobbyCode);

        // Start the countdown for the first round
        startRoundCountdown(lobby.getLobbyCode());

        return true;
    }

    /**
     * Handles a player's disconnection from the game.
     * <p>
     * Schedules the player for removal if they do not reconnect within a buffer
     * period.
     * Ends the game if there are too few players left.
     * Ends the round if the spy disconnects.
     *
     * @param session   The WebSocket session of the disconnected player.
     * @param lobbyCode The code of the lobby the player was in.
     */
    public void handlePlayerDisconnect(WebSocketSession session, String lobbyCode) {
        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(lobbyCode);
        if (lobby == null) {
            return;
        }

        Player player = lobby.getPlayerBySession(session);
        if (player == null)
            return;

        // Schedule the player for removal if they do not reconnect within 30 seconds
        final int DISCONNECT_BUFFER_SECONDS = 30;
        player.scheduleDisconnectRemoval(() -> {
            if (!player.isDisconnected()) {
                return;
            }

            // Remove the player from the lobby
            lobbyManager.removeUser(session);

            if (!gameManagerService.hasGame(lobbyCode)) {
                return;
            }

            Game game = gameManagerService.getGame(lobbyCode);

            // End the game if fewer than 3 players remain
            if (game != null && game.getPlayers().size() < 3) {
                endGame(lobbyCode);
            }

            // End the round early if the disconnected player was the spy
            else if (game != null && game.getCurrentRound() != null) {
                Player spy = game.getCurrentRound().getSpy();
                if (spy != null && spy.getUsername().equals(player.getUsername())) {
                    // Spy disconnected –> end round early
                    roundService.endRoundDueToSpyDisconnect(lobbyCode);
                    return;
                }
            }
        }, DISCONNECT_BUFFER_SECONDS);
    }

    /**
     * Starts the countdown for the current round.
     * <p>
     * Ends the round due to timeout if the countdown completes.
     *
     * @param lobbyCode The code of the lobby where the round is taking place.
     */
    public void startRoundCountdown(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();
        if (currentRound == null) {
            return;
        }

        int roundDuration = currentRound.getRoundDuration();

        // Start the timer and end the round due to timeout if necessary
        game.startTimer(roundDuration, () -> endRoundDueToTimeout(lobbyCode));
    }

    /**
     * Casts a vote for the spy during the current round.
     *
     * @param lobbyCode The code of the lobby where the game is taking place.
     * @param voter     The username of the player casting the vote.
     * @param target    The username of the player being voted for.
     */
    public void castVote(String lobbyCode, String voter, String target) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        votingService.castVote(lobbyCode, voter, target);
    }

    /**
     * Processes the spy's guess of the location.
     *
     * @param lobbyCode   The code of the lobby where the game is taking place.
     * @param spyUsername The username of the spy making the guess.
     * @param location    The location being guessed.
     */
    public void castSpyGuess(String lobbyCode, String spyUsername, String location) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        votingService.castSpyGuess(lobbyCode, spyUsername, location);
    }

    /**
     * Advances the game to the next round.
     *
     * @param lobbyCode The code of the lobby where the game is taking place.
     */
    public void advanceRound(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        // Prevent advancing if voting is not complete
        if (!game.getCurrentRound().isVotingComplete()) {
            return;
        }

        roundService.advanceRound(lobbyCode);

        // Stop the existing timer
        game.stopTimer();

        // Start the countdown for the next round
        startRoundCountdown(lobbyCode);
    }

    /**
     * Ends the current round due to a timeout.
     *
     * @param lobbyCode The code of the lobby where the game is taking place.
     */
    public void endRoundDueToTimeout(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        Round currentRound = game.getCurrentRound();

        if (currentRound == null) {
            return;
        }

        String spyName = currentRound.getSpy().getUsername();
        roundService.endRoundDueToTimeout(lobbyCode, spyName);
    }

    /**
     * Ends the game and removes it from active games.
     *
     * @param lobbyCode The code of the lobby where the game is taking place.
     */
    public void endGame(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        game.getLobby().setGameActive(false);
        gameManagerService.removeGame(lobbyCode);

        // Notify players that the game has ended
        messagingService.broadcastMessage(game.getLobby(), messageFactory.gameEnded());
    }

    /**
     * Allows the host to end an ongoing game prematurely.
     *
     * @param lobbyCode The code of the lobby where the game is taking place.
     * @param username  The username of the host attempting to end the game.
     * @param session   The WebSocket session of the host.
     */
    public void hostEndOngoingGame(String lobbyCode, String username, WebSocketSession session) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null) {
            return;
        }

        // Only the host can end the game
        if (!game.getLobby().getHost().getUsername().equals(username)) {
            messagingService.sendMessage(session, messageFactory.error("Only the host is allowed to end the game"));
            return;
        }

        endGame(lobbyCode);
    }

}
