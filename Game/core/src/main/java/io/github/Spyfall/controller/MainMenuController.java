package io.github.Spyfall.controller;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.RecieveMessageService;
import io.github.Spyfall.services.SendMessageService;

public class MainMenuController {
    private static MainMenuController instance;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    private MainMenuController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstace();
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

        // validate username, lobbycode
        if (username == null || username.trim().isEmpty() || 
            lobbyCode == null || lobbyCode.trim().isEmpty()) {
            System.out.println("Username or lobby code is empty");
            return;
        }
        
        gameModel.setUsername(username);
        gameModel.setLobbyCode(lobbyCode);
        
        // request join
        boolean success = sendMessageService.joinLobby(username, lobbyCode);
        if (!success) {
            System.out.println("Failed to send join lobby request");
            // error
        }
        
        gameModel.setCurrentState(GameState.LOBBY);
        System.out.println("Joined lobby with code: " + gameModel.getLobbyCode() + ", current state: " + gameModel.getCurrentState());
        // todo: response from server
    }
    
    public void onHowToPlay() {
        // TODO:
        AudioService.getInstance().playSound("click");
        System.out.println("How to play requested");
    }
}
