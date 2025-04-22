package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.view.ui.UIConstants;

import java.util.ArrayList;
import java.util.List;

public class LocationsEditorDialog extends Dialog {
    private final Table locationsTable;
    private final LobbyController lobbyController;
    private ScrollPane scrollPane;

    public LocationsEditorDialog(Skin skin, LobbyController lobbyController, Stage stage) {
        super("Edit Locations", skin);
        this.lobbyController = lobbyController;
        this.locationsTable = new Table(skin);
        locationsTable.top().left();
        // Makes backgorund transparent when used
        Drawable dim = skin.newDrawable("white", UIConstants.transparentBlack);
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());

        getStyle().stageBackground = dim;

        initDialog();
        pack();
    }

    private void initDialog() {
        // Populate existing locations
        for (Location loc : lobbyController.getLobbyData().getLocations()) {
            addLocation(loc);
        }

        this.scrollPane = new ScrollPane(locationsTable, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setScrollingDisabled(true, false);

        // Add-new-location row
        TextField newLoc = new TextField("", getSkin());
        newLoc.setMessageText("New location");
        TextButton addBtn = new TextButton("Add", getSkin());
        addBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                String name = newLoc.getText().trim();
                if (!name.isEmpty()) {
                    addLocation(new Location(name, new ArrayList<>()));
                    newLoc.setText("");
                    pack();
                }
            }
        });

        Table addRow = new Table(getSkin());
        addRow.add(newLoc).width(200).padRight(10);
        addRow.add(addBtn);

        // Lay out content
        Table ct = getContentTable();
        ct.pad(10);
        ct.add(scrollPane).width(450).maxHeight(350);
        ct.row();
        ct.add(addRow).padTop(10);
        ct.row();

        button("Save", true);
        button("Cancel", false);
    }

    private void addLocation(Location loc) {
        LocationRow row = new LocationRow(loc, getSkin(), this);
        locationsTable.add(row).expandX().fillX().row();
    }

    public void removeLocationRow(LocationRow row) {
        locationsTable.removeActor(row);
        locationsTable.invalidateHierarchy();
        scrollPane.layout();
        pack();
    }

    /**
     * Helper function to return a list of all location data from UI
     */
    public List<Location> getAllLocations() {
        List<Location> result = new ArrayList<>();
        for (Actor a : locationsTable.getChildren()) {
            if (a instanceof LocationRow lr) {
                result.add(lr.toLocation());
            }
        }
        return result;
    }

    // Runs if save is clicked
    @Override
    protected void result(Object obj) {
        if ((Boolean) obj) {
            List<Location> updated = getAllLocations();
            lobbyController.updateLobbyLocations(updated);
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
        hide();
    }
}
