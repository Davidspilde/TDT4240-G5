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
    private final MessagingService messagingService;
    private final LobbyService lobbyService;

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
            MessagingService messagingService, GameManagerService gameManagerService, LobbyService lobbyService) {
        this.lobbyService = lobbyService;
        this.votingService = votingService;
        this.roundService = roundService;
        this.messagingService = messagingService;
        this.gameManagerService = gameManagerService;
    }

    /**
     * Assigns roles (one Spy, rest Players) to all players in the lobby.
     * Example message: {"content": "startGame:a9b7f9", "username": "Alice"}
     * 
     * @return True if the host called the method, false if someone else did
     */
    public boolean startGame(String username, String lobbyCode, WebSocketSession session) {
        Lobby lobby = lobbyService.getLobbyFromLobbyCode(lobbyCode);

        if (!lobby.getHost().getUsername().equals(username)) {
            messagingService.sendMessage(session, Map.of(
                    "event", "error",
                    "message", "Only the host can start the game."));
            return false;
        }

        // Create a new game instance for this lobby
        Game game = new Game(lobby);
        gameManagerService.storeGame(lobby.getLobbyCode(), game);

        roundService.advanceRound(lobbyCode);

        // Start voting countdown for the first round
        startRoundCountdown(lobby.getLobbyCode());

        return true;
    }

    /**
     * Removes a player from a game when they disconnect
     * Ends the game if the player to disconnect is the only one left
     */
    public void handlePlayerDisconnect(WebSocketSession session, String lobbyCode) {

        Game game = gameManagerService.getGame(lobbyCode);

        if (game == null) {
            return;
        }

        lobbyService.removeUser(session);
        // If there is less than 2 left, end it
        if (game.getPlayers().size() < 2) {
            endGame(lobbyCode);
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

        // Broadcast round duration at the beginning of each round
        messagingService.broadcastMessage(game, "roundDuration:" + roundDuration);

        Timer timer = new Timer();
        game.setRoundTimer(timer);

        timer.schedule(new TimerTask() {

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

        // Mark voting as complete
        game.getCurrentRound().setVotingComplete();

        // Notify users that the round has ended
        messagingService.broadcastMessage(game, Map.of(
                "event", "roundEnded",
                "spy", game.getCurrentRound().getSpy().getUsername()));
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
            messagingService.broadcastMessage(game, "Round is not over yet!");
            return;
        }

        roundService.advanceRound(lobbyCode);

        // Stop existing timer if there is one
        Timer existing = game.getRoundTimer();
        if (existing != null) {
            existing.cancel();
        }

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

        // Mark voting as complete
        game.getCurrentRound().setVotingComplete();

        // Evaluate all votes, give and deduct points accordingly
        votingService.evaluateVotes(lobbyCode);

        // Stop timer here as well just to be sure
        Timer timer = game.getRoundTimer();
        if (timer != null) {
            timer.cancel();
            game.setRoundTimer(null);
        }

        // Notify users that the round has ended
        messagingService.broadcastMessage(game, Map.of(
                "event", "roundEnded",
                "spy", game.getCurrentRound().getSpy().getUsername()));
    }

    /**
     * Ends a game and removes it from active games.
     */
    public void endGame(String lobbyCode) {
        Game game = gameManagerService.getGame(lobbyCode);
        if (game == null)
            return;

        gameManagerService.removeGame(lobbyCode);
        messagingService.broadcastMessage(game, Map.of(
                "event", "gameEnded",
                "message", "Game has ended."));
    }

}
