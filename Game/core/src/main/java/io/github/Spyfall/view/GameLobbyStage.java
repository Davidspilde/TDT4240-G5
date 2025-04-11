package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
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
import io.github.Spyfall.model.GameStateObserver;

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

    // The background texture
    private Texture bgTexture;

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
        
        // Add a "Possible Locations" header
        Label locationsHeader = new Label("Possible Locations", skin);
        locationsHeader.setAlignment(Align.center);
        possibleLocationsTable.add(locationsHeader).colspan(2).padBottom(10).row();
        
        // Add all locations from the model
        GameData gameData = gameModel.getGameData();
        if (gameData.getPossibleLocations() != null) {
            for (String location : gameData.getPossibleLocations()) {
                Label locationLabel = new Label(location, skin);
                
                // If we're the spy, add guess buttons
                if (gameData.isSpy()) {
                    TextButton guessButton = new TextButton("Guess", skin);
                    guessButton.addListener(new ClickListener() {
                        @Override
                        public void clicked(InputEvent event, float x, float y) {
                            controller.spyGuessLocation(location);
                        }
                    });
                    
                    possibleLocationsTable.add(locationLabel).padRight(10);
                    possibleLocationsTable.add(guessButton).row();
                } else {
                    possibleLocationsTable.add(locationLabel).colspan(2).row();
                }
            }
        } else {
            // Add sample locations as placeholders
            String[] sampleLocations = {"Airplane", "Bank", "Beach", "Casino", "Hospital", "Hotel", "School"};
            for (String location : sampleLocations) {
                Label locationLabel = new Label(location, skin);
                possibleLocationsTable.add(locationLabel).colspan(2).row();
            }
        }
    }
    
    // called when the model changes
    public void updateFromModel() {
        GameData gameData = gameModel.getGameData();
        
        // update timer
        int timeRemaining = gameData.getTimeRemaining();
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("%d:%02d", minutes, seconds));
        
        // update location and role
        String displayedLocation = gameData.isSpy() ? "???" : gameData.getLocation();
        locationLabel.setText(displayedLocation);
        roleLabel.setText(gameData.getRole());
        
        // update players and locations lists
        updatePlayersList();
        updateLocationsList();
    }

    // @Override
    // public void onGameStateChanged(GameModel model) {
    //     updateFromModel();
    // }

    @Override
    public void update() {
        // update the stage normally
        stage.act();
        stage.draw();
        
        // check for model updates that need to be shown in the UI
        updateFromModel();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
        // remove observer
        // gameModel.removeObserver(this);

        stage.dispose();
    }

    
}
