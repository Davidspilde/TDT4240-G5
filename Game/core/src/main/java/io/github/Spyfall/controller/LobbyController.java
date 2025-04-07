package io.github.Spyfall.controller;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.SendMessageService;

public class LobbyController {
    private MainController mainController;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    public LobbyController(MainController mainController) {
        this.mainController = mainController;
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstace();
    }
    
    public void createLobby(String username) {
        gameModel.setUsername(username);
        
        boolean success = sendMessageService.createLobby(username);
        if (success) {
            // Wait for server response in ReceiveMessageService
            // It will update the model when we get a lobbyCreated response
        }
    }
    
    public void updateLobbySettings(int roundLimit, int locationNumber, int maxPlayers, int timePerRound) {
        boolean success = sendMessageService.updateLobbyOptions(
            gameModel.getUsername(),
            gameModel.getLobbyCode(),
            roundLimit,
            locationNumber,
            maxPlayers,
            timePerRound
        );
        
        if (success) {
            // Update local model immediately, it will be confirmed by server response
            gameModel.getLobbyData().setRoundLimit(roundLimit);
            gameModel.getLobbyData().setLocationCount(locationNumber);
            gameModel.getLobbyData().setMaxPlayers(maxPlayers);
            gameModel.getLobbyData().setTimePerRound(timePerRound);
        }
    }
    
    public void startGame() {
        boolean success = sendMessageService.startGame(
        gameModel.getUsername(),
        gameModel.getLobbyCode()
        );
        // transition happens when server responds with ok
    }
    
    public void leaveLobby() {
        // Send leave lobby request to server
        gameModel.setCurrentState(GameState.MAIN_MENU);
    }
}
