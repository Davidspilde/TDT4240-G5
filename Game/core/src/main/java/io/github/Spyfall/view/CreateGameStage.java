package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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
import io.github.Spyfall.services.LocalWebSocketClient;

public class CreateGameStage extends StageView {
    private SendMessageService sendMsgService;
    private TextField usernameField;
    private Skin skin;
    private Label errorLabel;

    public CreateGameStage(ScreenViewport viewport) {
        super(viewport);
        initCreateGame();
        sendMsgService = SendMessageService.getInstance();
    }

    private void initCreateGame() {
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create UI Elements
        TextButton createButton = new TextButton("Create Game", skin);
        TextButton backButton = new TextButton("Back", skin);
        usernameField = new TextField("", skin);
        usernameField.setMessageText("Enter your username");
        
        // Create error label (hidden by default)
        errorLabel = new Label("", skin);
        errorLabel.setColor(1, 0, 0, 1); // Red color
        errorLabel.setAlignment(Align.center);
        errorLabel.setVisible(false);

        // Background
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture("Background_city.png")));
        Table table = new Table();
        Image image = new Image(new TextureRegion(new Texture("logo-Photoroom.png")));

        // Add callbacks to buttons
        createButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    showError("Please enter a username");
                    return;
                }

                // Check WebSocket connection
                LocalWebSocketClient wsClient = LocalWebSocketClient.getInstance();
                if (!wsClient.isOpen()) {
                    showError("Cannot connect to server. Please ensure the server is running.");
                    return;
                }

                System.out.println("Creating game with username: " + username);
                boolean success = sendMsgService.createLobby(username);
                if (!success) {
                    showError("Failed to create game. Please try again.");
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new MainMenuStage(viewport));
            }
        });

        // Align Table to Top
        table.top().setFillParent(true);
        table.setBackground(texture);

        // Add padding and spacing
        table.add(image).padBottom((float) viewport.getScreenHeight() / 10)
                .padLeft((float) viewport.getScreenWidth() / 10)
                .padRight((float) viewport.getScreenWidth() / 10)
                .padTop((float) viewport.getScreenHeight() / 15);
        table.row();
        table.add(usernameField).width(200).padBottom(20);
        table.row();
        table.add(errorLabel).padBottom(10);
        table.row();
        table.add(createButton).padBottom((float) viewport.getScreenHeight() / 10);
        table.row();
        table.add(backButton).padBottom((float) viewport.getScreenHeight() / 10);

        // Add UI to Stage
        stage.addActor(table);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void showErrorDialog(String title, String message) {
        Dialog dialog = new Dialog(title, skin) {
            public void result(Object obj) {
                // Dialog closed
            }
        };
        dialog.text(message);
        dialog.button("OK");
        dialog.show(stage);
    }
}
