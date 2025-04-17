package com.interloperServer.interloperServer.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.interloperServer.interloperServer.model.*;
import com.interloperServer.interloperServer.service.messagingServices.GameMessageFactory;
import com.interloperServer.interloperServer.service.messagingServices.MessagingService;

/**
 * Service for handling game related logic
 */
@Service
public class GameService {
    private final GameManagerService gameManagerService;
    private final VotingService votingService;
    private final RoundService roundService;
    private final MessagingService messagingService;
    private final GameMessageFactory messageFactory;
    private final LobbyManagerService lobbyManager;

    // All active games
    // private final Map<String, Game> activeGames = new ConcurrentHashMap<>();
    /**
     * Initializes the game service with its dependent services
     * 
     * @param votingService
     * @param roundService
     * @param roleService
     */
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
     * Assigns roles (one Spy, rest Players) to all players in the lobby.
     * Example message: {"content": "startGame:a9b7f9", "username": "Alice"}
     * 
     * @return True if the host called the method, false if someone else did
     */
    public boolean startGame(String username, String lobbyCode, WebSocketSession session) {
        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(lobbyCode);

        if (lobby == null) {
            messagingService.sendMessage(session, messageFactory.error("Lobby doesn't exist."));
            return false;
        }
        // Prevents game to get started when there already is an active game in place
        if (lobby.getGameActive()) {
            messagingService.sendMessage(session, messageFactory.error("Active game is already in session"));
            return false;
        }

        // Prevent users other than host to begin the game
        if (!lobby.getHost().getUsername().equals(username)) {
            messagingService.sendMessage(session, messageFactory.error("Only the host can start the game"));
            return false;
        }

        // Prevent game from starting with too few players
        if (lobby.getPlayers().size() < 3) {
            messagingService.sendMessage(session, messageFactory.error("Too few players to start the game."));
            return false;
        }

        // Create a new game instance for this lobby
        Game game = new Game(lobby);
        gameManagerService.storeGame(lobby.getLobbyCode(), game);

        messagingService.broadcastMessage(lobby, messageFactory.gameStarted());

        roundService.advanceRound(lobbyCode);

        // Start voting countdown for the first round
        startRoundCountdown(lobby.getLobbyCode());

        return true;
    }

    /**
     * Removes a player from a lobby and game when they disconnect
     * Ends the game if there are too few players left after disconnect
     */
    public void handlePlayerDisconnect(WebSocketSession session, String lobbyCode) {
        Lobby lobby = lobbyManager.getLobbyFromLobbyCode(lobbyCode);
        if (lobby == null) {
            return;
        }

        Player player = lobby.getPlayerBySession(session);
        if (player == null)
            return;

        // Schedule them to be removed if they do not come back in 30 seconds
        final int DISCONNECT_BUFFER_SECONDS = 30;
        player.scheduleDisconnectRemoval(() -> {
            // This code runs only after the buffer if the player is still disconnected
            if (player.isDisconnected()) {
                // Now remove them from the lobby
                lobbyManager.removeUser(session);

                // If the game is left with fewer than 3 players, end the game
                if (gameManagerService.hasGame(lobbyCode)) {
                    Game game = gameManagerService.getGame(lobbyCode);
                    if (game != null && game.getPlayers().size() < 3) {
                        endGame(lobbyCode);
                    }

                    // If the disconnected player is the spy -> end the round without awarding any
                    // points
                    else if (game != null && game.getCurrentRound() != null) {
                        Player spy = game.getCurrentRound().getSpy();
                        if (spy != null && spy.getUsername().equals(player.getUsername())) {
                            // Spy disconnected –> end round early
                            roundService.endRoundDueToSpyDisconnect(lobbyCode);
                            return;
                        }
                    }

                }
            }
        }, DISCONNECT_BUFFER_SECONDS);
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

        Round currentRound = game.getCurrentRound();
        if (currentRound == null) {
            return;
        }

        int roundDuration = currentRound.getRoundDuration();

        // Start timer and end the round due to timeout if the spy haven't guessed or
        // the players haven't gotten a majority
        game.startTimer(roundDuration, () -> endRoundDueToTimeout(lobbyCode));
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

        votingService.castSpyGuess(lobbyCode, spyUsername, location);
    }

    /**
     * Advances the game to the next round
     */
    public void advanceRound(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        // Prevent premature advancing
        if (!game.getCurrentRound().isVotingComplete()) {
            return;
        }

        roundService.advanceRound(lobbyCode);

        // Stop existing timer if there is one
        game.stopTimer();

        // Start round countdown
        startRoundCountdown(lobbyCode);
    }

    /**
     * Ends the round, calls roundservice to award points and broadcast end round
     * message
     * 
     * @param lobbyCode
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
     * Ends a game and removes it from active games.
     */
    public void endGame(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        game.getLobby().setGameActive(false);
        gameManagerService.removeGame(lobbyCode);

        messagingService.broadcastMessage(game.getLobby(), messageFactory.gameEnded());
    }

    // Host can end game prematurly
    public void hostEndOngoingGame(String lobbyCode, String username, WebSocketSession session) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null) {
            return;
        }

        if (!game.getLobby().getHost().getUsername().equals(username)) {
            messagingService.sendMessage(session, messageFactory.error("Only the host is allowed to end the game"));
            return;
        }

        endGame(lobbyCode);
    }

}
