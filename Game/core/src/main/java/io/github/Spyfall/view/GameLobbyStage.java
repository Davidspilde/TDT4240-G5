// package io.github.Spyfall.view;

// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.HashSet;
// import java.util.List;
// import java.util.Map;
// import java.util.Set;

// import com.badlogic.gdx.Gdx;
// import com.badlogic.gdx.graphics.Color;
// import com.badlogic.gdx.graphics.Pixmap;
// import com.badlogic.gdx.graphics.Texture;
// import com.badlogic.gdx.graphics.g2d.TextureRegion;
// import com.badlogic.gdx.scenes.scene2d.InputEvent;
// import com.badlogic.gdx.scenes.scene2d.ui.Label;
// import com.badlogic.gdx.scenes.scene2d.ui.Skin;
// import com.badlogic.gdx.scenes.scene2d.ui.Table;
// import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
// import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
// import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
// import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
// import com.badlogic.gdx.utils.Align;
// import com.badlogic.gdx.utils.viewport.ScreenViewport;

// import io.github.Spyfall.controller.GameplayController;
// import io.github.Spyfall.model.GameData;
// import io.github.Spyfall.model.GameModel;
// import io.github.Spyfall.model.GameState;
// import io.github.Spyfall.view.ui.ErrorPopup;

// public class GameLobbyStage extends StageView {

//     private Skin skin;
//     private GameplayController controller;
//     private GameModel gameModel;

//     // UI elements
//     private Label timerLabel;
//     private Label locationLabel;
//     private Label roleLabel;
//     private TextButton endGameButton;
//     private TextButton leaveGameButton;
//     private Table playersTable;
//     private Table possibleLocationsTable;

//     // round ended UI
//     private Table roundEndTable;
//     private Label roundEndLabel;
//     private TextButton nextRoundButton;
//     private boolean isRoundEnded = false;

//     // The background texture
//     private Texture bgTexture;

//     // timer
//     private float accumulator = 0f;
//     private boolean timerRunning = false;

//     public GameLobbyStage(boolean isSpy, String locationName, String roleName, ScreenViewport viewport) {
//         super(viewport);
//         this.controller = GameplayController.getInstance();
//         this.gameModel = GameModel.getInstance();
//         // add observer
//         // gameModel.addObserver(this);
//         initStage(isSpy, locationName, roleName);
//     }

//     public void initStage(boolean isSpy, String locationName, String roleName) {
//         // Load the background texture
//         bgTexture = new Texture(Gdx.files.internal("Background_city.png"));

//         // Let the stage receive input events
//         Gdx.input.setInputProcessor(stage);

//         // Load skin
//         skin = new Skin(Gdx.files.internal("Custom/gdx-skins-master/gdx-skins-master/commodore64/skin/uiskin.json"));

//         // Create the root table that fills the screen
//         Table rootTable = new Table();
//         TextureRegion bgRegion = new TextureRegion(bgTexture);
//         TextureRegionDrawable bgDrawable = new TextureRegionDrawable(bgRegion);
//         rootTable.setBackground(bgDrawable);
//         rootTable.setFillParent(true);
//         stage.addActor(rootTable);

//         // Create timer label for top of screen
//         timerLabel = new Label("", skin);
//         timerLabel.setAlignment(Align.center);

//         // Create location label and role label
//         // If player is spy, location name is replaced with ???
//         String displayedLocation = isSpy ? "???" : locationName;
//         locationLabel = new Label(displayedLocation, skin);
//         roleLabel = new Label(roleName, skin);

//         // A container (vertical group) for the top "info" area
//         VerticalGroup infoGroup = new VerticalGroup();
//         infoGroup.addActor(timerLabel);
//         infoGroup.addActor(locationLabel);
//         infoGroup.addActor(roleLabel);

//         // Create the list of players
//         playersTable = new Table(skin);
//         possibleLocationsTable = new Table(skin);
//         updatePlayersList();
//         updateLocationsList();

//         // Create end/leave game buttons
//         endGameButton = new TextButton("End Game", skin);
//         leaveGameButton = new TextButton("Leave Game", skin);

//         endGameButton.addListener(new ClickListener(){
//             @Override
//             public void clicked(InputEvent event, float x, float y){
//                 controller.endGame();
//             }
//         });

//         leaveGameButton.addListener(new ClickListener(){
//             @Override
//             public void clicked(InputEvent event, float x, float y){
//                 controller.leaveGame();
//             }
//         });

//         // Layout with rootTable
//         rootTable.top().pad(20f);
//         rootTable.add(infoGroup).expandX().center().colspan(2).row();
        
//         // Middle area: Different layouts for spy vs regular players
//         rootTable.row().padTop(20);
//         if (isSpy) {
//             // Spy UI: Show locations with grey-out option
//             rootTable.add(possibleLocationsTable).expand().fill().colspan(2);
//         } else {
//             // Regular player UI: Show players with vote buttons
//             rootTable.add(playersTable).expand().fill().colspan(2);
//         }

//         // Bottom area: End game / Leave game side by side
//         Table bottomButtonsTable = new Table();
//         bottomButtonsTable.add(endGameButton).padRight(20);
//         bottomButtonsTable.add(leaveGameButton);

//         rootTable.row().padTop(30);
//         rootTable.add(bottomButtonsTable).colspan(2);
//     }



//     private void updatePlayersList() {
//         playersTable.clear();
    
//         // Add a "Players" header
//         Label playersHeader = new Label("Players", skin);
//         playersHeader.setAlignment(Align.center);
//         playersHeader.setFontScale(1.2f);
//         playersTable.add(playersHeader).colspan(2).padBottom(20).row();
        
//         // Add voting instructions for regular players
//         if (!gameModel.getGameData().isSpy() && 
//             gameModel.getCurrentState() == io.github.Spyfall.model.GameState.IN_GAME) {
//             Label instructionsLabel = new Label("Vote for who you think is the spy:", skin);
//             instructionsLabel.setWrap(true);
//             playersTable.add(instructionsLabel).colspan(2).padBottom(15).row();
//         }
        
//         // Add all players from the model
//         for (String playerName : gameModel.getLobbyData().getPlayers()) {
//             Label playerLabel = new Label(playerName, skin);
            
//             // If we're in game and not spy, add vote buttons
//             if (gameModel.getCurrentState() == GameState.IN_GAME &&
//                     !gameModel.getGameData().isSpy()) {

//                 boolean isCurrentPlayer = playerName.equals(gameModel.getUsername());
                
//                 TextButton voteButton = new TextButton("Vote", skin);
//                 if (isCurrentPlayer) {
//                     voteButton.setDisabled(true);
//                     voteButton.getLabel().setColor(Color.GRAY);
//                 }
//                 voteButton.addListener(new ClickListener() {
//                     @Override
//                     public void clicked(InputEvent event, float x, float y) {
//                         if (!isCurrentPlayer) {
//                             controller.votePlayer(playerName);
//                         }
//                     }
//                 });
                
//                 playersTable.add(playerLabel).width(150).padRight(10).left();
//                 playersTable.add(voteButton).width(80).right().row();
//             } else {
//                 playersTable.add(playerLabel).colspan(2).left().row();
//             }
//         }
//     }
    
//     private void updateLocationsList() {
//         possibleLocationsTable.clear();
    
//         GameData gameData = gameModel.getGameData();
//         boolean isSpy = gameData.isSpy();
        
//         // Only show locations list for spy
//         if (!isSpy) {
//             return;
//         }
        
//         // Add header
//         Label locationsHeader = new Label("Possible Locations", skin);
//         locationsHeader.setAlignment(Align.center);
//         locationsHeader.setFontScale(1.2f);
//         possibleLocationsTable.add(locationsHeader).colspan(3).padBottom(20).row();
        
//         // Add spy instructions
//         Label instructionsLabel = new Label("Try to figure out the location. Mark locations as unlikely and make your guess:", skin);
//         instructionsLabel.setWrap(true);
//         possibleLocationsTable.add(instructionsLabel).colspan(3).padBottom(15).fillX().row();
        
//         // Get greyed out locations
//         Set<String> greyedOutLocations = gameData.getGreyedOutLocations() != null ? 
//             gameData.getGreyedOutLocations() : new HashSet<>();
        
//         // Create a container table for the locations with fixed column widths
//         Table locationsContainer = new Table();
//         float tableWidth = Math.min(400, Gdx.graphics.getWidth() * 0.8f);
//         float locationColWidth = tableWidth * 0.6f;
        
//         if (gameData.getPossibleLocations() != null) {
//             for (String location : gameData.getPossibleLocations()) {
//                 boolean isGreyedOut = greyedOutLocations.contains(location);
//                 Label locationLabel = new Label(location, skin);
                
//                 if (isGreyedOut) {
//                     locationLabel.setColor(Color.GRAY);
//                     locationLabel.setText("[" + location + "]");
//                 }
                
//                 TextButton toggleButton = new TextButton(isGreyedOut ? "✓" : "✗", skin);
//                 toggleButton.addListener(new ClickListener() {
//                     @Override
//                     public void clicked(InputEvent event, float x, float y) {
//                         controller.toggleLocationGreyout(location);
//                     }
//                 });
                
//                 TextButton guessButton = new TextButton("Guess", skin);
//                 guessButton.addListener(new ClickListener() {
//                     @Override
//                     public void clicked(InputEvent event, float x, float y) {
//                         controller.spyGuessLocation(location);
//                     }
//                 });
                
//                 locationsContainer.add(locationLabel).width(locationColWidth).left().padRight(5);
//                 locationsContainer.add(toggleButton).width(40).padRight(5);
//                 locationsContainer.add(guessButton).width(70).right().row();
//             }
//         }
        
//         possibleLocationsTable.add(locationsContainer).colspan(3).fillX().row();
//         Label helpLabel = new Label("Mark locations with ✗ that you think are unlikely based on the conversation", skin);
//         helpLabel.setWrap(true);
//         helpLabel.setAlignment(Align.center);
//         possibleLocationsTable.add(helpLabel).colspan(3).padTop(20).width(tableWidth - 40);
//     }

//         /**
//      * Creates UI for round end or game end
//      * @param spy The spy's username (null for game end)
//      * @param location The round location (null for game end)
//      * @param scoreboard The current scoreboard
//      * @param isGameOver Whether this is final game end (true) or just round end (false)
//      */
//     private void createRoundEndedUI(String spy, String location, HashMap<String, Integer> scoreboard, boolean isGameOver) {
//         try {
//             // Create round end overlay
//             roundEndTable = new Table();
//             roundEndTable.setFillParent(true);
            
//             // Semi-transparent background
//             Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
//             pixmap.setColor(0, 0, 0, 0.8f);
//             pixmap.fill();
//             Texture bgTexture = new Texture(pixmap);
//             pixmap.dispose();
            
//             roundEndTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
            
//             // Calculate content width based on screen size
//             float contentWidth = Math.min(500, Gdx.graphics.getWidth() * 0.85f);
            
//             // Create content table with width constraint
//             Table contentTable = new Table();
//             contentTable.pad(30);
//             contentTable.defaults().pad(10).align(Align.center);
//             contentTable.setWidth(contentWidth);
            
//             // Title depends on whether game ended or just round ended
//             String titleText = isGameOver ? "GAME ENDED" : "ROUND ENDED";
//             Label titleLabel = new Label(titleText, skin);
//             titleLabel.setFontScale(1.5f);
//             contentTable.add(titleLabel).colspan(2).fillX().row();
            
            
//             // Scoreboard (shown for both round end and game end)
//             if (scoreboard != null && !scoreboard.isEmpty()) {
//                 // Add a bigger header for game end
//                 String scoreboardHeader = isGameOver ? "FINAL STANDINGS" : "SCOREBOARD";
//                 Label scoreboardLabel = new Label(scoreboardHeader, skin);
//                 if (isGameOver) {
//                     scoreboardLabel.setFontScale(1.3f);
//                 }
//                 contentTable.add(scoreboardLabel).colspan(2).padTop(20).padBottom(10).row();
                
//                 // Create a table for scoreboard with fixed column widths
//                 Table scoreboardTable = new Table();
//                 float playerColWidth = contentWidth * 0.6f;
//                 float scoreColWidth = contentWidth * 0.2f;
                
//                 // Add header row
//                 Label playerHeader = new Label("Player", skin);
//                 Label scoreHeader = new Label("Score", skin);
//                 scoreboardTable.add(playerHeader).width(playerColWidth).left().padBottom(8);
//                 scoreboardTable.add(scoreHeader).width(scoreColWidth).right().padBottom(8).row();
                

//                 List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoreboard.entrySet());
//                 sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));
                
//                 for (Map.Entry<String, Integer> entry : sortedEntries) {
//                     Label playerLabel = new Label(entry.getKey(), skin);
//                     playerLabel.setWrap(true);
//                     Label scoreLabel = new Label(Integer.toString(entry.getValue()), skin);
                    
//                     // Add trophy icon next to winner in game end screen
//                     if (isGameOver && sortedEntries.indexOf(entry) == 0) {
//                         playerLabel.setText("🏆 " + entry.getKey());
//                         playerLabel.setColor(Color.GOLD);
//                         scoreLabel.setColor(Color.GOLD);
//                     }
                    
//                     scoreboardTable.add(playerLabel).width(playerColWidth).left().fillX().padBottom(5);
//                     scoreboardTable.add(scoreLabel).width(scoreColWidth).right().padBottom(5).row();
//                 }
                
//                 contentTable.add(scoreboardTable).colspan(2).fillX().row();
//             }
            
//             // Buttons for host or all players
//             boolean isHost = gameModel.getUsername().equals(gameModel.getLobbyData().getHostPlayer());
            
            
//             // Only host gets "Start Next Round" for round end
//             if (isHost) {
//                 nextRoundButton = new TextButton("Next Round", skin);
//                 nextRoundButton.addListener(new ClickListener() {
//                     @Override
//                     public void clicked(InputEvent event, float x, float y) {
//                         controller.startNextRound();
//                         roundEndTable.setVisible(false);
//                         isRoundEnded = false;
//                     }
//                 });
//                 contentTable.add(nextRoundButton).colspan(2).padTop(20);
//             }
            
//             // Add content table to the overlay, centered
//             roundEndTable.add(contentTable).expand().fill().maxWidth(contentWidth);
//             stage.addActor(roundEndTable);
            
//             // Initially hidden
//             roundEndTable.setVisible(false);
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }

//     private void showFinalSpyGuessUI() {
//         // Only show for spy and only when timer reaches 0
//         if (!gameModel.getGameData().isSpy()) {
//             return;
//         }
        
//         // Create overlay for final guess
//         Table finalGuessTable = new Table();
//         finalGuessTable.setFillParent(true);
        
//         // Semi-transparent background
//         Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
//         pixmap.setColor(0, 0, 0, 0.9f);
//         pixmap.fill();
//         Texture bgTexture = new Texture(pixmap);
//         pixmap.dispose();
        
//         finalGuessTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));
        
//         // Content table
//         Table contentTable = new Table();
//         contentTable.pad(30);
        
//         // Add title
//         Label titleLabel = new Label("TIME'S UP! MAKE YOUR FINAL GUESS", skin);
//         titleLabel.setColor(Color.RED);
//         titleLabel.setFontScale(1.3f);
//         contentTable.add(titleLabel).padBottom(20).row();
        
//         // Add instruction
//         Label instructionLabel = new Label("As the spy, you get one last chance to guess the location:", skin);
//         instructionLabel.setWrap(true);
//         contentTable.add(instructionLabel).width(400).padBottom(30).row();
        
//         // Add locations table for final guess
//         Table locationsTable = new Table();
//         if (gameModel.getGameData().getPossibleLocations() != null) {
//             for (String location : gameModel.getGameData().getPossibleLocations()) {
//                 TextButton guessButton = new TextButton(location, skin);
//                 guessButton.addListener(new ClickListener() {
//                     @Override
//                     public void clicked(InputEvent event, float x, float y) {
//                         controller.spyGuessLocation(location);
//                         finalGuessTable.remove();
//                     }
//                 });
//                 locationsTable.add(guessButton).width(200).pad(5).row();
//             }
//         }
        
//         // Add locations to scroll pane if there are many
//         contentTable.add(locationsTable).row();
        
//         // Add content to overlay
//         finalGuessTable.add(contentTable);
//         stage.addActor(finalGuessTable);
//     }

//     public void showGameEndedUI(HashMap<String, Integer> scoreboard) {
//         isRoundEnded = true; 

//         updatePlayersList();
//         updateLocationsList();
        

//         if (roundEndTable != null) {
//             roundEndTable.remove(); 
//             Texture bgTexture = ((TextureRegionDrawable)roundEndTable.getBackground()).getRegion().getTexture();
//             if (bgTexture != null) {
//                 bgTexture.dispose();
//             }
//         }
        

//         createRoundEndedUI(null, null, scoreboard, true);
//         roundEndTable.setVisible(true);
//     }

//     public void handleRoundEnded(int roundNumber, String reason, String spy, 
//                            String location, HashMap<String, Integer> scoreboard) {
//         // Stop the timer
//         timerRunning = false;
        
//         if (roundEndTable != null) {
//             roundEndTable.remove(); 
//             Texture bgTexture = ((TextureRegionDrawable)roundEndTable.getBackground()).getRegion().getTexture();
//             if (bgTexture != null) {
//                 bgTexture.dispose(); // clean up
//             }
//         }
        
//         // Create new round end UI
//         createRoundEndedUI(spy, location, scoreboard, false);
//         roundEndTable.setVisible(true);
//         isRoundEnded = true;
//     }

//     public void showRoundEndedUI() {
//         isRoundEnded = true;
        
//         timerLabel.setText("TIME'S UP!");
        
//         // Hide vote buttons
//         updatePlayersList();
        
//         // Hide location guess buttons
//         updateLocationsList();
        
//         GameData gameData = gameModel.getGameData();
//         String spy = gameData.getIsSpyUsername();
//         String location = gameData.getLocation();
//         HashMap<String, Integer> scoreboard = gameData.getScoreboard();
        
//         if (roundEndTable != null) {
//             roundEndTable.remove(); 
//             Texture bgTexture = ((TextureRegionDrawable)roundEndTable.getBackground()).getRegion().getTexture();
//             if (bgTexture != null) {
//                 bgTexture.dispose(); 
//             }
//         }
        
//         createRoundEndedUI(spy, location, scoreboard, false);
//         roundEndTable.setVisible(true);
//     }

//     public void resetRoundEndUI() {
//         if (roundEndTable != null) {
//             roundEndTable.setVisible(false);
//         }
//         isRoundEnded = false;
        
//         // Update the UI to reflect game data
//         updateFromModel();
//     }

    
    
//     // called when the model changes
//     public void updateFromModel() {
//         GameData gameData = gameModel.getGameData();
        
//         if (!timerRunning) {
//             int timeRemaining = gameData.getTimeRemaining();
//             int minutes = timeRemaining / 60;
//             int seconds = timeRemaining % 60;
//             timerLabel.setText(String.format("%d:%02d", minutes, seconds));
//         }
        
//         // update location and role
//         String displayedLocation = gameData.isSpy() ? "???" : gameData.getLocation();
//         locationLabel.setText(displayedLocation);
//         roleLabel.setText(gameData.getRole());
        
//         // update players and locations lists
//         updatePlayersList();
//         updateLocationsList();
//     }

//     private void decrementTimer() {
//         GameData gameData = gameModel.getGameData();
//         int timeRemaining = gameData.getTimeRemaining();
        
//         if (timeRemaining > 0) {
//             timeRemaining--;
//             gameData.setTimeRemaining(timeRemaining);
            
//             // Update the timer display
//             updateTimerDisplay(timeRemaining);
            
//             if (timeRemaining == 0) {
//                 timerRunning = false;
                
//                 // If player is spy, show final guess UI first
//                 if (gameModel.getGameData().isSpy()) {
//                     showFinalSpyGuessUI();
//                 } else {
//                     showRoundEndedUI();
//                 }
//             }
//         }
//     }
    
//     public void updateTimerDisplay(int timeRemaining) {
//         int minutes = timeRemaining / 60;
//         int seconds = timeRemaining % 60;
//         timerLabel.setText(String.format("%d:%02d", minutes, seconds));
//     }

//     public void startTimer(int duration) {
//         gameModel.getGameData().setTimeRemaining(duration);
//         updateTimerDisplay(duration);
//         timerRunning = true;
//         accumulator = 0f;
//     }

//     @Override
//     public void update() {
//         // update the stage normally
//         if (timerRunning) {
//             float delta = Gdx.graphics.getDeltaTime();
//             accumulator += delta;
            
//             // Update timer every second
//             if (accumulator >= 1.0f) {
//                 accumulator -= 1.0f;
//                 decrementTimer();
//             }
//         }
//         updateFromModel();

//         stage.act();
//         stage.draw();
        
//     }

//     @Override
//     public void resize(int width, int height) {
//         viewport.update(width, height, true);
//     }

//     public void dispose() {
//         if (bgTexture != null) {
//             bgTexture.dispose();
//         }

//         stage.dispose();
//     }

    
// }
