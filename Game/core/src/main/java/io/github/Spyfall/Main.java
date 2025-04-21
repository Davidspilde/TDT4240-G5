package io.github.Spyfall;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.client.GameClient;
import io.github.Spyfall.launcher.GameLauncher;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public final int WIDTH = 720/2;
    public final int HEIGHT = 1280/2;
    public static final String TITLE = "Spyfall";
    private GameClient gameClient;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        gameClient = GameLauncher.initGameClient(new ScreenViewport());
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.DARK_GRAY);
        gameClient.update();
    }

    @Override
    public void resize(int width, int height){
        gameClient.resize(width, height);
    }
}
