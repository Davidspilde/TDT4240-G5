package io.github.Spyfall.controller;

import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.websocket.SendMessageService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.game.GameOverStage;
import io.github.Spyfall.view.game.GameStage;
import io.github.Spyfall.view.lobby.LobbyStage;
import io.github.Spyfall.view.ui.ErrorPopup;

/**
 * Controller for gameplay-related actions and server message handling
 */
public class GameplayController {
    // singleton
    private static GameplayController instance;

    // services
    private SendMessageService sendMessageService;
    private StageManager stageManager;

    // model reference
    private GameModel gameModel;

    /**
     * Private constructor for singleton pattern
     */
    private GameplayController() {
        this.gameModel = GameModel.getInstance();
        this.stageManager = StageManager.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
    }

    /**
     * Get the singleton instance
     */
    public static GameplayController getInstance() {
        return (instance == null) ? (instance = new GameplayController()) : instance;
    }

    // ==================================================
    // SERVER MESSAGE HANDLING (responses)
    // ==================================================

    /**
     * Handle game complete message
     */
    public void handleGameComplete(HashMap<String, Integer> scoreboard) {
        System.out.println("Game complete received");

        updateGameData(data -> {
            data.setRoundEnded(true);

            if (scoreboard != null) {
                data.setScoreboard(scoreboard);
            }
        });

        if (gameModel.getCurrentState() != GameState.GAME_OVER) {
            gameModel.setCurrentState(GameState.GAME_OVER);
        }
        StageManager stageManager = StageManager.getInstance();
        ScreenViewport viewport = new ScreenViewport();
        GameOverStage gameOverStage = new GameOverStage(scoreboard, viewport);

        stageManager.setStage(gameOverStage);

    }

    /**
     * Handle new round message
     */
    public void handleNewRound(int roundNumber, int roundDuration, String role, String location,
            String firstQuestioneer) {
        System.out.println("New round received: Round " + roundNumber);
        System.out.println("Role from server: '" + roundDuration);

        boolean isSpy = role.equalsIgnoreCase("spy");
        System.out.println("Is player spy: " + isSpy);

        updateGameData(data -> {
            data.setCurrentRound(roundNumber);
            data.setSpy(isSpy);
            data.setLocation(location);
            data.setRole(role);
            data.setTimeRemaining(roundDuration);
            data.setRoundEnded(false);

            if (data.getScoreboard() == null) {
                data.setScoreboard(new HashMap<>());
            }
        });

        try {
            System.out.println("Creating proper game stage for round " + roundNumber);

            // Always create a new stage for each round since roles can change
            ScreenViewport viewport = new ScreenViewport();
            GameStage newStage;

            newStage = new GameStage(role, location, isSpy, viewport);

            // Set the new stage
            StageManager.getInstance().setStage(newStage);

            // Start timer
            newStage.startTimer(roundDuration);

            System.out.println("Round " + roundNumber + " stage created and activated");

            // For first round only, trigger state change
            if (gameModel.getCurrentState() != GameState.IN_GAME) {
                gameModel.setCurrentState(GameState.IN_GAME);
            }

        } catch (Exception e) {
            System.err.println("Error in handleNewRound: " + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * Handle round ended message
     */
    public void handleRoundEnded(String spy, String location, HashMap<String, Integer> scoreboard, int roundNumber,
            String reason) {
        System.out.println("Round ended: Spy was " + spy);

        updateGameData(data -> {
            data.setRoundEnded(true);
            data.setIsSpyUsername(spy);
            data.setLocation(location);
            data.setCurrentRound(roundNumber);

            if (scoreboard != null) {
                data.setScoreboard(scoreboard);
            }
        });

        try {
            StageView currentStage = StageManager.getInstance().getStage();
            GameStage gameStage = (GameStage) currentStage;

            gameStage.handleRoundEnded(
                    roundNumber,
                    reason,
                    spy,
                    location,
                    scoreboard);

            // Stop the timer
            gameStage.stopTimer();
            gameStage.updateTimerDisplay(0); // in case of latency issues between server and client, set the display
                                             // to 00:00
        } catch (Exception e) {
            System.err.println("Error updating UI for round end: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void handleSpyLastAttempt(String spy, int spyLastAttemptDuration) {
        System.out.println(spy);
        System.out.println(spyLastAttemptDuration);
        updateGameData(data -> {

            data.setIsSpyUsername(spy);
            data.setTimeRemaining(spyLastAttemptDuration);
        });

        try {
            StageView currentStage = StageManager.getInstance().getStage();
            if (currentStage instanceof GameStage gameStage) {
                gameStage.startTimer(spyLastAttemptDuration);

                gameStage.showSpyReveal(spy); // this now includes fade-in!
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

    }

    public void handleVote() {
        System.out.println("VOTE HAPPENED");

    }

    // ==================================================
    // PLAYER ACTIONS (requests)
    // ==================================================

    /**
     * Send startNewRound request
     */
    public void onStartNewRound() {

        if (gameModel.getLobbyData().getHostPlayer() != null && gameModel.getUsername() != null) {
            sendMessageService.startNextRound(gameModel.getUsername(), gameModel.getLobbyCode());
            System.out.println("Sent startNewRound request to server");
        } else {
            ErrorPopup.getInstance().showClientError("Only the host can advance the round");
        }
    }

    /**
     * Handle spy guess of location
     *
     * @param spy
     * @param location
     */
    public void onSpyGuess(String location) {
        System.out.println("SPYVOTE LOCATION");
        sendMessageService.spyGuess(gameModel.getUsername(), location, gameModel.getLobbyCode());
    }

    /**
     * Vote a player you suspect is a spy (as a player)
     *
     * @param target
     */
    public void onVotePlayer(String target) {
        System.out.println("WE GET TO VOTING");
        String user = gameModel.getUsername();
        String lobbyCode = gameModel.getLobbyCode();

        if (user.equals(target)) {
            ErrorPopup.getInstance().showClientError("Cannot vote for yourself");
        } else {
            System.out.println(user + " voted for player: " + target);
            sendMessageService.vote(user, target, lobbyCode);
        }

    }

    // Only after game_over
    public void backTolobby() {
        ScreenViewport viewport = new ScreenViewport();
        LobbyStage lobbyStage = new LobbyStage(viewport);
        stageManager.setStage(lobbyStage);
    }

    /**
     * Interface for model data updates
     */
    private interface GameDataUpdater {
        void update(GameData data);
    }

    /**
     * Update the game data in a thread-safe manner
     */
    private void updateGameData(GameDataUpdater updater) {
        if (updater == null)
            return;

        GameData data = gameModel.getGameData();
        if (data != null) {
            updater.update(data);
        }
    }

}
