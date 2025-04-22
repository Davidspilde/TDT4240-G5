package io.github.Spyfall.view.game.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.Location;

/**
 * Component for displaying possible locations list for the spy
 */
public class LocationsListComponent extends GameComponent {

    private List<Location> locations;
    private Set<Location> greyedOutLocations = new HashSet<>();
    private GameplayController controller;
    
    public LocationsListComponent(Skin skin, GameplayController controller) {
        super(skin);
        this.controller = controller;
    }

    @Override
    protected void create() {
        rootTable.top();
        
        Label locationsHeader = new Label("Possible Locations", skin);
        locationsHeader.setAlignment(Align.center);
        locationsHeader.setFontScale(1.2f);
        rootTable.add(locationsHeader).colspan(3).padBottom(20).row();
    }

    /**
     * Set possible locations list
     */
    public void setLocations(List<Location> locations) {
        this.locations = locations;
        update();
    }

    /**
     * Set greyed out locations
     */
    public void setGreyedOutLocations(Set<Location> greyedOutLocations) {
        this.greyedOutLocations = greyedOutLocations != null ? 
                                  greyedOutLocations : new HashSet<>();
        update();
    }


    @Override
    public void update() {

        rootTable.clear();
        rootTable.top();

        Table headerTable = new Table();

        Label locationsHeader = new Label("Possible Locations", skin);
        locationsHeader.setAlignment(Align.center);
        locationsHeader.setFontScale(1f);

        headerTable.add(locationsHeader).expandX().fillX().padBottom(10);
        rootTable.add(headerTable).fillX().expandX().row();

        if (locations == null || locations.isEmpty()) {
            Label noLocationsLabel = new Label("No locations available", skin);
            noLocationsLabel.setAlignment(Align.center);
            noLocationsLabel.setFontScale(0.8f);
            rootTable.add(noLocationsLabel).expandX().fillX().padTop(20);
            return;
        }

        ScrollPane scrollPane = new ScrollPane(null, skin);
        Table locationsTable = new Table();
        locationsTable.top();
        locationsTable.defaults().pad(5);

        float availableWidth = rootTable.getWidth();
        if (availableWidth <= 0) {
            availableWidth = 400f;
        }

        float locationColWidth = availableWidth * 0.6f; 
        float toggleColWidth = availableWidth * 0.15f;  
        float guessColWidth = availableWidth * 0.25f;  

        for (Location location : locations) {
            boolean isGreyedOut = greyedOutLocations.contains(location);
            
            Label locationLabel = new Label(isGreyedOut ? "[" + location.getName() + "]" : location.getName(), skin);
            locationLabel.setWrap(true);
            locationLabel.setAlignment(Align.left);
            
            if (isGreyedOut) {
                locationLabel.setColor(Color.GRAY);
            }
            
            TextButton toggleButton = new TextButton(isGreyedOut ? "✓" : "✗", skin);
            toggleButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (controller != null) {
                        controller.toggleLocationGreyout(location);
                    }
                }
            });
            
            TextButton guessButton = new TextButton("Guess", skin);
            guessButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (controller != null) {
                        controller.handleSpyGuess(GameModel.getInstance().getUsername(), location.getName());
                    }
                }
            });
            
            locationsTable.add(locationLabel).width(locationColWidth).fillX().left();
            locationsTable.add(toggleButton).width(toggleColWidth).fillX().center();
            locationsTable.add(guessButton).width(guessColWidth).fillX().right().row();
        }

        scrollPane.setActor(locationsTable);
        scrollPane.setScrollingDisabled(true, false); 
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setSmoothScrolling(true);

        rootTable.add(scrollPane).expand().fill();
    }

}
