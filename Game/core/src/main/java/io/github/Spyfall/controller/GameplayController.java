package io.github.Spyfall.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.message.response.GameCompleteMessage;
import io.github.Spyfall.message.response.GameNewRoundMessage;
import io.github.Spyfall.message.response.GameRoundEndedMessage;
import io.github.Spyfall.message.response.GameSpyCaughtMessage;
import io.github.Spyfall.message.response.GameSpyGuessMessage;
import io.github.Spyfall.message.response.GameVoteMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.SendMessageService;
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
     * Handle incoming messages from the server
     */
    public void handleServerMessage(ResponseMessage message) {
        if (message instanceof GameCompleteMessage) {
            handleGameComplete((GameCompleteMessage) message);
        } else if (message instanceof GameNewRoundMessage) {
            handleNewRound((GameNewRoundMessage) message);
        } else if (message instanceof GameRoundEndedMessage) {
            handleRoundEnded((GameRoundEndedMessage) message);
        } else if (message instanceof GameSpyCaughtMessage) {
            handleSpyCaught((GameSpyCaughtMessage) message);
        } else if (message instanceof GameSpyGuessMessage) {
            handleSpyGuess((GameSpyGuessMessage) message);
        } else if (message instanceof GameVoteMessage) {
            handleVote((GameVoteMessage) message);
        } else {
            System.out.println("GameplayController: Unexpected message type: " + message.getClass().getName());
        }
    }

    /**
     * Handle the locations coming from the server
     * @param message
     */
    public void handleLocationsUpdate(ResponseMessage message) {
        // TODO:
    }

    /**
     * Handle game complete message
     */
    private void handleGameComplete(GameCompleteMessage message) {
        System.out.println("Game complete received");
    
        updateGameData(data -> {
            data.setRoundEnded(true);
            
            if (message.getScoreboard() != null) {
                data.setScoreboard(message.getScoreboard());
            }
        });
        
        Gdx.app.postRunnable(() -> {
            audioService.playMusic("victory", true);
            
            if (gameModel.getCurrentState() != GameState.GAME_OVER) {
                gameModel.setCurrentState(GameState.GAME_OVER);
            }
        });
    
    }

    /**
     * Handle new round message
     */
    private void handleNewRound(GameNewRoundMessage message) {
        System.out.println("New round received: Round " + message.getRoundNumber());
        System.out.println("Role from server: '" + message.getRole() + "'");

        boolean isSpy = message.getRole().equalsIgnoreCase("spy");
        System.out.println("Is player spy: " + isSpy);
        
        updateGameData(data -> {
            data.setCurrentRound(message.getRoundNumber());
            data.setSpy(isSpy);
            data.setLocation(message.getLocation());
            data.setRole(message.getRole());
            data.setTimeRemaining(message.getRoundDuration());
            data.setRoundEnded(false);
            
            if (data.getScoreboard() == null) {
                data.setScoreboard(new HashMap<>());
            }
        });

        Gdx.app.postRunnable(() -> {
            try {
                System.out.println("Creating proper game stage for round " + message.getRoundNumber());
                
                // Always create a new stage for each round since roles can change
                ScreenViewport viewport = new ScreenViewport();
                BaseGameStage newStage;
                
                if (isSpy) {
                    System.out.println("Creating new SPY stage for round " + message.getRoundNumber());
                    newStage = new SpyGameStage(message.getRole(), viewport);
                } else {
                    System.out.println("Creating new PLAYER stage for round " + message.getRoundNumber());
                    newStage = new PlayerGameStage(message.getLocation(), message.getRole(), viewport);
                }
                
                // Set the new stage
                StageManager.getInstance().setStage(newStage);
                
                // Start timer
                newStage.startTimer(message.getRoundDuration());
                
                System.out.println("Round " + message.getRoundNumber() + " stage created and activated");
                
                // For first round only, trigger state change
                if (gameModel.getCurrentState() != GameState.IN_GAME) {
                    gameModel.setCurrentState(GameState.IN_GAME);
                }
                
            } catch (Exception e) {
                System.err.println("Error in handleNewRound: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
    }


    /**
     * Handle round ended message
     */
    private void handleRoundEnded(GameRoundEndedMessage message) {
        System.out.println("Round ended: Spy was " + message.getSpy());
        
        updateGameData(data -> {
            data.setRoundEnded(true);
            data.setIsSpyUsername(message.getSpy());
            data.setLocation(message.getLocation());
            data.setCurrentRound(message.getRoundNumber());
            
            if (message.getScoreboard() != null) {
                data.setScoreboard(message.getScoreboard());
            }
        });

        Gdx.app.postRunnable(() -> {
            try {
                StageView currentStage = StageManager.getInstance().getStage();
                if (currentStage instanceof BaseGameStage) {
                    BaseGameStage gameStage = (BaseGameStage) currentStage;
                    
                    gameStage.handleRoundEnded(
                        message.getRoundNumber(),
                        message.getReason(),
                        message.getSpy(),
                        message.getLocation(),
                        message.getScoreboard()
                    );
                    
                    // Stop the timer
                    gameStage.stopTimer();
                    gameStage.updateTimerDisplay(0); // in case of latency issues between server and client, set the display to 00:00
                }
            } catch (Exception e) {
                System.err.println("Error updating UI for round end: " + e.getMessage());
                e.printStackTrace();
            }
        });
        
    }

    public void toggleLocationGreyout(String location) {
        gameModel.getGameData().toggleLocationGreyout(location);

    }

    private void handleSpyCaught(GameSpyCaughtMessage message) {
        System.out.println("Spy caught: " + message.getSpy() + " with " + message.getVotes() + " votes");
        
        // Play sound
        AudioService.getInstance().playSound("click");
        
        // Show dialog with result
        // This might be better handled in the view
    }

    private void handleSpyGuess(GameSpyGuessMessage message) {
        System.out.println("Spy " + message.getSpy() + " guessed location: " + message.getLocation());
        
        // Play sound
        AudioService.getInstance().playSound("click");
        
        // Show dialog with result

    }

    private void handleVote(GameVoteMessage message) {
        System.out.println("Vote received: " + message.getVoted() + " was voted");
        
        // Update UI if this stage is active
        // Could trigger an update in the GameLobbyStage
    }


    //==================================================
    // PLAYER ACTIONS
    //==================================================


    /**
     * Leave the current game and return to main menu
     */
    public boolean leaveGame() {
        audioService.playSound("click");
        
        sendMessageService.leaveLobby(gameModel.getUsername(), gameModel.getLobbyCode());
        
        //transition back to main menu
        gameModel.setCurrentState(GameState.MAIN_MENU);
        
        return true;
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

    