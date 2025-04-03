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
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.services.SendMessageService;

public class MainMenuStage extends StageView {
    private SendMessageService sendMsgService;

    public MainMenuStage(ScreenViewport viewport) {
        super(viewport);
        initMainMenu();

        sendMsgService = SendMessageService.getInstace();
    }

    private void initMainMenu() {
        Gdx.input.setInputProcessor(stage);
        Skin skin = new Skin(
                Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create UI Elements
        TextButton createGameButton = new TextButton("Create game", skin);
        TextButton joinGameButton = new TextButton("Join game", skin);
        TextButton howToPlayButton = new TextButton("How to play", skin);
        TextureRegionDrawable texture = new TextureRegionDrawable(
                new TextureRegion(new Texture("Background_city.png")));
        Table table = new Table();
        Image image = new Image(new TextureRegion(new Texture("logo-Photoroom.png")));

        // Add callbacks to buttons
        createGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new CreateGameStage(viewport));
            }
        });

        joinGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                final TextField username = new TextField("", skin);
                final TextField textField = new TextField("", skin);
                textField.setMessageText("Enter Lobby Code");
                username.setMessageText("Enter Username");
                Dialog dialog = new Dialog("Join", skin, "dialog") {
                    @Override
                    public void result(Object obj) {
                        if (obj.equals(true)) {  // Only change stage if "Join" is pressed
                            String lobbyCode = textField.getText();
                            String username_string = username.getText();
                            System.out.println("User typed lobbycode: " + lobbyCode + "\n" + "Username: " + username_string);
                            StageManager.getInstance().setStage(new GameLobby(true,"meow", "mjes",viewport));
                        }
                    }
                };

                dialog.getTitleTable().padTop(20f);
                dialog.getTitleTable().padBottom(5f);

                Label label = new Label("Join Lobby", skin);
                label.setAlignment(Align.center);
                label.setWrap(true);

                ScrollPane scrollPane = new ScrollPane(label, skin);
                scrollPane.setFadeScrollBars(false);
                dialog.getContentTable().add(scrollPane).width((viewport.getScreenWidth()*0.8f)).height((viewport.getScreenWidth()*0.2f)).row();
                dialog.getContentTable().add(textField).width(250).center().pad(15).row();
                dialog.getContentTable().add(username).width(250).center().pad(15);


                System.out.println(dialog.getWidth()+"\t"+dialog.getHeight());
                dialog.button("Join", true); // Sends "true" when clicked
                dialog.button("Cancel", false);  // Sends "false" when clicked
                dialog.key(Input.Keys.ENTER, true); // Pressing ENTER is the same as clicking "Yes"
                // dialog.setDebug(true);
                
                dialog.show(stage);
                dialog.pack(); // for calculating layout libgdx stuff

                dialog.setSize(dialog.getWidth(), dialog.getHeight() + 50);
            }
        });

        howToPlayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Skal egt vise en pop up her");
            }
        });

        // Align Table to Top
        table.top().setFillParent(true);
        table.setBackground(texture);

        // Add padding and spacing
        table.add(image).padBottom((float) viewport.getScreenHeight() / 10)
                .padLeft((float) viewport.getScreenWidth() / 10).padRight((float) viewport.getScreenWidth() / 10)
                .padTop((float) viewport.getScreenHeight() / 15);
        table.row();
        table.add(createGameButton).padBottom((float) viewport.getScreenHeight() / 10);
        table.row();
        table.add(joinGameButton).padBottom((float) viewport.getScreenHeight() / 10);
        table.row();
        table.add(howToPlayButton).padBottom((float) viewport.getScreenHeight() / 10);

        // Add UI to Stage
        stage.addActor(table);
    }
}
