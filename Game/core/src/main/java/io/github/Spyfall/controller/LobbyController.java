package io.github.Spyfall.controller;

import io.github.Spyfall.message.response.LobbyCreatedMessage;
import io.github.Spyfall.message.response.LobbyJoinedMessage;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.message.response.LobbyPlayersMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.SendMessageService;

public class LobbyController {
    private static LobbyController instance;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    private LobbyController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstace();
    }

    public static LobbyController getInstance(){
        return (instance == null) ? (instance = new LobbyController()) : instance;
    }

    public void handleServerMessage(ResponseMessage message) {
    if (message instanceof LobbyCreatedMessage) {
        handleLobbyCreated((LobbyCreatedMessage) message);
    } else if (message instanceof LobbyJoinedMessage) {
        handleLobbyJoined((LobbyJoinedMessage) message);
    } else if (message instanceof LobbyNewHostMessage) {
        handleLobbyNewHost((LobbyNewHostMessage) message);
    } else if (message instanceof LobbyPlayersMessage) {
        handleLobbyUpdate((LobbyPlayersMessage) message);
    } else {
        System.out.println("LobbyController: Unexpected message type: " + message.getClass().getName());
    }
}
    
    private void handleLobbyUpdate(LobbyPlayersMessage message) {
        gameModel.getLobbyData().setPlayers(message.getPlayers());
    }

    private void handleLobbyNewHost(LobbyNewHostMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLobbyNewHost'");
    }

    private void handleLobbyJoined(LobbyJoinedMessage message) {
        gameModel.getLobbyData().setHostPlayer(message.getHost());
    }

    private void handleLobbyCreated(LobbyCreatedMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLobbyCreated'");
    }

    public void createLobby(String username) {
        AudioService.getInstance().playSound("click");
        
        // validate username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is empty");
            return;
        }
        gameModel.setUsername(username);
        
        boolean success = sendMessageService.createLobby(username);
        if (success) {
            gameModel.setCurrentState(GameState.LOBBY);
            System.out.println("State is: " + gameModel.getCurrentState());
        } else {
            System.out.println("something failed on the backend");
        }
    }
    
    public void updateLobbySettings(int roundLimit, int locationNumber, int maxPlayers, int timePerRound) {
        AudioService.getInstance().playSound("click");
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
            gameModel.getLobbyCode()
        );
        
        if (!success) {
            System.out.println("Failed to send start game request");
        }
        // do stuff here
    }
    
    public void leaveLobby() {
        AudioService.getInstance().playSound("click");
        // Send leave lobby request to server
        gameModel.setCurrentState(GameState.MAIN_MENU);
    }
}
