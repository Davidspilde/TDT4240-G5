package io.github.Spyfall.controller;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameStateObserver;
import io.github.Spyfall.view.CreateGameStage;
import io.github.Spyfall.view.GameConfigStage;
import io.github.Spyfall.view.GameLobbyStage;
import io.github.Spyfall.view.LobbyStage;
import io.github.Spyfall.view.MainMenuStage;
import io.github.Spyfall.view.StageView;

public class MainController implements GameStateObserver{

    private StageManager stageManager;
    private GameModel gameModel;
    private ScreenViewport viewport;
    
    // sub-controllers
    private MainMenuController mainMenuController;
    private LobbyController lobbyController;
    private GameplayController gameplayController;
    
    public MainController(ScreenViewport viewport) {
        this.viewport = viewport;
        this.stageManager = StageManager.getInstance();
        this.gameModel = GameModel.getInstance();
        
        // register as observer
        gameModel.addObserver(this);
        
        // Init sub-controllers
        this.mainMenuController = new MainMenuController(this);
        this.lobbyController = new LobbyController(this);
        this.gameplayController = new GameplayController(this);
        
        // Initial state is main menu
        setMainMenuStage();
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
        LobbyStage lobbyStage = new LobbyStage(viewport, lobbyController);
        stageManager.setStage(lobbyStage);
    }
    
    public void setGameConfigStage() {
        GameConfigStage gameConfigStage = new GameConfigStage(
            viewport,
            gameModel.getLobbyCode(),
            gameModel.getLobbyData().getHostPlayer(),
            this
        );
        stageManager.setStage(gameConfigStage);
    }
    
    public void setGameLobbyStage() {
        GameLobbyStage gameLobbyStage = new GameLobbyStage(
            gameModel.getGameData().isSpy(),
            gameModel.getGameData().getLocation(), 
            gameModel.getGameData().getRole(),
            viewport,
            gameplayController
        );
        stageManager.setStage(gameLobbyStage);
    }
    
    // implementation of GameStateObserver
    @Override
    public void onGameStateChanged(GameModel model) {
        // update view based on model state
        switch (model.getCurrentState()) {
            case MAIN_MENU:
                setMainMenuStage();
                break;
            case CREATE_GAME:
                setCreateGameStage();
                break;
            case LOBBY:
                setLobbyStage();
                break;
            case GAME_CONFIG:
                setGameConfigStage();
                break;
            case IN_GAME:
                setGameLobbyStage();
                break;
            default:
                System.out.println("Something went wrong with state");
                break;
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
