package io.github.Spyfall.controller;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.SendMessageService;

public class GameplayController {
    private MainController mainController;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    public GameplayController(MainController mainController) {
        this.mainController = mainController;
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstace();
    }
    
    public void votePlayer(String targetPlayer) {
        boolean success = sendMessageService.vote(
            gameModel.getUsername(),
            targetPlayer,
            gameModel.getLobbyCode()
        );
    }
    
    public void spyGuessLocation(String location) {
        boolean success = sendMessageService.spyVote(
            gameModel.getUsername(),
            location,
            gameModel.getLobbyCode()
        );
    }
    
    public void startNextRound() {
        boolean success = sendMessageService.startNextRound(
            gameModel.getUsername(),
            gameModel.getLobbyCode()
        );
    }
    
    public void endGame() {
        // Send end game request to server
        gameModel.setCurrentState(GameState.MAIN_MENU);
    }
    
    public void leaveGame() {
        // Send leave game request to server
        gameModel.setCurrentState(GameState.MAIN_MENU);
    }
}
