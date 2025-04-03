package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;

public class MainMenuStage extends StageView {

    public MainMenuStage(ScreenViewport viewport){
        super(viewport);
        initMainMenu();
    }

    private void initMainMenu() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

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
                StageManager.getInstance().setStage(new CreateGameStage(viewport));
            }
        });

        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Dialog dialog = new Dialog("Warning", skin, "dialog") {
                    @Override
                    public void result(Object obj) {
                        if (obj.equals(true)) {  // Only change stage if "Yes" is pressed
                            StageManager.getInstance().setStage(new GameLobby(true,"meow", "mjes",viewport));
                        }
                    }
                };

                Label label = new Label("Are you sure you want to join the game?", skin);
                label.setWrap(true);
                ScrollPane scrollPane = new ScrollPane(label, skin);
                scrollPane.setFadeScrollBars(false);
                dialog.getContentTable().add(scrollPane).width((float) ((float)viewport.getScreenWidth()*0.83333333333)).height((float) ((float)viewport.getScreenWidth()*0.2));
                System.out.println(dialog.getWidth()+"\t"+dialog.getHeight());
                dialog.button("Yes", true); // Sends "true" when clicked
                dialog.button("No", false);  // Sends "false" when clicked
                dialog.key(Input.Keys.ENTER, true); // Pressing ENTER is the same as clicking "Yes"
                dialog.setDebug(true);
                dialog.show(stage);
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
}
