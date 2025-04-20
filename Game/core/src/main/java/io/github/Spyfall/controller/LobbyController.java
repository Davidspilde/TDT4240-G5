package io.github.Spyfall.controller;

import java.util.List;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.websocket.SendMessageService;
import io.github.Spyfall.view.LobbyStage;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.ui.ErrorPopup;

public class LobbyController {
    private static LobbyController instance;
    private final GameModel gameModel;
    private final SendMessageService sendMessageService;

    private LobbyController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
    }

    public static LobbyController getInstance() {
        if (instance == null) {
            instance = new LobbyController();
        }
        return instance;
    }

    public void handleLobbyUpdate(List<String> players) {
        gameModel.getLobbyData().setPlayers(players);
        System.out.println("PLAYERS: " + gameModel.getLobbyData().getPlayers());

        updateCurrentLobbyStage();
    }

    private void updateCurrentLobbyStage() {
        StageView currentStage = StageManager.getInstance().getStage();
        if (currentStage instanceof LobbyStage lobbyStage) {
            lobbyStage.updateFromModel();
        }
    }

    public void handleLobbyNewHost(String host) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLobbyNewHost'");
    }

    public void handleLobbyJoined(String host, String lobbyCode) {
        gameModel.getLobbyData().setHostPlayer(host);
        if (gameModel.getCurrentState() != GameState.LOBBY) {
            gameModel.setCurrentState(GameState.LOBBY);
        } else {
            ErrorPopup.getInstance().showClientError("Wrong state");
        }
    }

    public void handleLobbyCreated(String host, String lobbyCode) {
        gameModel.setLobbyCode(lobbyCode);
        gameModel.getLobbyData().setHostPlayer(host);
        gameModel.getLobbyData().getPlayers().clear();
        gameModel.getLobbyData().addPlayer(gameModel.getUsername());
        System.out.println("Handling lobby created: " + lobbyCode);

        // transition to game config state
        if (gameModel.getCurrentState() != GameState.GAME_CONFIG) {
            gameModel.setCurrentState(GameState.GAME_CONFIG);
        } else {
            System.out.println("WRONG STATE: " + gameModel.getCurrentState());
        }
    }

    public void handleGameStarted() {

    }

    public void createLobby(String username) {
        AudioService.getInstance().playSound("click");

        // validate username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is empty");
            return;
        }
        gameModel.setUsername(username);
        gameModel.getLobbyData().setHostPlayer(username);

        sendMessageService.createLobby(username);
    }

    public void updateLobbySettings(int roundLimit, int locationNumber, int maxPlayers, int timePerRound,
            int spyLastAttemptTime) {
        boolean success = sendMessageService.updateLobbyOptions(
                gameModel.getUsername(),
                gameModel.getLobbyCode(),
                roundLimit,
                locationNumber,
                maxPlayers,
                timePerRound,
                spyLastAttemptTime);

        if (success) {
            // Update local model immediately, it will be confirmed by server response
            gameModel.getLobbyData().setRoundLimit(roundLimit);
            gameModel.getLobbyData().setLocationCount(locationNumber);
            gameModel.getLobbyData().setMaxPlayers(maxPlayers);
            gameModel.getLobbyData().setTimePerRound(timePerRound);
        } else {
            System.out.println("Failed to send update lobby options request");
        }
    }

    public void startGame() {
        AudioService.getInstance().playSound("click");

        // only the host can start the game
        if (!gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer())) {
            System.out.println("Only the host can start the game");
            return;
        }

        boolean success = sendMessageService.startGame(
                gameModel.getUsername(),
                gameModel.getLobbyCode());

        if (!success) {
            System.out.println("Failed to send start game request");
        }
        // do stuff here
    }

    public void leaveLobby() {
        try {
            AudioService.getInstance().playSound("click");
            // Send leave lobby request to server

            SendMessageService.getInstance().leaveLobby(gameModel.getUsername(), gameModel.getLobbyCode());
            gameModel.setCurrentState(GameState.MAIN_MENU);

        } catch (Exception e) {
            System.err.println("An error occurred while leaving the lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
