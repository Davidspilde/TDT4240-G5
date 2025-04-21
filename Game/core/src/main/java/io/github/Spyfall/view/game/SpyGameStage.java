package io.github.Spyfall.view.game;

import java.util.HashMap;

import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import io.github.Spyfall.model.GameData;
import io.github.Spyfall.view.game.ui.LocationsListComponent;

public class SpyGameStage extends BaseGameStage {
    
    private LocationsListComponent locationsComponent;
    private float finalGuessTimeRemaining;
    private boolean finalGuessActive = false;
    
    public SpyGameStage(String roleName, ScreenViewport viewport) {
        super(viewport);
        initStage(roleName);
    }
    
    protected void initStage(String roleName) {
        super.init();
        
        playerInfoComponent.setRole(roleName);
        playerInfoComponent.setLocation(""); // spy doesn't know location
        
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
    
    
    private void updateLocationsList() {
        GameData gameData = gameModel.getGameData();
        locationsComponent.setLocations(gameData.getPossibleLocations());
        locationsComponent.setGreyedOutLocations(gameData.getGreyedOutLocations());
    }
    
    @Override
    public void update() {
        super.update();
    
        updateLocationsList();
    }
    
    @Override
    public void onTimerEnd() {
        // TODO:
        // showFinalSpyGuessUI();
    }

    private void showFinalSpyGuessUI() {
        // Final guess UI implementation (could also be moved to a component)
        finalGuessActive = true;
    }
    
    private void removeFinalGuessUI() {
        finalGuessActive = false;
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