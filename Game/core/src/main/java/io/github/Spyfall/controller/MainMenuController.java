package io.github.Spyfall.controller;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.RecieveMessageService;
import io.github.Spyfall.services.SendMessageService;
import io.github.Spyfall.view.mainMenu.GameRulesStage;
import io.github.Spyfall.view.ui.ErrorPopup;
import io.github.Spyfall.controller.StageManager;

public class MainMenuController {
    private static MainMenuController instance;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    private MainMenuController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
    }

    public static MainMenuController getInstance() {
        return (instance == null) ? (instance = new MainMenuController()) : instance;
    }
    
    public void onCreateGame() {
        AudioService.getInstance().playSound("click");
        gameModel.setCurrentState(GameState.CREATE_GAME);
        System.out.println("state:" + gameModel.getCurrentState());
    }
    
    public void onJoinLobby(String username, String lobbyCode) {
        AudioService.getInstance().playSound("click");

        // validate username
        if (username == null || username.trim().isEmpty()) {
            ErrorPopup.getInstance().showClientError("Username cannot be empty");
            return;
        }
        
        // validate lobbycode
        if (lobbyCode == null || lobbyCode.trim().isEmpty()) {
            ErrorPopup.getInstance().showClientError("Lobby code cannot be empty");
            return;
        }
        
        gameModel.setUsername(username);
        gameModel.setLobbyCode(lobbyCode);
        
        // request join
        boolean success = sendMessageService.joinLobby(username, lobbyCode);
        if (!success) {
            ErrorPopup.getInstance().showClientError("Failed to join lobby");
            System.out.println("Failed to send join lobby request");
            // error
        } else {
            System.out.println("Joined lobby with code: " + gameModel.getLobbyCode() + ", current state: " + gameModel.getCurrentState());
        }
        
    }

    public void onHowToPlay() {
        GameRulesStage rulesStage = new GameRulesStage(this);
        StageManager.getInstance().setStage(rulesStage);
    }
}
