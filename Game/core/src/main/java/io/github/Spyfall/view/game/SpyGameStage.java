package io.github.Spyfall.view.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;

public class SpyGameStage extends BaseGameStage {
    
    private Table locationsTable;
    private float finalGuessTimeRemaining;
    private boolean finalGuessActive = false;
    private Table finalGuessTable;
    private Label finalGuessTimerLabel;
    
    public SpyGameStage(String roleName, ScreenViewport viewport) {
        super(viewport);
        initStage(roleName);
    }
    
    protected void initStage(String roleName) {
        super.initCommonElements();
        
        // Create spy-specific UI elements
        roleLabel = new Label("Spy", skin);
        roleLabel.setColor(Color.WHITE);
        roleLabel.setFontScale(1.2f);
        
        // Create info group (timer + role)
        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.addActor(timerLabel);
        infoGroup.addActor(roleLabel);
        
        // Create locations table
        locationsTable = new Table(skin);
        updateLocationsList();
        
        // Layout the root table
        rootTable.top().pad(20f);
        rootTable.add(infoGroup).expandX().center().row();
        rootTable.row().padTop(20);
        rootTable.add(locationsTable).expand().fill();
        
        // Bottom buttons
        rootTable.row().padTop(20);
        Table buttonTable = new Table();
        buttonTable.add(endGameButton).padRight(20);
        buttonTable.add(leaveGameButton);
        rootTable.add(buttonTable);
    }
    
    
    private void updateLocationsList() {
        locationsTable.clear();
        
        // Add header
        Label locationsHeader = new Label("Possible Locations", skin);
        locationsHeader.setAlignment(Align.center);
        locationsHeader.setFontScale(1.2f);
        locationsTable.add(locationsHeader).colspan(3).padBottom(15).row();
        
        GameData gameData = gameModel.getGameData();
        
        // Get greyed out locations
        Set<String> greyedOutLocations = gameData.getGreyedOutLocations() != null ? 
                gameData.getGreyedOutLocations() : new HashSet<>();
            
        // Create locations table with fixed widths
        float tableWidth = Math.min(400, Gdx.graphics.getWidth() * 0.8f);
        float locationColWidth = tableWidth * 0.6f;
        
        if (gameData.getPossibleLocations() != null) {
            for (String location : gameData.getPossibleLocations()) {
                boolean isGreyedOut = greyedOutLocations.contains(location);
                Label locationLabel = new Label(location, skin);
                
                // Apply styling for greyed-out locations
                if (isGreyedOut) {
                    locationLabel.setColor(Color.GRAY);
                    locationLabel.setText("[" + location + "]");
                }
                
                // Toggle button
                TextButton toggleButton = new TextButton(isGreyedOut ? "✓" : "✗", skin);
                toggleButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.toggleLocationGreyout(location);
                    }
                });
                
                // Guess button
                TextButton guessButton = new TextButton("Guess", skin);
                guessButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.spyGuessLocation(location);
                    }
                });
                
                locationsTable.add(locationLabel).width(locationColWidth).left().padRight(5);
                locationsTable.add(toggleButton).width(40).padRight(5);
                locationsTable.add(guessButton).width(70).right().row();
            }
        }
        
    }
    
    @Override
    protected void onTimerEnd() {
        showFinalSpyGuessUI();
    }
    
    private void showFinalSpyGuessUI() {
        // Get duration from GameData (comes from server)
        finalGuessTimeRemaining = gameModel.getGameData().getSpyLastAttemptDuration();
        finalGuessActive = true;
        
        // Create overlay for final guess
        finalGuessTable = new Table();
        finalGuessTable.setFillParent(true);
        
        // Semi-transparent background
        createOverlayBackground(finalGuessTable, 0.9f);
        
        // Content table
        Table contentTable = new Table();
        contentTable.pad(30);
        
        // Add title
        Label titleLabel = new Label("TIME'S UP!", skin);
        titleLabel.setColor(Color.RED);
        titleLabel.setFontScale(1.3f);
        contentTable.add(titleLabel).padBottom(20).row();
        
        // Countdown timer for final guess
        finalGuessTimerLabel = new Label(formatTime((int)finalGuessTimeRemaining), skin);
        finalGuessTimerLabel.setColor(Color.YELLOW);
        contentTable.add(finalGuessTimerLabel).padBottom(15).row();
        
        // Add instruction
        Label instructionLabel = new Label("You have " + (int)finalGuessTimeRemaining + 
                                        " seconds to make your final guess:", skin);
        instructionLabel.setWrap(true);
        contentTable.add(instructionLabel).width(400).padBottom(30).row();
        
        // Add locations table
        Table locationsTable = new Table();
        if (gameModel.getGameData().getPossibleLocations() != null) {
            for (String location : gameModel.getGameData().getPossibleLocations()) {
                TextButton guessButton = new TextButton(location, skin);
                guessButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.spyGuessLocation(location);
                        removeFinalGuessUI();
                    }
                });
                locationsTable.add(guessButton).width(200).pad(5).row();
            }
        }
        
        // Add locations to content table
        contentTable.add(locationsTable).row();
        
        // Add content to overlay
        finalGuessTable.add(contentTable);
        stage.addActor(finalGuessTable);
    }


    private void removeFinalGuessUI() {
        if (finalGuessTable != null) {
            finalGuessTable.remove();
            finalGuessTable = null;
        }
        finalGuessActive = false;
    }

    private String formatTime(int timeInSeconds) {
        int minutes = timeInSeconds / 60;
        int seconds = timeInSeconds % 60;
        return String.format("Time remaining: %d:%02d", minutes, seconds);
    }

    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy, String location,
            HashMap<String, Integer> scoreboard) {
        // Stop timer
        timerRunning = false;
        
        // Update display
        timerLabel.setText("ROUND ENDED");
        
        // Clear any existing round end UI
        if (roundEndTable != null) {
            roundEndTable.remove();
            if (roundEndTable.getBackground() != null) {
                Texture bgTexture = ((TextureRegionDrawable)roundEndTable.getBackground()).getRegion().getTexture();
                if (bgTexture != null) {
                    bgTexture.dispose();
                }
            }
        }
        
        // Create overlay for round end
        roundEndTable = new Table();
        roundEndTable.setFillParent(true);
        
        // Semi-transparent background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();
        Texture bgTexture = new Texture(pixmap);
        pixmap.dispose();
        
        roundEndTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        
        // Content table
        float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
        Table contentTable = new Table();
        contentTable.pad(30);
        contentTable.defaults().pad(10).align(Align.center);
        contentTable.setWidth(contentWidth);
        
        // Round ended title
        Label titleLabel = new Label("ROUND " + roundNumber + " ENDED", skin);
        titleLabel.setFontScale(1.5f);
        titleLabel.setAlignment(Align.center);
        contentTable.add(titleLabel).colspan(2).fillX().row();
        
        // Special message for spy
        
        if (reason != null && !reason.isEmpty()) {
            Label reasonLabel = new Label(reason, skin);
            reasonLabel.setWrap(true);
            reasonLabel.setAlignment(Align.center);
            contentTable.add(reasonLabel).colspan(2).fillX().padBottom(15).row();
        }
        
        // Show spy info
        Label spyLabel = new Label("The Spy was: " + spy, skin);
        spyLabel.setWrap(true);
        contentTable.add(spyLabel).colspan(2).fillX().padTop(10).row();
        
        // Show location
        Label locationLabel = new Label("Location: " + location, skin);
        locationLabel.setWrap(true);
        contentTable.add(locationLabel).colspan(2).fillX().padTop(5).row();
        
        // Scoreboard
        if (scoreboard != null && !scoreboard.isEmpty()) {
            Label scoreboardLabel = new Label("SCOREBOARD", skin);
            contentTable.add(scoreboardLabel).colspan(2).padTop(20).padBottom(10).row();
            
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
                
                // Highlight yourself
                if (entry.getKey().equals(gameModel.getUsername())) {
                    playerLabel.setText("→ " + entry.getKey());
                    playerLabel.setColor(Color.YELLOW);
                    scoreLabel.setColor(Color.YELLOW);
                }
                
                scoreboardTable.add(playerLabel).width(playerColWidth).left().fillX().padBottom(5);
                scoreboardTable.add(scoreLabel).width(scoreColWidth).right().padBottom(5).row();
            }
            
            contentTable.add(scoreboardTable).colspan(2).fillX().row();
        }
        
        // Next round button (only for host)
        boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
        if (isHost) {
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
        
        // Add content to overlay
        roundEndTable.add(contentTable).expand().fill().maxWidth(contentWidth);
        stage.addActor(roundEndTable);
        
        // Show the round end UI
        roundEndTable.setVisible(true);
        isRoundEnded = true;
    }

    public boolean isFinalGuessActive() {
        return finalGuessActive;
    }

    @Override
    public void resetRoundEndUI() {
        super.resetRoundEndUI();
        
        // Also remove final guess UI if active
        removeFinalGuessUI();
    }   

    @Override
    public void update() {
        super.update();
        
        // Handle final guess timer if active
        if (finalGuessActive) {
            finalGuessTimeRemaining -= Gdx.graphics.getDeltaTime();
            
            if (finalGuessTimerLabel != null) {
                finalGuessTimerLabel.setText(formatTime((int)finalGuessTimeRemaining));
            }
            
            // Time's up for final guess
            if (finalGuessTimeRemaining <= 0) {
                removeFinalGuessUI();
                // Show the regular round end UI after timeout
                GameData gameData = gameModel.getGameData();
                handleRoundEnded(
                    gameData.getCurrentRound(),
                    "Time expired for final guess",
                    gameData.getIsSpyUsername(),
                    gameData.getLocation(),
                    gameData.getScoreboard()
                );
            }
        }
        
        updateLocationsList();
    }
}