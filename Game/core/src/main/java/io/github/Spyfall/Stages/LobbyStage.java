package io.github.Spyfall.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.graphics.Color;

public class LobbyStage extends StageController {
    private Skin skin;
    private TextButton optionsButton;
    private TextButton startGameButton;
    private TextButton editLocationsButton;
    private TextButton backButton;
    private Texture bgTexture;
    private ScrollPane scrollPane;
    private Table playerTable;

    public LobbyStage(ScreenViewport viewport) {
        super(viewport);
        initStage();
    }

    private void initStage() {
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("metal-ui.json"));

        // Load background texture
        bgTexture = new Texture(Gdx.files.internal("Background_city.png"));
        TextureRegionDrawable texture = new TextureRegionDrawable(new TextureRegion(bgTexture));

        // Create main table
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.setBackground(texture);
        mainTable.top(); // Align to top

        // Create game lobby placeholder
        Table gameLobbyTable = new Table();
        gameLobbyTable.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.8f)));
        Label gameLobbyLabel = new Label("Game Lobby", skin);
        gameLobbyLabel.setFontScale(1.5f);
        gameLobbyTable.add(gameLobbyLabel).pad(20f);

        // Create button table
        Table topButtonTable = new Table();
        optionsButton = new TextButton("Options", skin);
        startGameButton = new TextButton("Start Game", skin);
        editLocationsButton = new TextButton("Edit Locations", skin);
        backButton = new TextButton("Back", skin);

        // Add buttons to top table
        topButtonTable.add(optionsButton).pad(10f);
        topButtonTable.add(startGameButton).pad(10f);
        topButtonTable.add(editLocationsButton).pad(10f);
        topButtonTable.add(backButton).pad(10f);

        // Create player list
        playerTable = new Table();
        playerTable.top(); // Align to top
        playerTable.defaults().pad(5f);
        
        // Add some placeholder players (remove this in production)
        for (int i = 1; i <= 10; i++) {
            Table playerRow = new Table();
            playerRow.setBackground(skin.newDrawable("white", new Color(0.8f, 0.8f, 0.8f, 0.6f)));
            Label playerLabel = new Label("Player " + i, skin);
            playerLabel.setFontScale(1.2f);
            playerRow.add(playerLabel).pad(10f);
            playerTable.add(playerRow).width(viewport.getWorldWidth() * 0.8f).row();
        }

        // Create scroll pane for player list
        scrollPane = new ScrollPane(playerTable, skin);
        scrollPane.setScrollingDisabled(true, false); // Enable only vertical scrolling
        scrollPane.setFadeScrollBars(false);

        // Add all components to main table with specific spacing
        float totalHeight = viewport.getWorldHeight();
        float lobbyHeight = totalHeight * 0.15f;
        float buttonHeight = totalHeight * 0.1f;
        float playerListHeight = totalHeight * 0.6f;
        float topPadding = totalHeight * 0.05f;

        mainTable.add(gameLobbyTable)
            .width(viewport.getWorldWidth() * 0.8f)
            .height(lobbyHeight)
            .padTop(topPadding)
            .padBottom(20f)
            .row();

        mainTable.add(topButtonTable)
            .width(viewport.getWorldWidth() * 0.8f)
            .height(buttonHeight)
            .padBottom(20f)
            .row();

        mainTable.add(scrollPane)
            .width(viewport.getWorldWidth() * 0.8f)
            .height(playerListHeight)
            .top();

        // Add listeners
        optionsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new LobbyConfigStage(viewport));
            }
        });

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Start the game
                StageManager.getInstance().setStage(new GameLobby(false, "Game in progress...", "Host", viewport));
            }
        });

        editLocationsButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO: Implement edit locations functionality
                Dialog dialog = new Dialog("Info", skin, "dialog") {
                    @Override
                    public void result(Object obj) {
                        // Do nothing, just close the dialog
                    }
                };
                dialog.text("Edit locations functionality coming soon!");
                dialog.button("OK");
                dialog.show(stage);
            }
        });

        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                StageManager.getInstance().setStage(new MainMenuStage(viewport));
            }
        });

        stage.addActor(mainTable);
    }

    @Override
    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        super.dispose();
    }
} 