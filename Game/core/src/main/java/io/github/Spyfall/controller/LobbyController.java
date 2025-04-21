package io.github.Spyfall.controller;

import com.badlogic.gdx.Gdx;

import io.github.Spyfall.message.response.LobbyCreatedMessage;
import io.github.Spyfall.message.response.LobbyJoinedMessage;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.message.response.LobbyPlayersMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.services.SendMessageService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.lobby.LobbyStage;
import io.github.Spyfall.view.ui.ErrorPopup;

public class LobbyController {
    private static LobbyController instance;
    private SendMessageService sendMessageService;
    private GameModel gameModel;
    
    private LobbyController() {
        this.gameModel = GameModel.getInstance();
        this.sendMessageService = SendMessageService.getInstance();
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
        System.out.println("PLAYERS: " + gameModel.getLobbyData().getPlayers());

        updateCurrentLobbyStage();
    }

    private void handleLobbyNewHost(LobbyNewHostMessage message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleLobbyNewHost'");
    }

    private void handleLobbyJoined(LobbyJoinedMessage message) {
        gameModel.getLobbyData().setHostPlayer(message.getHost());
        Gdx.app.postRunnable(() -> {
            if (gameModel.getCurrentState() != GameState.LOBBY) {
                gameModel.setCurrentState(GameState.LOBBY);
            } else {
                ErrorPopup.getInstance().showClientError("Wrong state");
            }
        });
    }

    private void handleLobbyCreated(LobbyCreatedMessage message) {
        gameModel.setLobbyCode(message.getLobbyCode());
        gameModel.getLobbyData().setHostPlayer(message.getHost());
        gameModel.getLobbyData().getPlayers().clear();
        gameModel.getLobbyData().addPlayer(gameModel.getUsername());
        System.out.println("Lobby created: " + message.getLobbyCode());
        
        Gdx.app.postRunnable(() -> {
            if (gameModel.getCurrentState() != GameState.GAME_CONFIG) {
                gameModel.setCurrentState(GameState.GAME_CONFIG);
            } else {
                System.out.println("Cannot already be in Game config state: " + gameModel.getCurrentState());
            }
            
        });
    }

    public void createLobby(String username) {
        AudioService.getInstance().playSound("click");
        
        // validate username
        if (username == null || username.trim().isEmpty()) {
            System.out.println("Username is empty");
            return;
        }
        gameModel.setUsername(username);

        sendMessageService.createLobby(username);
        System.out.println("Sent createLobby request to server");
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
            gameModel.getLobbyData().setRoundLimit(roundLimit);
            gameModel.getLobbyData().setLocationCount(locationNumber);
            gameModel.getLobbyData().setMaxPlayers(maxPlayers);
            gameModel.getLobbyData().setTimePerRound(timePerRound);
        } else {
            System.out.println("Failed to send update lobby options request");
        }
    }

    private void updateCurrentLobbyStage() {
        StageView currentStage = StageManager.getInstance().getStage();
        if (currentStage instanceof LobbyStage) {
            ((LobbyStage) currentStage).updateFromModel();
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
