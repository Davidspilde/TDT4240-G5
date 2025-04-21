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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.model.GameState;

public class PlayerGameStage extends BaseGameStage {
    
    private Label locationLabel;
    private Table playersTable;
    
    public PlayerGameStage(String locationName, String roleName, ScreenViewport viewport) {
        super(viewport);
        initStage(locationName, roleName);
    }
    
    protected void initStage(String locationName, String roleName) {
        super.initCommonElements();
        
        // Create location and role labels
        locationLabel = new Label("Location: " + locationName, skin);
        roleLabel = new Label("Role: " + roleName, skin);
        
        // Create info group
        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.addActor(timerLabel);
        infoGroup.addActor(locationLabel);
        infoGroup.addActor(roleLabel);
        
        // Create players table
        playersTable = new Table(skin);
        updatePlayersList();
        
        // Layout the root table
        rootTable.top().pad(20f);
        rootTable.add(infoGroup).expandX().center().row();
        rootTable.row().padTop(20);
        rootTable.add(playersTable).expand().fill();
        
        // Bottom buttons
        rootTable.row().padTop(20);
        Table buttonTable = new Table();
        buttonTable.add(endGameButton).padRight(20);
        buttonTable.add(leaveGameButton);
        rootTable.add(buttonTable);
    }
    
    @Override
    public void update() {
        super.update();
        updatePlayersList();
    }
    
    private void updatePlayersList() {
        playersTable.clear();
        
        // Add header
        Label playersHeader = new Label("Players", skin);
        playersHeader.setAlignment(Align.center);
        playersHeader.setFontScale(1.2f);
        playersTable.add(playersHeader).colspan(2).padBottom(20).row();
        
        
        // Add player list with vote buttons
        for (String playerName : gameModel.getLobbyData().getPlayers()) {
            Label playerLabel = new Label(playerName, skin);
            
            // If in game, add vote buttons
            if (gameModel.getCurrentState() == GameState.IN_GAME) {
                boolean isCurrentPlayer = playerName.equals(gameModel.getUsername());
                
                TextButton voteButton = new TextButton("Vote", skin);
                if (isCurrentPlayer) {
                    voteButton.setDisabled(true);
                    voteButton.getLabel().setColor(Color.GRAY);
                }
                
                voteButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!isCurrentPlayer) {
                            controller.votePlayer(playerName);
                        }
                    }
                });
                
                playersTable.add(playerLabel).width(150).padRight(10).left();
                playersTable.add(voteButton).width(80).right().row();
            } else {
                playersTable.add(playerLabel).colspan(2).left().row();
            }
        }
    }
    
    @Override
    protected void onTimerEnd() {
        showRoundEndedUI();
    }

    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy, String location,
            HashMap<String, Integer> scoreboard) {
        // Stop the timer
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
        
        // Reason for round end
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
        
        // Add content table to overlay
        roundEndTable.add(contentTable).expand().fill().maxWidth(contentWidth);
        stage.addActor(roundEndTable);
        
        // Show the round end UI
        roundEndTable.setVisible(true);
        isRoundEnded = true;
    }
}