package io.github.Spyfall.view;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class StageView {
    protected Stage stage;
    protected ScreenViewport viewport;

    //constructor
    public StageView(ScreenViewport viewport){
        stage = new Stage(viewport);
        this.viewport = viewport;
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
