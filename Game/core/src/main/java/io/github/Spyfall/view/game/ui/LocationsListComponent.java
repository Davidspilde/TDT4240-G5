package io.github.Spyfall.view.game.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;

/**
 * Component for displaying possible locations list for the spy
 */
public class LocationsListComponent extends GameComponent {

    private List<String> locations;
    private Set<String> greyedOutLocations = new HashSet<>();
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
    public void setLocations(List<String> locations) {
        this.locations = locations;
        update();
    }

    /**
     * Set greyed out locations
     */
    public void setGreyedOutLocations(Set<String> greyedOutLocations) {
        this.greyedOutLocations = greyedOutLocations != null ? 
                                  greyedOutLocations : new HashSet<>();
        update();
    }

    @Override
    public void update() {
        int childCount = rootTable.getChildren().size;
        if (childCount > 3) { // Header takes 3 cells (label + colspan)
            rootTable.clearChildren();
            
            Label locationsHeader = new Label("Possible Locations", skin);
            locationsHeader.setAlignment(Align.center);
            locationsHeader.setFontScale(1.2f);
            rootTable.add(locationsHeader).colspan(3).padBottom(20).row();
        }
        
        if (locations == null || locations.isEmpty()) {
            Label noLocationsLabel = new Label("No locations available", skin);
            rootTable.add(noLocationsLabel).colspan(3).center();
            return;
        }
        
        for (String location : locations) {
            boolean isGreyedOut = greyedOutLocations.contains(location);
            Label locationLabel = new Label(location, skin);

            if (isGreyedOut) {
                locationLabel.setColor(Color.GRAY);
                locationLabel.setText("[" + location + "]");
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
                        // TODO:
                        //controller.spyGuessLocation(location);
                    }
                }
            });
            
            rootTable.add(locationLabel).width(150).left().padRight(5);
            rootTable.add(toggleButton).width(40).padRight(5);
            rootTable.add(guessButton).width(70).right().row();
        }
    }

}
