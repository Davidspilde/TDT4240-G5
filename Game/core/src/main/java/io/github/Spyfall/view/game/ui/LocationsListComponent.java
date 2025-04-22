
package io.github.Spyfall.view.game.ui;

import java.util.List;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
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

/**
 * Component for displaying possible locations list for the spy.
 * Each location appears in a styled clickable box.
 */
public class LocationsListComponent extends GameComponent {

    private List<Location> locations;
    private final GameplayController controller;
    private Stage stage;
    private AudioService audioService;

    public LocationsListComponent(Skin skin, GameplayController controller, Stage stage, AudioService audioService) {
        super(skin);
        this.audioService = audioService;
        this.stage = stage;
        this.controller = controller;
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
        locationsTable.defaults().pad(10).width(220);

        int itemsPerRow = 3;
        int colCount = 0;

        for (Location location : locations) {
            Table block = new Table(skin);
            block.setBackground(skin.newDrawable("white", new Color(0.2f, 0.2f, 0.2f, 0.3f)));
            block.defaults().pad(5).fillX();

            Label locationLabel = new Label(location.getName(), skin);
            locationLabel.setWrap(true);
            locationLabel.setAlignment(Align.center);
            locationLabel.setFontScale(1f);

            block.add(locationLabel).width(180).padBottom(8).row();

            // Make the entire block clickable
            block.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    audioService.playSound("click");
                    showConfirmationPopup(location.getName());
                }
            });

            Container<Table> container = new Container<>(block);
            container.pad(5);
            locationsTable.add(container).expand().fill();

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

        rootTable.add(scrollPane).expand().fill().colspan(3).row();
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

        confirmDialog.text("Confirm guess \"" + locationName + "\"?");
        confirmDialog.button("Yes", true);
        confirmDialog.button("Cancel", false);
        confirmDialog.show(stage);
    }
}
