
package io.github.Spyfall.view.game.ui;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;

public class LocationsListComponent extends GameComponent {

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
        header.setFontScale(1.2f);
        rootTable.add(header).colspan(3).padBottom(20).row();
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
        header.setFontScale(1.2f);
        rootTable.add(header).colspan(3).padBottom(20).row();

        if (locations == null || locations.isEmpty()) {
            Label noLocationsLabel = new Label("No locations available", skin);
            noLocationsLabel.setAlignment(Align.center);
            noLocationsLabel.setFontScale(0.8f);
            rootTable.add(noLocationsLabel).colspan(3).padTop(20).row();
            return;
        }

        Table locationsTable = new Table();
        locationsTable.top().pad(10);
        locationsTable.defaults().pad(8).width(200).height(70);

        int itemsPerRow = 3;
        int colCount = 0;

        for (Location location : locations) {
            TextButton locationButton = new TextButton(location.getName(), skin);
            locationButton.getLabel().setWrap(true);
            locationButton.getLabel().setAlignment(Align.center);
            locationButton.getLabel().setFontScale(1.0f);

            locationButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    audioService.playSound("click");
                    showConfirmationPopup(location.getName());
                }
            });

            locationsTable.add(locationButton).expand().fill();

            colCount++;
            if (colCount % itemsPerRow == 0) {
                locationsTable.row();
            }
        }

        ScrollPane scrollPane = new ScrollPane(locationsTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setSmoothScrolling(true);

        Table scrollWrapper = new Table();
        scrollWrapper.padLeft(8).padRight(8);
        scrollWrapper.add(scrollPane).expand().fill();

        rootTable.add(scrollWrapper).expand().fill().colspan(3).row();
    }

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
