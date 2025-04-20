package io.github.Spyfall.controller;

import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;

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
import io.github.Spyfall.services.websocket.SendMessageService;
import io.github.Spyfall.view.GameLobbyStage;
import io.github.Spyfall.view.StageView;

public class GameplayController {
    private static GameplayController instance;
    private SendMessageService sendMessageService;
    private GameModel gameModel;

    private GameplayController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
    }

    public static GameplayController getInstance() {
        return (instance == null) ? (instance = new GameplayController()) : instance;
    }

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

    private void handleGameComplete(GameCompleteMessage message) {
        System.out.println("Game complete received: " + message.getScoreboard());

        // Play sound
        AudioService.getInstance().playSound("click");

        // TODO: Show scoreboard in a dialog or screen

        // Return to lobby after game ends
        gameModel.setCurrentState(GameState.LOBBY);
    }

    private void handleNewRound(GameNewRoundMessage message) {

        System.out.println("New round received: Round " + message.getRoundNumber());

        // Update game data
        gameModel.getGameData().setCurrentRound(message.getRoundNumber());
        gameModel.getGameData().setSpy(message.getRole().equals("spy"));
        gameModel.getGameData().setLocation(message.getLocation());
        gameModel.getGameData().setRole(message.getRole());
        gameModel.getGameData().setTimeRemaining(message.getRoundDuration()); //
        gameModel.getGameData().setRole(message.getRole());

        gameModel.getGameData().setRoundEnded(false);

        // test locations
        if (message.getRole().equals("spy")) {
            List<String> defaultLocations = Arrays.asList(
                    "Airplane", "Bank", "Beach", "Casino", "Hospital",
                    "Hotel", "Military Base", "Movie Studio", "Ocean Liner",
                    "Passenger Train", "Restaurant", "School", "Space Station",
                    "Submarine", "Supermarket", "University");
            gameModel.getGameData().setPossibleLocations(defaultLocations);
        }

        Gdx.app.postRunnable(() -> {
            // Change game state if not already in game
            if (gameModel.getCurrentState() != GameState.IN_GAME) {
                gameModel.setCurrentState(GameState.IN_GAME);
            }

            StageView currentStage = StageManager.getInstance().getStage();
            if (currentStage instanceof GameLobbyStage) {
                ((GameLobbyStage) currentStage).resetRoundEndUI();

                ((GameLobbyStage) currentStage).startTimer(message.getRoundDuration());
            }
        });
    }

    private void handleRoundEnded(GameRoundEndedMessage message) {
        System.out.println("HELLLOOOOO: Spy was " + message.getSpy());

        // Update game model with round end information
        GameData gameData = gameModel.getGameData();
        gameData.setRoundEnded(true);
        gameData.setIsSpyUsername(message.getSpy());

        gameData.setLocation(message.getLocation());
        gameData.setCurrentRound(message.getRoundNumber());

        if (message.getScoreboard() != null) {
            gameData.setScoreboard(message.getScoreboard());
        }

        Gdx.app.postRunnable(() -> {
            try {
                StageView currentStage = StageManager.getInstance().getStage();
                if (currentStage instanceof GameLobbyStage) {

                    ((GameLobbyStage) currentStage).handleRoundEnded(
                            message.getRoundNumber(), // Round number from message
                            message.getReason(), // Reason for round end (e.g., "TIMEOUT")
                            message.getSpy(), // Spy player name
                            message.getLocation(), // Location from message
                            message.getScoreboard() // Scoreboard
                    );

                    ((GameLobbyStage) currentStage).updateTimerDisplay(0);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }

        });

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

    /**
     * Vote for a player being the spy
     *
     * @param targetPlayer The username of the player to vote for
     * @return Whether the vote was sent successfully
     */
    public boolean votePlayer(String targetPlayer) {
        AudioService.getInstance().playSound("click");

        // validate target
        if (targetPlayer == null || targetPlayer.trim().isEmpty()) {
            System.out.println("Target player is empty");
            return false;
        }

        // can't vote for yourself idiot
        if (targetPlayer.equals(gameModel.getUsername())) {
            System.out.println("Can't vote for yourself");
            return false;
        }

        boolean success = sendMessageService.vote(
                gameModel.getUsername(),
                targetPlayer,
                gameModel.getLobbyCode());

        if (!success) {
            System.out.println("Failed to send vote request");
        }

        return success;
    }

    /**
     * Spy guesses the location
     *
     * @param location The location the spy is guessing
     * @return Whether the guess was sent successfully
     */
    public boolean spyGuessLocation(String location) {
        AudioService.getInstance().playSound("click");

        // validate location
        if (location == null || location.trim().isEmpty()) {
            System.out.println("Location is empty");
            return false;
        }

        // only the spy can guess
        if (!gameModel.getGameData().isSpy()) {
            System.out.println("Only the spy can guess the location");
            return false;
        }

        boolean success = sendMessageService.spyVote(
                gameModel.getUsername(),
                location,
                gameModel.getLobbyCode());

        if (!success) {
            System.out.println("Failed to send spy guess request");
        }

        return success;
    }

    /**
     * Start the next round of the game
     *
     * @return Whether the request was sent successfully
     */
    public boolean startNextRound() {
        AudioService.getInstance().playSound("click");

        // Only the host can start the next round
        if (!gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer())) {
            System.out.println("Only the host can start the next round");
            return false;
        }

        boolean success = sendMessageService.startNextRound(
                gameModel.getUsername(),
                gameModel.getLobbyCode());

        if (!success) {
            System.out.println("Failed to send start next round request");
        }

        return success;
    }

    /**
     * End the current game and return to lobby
     */
    public boolean endGame() {
        AudioService.getInstance().playSound("click");

        // Only the host can end the game
        if (!gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer())) {
            System.out.println("Only the host can end the game");
            return false;
        }

        // TODO: Send end game request to server
        // For now, just transition back to lobby
        gameModel.setCurrentState(GameState.LOBBY);

        return true;
    }

    /**
     * Leave the current game and return to main menu
     */
    public boolean leaveGame() {
        AudioService.getInstance().playSound("click");

        // TODO: Send leave game request to server

        // For now, just transition back to main menu
        gameModel.setCurrentState(GameState.MAIN_MENU);

        return true;
    }

    /**
     * Update the timer (libgdx timer)
     *
     * @param delta Time passed in seconds
     */
    public void updateTimer(float delta) {
        int currentTime = gameModel.getGameData().getTimeRemaining();
        if (currentTime > 0) {
            currentTime -= Math.round(delta);
            if (currentTime < 0)
                currentTime = 0;
            gameModel.getGameData().setTimeRemaining(currentTime);
        }
    }

}
