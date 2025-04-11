package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameStateObserver;

public class LobbyStage extends StageView {
    private LobbyController controller;
    private GameModel gameModel;
    private Skin skin;
    
    // UI elements
    private Label lobbyCodeLabel;
    private Label hostLabel;
    private Table playersTable;
    private TextButton startGameButton;
    private TextButton leaveLobbyButton;
    private Texture bgTexture;

    public LobbyStage(ScreenViewport viewport) {
        super(viewport);
        this.controller = LobbyController.getInstance();
        this.gameModel = GameModel.getInstance();
        
        // Register as observer to get model updates
        // gameModel.addObserver(this);
        
        initStage();
    }

    private void initStage() {
        // Load background texture
        bgTexture = new Texture(Gdx.files.internal("Background_city.png"));
        
        // Let the stage receive input events
        Gdx.input.setInputProcessor(stage);
        
        // Load skin
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));
        
        // Create the root table
        Table rootTable = new Table();
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);
        
        // Create lobby info section
        Table infoTable = new Table();
        Label titleLabel = new Label("Game Lobby", skin);
        titleLabel.setFontScale(1.5f);
        lobbyCodeLabel = new Label("Lobby Code: " + gameModel.getLobbyCode(), skin);
        hostLabel = new Label("Host: " + gameModel.getLobbyData().getHostPlayer(), skin);
        
        infoTable.add(titleLabel).padBottom(10).row();
        infoTable.add(lobbyCodeLabel).padBottom(5).row();
        infoTable.add(hostLabel).padBottom(20);
        
        // Create players list
        Label playersLabel = new Label("Players", skin);
        playersTable = new Table(skin);
        updatePlayersList();
        
        ScrollPane playersScroll = new ScrollPane(playersTable, skin);
        playersScroll.setFadeScrollBars(false);
        
        // Create buttons
        startGameButton = new TextButton("Start Game", skin);
        leaveLobbyButton = new TextButton("Leave Lobby", skin);
        
        // Only host can start the game
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        startGameButton.setVisible(isHost);
        
        // Add button listeners
        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.startGame();
            }
        });
        
        leaveLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                controller.leaveLobby();
            }
        });
        
        // Layout the UI
        rootTable.top().pad(20);
        rootTable.add(infoTable).expandX().center().row();
        
        Table centerTable = new Table();
        centerTable.add(playersLabel).center().padBottom(10).row();
        centerTable.add(playersScroll).width(200).height(200);
        
        rootTable.add(centerTable).expand().row();
        
        Table buttonsTable = new Table();
        buttonsTable.add(startGameButton).padRight(20);
        buttonsTable.add(leaveLobbyButton);
        
        rootTable.add(buttonsTable).padBottom(20);
    }
    
    private void updatePlayersList() {
        playersTable.clear();
        
        for (String playerName : gameModel.getLobbyData().getPlayers()) {
            Label playerLabel = new Label(playerName, skin);
            
            // if player is the host, mark them
            if (playerName.equals(gameModel.getLobbyData().getHostPlayer())) {
                playerLabel.setText(playerName + " (Host)");
            }
            
            playersTable.add(playerLabel).padBottom(5).row();
        }
    }
    
    @Override
    public void update() {
        stage.act();
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }
    
    // @Override
    // public void onGameStateChanged(GameModel model) {
    //     // Update UI based on model changes
    //     lobbyCodeLabel.setText("Lobby Code: " + model.getLobbyCode());
    //     hostLabel.setText("Host: " + model.getLobbyData().getHostPlayer());
    //     updatePlayersList();
        
    //     // Check if user is host and update UI
    //     boolean isHost = model.getUsername().equals(model.getLobbyData().getHostPlayer());
    //     startGameButton.setVisible(isHost);
    // }
    
    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        
        // remove observer
        // gameModel.removeObserver(this);
        
        stage.dispose();
    }
}