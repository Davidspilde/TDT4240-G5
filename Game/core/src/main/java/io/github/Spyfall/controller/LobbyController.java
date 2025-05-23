package io.github.Spyfall.controller;

import java.util.List;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.model.LobbyData;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.websocket.SendMessageService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.lobby.LobbyStage;
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

    // ==================================================
    // SERVER MESSAGE HANDLING (responses)
    // ==================================================

    /**
     * Handle an incoming update to the lobby
     *
     * @param players
     */
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

    public void handleLobbyLocationsUpdate(List<Location> locations) {
        gameModel.getLobbyData().setLocations(locations);
    }

    public void handleLobbyNewHost(String host) {
        gameModel.getLobbyData().setHostPlayer(host);
    }

    /**
     * Handle joinLobby request
     *
     * @param host
     * @param lobbyCode
     */
    public void handleLobbyJoined(String host, String lobbyCode) {

        gameModel.getLobbyData().setHostPlayer(host);
        if (gameModel.getCurrentState() != GameState.LOBBY) {
            gameModel.setCurrentState(GameState.LOBBY);
        } else {
            ErrorPopup.getInstance().showClientError("Wrong state");
        }
    }

    /**
     * Handle createLobby message
     *
     * @param host
     * @param lobbyCode
     */
    public void handleLobbyCreated(String host, String lobbyCode) {
        gameModel.setLobbyCode(lobbyCode);
        gameModel.getLobbyData().setHostPlayer(host);
        gameModel.getLobbyData().getPlayers().clear();
        gameModel.getLobbyData().addPlayer(gameModel.getUsername());
        System.out.println("Handling lobby created: " + lobbyCode);

        // transition to Lobby Stage
        if (gameModel.getCurrentState() != GameState.LOBBY) {
            gameModel.setCurrentState(GameState.LOBBY);
        } else {
            System.out.println("Cannot already be in Game config state: " + gameModel.getCurrentState());
        }
        // Send default lobby settings to backend
        int roundLimit = gameModel.getLobbyData().getRoundLimit();
        int locationCount = gameModel.getLobbyData().getLocationLimit();
        int maxPlayers = gameModel.getLobbyData().getMaxPlayers();
        int timePerRound = gameModel.getLobbyData().getTimePerRound();
        int spyLastAttemptTime = gameModel.getLobbyData().getSpyLastAttemptTime();

        updateLobbyOptions(roundLimit, locationCount, maxPlayers, timePerRound, spyLastAttemptTime);
    }

    /**
     * Handle gameStarted request
     */
    public void handleGameStarted() {
        // temp
        gameModel.getGameData().setPossibleLocations(gameModel.getLobbyData().getLocations());

        System.out.println("Server sent game started response");
        if (gameModel.getCurrentState() != GameState.IN_GAME) {
            gameModel.setCurrentState(GameState.IN_GAME);
        }
    }

    // ==================================================
    // PLAYER ACTIONS (requests)
    // ==================================================

    /**
     * Send createLobby request
     *
     * @param username
     */
    public void createLobby(String username) {

        // validate username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is empty");
            ErrorPopup.getInstance().showClientError("username is empty");
        }
        gameModel.setUsername(username);
        sendMessageService.createLobby(username);
        System.out.println("Sent createLobby request to server");
    }

    /**
     * Update lobbySettings
     *
     * @param roundLimit
     * @param locationNumber
     * @param maxPlayers
     * @param timePerRound
     * @param spyLastAttemptTime
     */
    public void updateLobbyOptions(int roundLimit, int locationNumber, int maxPlayers, int timePerRound,
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
            gameModel.getLobbyData().setRoundLimit(roundLimit);
            gameModel.getLobbyData().setLocationCount(locationNumber);
            gameModel.getLobbyData().setMaxPlayers(maxPlayers);
            gameModel.getLobbyData().setTimePerRound(timePerRound);
        } else {
            System.out.println("Failed to send update lobby options request");
        }
    }

    /**
     * Send startGame request
     */
    public void startGame() {

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

    /**
     * Leave lobby and inform the server
     */
    public void leaveLobby() {
        try {
            SendMessageService.getInstance().leaveLobby(gameModel.getUsername(), gameModel.getLobbyCode());
            gameModel.setCurrentState(GameState.MAIN_MENU); // transitition to main menu

        } catch (Exception e) {
            System.err.println("An error occurred while leaving the lobby: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void updateLobbyLocations(List<Location> locations) {
        sendMessageService.updateLobbyLocations(gameModel.getUsername(), gameModel.getLobbyCode(), locations);

    }

    public LobbyData getLobbyData() {
        return GameModel.getInstance().getLobbyData();
    }

}
