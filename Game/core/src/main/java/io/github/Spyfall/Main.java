package io.github.Spyfall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.client.GameClient;
import io.github.Spyfall.launcher.GameLauncher;
import io.github.Spyfall.states.StateManager;
import io.github.Spyfall.states.MainMenuState;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public final int WIDTH = 720;
    public final int HEIGHT = 1280;
    public static final String TITLE = "Spyfall";

    private SpriteBatch batch;
    private StateManager stateManager;
    private Viewport viewport;
    private OrthographicCamera camera;
    private GameClient gameClient;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false,WIDTH,HEIGHT);
        camera.update();
        viewport = new ExtendViewport(WIDTH,HEIGHT,camera);
        gameClient = GameLauncher.initGameClient(camera, viewport);
    }

    @Override
    public void render() {
        gameClient.update();
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height);
        viewport.apply();
        camera.setToOrtho(false,width,height);
        camera.update();
    }
}
