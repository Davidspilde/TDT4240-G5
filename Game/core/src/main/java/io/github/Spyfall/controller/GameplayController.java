package io.github.Spyfall.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.message.response.GameCompleteMessage;
import io.github.Spyfall.message.response.GameNewRoundMessage;
import io.github.Spyfall.message.response.GameRoundEndedMessage;
import io.github.Spyfall.message.response.GameSpyGuessMessage;
import io.github.Spyfall.message.response.GameVoteMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.websocket.SendMessageService;
import io.github.Spyfall.services.websocket.handlers.*;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.game.BaseGameStage;
import io.github.Spyfall.view.game.PlayerGameStage;
import io.github.Spyfall.view.game.SpyGameStage;
import io.github.Spyfall.view.ui.ErrorPopup;

/**
 * Controller for gameplay-related actions and server message handling
 */
public class GameplayController {
    // singleton
    private static GameplayController instance;

    // services
    private SendMessageService sendMessageService;
    private AudioService audioService;

    // model reference
    private GameModel gameModel;
    
    /**
     * Private constructor for singleton pattern
     */
    private GameplayController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
        this.audioService = AudioService.getInstance();
    }

    /**
     * Get the singleton instance
     */
    public static GameplayController getInstance() {
        return (instance == null) ? (instance = new GameplayController()) : instance;
    }

    //==================================================
    // SERVER MESSAGE HANDLING
    //==================================================

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
        
        audioService.playMusic("victory", true);
        
        if (gameModel.getCurrentState() != GameState.GAME_OVER) {
            gameModel.setCurrentState(GameState.GAME_OVER);
        }
    
    }

    /**
     * Handle new round message
     */
    public void handleNewRound(int roundNumber, int roundDuration, String role, String location, String firstQuestioneer) {
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
            BaseGameStage newStage;
            
            if (isSpy) {
                System.out.println("Creating new SPY stage for round " + roundNumber);
                newStage = new SpyGameStage(role, viewport);
            } else {
                System.out.println("Creating new PLAYER stage for round " + roundNumber);
                newStage = new PlayerGameStage(location, role, viewport);
            }
            
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
    public void handleRoundEnded(String spy, String location, HashMap<String, Integer> scoreboard, int roundNumber, String reason) {
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
            if (currentStage instanceof BaseGameStage) {
                BaseGameStage gameStage = (BaseGameStage) currentStage;
                
                gameStage.handleRoundEnded(
                    roundNumber,
                    reason,
                    spy,
                    location,
                    scoreboard
                );
                
                // Stop the timer
                gameStage.stopTimer();
                gameStage.updateTimerDisplay(0); // in case of latency issues between server and client, set the display to 00:00
            }
        } catch (Exception e) {
            System.err.println("Error updating UI for round end: " + e.getMessage());
            e.printStackTrace();
        }
        
    }


    // public void handleSpyCaught(GameSpyCaughtMessage message) {
    //     System.out.println("Spy caught: " + message.getSpy() + " with " + message.getVotes() + " votes");
        
    //     // Play sound
    //     AudioService.getInstance().playSound("click");
        
    //     // Show dialog with result
    //     // This might be better handled in the view
    // }

    /**
     * Handle spy guess of location
     * @param spy
     * @param location
     */
    public void handleSpyGuess(String spy, String location) {
        System.out.println("Spy " + spy + " guessed location: " + location);
        // TODO:
    }

    /**
     * Handle vote
     */
    public void handleVote() {
        System.out.println("VOTE HAPPENED");
        
        // Update UI if this stage is active
        // Could trigger an update in the GameLobbyStage
    }

    //==================================================
    // PLAYER ACTIONS
    //==================================================


    /**
     * Leave the current game and return to main menu
     */
    public void leaveGame() {
        audioService.playSound("click");
        
        sendMessageService.leaveLobby(gameModel.getUsername(), gameModel.getLobbyCode());
        
        //transition back to main menu
        gameModel.setCurrentState(GameState.MAIN_MENU);
    }

    /**
     * Toggle greying out a location as a spy
     * @param location
     */
    public void toggleLocationGreyout(Location location) {
        gameModel.getGameData().toggleLocationGreyout(location);
        System.out.println("Greyed out location: " + location);

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
        if (updater == null) return;
        
        GameData data = gameModel.getGameData();
        if (data != null) {
            updater.update(data);
        }
    }
    
}

    