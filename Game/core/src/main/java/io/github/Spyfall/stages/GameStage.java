package io.github.Spyfall.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
public class GameStage {

    private Skin skin;
    private ScreenViewport viewport;
    private Stage stage;

    public GameStage() {
        viewport = new ScreenViewport();
        stage = new Stage(viewport);
    }

    public void initStage() {
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("metal-ui.json"));
        TextButton button = new TextButton("GameStage", skin);
        stage.addActor(button);

    }

    public void update() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        // Resize the viewport so the stage knows how to scale
        viewport.update(width, height, true);
    }

    public Stage getStage(){
        return stage;
    }

}
