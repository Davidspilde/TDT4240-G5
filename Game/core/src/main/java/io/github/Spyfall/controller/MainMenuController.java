package io.github.Spyfall.controller;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.SendMessageService;

public class MainMenuController {
    private GameController gameController;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    public MainMenuController(GameController gameController) {
        this.gameController = gameController;
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstace();
    }
    
    public void onCreateGame() {
        gameModel.setCurrentState(GameState.CREATE_GAME);
    }
    
    public void onJoinLobby(String username, String lobbyCode) {
        gameModel.setUsername(username);
        gameModel.setLobbyCode(lobbyCode);
        
        // Send join request to server
        boolean success = sendMessageService.joinLobby(username, lobbyCode);
        if (success) {
            // Wait for server response in ReceiveMessageService
            // It will update the model when we get a lobbyJoined response
        }
    }
    
    public void onHowToPlay() {
        // Show how to play dialog or screen
    }
}
