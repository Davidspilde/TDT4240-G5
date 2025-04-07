package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.StageManager;
import io.github.Spyfall.services.SendMessageService;

import java.util.List;

public class GameLobby extends StageView {

    private Skin skin;
    private Stage stage;
    private String lobbyCode;
    private String host;
    private boolean isHost;
    private String username;

    // UI elements
    private Label lobbyCodeLabel;
    private TextButton createGameButton;
    private TextButton gameSettingsButton;
    private TextButton editLocationsButton;
    private TextButton backButton;
    private Table playersTable;

    // The background texture
    private Texture bgTexture;

    public GameLobby(String lobbyCode, String host, String username, ScreenViewport viewport) {
        super(viewport);
        this.lobbyCode = lobbyCode;
        this.host = host;
        this.username = username;
        this.isHost = username.equals(host); // Set isHost based on whether the current user is the host
        System.out.println("GameLobby created with lobbyCode: " + lobbyCode + ", host: " + host + ", username: " + username + ", isHost: " + isHost);
        init();
    }

    private void init() {
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        initStage();
    }

    private void initStage() {
        System.out.println("Initializing GameLobby stage");
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create UI Elements
        createGameButton = new TextButton("Start Game", skin);
        gameSettingsButton = new TextButton("Game Settings", skin);
        editLocationsButton = new TextButton("Edit Locations", skin);
        backButton = new TextButton("Back", skin);
        lobbyCodeLabel = new Label("Lobby Code: " + lobbyCode, skin);
        lobbyCodeLabel.setAlignment(Align.center);

        // Create players table
        playersTable = new Table(skin);
        playersTable.defaults().pad(5);
        Label playersLabel = new Label("Players in Lobby:", skin);
        playersTable.add(playersLabel).row();
        
        // Add host as first player
        Label hostLabel = new Label(host + " (Host)", skin);
        playersTable.add(hostLabel).row();

        // Create root table
        Table rootTable = new Table();
        rootTable.setFillParent(true);
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("Background_city.png"))));

        // Add lobby code
        rootTable.add(lobbyCodeLabel).expandX().center().padTop(50).row();

        // Create buttons table
        Table buttonsTable = new Table(skin);
        buttonsTable.defaults().pad(5);
        buttonsTable.add(createGameButton).row();
        buttonsTable.add(gameSettingsButton).row();
        buttonsTable.add(editLocationsButton).row();
        buttonsTable.add(backButton).row();

        rootTable.add(buttonsTable).expandX().center().padTop(50).row();

        // Add players table
        rootTable.add(playersTable).expandX().center().padTop(50).row();

        // Add button listeners
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new MainMenuStage(viewport));
            }
        });

        createGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isHost) {
                    System.out.println("Start Game button clicked by host: " + username);
                    SendMessageService.getInstance().startGame(lobbyCode);
                } else {
                    System.out.println("Only the host can start the game. Current user: " + username + ", Host: " + host);
                }
            }
        });

        gameSettingsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isHost) {
                    GameSettingsDialog dialog = new GameSettingsDialog("Game Settings", skin, username, lobbyCode);
                    dialog.show(stage);
                }
            }
        });

        editLocationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (isHost) {
                    LocationsEditorDialog dialog = new LocationsEditorDialog(skin, lobbyCode);
                    dialog.show(stage);
                }
            }
        });

        stage.addActor(rootTable);
        System.out.println("GameLobby stage initialized");
    }

    public void updatePlayerList(List<String> players) {
        System.out.println("Updating player list: " + players);
        Gdx.app.postRunnable(() -> {
            playersTable.clear();
            Label playersLabel = new Label("Players in Lobby:", skin);
            playersTable.add(playersLabel).row();
            
            for (String player : players) {
                Label playerLabel = new Label(player + (player.equals(host) ? " (Host)" : ""), skin);
                playersTable.add(playerLabel).row();
            }
        });
    }

    public void update() {
        stage.act();
        stage.draw();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        stage.dispose();
    }
}
