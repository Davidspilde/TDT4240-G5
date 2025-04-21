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
import io.github.Spyfall.view.game.ui.PlayersList;

public class PlayerGameStage extends BaseGameStage {
    
    private PlayersList playersListComponent;
    
    public PlayerGameStage(String locationName, String roleName, ScreenViewport viewport) {
        super(viewport);
        initStage(locationName, roleName);
    }
    
    protected void initStage(String locationName, String roleName) {
        super.init();
        
        playerInfoComponent.setRole(roleName);
        playerInfoComponent.setLocation(locationName);
        
        playersListComponent = new PlayersList(skin, gameModel.getUsername());
        playersListComponent.setController(controller);
        playersListComponent.setPlayers(gameModel.getLobbyData().getPlayers());

        VerticalGroup infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.addActor(timerComponent.getActor());
        infoGroup.addActor(playerInfoComponent.getActor());

        rootTable.top().pad(20f);
        rootTable.add(infoGroup).expandX().center().row();
        rootTable.row().padTop(20);
        rootTable.add(playersListComponent.getActor()).expand().fill();

        rootTable.row().padTop(20);
        rootTable.add(gameControlsComponent.getActor());
    }
    
    @Override
    public void update() {
        super.update();
        
        if (playersListComponent != null) {
            playersListComponent.setPlayers(gameModel.getLobbyData().getPlayers());
            playersListComponent.setShowVoteButtons(gameModel.getCurrentState() == GameState.IN_GAME);
            playersListComponent.update();
        }
        
        // update other components if needed
    }
    
    @Override
    public void onTimerEnd() {
        if (roundEndOverlay != null) {
            roundEndOverlay.getActor().setVisible(true);
            isRoundEnded = true;
        }
    }
    
    @Override
    public void handleRoundEnded(int roundNumber, String reason, String spy, 
                              String location, HashMap<String, Integer> scoreboard) {
        super.handleRoundEnded(roundNumber, reason, spy, location, scoreboard);
    }
}