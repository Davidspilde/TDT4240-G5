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
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.StageView;

public class GameOverStage extends StageView {

    private Skin skin;
    private GameModel gameModel;
    private GameplayController controller;
    private Texture bgTexture;
    private HashMap<String, Integer> scoreboard;

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

        // Create the root table that fills the screen
        Table rootTable = new Table();
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);


        // Content container
        Table contentTable = new Table();
        float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
        contentTable.setWidth(contentWidth);
        contentTable.pad(30);
        contentTable.defaults().pad(10).align(Align.center);
        
        // title
        Label titleLabel = new Label("GAME ENDED", skin);
        titleLabel.setFontScale(1.5f);
        titleLabel.setAlignment(Align.center); // Add this line to center the text
        contentTable.add(titleLabel).colspan(2).fillX().row();
        
        // final standings header
        Label standingsLabel = new Label("FINAL STANDINGS", skin);
        standingsLabel.setFontScale(1.3f);
        standingsLabel.setAlignment(Align.center); // Add this line
        contentTable.add(standingsLabel).colspan(2).padTop(20).padBottom(10).row();
        
        // scoreboard table
        Table scoreboardTable = new Table();
        float playerColWidth = contentWidth * 0.6f;
        float scoreColWidth = contentWidth * 0.2f;
        
        // header row
        Label playerHeader = new Label("Player", skin);
        Label scoreHeader = new Label("Score", skin);
        scoreboardTable.add(playerHeader).width(playerColWidth).left().padBottom(8);
        scoreboardTable.add(scoreHeader).width(scoreColWidth).right().padBottom(8).row();
        
        if (scoreboard != null && !scoreboard.isEmpty()) {
            // Sort entries by score (descending)
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoreboard.entrySet());
            sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
            
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                Label playerLabel = new Label(entry.getKey(), skin);
                playerLabel.setWrap(true);
                Label scoreLabel = new Label(Integer.toString(entry.getValue()), skin);
                
                // Add trophy icon next to winner
                if (sortedEntries.indexOf(entry) == 0) {
                    playerLabel.setText("🏆 " + entry.getKey());
                    playerLabel.setColor(Color.GOLD);
                    scoreLabel.setColor(Color.GOLD);
                }
                
                scoreboardTable.add(playerLabel).width(playerColWidth).left().fillX().padBottom(5);
                scoreboardTable.add(scoreLabel).width(scoreColWidth).right().padBottom(5).row();
            }
        }
        
        contentTable.add(scoreboardTable).colspan(2).fillX().row();
        
        // Return to lobby button
        TextButton returnToLobbyButton = new TextButton("Return to Lobby", skin);
        returnToLobbyButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                AudioService.getInstance().playSound("click");
                // controller.returnToLobby();
            }
        });
        contentTable.add(returnToLobbyButton).colspan(2).padTop(20);
        
        // Add content to root table
        rootTable.add(contentTable).expand().center();
        
        // should be controller
        //AudioService.getInstance().playMusic("victory", false);
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