package io.github.Spyfall.view.game;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.view.game.ui.LocationsListComponent;

public class SpyGameStage extends BaseGameStage {
    
    private LocationsListComponent locationsComponent;
    private float finalGuessTimeRemaining;
    private boolean finalGuessActive = false;
    private GameplayController controller;
    
    public SpyGameStage(String roleName, ScreenViewport viewport) {
        super(viewport);
        initStage(roleName);
        this.controller = GameplayController.getInstance();
    }
    
    protected void initStage(String roleName) {
        super.init();
        
        playerInfoComponent.setRole(roleName);
        playerInfoComponent.setLocation("???"); // spy doesn't know location
        
        locationsComponent = new LocationsListComponent(skin, controller);
        updateLocationsList();

        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.addActor(timerComponent.getActor());
        infoGroup.addActor(playerInfoComponent.getActor());
        
        rootTable.top().pad(20f);
        rootTable.add(infoGroup).expandX().center().row();
        rootTable.row().padTop(20);
        rootTable.add(locationsComponent.getActor()).expand().fill();
        
        rootTable.row().padTop(20);
        rootTable.add(gameControlsComponent.getActor());
    }

    /**
     * Initialize locations list once at the start of the round
     */
    private void initializeLocationsList() {
        GameData gameData = gameModel.getGameData();
        locationsComponent.setLocations(gameData.getPossibleLocations());
        
        // Set any previously greyed out locations (if applicable)
        locationsComponent.setGreyedOutLocations(gameData.getGreyedOutLocations());
        
        System.out.println("Initialized spy locations list with " + 
                          gameData.getPossibleLocations().size() + " locations");
    }
    
    
    private void updateLocationsList() {
        GameData gameData = gameModel.getGameData();
        locationsComponent.setLocations(gameData.getPossibleLocations());
        locationsComponent.setGreyedOutLocations(gameData.getGreyedOutLocations());
    }
    
    @Override
    public void update() {
        super.update();
    
        if (finalGuessActive) {
            // Update the final guess UI if active
        }
    }
    
    @Override
    public void onTimerEnd() {
        // TODO:
    }

    private void showFinalSpyGuessUI() {
        // Final guess UI implementation (could also be moved to a component)
        finalGuessActive = true;
    }
    
    private void removeFinalGuessUI() {
        finalGuessActive = false;
    }

    /**
     * Called when a new round starts (to reset the UI for the new round)
     */
    public void handleNewRound() {
        // Re-initialize locations for the new round
        initializeLocationsList();
        
        // Reset other UI elements
        removeFinalGuessUI();
    }

    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy, 
                              String location, HashMap<String, Integer> scoreboard) {
        // Use the base class implementation which handles the overlay
        super.handleRoundEnded(roundNumber, reason, spy, location, scoreboard);
        
        // Also remove final guess UI if active
        removeFinalGuessUI();
    }
    
}