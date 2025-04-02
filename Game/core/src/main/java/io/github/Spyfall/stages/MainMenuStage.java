package io.github.Spyfall.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

public class MainMenuStage extends StageController{

    public MainMenuStage(ScreenViewport viewport){
        super(viewport);
        initMainMenu();
    }

    private void initMainMenu() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("metal-ui.json"));

        // Create UI Elements
        TextButton createGameButton = new TextButton("Create game", skin);
        TextButton joinGameButton = new TextButton("Join game", skin);
        TextButton howToPlayButton = new TextButton("How to play", skin);
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture("Background_city.png")));
        Table table = new Table();
        Image image = new Image(new TextureRegion(new Texture("logo-Photoroom.png")));

        // Add callbacks to buttons
        createGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event,float x, float y){
                changeStage(new CreateGameStage(viewport));
            }
        });

        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                changeStage(new GameLobby(viewport,"TEST"));
            }
        });

        howToPlayButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                System.out.println("Skal egt vise en pop up her");
            }
        });

        // Align Table to Top
        table.top().setFillParent(true);
        table.setBackground(texture);

        // Add padding and spacing
        table.add(image).padBottom((float) viewport.getScreenHeight()/10).padLeft((float) viewport.getScreenWidth() /10).padRight((float) viewport.getScreenWidth() /10).padTop((float) viewport.getScreenHeight()/15);
        table.row();
        table.add(createGameButton).padBottom((float) viewport.getScreenHeight()/10);
        table.row();
        table.add(joinGameButton).padBottom((float) viewport.getScreenHeight()/10);
        table.row();
        table.add(howToPlayButton).padBottom((float) viewport.getScreenHeight()/10);

        // Add UI to Stage
        stage.addActor(table);
    }


    private void changeStage(StageController newStage) {
        // Logic to switch stages (could be using a ScreenManager)
        System.out.println("Stage changed to: " + newStage.getClass().getSimpleName());
    }
}
