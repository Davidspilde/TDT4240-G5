package io.github.Spyfall.controller;

import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameStateObserver;
import io.github.Spyfall.view.CreateGameStage;
import io.github.Spyfall.view.GameLobbyStage;
import io.github.Spyfall.view.MainMenuStage;
import io.github.Spyfall.view.StageView;

public class GameController implements GameStateObserver{

    private StageManager stageManager;
    private GameModel gameModel;
    private ScreenViewport viewport;
    
    // Sub-controllers
    private MainMenuController mainMenuController;
    private LobbyController lobbyController;
    private GameplayController gameplayController;
    
    public GameController(ScreenViewport viewport) {
        this.viewport = viewport;
        this.stageManager = StageManager.getInstance();
        this.gameModel = GameModel.getInstance();
        
        // Register as observer
        gameModel.addObserver(this);
        
        // Initialize sub-controllers
        this.mainMenuController = new MainMenuController(this);
        this.lobbyController = new LobbyController(this);
        this.gameplayController = new GameplayController();
        
        // Initial state is main menu
        setMainMenuStage();
    }
    
    public void setMainMenuStage() {
        MainMenuStage mainMenuStage = new MainMenuStage(viewport, mainMenuController);
        stageManager.setStage(mainMenuStage);
    }
    
    public void setCreateGameStage() {
        CreateGameStage createGameStage = new CreateGameStage(viewport);
        stageManager.setStage(createGameStage);
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
    
    // Implementation of GameStateObserver
    @Override
    public void onGameStateChanged(GameModel model) {
        // Update view based on model state
        switch (model.getCurrentState()) {
            case MAIN_MENU:
                setMainMenuStage();
                break;
            case CREATE_GAME:
                setCreateGameStage();
                break;
            case IN_GAME:
                setGameLobbyStage();
                break;
            default:
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
