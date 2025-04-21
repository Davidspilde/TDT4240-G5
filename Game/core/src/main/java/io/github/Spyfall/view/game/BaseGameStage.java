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
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.view.StageView;

public abstract class BaseGameStage extends StageView {
    protected Skin skin;
    protected GameplayController controller;
    protected GameModel gameModel;
    
    // Common UI elements
    protected Label timerLabel;
    protected Label roleLabel;
    protected TextButton endGameButton;
    protected TextButton leaveGameButton;
    protected Table rootTable;
    protected Texture bgTexture;
    
    // Round ended UI
    protected Table roundEndTable;
    protected TextButton nextRoundButton;
    protected boolean isRoundEnded = false;
    
    // Timer
    protected float accumulator = 0f;
    protected boolean timerRunning = false;
    
    public BaseGameStage(ScreenViewport viewport) {
        super(viewport);
        this.controller = GameplayController.getInstance();
        this.gameModel = GameModel.getInstance();
    }
    
    protected void initCommonElements() {
        // Load the background texture
        bgTexture = new Texture(Gdx.files.internal("Background_city.png"));

        // Let the stage receive input events
        Gdx.input.setInputProcessor(stage);

        // Load skin
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // root table
        rootTable = new Table();
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);
        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // timer label
        timerLabel = new Label("", skin);
        timerLabel.setAlignment(Align.center);
        
        // end/leave game buttons
        endGameButton = new TextButton("End Game", skin);
        leaveGameButton = new TextButton("Leave Game", skin);

        endGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                controller.endGame();
            }
        });

        leaveGameButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y){
                controller.leaveGame();
            }
        });
    }
    
    public void startTimer(int duration) {
        gameModel.getGameData().setTimeRemaining(duration);
        updateTimerDisplay(duration);
        timerRunning = true;
        accumulator = 0f;
    }
    
    public void stopTimer() {
        timerRunning = false;
    }
    
    public void updateTimerDisplay(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%d:%02d", minutes, seconds));
    }
    
    protected void decrementTimer() {
        GameData gameData = gameModel.getGameData();
        int timeRemaining = gameData.getTimeRemaining();
        
        if (timeRemaining > 0) {
            timeRemaining--;
            gameData.setTimeRemaining(timeRemaining);
            
            updateTimerDisplay(timeRemaining);
            
            if (timeRemaining == 0) {
                timerRunning = false;
                onTimerEnd();
            }
        }
    }

    protected abstract void onTimerEnd();
    
    public abstract void handleRoundEnded(int roundNumber, String reason, String spy, 
                                        String location, HashMap<String, Integer> scoreboard);
    
    
    
    @Override
    public void update() {
        if (timerRunning) {
            float delta = Gdx.graphics.getDeltaTime();
            accumulator += delta;
            
            if (accumulator >= 1.0f) {
                accumulator -= 1.0f;
                decrementTimer();
            }
        }
        
        stage.act();
        stage.draw();
    }
    
    protected void createOverlayBackground(Table table, float alpha) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, alpha);
        pixmap.fill();
        Texture bgTexture = new Texture(pixmap);
        pixmap.dispose();
        
        table.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
    }
    
    public void showRoundEndedUI() {
        isRoundEnded = true;
        timerLabel.setText("TIME'S UP!");
        
        GameData gameData = gameModel.getGameData();
        String spy = gameData.getIsSpyUsername();
        String location = gameData.getLocation();
        HashMap<String, Integer> scoreboard = gameData.getScoreboard();
        
        if (roundEndTable != null) {
            roundEndTable.remove(); 
            Texture bgTexture = ((TextureRegionDrawable)roundEndTable.getBackground()).getRegion().getTexture();
            if (bgTexture != null) {
                bgTexture.dispose(); 
            }
        }
        
        createRoundEndedUI(spy, location, scoreboard, false);
        roundEndTable.setVisible(true);
    }
    
    protected void createRoundEndedUI(String spy, String location, HashMap<String, Integer> scoreboard, boolean isGameOver) {
        try {
            // round end overlay
            roundEndTable = new Table();
            roundEndTable.setFillParent(true);
            
            // Background
            createOverlayBackground(roundEndTable, 0.8f);
            
            // Content table
            float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
            Table contentTable = new Table();
            contentTable.pad(30);
            contentTable.defaults().pad(10).align(Align.center);
            contentTable.setWidth(contentWidth);
            
            // Title
            String titleText = isGameOver ? "GAME ENDED" : "ROUND ENDED";
            Label titleLabel = new Label(titleText, skin);
            titleLabel.setFontScale(1.5f);
            contentTable.add(titleLabel).colspan(2).fillX().row();
            
            // Show spy and location (only in round end)
            if (!isGameOver && spy != null) {
                Label spyLabel = new Label("The Spy was: " + spy, skin);
                spyLabel.setWrap(true);
                contentTable.add(spyLabel).colspan(2).fillX().padTop(10).row();
                
                Label locationLabel = new Label("Location: " + location, skin);
                locationLabel.setWrap(true);
                contentTable.add(locationLabel).colspan(2).fillX().padTop(10).row();
            }
            
            // Scoreboard
            if (scoreboard != null && !scoreboard.isEmpty()) {
                String scoreboardHeader = isGameOver ? "FINAL STANDINGS" : "SCOREBOARD";
                Label scoreboardLabel = new Label(scoreboardHeader, skin);
                if (isGameOver) {
                    scoreboardLabel.setFontScale(1.3f);
                }
                contentTable.add(scoreboardLabel).colspan(2).padTop(20).padBottom(10).row();
                
                // Create scoreboard table
                Table scoreboardTable = new Table();
                float playerColWidth = contentWidth * 0.6f;
                float scoreColWidth = contentWidth * 0.2f;
                
                // Header row
                Label playerHeader = new Label("Player", skin);
                Label scoreHeader = new Label("Score", skin);
                scoreboardTable.add(playerHeader).width(playerColWidth).left().padBottom(8);
                scoreboardTable.add(scoreHeader).width(scoreColWidth).right().padBottom(8).row();
                
                // Sort entries by score
                List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoreboard.entrySet());
                sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                
                for (Map.Entry<String, Integer> entry : sortedEntries) {
                    Label playerLabel = new Label(entry.getKey(), skin);
                    playerLabel.setWrap(true);
                    Label scoreLabel = new Label(Integer.toString(entry.getValue()), skin);
                    
                    // Highlight winner
                    if (isGameOver && sortedEntries.indexOf(entry) == 0) {
                        playerLabel.setText("🏆 " + entry.getKey());
                        playerLabel.setColor(Color.GOLD);
                        scoreLabel.setColor(Color.GOLD);
                    }
                    
                    scoreboardTable.add(playerLabel).width(playerColWidth).left().fillX().padBottom(5);
                    scoreboardTable.add(scoreLabel).width(scoreColWidth).right().padBottom(5).row();
                }
                
                contentTable.add(scoreboardTable).colspan(2).fillX().row();
            }
            
            // Next round button (for host)
            boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
            if (!isGameOver && isHost) {
                nextRoundButton = new TextButton("Next Round", skin);
                nextRoundButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.startNextRound();
                        roundEndTable.setVisible(false);
                        isRoundEnded = false;
                    }
                });
                contentTable.add(nextRoundButton).colspan(2).padTop(20);
            }
            
            roundEndTable.add(contentTable).expand().fill().maxWidth(contentWidth);
            stage.addActor(roundEndTable);
            roundEndTable.setVisible(false);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void resetRoundEndUI() {
        if (roundEndTable != null) {
            roundEndTable.setVisible(false);
        }
        isRoundEnded = false;
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