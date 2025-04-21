package io.github.Spyfall.view.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.StageView;
import io.github.Spyfall.view.game.ui.Scoreboard;

public class GameOverStage extends StageView {

    private Skin skin;
    private GameModel gameModel;
    private GameplayController controller;
    private Texture bgTexture;
    private HashMap<String, Integer> scoreboard;
    private Scoreboard scoreboardComponent;

    public GameOverStage(HashMap<String, Integer> finalScores, ScreenViewport viewport) {
        super(viewport);
        this.gameModel = GameModel.getInstance();
        this.controller = GameplayController.getInstance();
        this.scoreboard = finalScores;
        initStage();
    }

    private void initStage() {
        // Load the background texture
        bgTexture = new Texture(Gdx.files.internal("Background_city.png"));

        // Let the stage receive input events
        Gdx.input.setInputProcessor(stage);

        // Load skin
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        Table rootTable = new Table();
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        Table contentTable = new Table();
        float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
        contentTable.setWidth(contentWidth);
        contentTable.pad(30);
        contentTable.defaults().pad(10).align(Align.center);

        Label titleLabel = new Label("GAME ENDED", skin);
        titleLabel.setFontScale(1.5f);
        titleLabel.setAlignment(Align.center); 
        contentTable.add(titleLabel).colspan(2).fillX().row();
        
        // Create scoreboard component
        scoreboardComponent = new Scoreboard(
            skin, 
            gameModel.getUsername(),
            contentWidth
        );
        scoreboardComponent.setTitle("FINAL STANDINGS");
        scoreboardComponent.setScoreboard(scoreboard);
        scoreboardComponent.setHighlightWinner(true); // Enable trophy icon and gold color for winner
        
        contentTable.add(scoreboardComponent.getActor()).colspan(2).fillX().padTop(20).row();
        
        TextButton returnToLobbyButton = new TextButton("Return to Main Menu", skin);
        returnToLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioService.getInstance().playSound("click");
                gameModel.setCurrentState(GameState.MAIN_MENU);
            }
        });

        contentTable.add(returnToLobbyButton).colspan(2).padTop(20);

        rootTable.add(contentTable).expand().center();
    
    }

    @Override
    public void update() {
        if (scoreboardComponent != null) {
            scoreboardComponent.update();
        }
        
        // Regular stage updates
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