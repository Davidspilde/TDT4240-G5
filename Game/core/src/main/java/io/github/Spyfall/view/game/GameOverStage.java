
package io.github.Spyfall.view.game;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.client.AssetLoader;
import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.game.ui.Scoreboard;
import io.github.Spyfall.view.ui.SettingsIcon;

public class GameOverStage extends StageView {

    // Layout constants (instance, not static)
    private final float CONTENT_WIDTH_MAX = 500f;
    private final float CONTENT_WIDTH_PERCENT = 0.85f;
    private final float PADDING = 30f;
    private final float BUTTON_FONT_SCALE = 1.4f;
    private final float BUTTON_HEIGHT = 60f;

    // References
    private Skin skin;
    private GameModel gameModel;
    private GameplayController controller;
    private Texture bgTexture;
    private HashMap<String, Integer> scoreboard;
    private Scoreboard scoreboardComponent;
    private AudioService audioService;

    public GameOverStage(HashMap<String, Integer> finalScores, ScreenViewport viewport) {
        super(viewport);
        this.audioService = AudioService.getInstance();
        this.gameModel = GameModel.getInstance();
        this.controller = GameplayController.getInstance();
        this.scoreboard = finalScores;
        initStage();
    }

    private void initStage() {
        // Load background and play victory music
        bgTexture = AssetLoader.mainBackground;
        audioService.playMusic("victory", true);
        Gdx.input.setInputProcessor(stage);

        // Load UI skin
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Root background table
        Table rootTable = new Table();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Main content container
        Table contentTable = new Table();
        float contentWidth = Math.min(CONTENT_WIDTH_MAX, Gdx.graphics.getWidth() * CONTENT_WIDTH_PERCENT);
        contentTable.setWidth(contentWidth);
        contentTable.pad(PADDING);
        contentTable.defaults().pad(10).align(Align.center);

        // Title label
        Label titleLabel = new Label("GAME ENDED", skin);
        titleLabel.setFontScale(1.5f);
        titleLabel.setAlignment(Align.center);
        contentTable.add(titleLabel).colspan(2).fillX().row();

        // Scoreboard
        scoreboardComponent = new Scoreboard(
                skin,
                gameModel.getUsername(),
                contentWidth);
        scoreboardComponent.setTitle("FINAL STANDINGS");
        scoreboardComponent.setScoreboard(scoreboard);
        scoreboardComponent.setHighlightWinner(true); // Enable trophy & highlight

        contentTable.add(scoreboardComponent.getActor()).colspan(2).fillX().padTop(20).row();

        // Back to Lobby button
        TextButton backToLobbyButton = new TextButton("Back to Lobby", skin);
        backToLobbyButton.getLabel().setFontScale(BUTTON_FONT_SCALE);
        backToLobbyButton.setHeight(BUTTON_HEIGHT);

        backToLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");

                controller.backTolobby();
            }
        });

        contentTable.add(backToLobbyButton).colspan(2).padTop(20).center().row();
        rootTable.add(contentTable).expand().center();

        // Floating settings icon
        SettingsIcon settingsIcon = new SettingsIcon(skin, AudioService.getInstance(), stage);
        stage.addActor(settingsIcon);

        // Reposition icon after stage is ready
        Gdx.app.postRunnable(() -> {
            float x = viewport.getWorldWidth() - settingsIcon.getWidth() - 20f;
            float y = 20f;
            settingsIcon.setPosition(x, y);
        });
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

    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        stage.dispose();
    }
}
