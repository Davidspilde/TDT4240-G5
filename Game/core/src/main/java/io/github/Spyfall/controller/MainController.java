package io.github.Spyfall.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameStateObserver;

import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.lobby.LobbyStage;
import io.github.Spyfall.view.mainmenu.MainMenuStage;

public class MainController implements GameStateObserver {
    private static MainController instance;
    private StageManager stageManager;
    private GameModel gameModel;
    private ScreenViewport viewport;

    private MainController(ScreenViewport viewport) {
        this.viewport = viewport;
        this.stageManager = StageManager.getInstance();
        this.gameModel = GameModel.getInstance();

        // register as observer
        gameModel.addObserver(this);

        // Initial state is main menu
        setMainMenuStage();
    }

    public static MainController getInstance(ScreenViewport viewport) {
        return (instance == null) ? (instance = new MainController(viewport)) : instance;
    }

    public static MainController getInstance() {
        if (instance == null) {
            throw new RuntimeException("MainController must be initialized with viewport first");
        }
        return instance;
    }

    public void setMainMenuStage() {
        MainMenuStage mainMenuStage = new MainMenuStage(viewport);
        stageManager.setStage(mainMenuStage);
    }

    public void setLobbyStage() {
        // lobby is where players join before game starts
        LobbyStage lobbyStage = new LobbyStage(viewport);
        System.out.println("We initialize lobbystage");
        stageManager.setStage(lobbyStage);
    }

    // implementation of GameStateObserver
    @Override
    public void onGameStateChanged(GameModel model) {
        // update view based on model state
        Gdx.app.postRunnable(() -> {
            switch (model.getCurrentState()) {
                case MAIN_MENU: {
                    System.out.println("State: MAIN MENU");
                    setMainMenuStage();
                    break;
                }
                case LOBBY: {
                    System.out.println("State: LOBBY");
                    setLobbyStage();
                    break;
                }
                case IN_GAME:
                    System.out.println(
                            "State changed to IN_GAME (warning: stages should be created via GameplayController)");
                    break;
                default:
                    System.out.println("State changed");
                    break;
            }
        });
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
