
package io.github.Spyfall.view.game.ui;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.model.Location;

/**
 * Component for displaying possible locations list for the spy (without
 * grey-out toggling)
 */
public class LocationsListComponent extends GameComponent {

    private List<Location> locations;
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
        rootTable.add(locationsHeader).colspan(2).padBottom(20).row();
    }

    public void setLocations(List<Location> locations) {
        this.locations = locations;
        update();
    }

    @Override
    public void update() {
        rootTable.clear();
        rootTable.top();

        Label locationsHeader = new Label("Possible Locations", skin);
        locationsHeader.setAlignment(Align.center);
        locationsHeader.setFontScale(1f);
        rootTable.add(locationsHeader).colspan(2).padBottom(10).row();

        if (locations == null || locations.isEmpty()) {
            Label noLocationsLabel = new Label("No locations available", skin);
            noLocationsLabel.setAlignment(Align.center);
            noLocationsLabel.setFontScale(0.8f);
            rootTable.add(noLocationsLabel).colspan(2).padTop(20).row();
            return;
        }

        Table locationsTable = new Table();
        locationsTable.top();
        locationsTable.defaults().pad(5);

        float availableWidth = rootTable.getWidth();
        if (availableWidth <= 0) {
            availableWidth = 400f;
        }

        float locationColWidth = availableWidth * 0.7f;
        float guessColWidth = availableWidth * 0.3f;

        for (Location location : locations) {
            Label locationLabel = new Label(location.getName(), skin);
            locationLabel.setWrap(true);
            locationLabel.setAlignment(Align.left);

            TextButton guessButton = new TextButton("Guess", skin);
            guessButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    controller.onSpyGuess(location.getName());
                }
            });

            locationsTable.add(locationLabel).width(locationColWidth).fillX().left();
            locationsTable.add(guessButton).width(guessColWidth).fillX().right().row();
        }

        ScrollPane scrollPane = new ScrollPane(locationsTable, skin);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setSmoothScrolling(true);

        rootTable.add(scrollPane).expand().fill().colspan(2).row();
    }
}
