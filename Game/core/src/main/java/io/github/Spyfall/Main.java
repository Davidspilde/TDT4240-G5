package io.github.Spyfall;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.client.GameClient;
import io.github.Spyfall.launcher.GameLauncher;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all
 * platforms.
 */
public class Main extends ApplicationAdapter {
    public final int WIDTH = 720 / 2;
    public final int HEIGHT = 1280 / 2;
    public static final String TITLE = "interloper";
    private GameClient gameClient;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        gameClient = GameLauncher.initGameClient(new ScreenViewport());
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        gameClient.update();
    }

    @Override
    public void resize(int width, int height) {
        gameClient.resize(width, height);
    }
}
