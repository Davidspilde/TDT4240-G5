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
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.services.SendMessageService;

public class MainMenuStage extends StageView {
    private SendMessageService sendMsgService;

    public MainMenuStage(ScreenViewport viewport) {
        super(viewport);
        initMainMenu();

        sendMsgService = SendMessageService.getInstance();
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
                System.out.println("Join game button clicked");
                
                // Create text fields before dialog
                final TextField lobbyCodeField = new TextField("", skin);
                lobbyCodeField.setMessageText("Enter lobby code");
                final TextField usernameField = new TextField("", skin);
                usernameField.setMessageText("Enter your username");

                // Create a custom dialog that handles its own result
                Dialog dialog = new Dialog("Join Game", skin) {
                    protected void result(Object obj) {
                        if (obj instanceof Boolean && (Boolean) obj) {
                            String lobbyCode = lobbyCodeField.getText().trim();
                            String username = usernameField.getText().trim();
                            System.out.println("Lobby code: " + lobbyCode);
                            System.out.println("Username: " + username);
                            if (!lobbyCode.isEmpty() && !username.isEmpty()) {
                                sendMsgService.joinLobby(username, lobbyCode);
                            }
                        }
                    }
                };

                dialog.getContentTable().add(new Label("Enter lobby code:", skin)).pad(5).row();
                dialog.getContentTable().add(lobbyCodeField).width(200).pad(5).row();
                dialog.getContentTable().add(new Label("Enter your username:", skin)).pad(5).row();
                dialog.getContentTable().add(usernameField).width(200).pad(5).row();

                dialog.button("Join", true);
                dialog.button("Cancel", false);
                dialog.show(stage);
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
