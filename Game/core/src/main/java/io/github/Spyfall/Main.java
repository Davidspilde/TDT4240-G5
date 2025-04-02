package io.github.Spyfall;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.client.GameClient;
import io.github.Spyfall.launcher.GameLauncher;
import io.github.Spyfall.stages.GameStage;
import io.github.Spyfall.stages.StageManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    public final int WIDTH = 720;
    public final int HEIGHT = 1280;
    public static final String TITLE = "Spyfall";
    private GameClient gameClient;

    //Ny kode
    private Stage stage;
    private Skin skin;
    private StageManager stageManager;

    private GameStage gameStage;

    @Override
    public void create() {
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
