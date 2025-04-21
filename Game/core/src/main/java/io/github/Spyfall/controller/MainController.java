package io.github.Spyfall.controller;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.handlers.MessageHandler;
import io.github.Spyfall.message.response.GameCompleteMessage;
import io.github.Spyfall.message.response.GameNewRoundMessage;
import io.github.Spyfall.message.response.GameRoundEndedMessage;
import io.github.Spyfall.message.response.GameSpyCaughtMessage;
import io.github.Spyfall.message.response.GameSpyGuessMessage;
import io.github.Spyfall.message.response.GameVoteMessage;
import io.github.Spyfall.message.response.LobbyCreatedMessage;
import io.github.Spyfall.message.response.LobbyJoinedMessage;
import io.github.Spyfall.message.response.LobbyNewHostMessage;
import io.github.Spyfall.message.response.LobbyPlayersMessage;
import io.github.Spyfall.message.response.ResponseMessage;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameStateObserver;
import io.github.Spyfall.services.RecieveMessageService;
// import io.github.Spyfall.view.GameLobbyStage;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.createGame.CreateGameStage;
import io.github.Spyfall.view.createGame.GameConfigStage;
import io.github.Spyfall.view.game.BaseGameStage;
import io.github.Spyfall.view.game.GameOverStage;
import io.github.Spyfall.view.game.PlayerGameStage;
import io.github.Spyfall.view.game.SpyGameStage;
import io.github.Spyfall.view.lobby.LobbyStage;
import io.github.Spyfall.view.mainMenu.MainMenuStage;

public class MainController implements GameStateObserver, MessageHandler {
    private static MainController instance;
    private StageManager stageManager;
    private GameModel gameModel;
    private ScreenViewport viewport;
    
    // sub-controllers
    private MainMenuController mainMenuController;
    private LobbyController lobbyController;
    private GameplayController gameplayController;
    
    private MainController(ScreenViewport viewport) {
        this.viewport = viewport;
        this.stageManager = StageManager.getInstance();
        this.gameModel = GameModel.getInstance();
        
        // register as observer
        gameModel.addObserver(this);
        
        // Init sub-controllers
        this.mainMenuController = MainMenuController.getInstance();
        this.lobbyController = LobbyController.getInstance();
        this.gameplayController = GameplayController.getInstance();

        RecieveMessageService.GetInstance().setMessageHandler(this);
        
        // Initial state is main menu
        setMainMenuStage();
    }

    public static MainController getInstance(ScreenViewport viewport){
        return (instance == null) ? (instance = new MainController(viewport)) : instance;
    }

    public static MainController getInstance() {
        if (instance == null) {
            throw new RuntimeException("MainController must be initialized with viewport first");
        }
        return instance;
    }
    
    public void setMainMenuStage() {
        MainMenuStage mainMenuStage = new MainMenuStage(viewport, mainMenuController);
        stageManager.setStage(mainMenuStage);
    }
    
    public void setCreateGameStage() {
        CreateGameStage createGameStage = new CreateGameStage(viewport, lobbyController, this);
        stageManager.setStage(createGameStage);
    }

    public void setLobbyStage() {
        // lobby is where players join before game starts
        System.out.println("We also get to setLobbStage");
        LobbyStage lobbyStage = new LobbyStage(viewport);
        System.out.println("We initialize lobbystage");
        stageManager.setStage(lobbyStage);
    }
    
    public void setGameConfigStage() {
        GameConfigStage gameConfigStage = new GameConfigStage(
            viewport,
            gameModel.getLobbyCode(),
            gameModel.getLobbyData().getHostPlayer()
        );
        stageManager.setStage(gameConfigStage);
    }
    
    // public void setGameLobbyStage() {
    //     GameLobbyStage gameLobbyStage = new GameLobbyStage(
    //         gameModel.getGameData().isSpy(),
    //         gameModel.getGameData().getLocation(), 
    //         gameModel.getGameData().getRole(),
    //         viewport);
    //     stageManager.setStage(gameLobbyStage);
    // }
    public void setGameOverStage() {
        GameOverStage gameOverStage = new GameOverStage(gameModel.getGameData().getScoreboard(), viewport);
        stageManager.setStage(gameOverStage);
    }

    // public void setSpyStage() {
    //     SpyGameStage spyGameStage = new SpyGameStage(gameModel.getGameData().getRole(), viewport);
    //     stageManager.setStage(spyGameStage);
    // }

    // public void setPlayerStage() {
    //     GameData gameData = gameModel.getGameData();

    //     PlayerGameStage playerGameStage = new PlayerGameStage(gameData.getLocation(),gameData.getRole(), viewport);
    //     stageManager.setStage(playerGameStage);
    // }
    
    // implementation of GameStateObserver
    @Override
    public void onGameStateChanged(GameModel model) {
        // update view based on model state
        switch (model.getCurrentState()) {
            case MAIN_MENU:
                System.out.println("State: MAIN MENU");
                setMainMenuStage();
                break;
            case CREATE_GAME:
                setCreateGameStage();
                break;
            case LOBBY:
                System.out.println("State: LOBBY");
                setLobbyStage();
                break;
            case GAME_CONFIG:
                setGameConfigStage();
                break;
            case IN_GAME:
                System.out.println("State changed to IN_GAME (warning: stages should be created via GameplayController)");
                break;
            case GAME_OVER:
                System.out.println("State: GAME OVER");
                setGameOverStage();
                break;
            default:
                System.out.println("Something went wrong with state");
                break;
        }
    }

    @Override
    public void handleMessage(ResponseMessage message) {
        String eventType = message.getEvent();
        System.out.println("Handling message of type: " + eventType);
        
        // route to sub-controller based on message class
        if (message instanceof LobbyCreatedMessage || 
            message instanceof LobbyJoinedMessage ||
            message instanceof LobbyNewHostMessage ||
            message instanceof LobbyPlayersMessage) {
            
            lobbyController.handleServerMessage(message);
            
        } else if (message instanceof GameCompleteMessage ||
                   message instanceof GameNewRoundMessage ||
                   message instanceof GameRoundEndedMessage ||
                   message instanceof GameSpyCaughtMessage ||
                   message instanceof GameSpyGuessMessage ||
                   message instanceof GameVoteMessage) {
            
            gameplayController.handleServerMessage(message);
            
        } else {
            System.out.println("Unknown message class: " + message.getClass().getName());
        }
    }
    
    public GameModel getGameModel() {
        return gameModel;
    }
    
    public ScreenViewport getViewport() {
        return viewport;
    }
    
    // Update method called from game loop
    public void update() {
        StageView currentStage = stageManager.getStage();
        if (currentStage != null) {
            currentStage.update();
        }
    }
    
    public void resize(int width, int height) {
        StageView currentStage = stageManager.getStage();
        if (currentStage != null) {
            currentStage.resize(width, height);
        }
    }


    

}
