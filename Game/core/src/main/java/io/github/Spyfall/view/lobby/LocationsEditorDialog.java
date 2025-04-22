
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
import io.github.Spyfall.services.AudioService;

import java.util.ArrayList;
import java.util.List;

public class LocationsEditorDialog extends Dialog {

    // Layout constants
    private final float DIALOG_WIDTH = 500f;
    private final float DIALOG_HEIGHT = 400f;
    private final float FIELD_WIDTH = 320f;
    private final float FIELD_HEIGHT = 60f;
    private final float BUTTON_WIDTH = 160f;
    private final float BUTTON_HEIGHT = 60f;
    private final float BUTTON_FONT_SCALE = 1.5f;
    private final float FIELD_FONT_SCALE = 1.4f;
    private final float GAP = 15f;

    private final Table locationsTable;
    private final LobbyController lobbyController;
    private final AudioService audioService;
    private ScrollPane scrollPane;

    public LocationsEditorDialog(Skin skin, LobbyController lobbyController, Stage stage, AudioService audioService) {
        super("Edit Locations", skin);
        this.lobbyController = lobbyController;
        this.audioService = audioService;
        this.locationsTable = new Table(skin);
        locationsTable.top().left();

        // Semi-transparent background for dialog
        Drawable dim = skin.newDrawable("white", new com.badlogic.gdx.graphics.Color(0, 0, 0, 0.75f));
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

        // Scrollable list of locations
        scrollPane = new ScrollPane(locationsTable, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setScrollingDisabled(true, false);

        // New location input field
        TextField newLoc = new TextField("", getSkin());
        newLoc.setMessageText("New location");
        newLoc.getStyle().font.getData().setScale(FIELD_FONT_SCALE);
        newLoc.setHeight(FIELD_HEIGHT);

        // Add button
        TextButton addBtn = new TextButton("Add", getSkin());
        addBtn.getLabel().setFontScale(FIELD_FONT_SCALE);
        addBtn.setHeight(FIELD_HEIGHT);
        addBtn.setWidth(BUTTON_WIDTH);

        addBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                String name = newLoc.getText().trim();
                if (!name.isEmpty()) {
                    addLocation(new Location(name, new ArrayList<>()));
                    newLoc.setText("");
                    pack();
                }
            }
        });

        // Row for adding new location
        Table inputRow = new Table(getSkin());
        inputRow.add(newLoc).width(FIELD_WIDTH).height(FIELD_HEIGHT).padRight(GAP);
        inputRow.add(addBtn).width(BUTTON_WIDTH).height(FIELD_HEIGHT);

        // Save and cancel buttons
        TextButton saveBtn = new TextButton("Save", getSkin());
        saveBtn.getLabel().setFontScale(BUTTON_FONT_SCALE);
        saveBtn.setHeight(BUTTON_HEIGHT);

        TextButton cancelBtn = new TextButton("Cancel", getSkin());
        cancelBtn.getLabel().setFontScale(BUTTON_FONT_SCALE);
        cancelBtn.setHeight(BUTTON_HEIGHT);

        Table buttonRow = new Table(getSkin());
        buttonRow.defaults().pad(5);
        buttonRow.add(saveBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);
        buttonRow.add(cancelBtn).width(BUTTON_WIDTH).height(BUTTON_HEIGHT);

        // Assemble full layout
        Table ct = getContentTable();
        ct.pad(5);
        ct.add(scrollPane).width(DIALOG_WIDTH).maxHeight(DIALOG_HEIGHT).row();
        ct.add(inputRow).padTop(5).row();
        ct.add(buttonRow).padTop(5).row();

        // Dialog logic
        button(saveBtn, true);
        button(cancelBtn, false);
        key(Input.Keys.ESCAPE, false);
    }

    private void addLocation(Location loc) {
        LocationRow row = new LocationRow(loc, getSkin(), this, audioService);
        locationsTable.add(row).expandX().fillX().padBottom(10).row();
    }

    public void removeLocationRow(LocationRow row) {
        locationsTable.removeActor(row);
        locationsTable.invalidateHierarchy();
        scrollPane.layout();
        pack();
    }

    public List<Location> getAllLocations() {
        List<Location> result = new ArrayList<>();
        for (Actor a : locationsTable.getChildren()) {
            if (a instanceof LocationRow lr) {
                result.add(lr.toLocation());
            }
        }
        return result;
    }

    @Override
    protected void result(Object obj) {
        audioService.playSound("click");
        if (Boolean.TRUE.equals(obj)) {
            List<Location> updated = getAllLocations();
            lobbyController.updateLobbyLocations(updated);
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
        hide();
    }
}
