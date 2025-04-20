package io.github.Spyfall.controller;

import java.util.HashMap;

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

    public void handleGameComplete(HashMap<String, Integer> scoreboard) {
        System.out.println("Game complete received: " + scoreboard);

        // Play sound
        AudioService.getInstance().playSound("click");

        // TODO: Show scoreboard in a dialog or screen

        // Return to lobby after game ends
        gameModel.setCurrentState(GameState.LOBBY);
    }

    public void handleNewRound(int roundNumber, String role, String location, int roundDuration) {

        System.out.println("New round received: Round " + roundNumber);

        // Update game data
        gameModel.getGameData().setCurrentRound(roundNumber);
        gameModel.getGameData().setSpy(role.equals("spy"));
        gameModel.getGameData().setLocation(location);
        gameModel.getGameData().setRole(role);
        gameModel.getGameData().setTimeRemaining(roundDuration);
        gameModel.getGameData().setRole(role);

        gameModel.getGameData().setRoundEnded(false);

        // Change game state if not already in game
        if (gameModel.getCurrentState() != GameState.IN_GAME) {
            gameModel.setCurrentState(GameState.IN_GAME);
        }

        StageView currentStage = StageManager.getInstance().getStage();
        if (currentStage instanceof GameLobbyStage) {
            ((GameLobbyStage) currentStage).resetRoundEndUI();

            ((GameLobbyStage) currentStage).startTimer(roundDuration);
        }

    }

    public void handleRoundEnded(String spy, String location, HashMap<String, Integer> scoreboard, int roundNumber,
            String reason) {
        System.out.println("HELLLOOOOO: Spy was " + spy);

        // Update game model with round end information
        GameData gameData = gameModel.getGameData();
        gameData.setRoundEnded(true);
        gameData.setIsSpyUsername(spy);

        gameData.setLocation(location);
        gameData.setCurrentRound(roundNumber);

        if (scoreboard != null) {
            gameData.setScoreboard(scoreboard);
        }

        try {
            StageView currentStage = StageManager.getInstance().getStage();
            if (currentStage instanceof GameLobbyStage) {

                ((GameLobbyStage) currentStage).handleRoundEnded(
                        roundNumber, // Round number from message
                        reason, // Reason for round end (e.g., "TIMEOUT")
                        spy, // Spy player name
                        location, // Location from message
                        scoreboard // Scoreboard
                );

                ((GameLobbyStage) currentStage).updateTimerDisplay(0);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public void handleSpyLastAttempt(String spy, int duration) {
        System.out.println("Spy caught: " + spy + "and has 45 seconds to guess the location");

        // Show dialog with result
        // This might be better handled in the view
    }

    public void handleSpyGuess(String spy, String location) {
        System.out.println("Spy " + spy + " guessed location: " + location);

        // Play sound
        AudioService.getInstance().playSound("click");

        // Show dialog with result

    }

    public void handleVote() {
        System.out.println("Vote has been added succesfully");

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
