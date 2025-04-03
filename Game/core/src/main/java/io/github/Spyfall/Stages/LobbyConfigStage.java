package io.github.Spyfall.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class LobbyConfigStage extends StageController {
    private Skin skin;
    private TextField roundLimitField;
    private TextField locationNumberField;
    private TextField spyCountField;
    private TextField maxPlayersField;
    private TextField roundDurationField;
    private TextButton saveButton;
    private TextButton startButton;
    private TextButton backButton;
    private Texture bgTexture;
    private boolean isConfigured = false;

    public LobbyConfigStage(ScreenViewport viewport) {
        super(viewport);
        initStage();
    }

    private void initStage() {
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("metal-ui.json"));

        // Load background texture
        bgTexture = new Texture(Gdx.files.internal("Background_city.png"));
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(bgTexture));

        // Create input fields with default values
        roundLimitField = new TextField("10", skin);
        locationNumberField = new TextField("30", skin);
        spyCountField = new TextField("1", skin);
        maxPlayersField = new TextField("8", skin);
        roundDurationField = new TextField("120", skin);

        // Create buttons
        saveButton = new TextButton("Save", skin);
        startButton = new TextButton("Start Game", skin);
        backButton = new TextButton("Back", skin);

        // Create main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.pad(20f);
        mainTable.setBackground(texture);

        // Add labels and input fields
        mainTable.add(new Label("Round Limit:", skin)).pad(10f);
        mainTable.add(roundLimitField).width(100f).pad(10f).row();
        
        mainTable.add(new Label("Location Number:", skin)).pad(10f);
        mainTable.add(locationNumberField).width(100f).pad(10f).row();
        
        mainTable.add(new Label("Spy Count:", skin)).pad(10f);
        mainTable.add(spyCountField).width(100f).pad(10f).row();
        
        mainTable.add(new Label("Max Players:", skin)).pad(10f);
        mainTable.add(maxPlayersField).width(100f).pad(10f).row();
        
        mainTable.add(new Label("Round Duration (s):", skin)).pad(10f);
        mainTable.add(roundDurationField).width(100f).pad(10f).row();

        // Add buttons
        Table buttonTable = new Table();
        buttonTable.add(saveButton).pad(10f);
        buttonTable.add(startButton).pad(10f);
        buttonTable.add(backButton).pad(10f);
        
        mainTable.add(buttonTable).colspan(2).padTop(20f);

        // Add listeners
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveConfig();
            }
        });

        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isConfigured) {
                    startGame();
                } else {
                    Dialog dialog = new Dialog("Warning", skin, "dialog") {
                        @Override
                        public void result(Object obj) {
                            // Do nothing, just close the dialog
                        }
                    };
                    dialog.text("Please save the configuration first");
                    dialog.button("OK");
                    dialog.show(stage);
                }
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new LobbyStage(viewport));
            }
        });

        stage.addActor(mainTable);
    }

    private void saveConfig() {
        try {
            int roundLimit = Integer.parseInt(roundLimitField.getText());
            int locationNumber = Integer.parseInt(locationNumberField.getText());
            int spyCount = Integer.parseInt(spyCountField.getText());
            int maxPlayers = Integer.parseInt(maxPlayersField.getText());
            int roundDuration = Integer.parseInt(roundDurationField.getText());

            // TODO: Send these values to the server and save configuration
            // You'll need to implement the WebSocket message sending here
            
            isConfigured = true;
            
            Dialog dialog = new Dialog("Success", skin, "dialog") {
                @Override
                public void result(Object obj) {
                    // Do nothing, just close the dialog
                }
            };
            dialog.text("Configuration saved successfully");
            dialog.button("OK");
            dialog.show(stage);
        } catch (NumberFormatException e) {
            Dialog dialog = new Dialog("Error", skin, "dialog") {
                @Override
                public void result(Object obj) {
                    // Do nothing, just close the dialog
                }
            };
            dialog.text("Please enter valid numbers");
            dialog.button("OK");
            dialog.show(stage);
        }
    }

    private void startGame() {
        // TODO: Send start game message to server
        // After starting game, go to game lobby
        StageManager.getInstance().setStage(new GameLobby(false, "Game in progress...", "Host", viewport));
    }

    @Override
    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        super.dispose();
    }
} 