package io.github.Spyfall.controller;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.SendMessageService;
import io.github.Spyfall.view.GameRulesStage;
import io.github.Spyfall.controller.StageManager;

public class MainMenuController {
    private MainController mainController;
    private SendMessageService sendMessageService;
    private GameModel gameModel;

    public MainMenuController(MainController mainController) {
        this.mainController = mainController;
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
    }

    public void onCreateGame() {
        gameModel.setCurrentState(GameState.CREATE_GAME);
    }

    public boolean onJoinLobby(String username, String lobbyCode) {
        gameModel.setUsername(username);
        gameModel.setLobbyCode(lobbyCode);

        // Send join request to server
        boolean success = sendMessageService.joinLobby(username, lobbyCode);
        if (success) {
            // TODO:
            // wait for server response in ReceiveMessageService
            // update the model when we get a lobbyJoined response
        }
        return success;
    }

    public void onHowToPlay() {
        GameRulesStage rulesStage = new GameRulesStage(this);
        StageManager.getInstance().setStage(rulesStage);
    }
}
