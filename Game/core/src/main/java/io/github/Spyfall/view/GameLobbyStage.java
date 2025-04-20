package io.github.Spyfall.view;

import java.util.HashMap;
import java.util.HashSet;
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
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.model.GameModel;

public class GameLobbyStage extends StageView {

    private Skin skin;
    private GameplayController controller;
    private GameModel gameModel;

    // UI elements
    private Label timerLabel;
    private Label locationLabel;
    private Label roleLabel;
    private TextButton endGameButton;
    private TextButton leaveGameButton;
    private Table playersTable;
    private Table possibleLocationsTable;

    // round ended UI
    private Table roundEndTable;
    private Label roundEndLabel;
    private TextButton nextRoundButton;
    private boolean isRoundEnded = false;

    // The background texture
    private Texture bgTexture;

    // timer
    private float accumulator = 0f;
    private boolean timerRunning = false;

    public GameLobbyStage(boolean isSpy, String locationName, String roleName, ScreenViewport viewport) {
        super(viewport);
        this.controller = GameplayController.getInstance();
        this.gameModel = GameModel.getInstance();
        // add observer
        // gameModel.addObserver(this);
        initStage(isSpy, locationName, roleName);
    }

    public void initStage(boolean isSpy, String locationName, String roleName) {
        // Load the background texture
        bgTexture = new Texture(Gdx.files.internal("Background_city.png"));

        // Let the stage receive input events
        Gdx.input.setInputProcessor(stage);

        // Load skin
        skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

        // Create the root table that fills the screen
        Table rootTable = new Table();

        // Create a drawable from the background texture and set as table background
        TextureRegion bgRegion = new TextureRegion(bgTexture);
        TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
        rootTable.setBackground(bgDrawable);

        rootTable.setFillParent(true);
        stage.addActor(rootTable);

        // Create timer label for top of screen
        timerLabel = new Label("2:53", skin);
        timerLabel.setAlignment(Align.center);

        // Create location label and role label
        // If player is spy, location name is replaced with ???
        String displayedLocation = isSpy ? "???" : locationName;
        locationLabel = new Label(displayedLocation, skin);
        roleLabel = new Label(roleName, skin);

        // A container (vertical group) for the top "info" area
        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.addActor(timerLabel);
        infoGroup.addActor(locationLabel);
        infoGroup.addActor(roleLabel);

        // Create the list of players
        playersTable = new Table(skin);
        updatePlayersList();

        // Create the list of possible locations
        possibleLocationsTable = new Table(skin);
        updateLocationsList();

        // Create end/leave game buttons
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

        // Layout with rootTable
        rootTable.top().pad(20f);

        // Top row: infoGroup
        rootTable.add(infoGroup).expandX().center().colspan(2).row();

        // Middle area: players (left), locations (right)
        rootTable.row().padTop(30);
        rootTable.add(playersTable).expand().fill().padRight(20);
        rootTable.add(possibleLocationsTable).expand().fill();

        // Bottom area: End game / Leave game side by side
        Table bottomButtonsTable = new Table();
        bottomButtonsTable.add(endGameButton).padRight(20);
        bottomButtonsTable.add(leaveGameButton);

        rootTable.row().padTop(30);
        rootTable.add(bottomButtonsTable).colspan(2);
    }



    private void updatePlayersList() {
        playersTable.clear();
        
        // Add a "Players" header
        Label playersHeader = new Label("Players", skin);
        playersHeader.setAlignment(Align.center);
        playersTable.add(playersHeader).colspan(2).padBottom(10).row();
        
        // Add all players from the model
        for (String playerName : gameModel.getLobbyData().getPlayers()) {
            Label playerLabel = new Label(playerName, skin);
            
            // If we're in game and not spy, add vote buttons
            if (gameModel.getCurrentState() == io.github.Spyfall.model.GameState.IN_GAME &&
                    !gameModel.getGameData().isSpy()) {
                TextButton voteButton = new TextButton("Vote", skin);
                voteButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        controller.votePlayer(playerName);
                    }
                });
                
                playersTable.add(playerLabel).padRight(10);
                playersTable.add(voteButton).row();
            } else {
                playersTable.add(playerLabel).colspan(2).row();
            }
        }
    }
    
    private void updateLocationsList() {
        possibleLocationsTable.clear();
        
        // Add header
        Label locationsHeader = new Label("Possible Locations", skin);
        locationsHeader.setAlignment(Align.center);
        possibleLocationsTable.add(locationsHeader).colspan(3).padBottom(10).row();
        
        GameData gameData = gameModel.getGameData();
        boolean isSpy = gameData.isSpy();
        
        // Get greyed out locations if they exist
        Set<String> greyedOutLocations = gameData.getGreyedOutLocations() != null ? 
            gameData.getGreyedOutLocations() : new HashSet<>();
        
        if (gameData.getPossibleLocations() != null) {
            for (String location : gameData.getPossibleLocations()) {
                boolean isGreyedOut = greyedOutLocations.contains(location);
                Label locationLabel = new Label(location, skin);
                
                // Apply styling for greyed-out locations
                if (isGreyedOut) {
                    locationLabel.setColor(Color.GRAY);
                    locationLabel.setText("[" + location + "]");
                }
                
                if (isSpy) {
                    // For spy: add toggle and guess buttons
                    TextButton toggleButton = new TextButton(isGreyedOut ? "✓" : "×", skin);
                    toggleButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            // Toggle this location's greyed state
                            controller.toggleLocationGreyout(location);
                        }
                    });
                    
                    TextButton guessButton = new TextButton("Guess", skin);
                    guessButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            controller.spyGuessLocation(location);
                        }
                    });
                    
                    possibleLocationsTable.add(locationLabel).expandX().fillX();
                    possibleLocationsTable.add(toggleButton).width(40).padRight(5);
                    possibleLocationsTable.add(guessButton).width(70).row();
                } else {
                    // Regular players just see the location list
                    possibleLocationsTable.add(locationLabel).colspan(3).row();
                }
            }
            
            // Add help text for spy
            if (isSpy) {
                Label helpLabel = new Label("Mark unlikely locations with × to track your progress", skin);
                helpLabel.setWrap(true);
                helpLabel.setAlignment(Align.center);
                possibleLocationsTable.add(helpLabel).colspan(3).padTop(15);
            }
        }
    }

    private void createRoundEndedUI(String spy, String location, HashMap<String, Integer> scoreboard) {
        try {
            // Create round end overlay
            roundEndTable = new Table();
            roundEndTable.setFillParent(true);
            
            // Semi-transparent background (unchanged)
            Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
            pixmap.setColor(0, 0, 0, 0.8f);
            pixmap.fill();
            Texture bgTexture = new Texture(pixmap);
            pixmap.dispose();
            
            roundEndTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
            
            // Calculate content width based on screen size
            float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
            
            // Create content table with width constraint
            Table contentTable = new Table();
            contentTable.pad(30);
            contentTable.defaults().pad(10).align(Align.center);
            contentTable.setWidth(contentWidth);
            
            // Round end title
            Label titleLabel = new Label("ROUND ENDED", skin);
            titleLabel.setFontScale(1.5f); // Slightly smaller for mobile
            contentTable.add(titleLabel).colspan(2).fillX().row();
            
            // Spy reveal information with text wrapping
            Label spyRevealLabel = new Label("Spy: " + spy, skin);
            spyRevealLabel.setWrap(true);
            contentTable.add(spyRevealLabel).colspan(2).fillX().width(contentWidth - 60).row();
            
            // Location reveal with text wrapping
            Label locationRevealLabel = new Label("Location: " + location, skin);
            locationRevealLabel.setWrap(true); // Enable text wrapping
            contentTable.add(locationRevealLabel).colspan(2).fillX().width(contentWidth - 60).row();
            
            // Scoreboard with constraints
            if (scoreboard != null && !scoreboard.isEmpty()) {
                contentTable.add(new Label("SCOREBOARD", skin)).colspan(2).padTop(20).row();
                
                // Create a table for scoreboard with fixed column widths
                Table scoreboardTable = new Table();
                float playerColWidth = contentWidth * 0.6f;
                float scoreColWidth = contentWidth * 0.2f;
                
                // Add header row
                Label playerHeader = new Label("Player", skin);
                Label scoreHeader = new Label("Score", skin);
                scoreboardTable.add(playerHeader).width(playerColWidth).left().padBottom(8);
                scoreboardTable.add(scoreHeader).width(scoreColWidth).right().padBottom(8).row();
                
                // Add all players' scores with text wrapping
                for (java.util.Map.Entry<String, Integer> entry : scoreboard.entrySet()) {
                    Label playerLabel = new Label(entry.getKey(), skin);
                    playerLabel.setWrap(true);
                    Label scoreLabel = new Label(Integer.toString(entry.getValue()), skin);
                    
                    scoreboardTable.add(playerLabel).width(playerColWidth).left().fillX();
                    scoreboardTable.add(scoreLabel).width(scoreColWidth).right().row();
                }
                
                contentTable.add(scoreboardTable).colspan(2).fillX().row();
            }
            
            // Next round button (only for host)
            boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
            if (isHost) {
                nextRoundButton = new TextButton("Start Next Round", skin);
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
            
            // Add content table to the overlay, centered
            roundEndTable.add(contentTable).expand().fill().maxWidth(contentWidth);
            stage.addActor(roundEndTable);
            
            // Initially hidden
            roundEndTable.setVisible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRoundEnded(int roundNumber, String reason, String spy, 
                           String location, HashMap<String, Integer> scoreboard) {
        // Stop the timer
        timerRunning = false;
        
        if (roundEndTable != null) {
            roundEndTable.remove(); 
            Texture bgTexture = ((TextureRegionDrawable)roundEndTable.getBackground()).getRegion().getTexture();
            if (bgTexture != null) {
                bgTexture.dispose(); // clean up
            }
        }
        
        // Create new round end UI
        createRoundEndedUI(spy, location, scoreboard);
        roundEndTable.setVisible(true);
        isRoundEnded = true;
    }

    public void showRoundEndedUI() {
        isRoundEnded = true;
        
        timerLabel.setText("TIME'S UP!");
        
        // Hide vote buttons
        updatePlayersList();
        
        // Hide location guess buttons
        updateLocationsList();
        
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
        
        createRoundEndedUI(spy, location, scoreboard);
        roundEndTable.setVisible(true);
    }

    public void resetRoundEndUI() {
        if (roundEndTable != null) {
            roundEndTable.setVisible(false);
        }
        isRoundEnded = false;
        
        // Update the UI to reflect game data
        updateFromModel();
    }

    
    
    // called when the model changes
    public void updateFromModel() {
        GameData gameData = gameModel.getGameData();
        
        if (!timerRunning) {
            int timeRemaining = gameData.getTimeRemaining();
            int minutes = timeRemaining / 60;
            int seconds = timeRemaining % 60;
            timerLabel.setText(String.format("%d:%02d", minutes, seconds));
        }
        
        // update location and role
        String displayedLocation = gameData.isSpy() ? "???" : gameData.getLocation();
        locationLabel.setText(displayedLocation);
        roleLabel.setText(gameData.getRole());
        
        // update players and locations lists
        updatePlayersList();
        updateLocationsList();
    }

    private void decrementTimer() {
        GameData gameData = gameModel.getGameData();
        int timeRemaining = gameData.getTimeRemaining();
        
        if (timeRemaining > 0) {
            timeRemaining--;
            gameData.setTimeRemaining(timeRemaining);
            
            // Update the timer display
            updateTimerDisplay(timeRemaining);
            
            // If timer reaches 0, handle round end
            if (timeRemaining == 0) {
                timerRunning = false;
                showRoundEndedUI();
            }
        }
    }
    
    public void updateTimerDisplay(int timeRemaining) {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%d:%02d", minutes, seconds));
    }

    public void startTimer(int duration) {
        gameModel.getGameData().setTimeRemaining(duration);
        updateTimerDisplay(duration);
        timerRunning = true;
        accumulator = 0f;
    }

    @Override
    public void update() {
        // update the stage normally
        if (timerRunning) {
            float delta = Gdx.graphics.getDeltaTime();
            accumulator += delta;
            
            // Update timer every second
            if (accumulator >= 1.0f) {
                accumulator -= 1.0f;
                decrementTimer();
            }
        }
        updateFromModel();

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
