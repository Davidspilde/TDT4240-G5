
package io.github.Spyfall.view.game.ui;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;

/**
 * Component that displays the list of possible locations for the spy to guess
 * from.
 */
public class LocationsListComponent extends GameComponent {

    // Layout constants
    private final float BUTTON_WIDTH = 200f;
    private final float BUTTON_HEIGHT = 70f;
    private final float BUTTON_FONT_SCALE = 1.0f;
    private final float HEADER_FONT_SCALE = 1.3f;
    private final float ITEM_PADDING = 8f;
    private final int ITEMS_PER_ROW = 3;

    private List<Location> locations;
    private final GameplayController controller;
    private final Stage stage;
    private final AudioService audioService;

    public LocationsListComponent(Skin skin, GameplayController controller, Stage stage, AudioService audioService) {
        super(skin);
        this.controller = controller;
        this.stage = stage;
        this.audioService = audioService;
    }

    @Override
    protected void create() {
        rootTable.top();

        Label header = new Label("Possible Locations", skin);
        header.setAlignment(Align.center);
        header.setFontScale(HEADER_FONT_SCALE);

        rootTable.add(header).colspan(ITEMS_PER_ROW).padBottom(20).row();
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        update();
    }

    @Override
    public void update() {
        rootTable.clear();
        rootTable.top();

        Label header = new Label("Possible Locations", skin);
        header.setAlignment(Align.center);
        header.setFontScale(HEADER_FONT_SCALE);
        rootTable.add(header).colspan(ITEMS_PER_ROW).padBottom(20).row();

        if (locations == null || locations.isEmpty()) {
            Label noLocationsLabel = new Label("No locations available", skin);
            noLocationsLabel.setAlignment(Align.center);
            noLocationsLabel.setFontScale(1.0f);
            rootTable.add(noLocationsLabel).colspan(ITEMS_PER_ROW).padTop(20).row();
            return;
        }

        Table locationsTable = new Table();
        locationsTable.top().pad(10);
        locationsTable.defaults().pad(ITEM_PADDING).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);

        int colCount = 0;

        // Create location buttons
        for (Location location : locations) {
            TextButton locationButton = new TextButton(location.getName(), skin);
            locationButton.getLabel().setWrap(true);
            locationButton.getLabel().setAlignment(Align.center);
            locationButton.getLabel().setFontScale(BUTTON_FONT_SCALE);

            locationButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    audioService.playSound("click");
                    showConfirmationPopup(location.getName());
                }
            });

            locationsTable.add(locationButton).expand().fill();

            colCount++;
            if (colCount % ITEMS_PER_ROW == 0) {
                locationsTable.row();
            }
        }

        // Wrap in scroll pane
        ScrollPane scrollPane = new ScrollPane(locationsTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setSmoothScrolling(true);

        Table scrollWrapper = new Table();
        scrollWrapper.padLeft(8).padRight(8);
        scrollWrapper.add(scrollPane).expand().fill();

        rootTable.add(scrollWrapper).expand().fill().colspan(ITEMS_PER_ROW).row();
    }

    /**
     * Shows a confirmation popup before submitting the spy's location guess.
     */
    private void showConfirmationPopup(String locationName) {
        Dialog confirmDialog = new Dialog("Confirm Guess", skin) {
            @Override
            protected void result(Object object) {
                audioService.playSound("click");
                if (Boolean.TRUE.equals(object)) {
                    controller.onSpyGuess(locationName);
                }
            }
        };

        confirmDialog.text("Guess location \"" + locationName + "\"?");
        confirmDialog.button("Yes", true);
        confirmDialog.button("Cancel", false);
        confirmDialog.show(stage);
    }
}
