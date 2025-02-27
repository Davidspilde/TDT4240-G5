package io.github.Spyfall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import io.github.Spyfall.Controller.GameStateManager;
import io.github.Spyfall.View.MainMenuState;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public static final int WIDTH = 1080/3;
    public static final int HEIGHT = 2400/3;
    public static final String TITLE = "Spyfall";

    private SpriteBatch batch;
    private GameStateManager gameStateManager;

    @Override
    public void create() {
        gameStateManager = new GameStateManager();

        // Start with the Main Menu
        gameStateManager.setGameState(new MainMenuState(gameStateManager));
    }

    @Override
    public void render() {
        // Clear the screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Update and render the current game state
        gameStateManager.update(Gdx.graphics.getDeltaTime());
        gameStateManager.render();
    }

    @Override
    public void dispose() {
        gameStateManager.dispose();
    }
}
