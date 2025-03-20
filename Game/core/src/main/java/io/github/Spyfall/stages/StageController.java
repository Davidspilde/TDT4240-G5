package io.github.Spyfall.stages;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class StageController {
    protected Stage stage;

    //constructor
    public StageController(ScreenViewport viewport){
        stage = new Stage(viewport);
    }


    //Get stage
    public Stage getStage(){
        return stage;
    }

    //Trenger ikke setStage?

    //update
    public void update(){
        stage.act();
        stage.draw();
    }

    //resize
    public void resize(int width, int height){
        stage.getViewport().update(width, height);
    }
}
