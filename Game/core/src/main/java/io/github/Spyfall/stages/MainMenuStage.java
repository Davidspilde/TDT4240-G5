package io.github.Spyfall.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuStage extends StageController{

    private Skin skin;
    private ScreenViewport viewport;
    private Stage stage;


    public MainMenuStage(ScreenViewport viewport){
        this.stage = new Stage(viewport);
    }

    public void initStage() {
        Gdx.input.setInputProcessor(stage);

        skin = new Skin(Gdx.files.internal("metal-ui.json"));
        TextButton button = new TextButton("Hello", skin);
        stage.addActor(button);
    }

    public Stage getStage(){
        return stage;
    }

    public void setStage(){

    }
}
