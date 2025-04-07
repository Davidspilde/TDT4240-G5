package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.services.SendMessageService;

public class GameConfigStage extends StageView {
    private SendMessageService sendMsgService;
    private String lobbyCode;
    private String host;
    private Label lobbyCodeLabel;
    private VerticalGroup playersGroup;
    private Skin skin;

    public GameConfigStage(ScreenViewport viewport, String lobbyCode, String host) {
        super(viewport);
        this.lobbyCode = lobbyCode;
        this.host = host;
        this.sendMsgService = SendMessageService.getInstance();
        initGameConfig();
    }

    private void initGameConfig() {
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create UI Elements
        TextButton settingsButton = new TextButton("Game Settings", skin);
        TextButton locationsButton = new TextButton("Edit Locations", skin);
        TextButton backButton = new TextButton("Back", skin);
        
        // Create lobby code label
        lobbyCodeLabel = new Label("Lobby Code: " + lobbyCode, skin);
        lobbyCodeLabel.setAlignment(Align.center);
        
        // Create players group for scrolling
        playersGroup = new VerticalGroup();
        playersGroup.space(10);
        playersGroup.align(Align.center);
        
        // Add host as first player
        Label hostLabel = new Label("Host: " + host, skin);
        hostLabel.setAlignment(Align.center);
        playersGroup.addActor(hostLabel);
        
        // Create scroll pane for players
        ScrollPane playersScrollPane = new ScrollPane(playersGroup, skin);
        playersScrollPane.setFadeScrollBars(false);
        playersScrollPane.setScrollbarsVisible(true);
        playersScrollPane.setScrollbarsOnTop(true);

        // Background
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(new Texture("Background_city.png")));
        Table mainTable = new Table();
        Image logo = new Image(new TextureRegion(new Texture("logo-Photoroom.png")));

        // Add callbacks to buttons
        settingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Open game settings dialog
                openGameSettings();
            }
        });

        locationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Open locations editor
                openLocationsEditor();
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new CreateGameStage(viewport));
            }
        });

        // Layout
        mainTable.setFillParent(true);
        mainTable.setBackground(texture);
        
        // Top section with logo and lobby code
        Table topTable = new Table();
        topTable.add(logo).padBottom(20).row();
        topTable.add(lobbyCodeLabel).padBottom(20);
        
        // Buttons table
        Table buttonsTable = new Table();
        buttonsTable.add(settingsButton).padRight(10);
        buttonsTable.add(locationsButton).padLeft(10);
        
        // Main layout
        mainTable.add(topTable).expandX().center().padTop(20).row();
        mainTable.add(buttonsTable).expandX().center().padTop(20).padBottom(20).row();
        mainTable.add(playersScrollPane).expand().fill().pad(20).row();
        mainTable.add(backButton).padBottom(20);

        // Add UI to Stage
        stage.addActor(mainTable);
    }
    
    private void openGameSettings() {
        // Create a dialog for game settings
        GameSettingsDialog settingsDialog = new GameSettingsDialog("Game Settings", skin, host, lobbyCode);
        settingsDialog.show(stage);
    }
    
    private void openLocationsEditor() {
        // Create a dialog for editing locations
        LocationsEditorDialog locationsDialog = new LocationsEditorDialog(skin, lobbyCode);
        locationsDialog.show(stage);
    }
    
    public void addPlayer(String playerName) {
        Label playerLabel = new Label(playerName, skin);
        playerLabel.setAlignment(Align.center);
        playersGroup.addActor(playerLabel);
    }
    
    public void removePlayer(String playerName) {
        // Find and remove the player label
        for (int i = 0; i < playersGroup.getChildren().size; i++) {
            Label label = (Label) playersGroup.getChildren().get(i);
            if (label.getText().toString().equals(playerName)) {
                playersGroup.removeActor(label);
                break;
            }
        }
    }
    
    public void updateLobbyCode(String newLobbyCode) {
        this.lobbyCode = newLobbyCode;
        lobbyCodeLabel.setText("Lobby Code: " + newLobbyCode);
    }
} 