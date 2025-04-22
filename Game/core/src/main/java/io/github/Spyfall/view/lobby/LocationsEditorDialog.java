
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
import io.github.Spyfall.view.ui.UIConstants;

import java.util.ArrayList;
import java.util.List;

public class LocationsEditorDialog extends Dialog {
    private final Table locationsTable;
    private final LobbyController lobbyController;
    private final AudioService audioService;
    private ScrollPane scrollPane;

    public LocationsEditorDialog(Skin skin, LobbyController lobbyController, Stage stage, AudioService audioService) {
        super("Edit Locations", skin);
        this.audioService = audioService;
        this.lobbyController = lobbyController;
        this.locationsTable = new Table(skin);
        locationsTable.top().left();

        Drawable dim = skin.newDrawable("white", UIConstants.transparentBlack);
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());

        getStyle().stageBackground = dim;

        initDialog();
        pack();
    }

    private void initDialog() {
        for (Location loc : lobbyController.getLobbyData().getLocations()) {
            addLocation(loc);
        }

        this.scrollPane = new ScrollPane(locationsTable, getSkin());
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setScrollingDisabled(true, false);

        // Create input field and Add button
        TextField newLoc = new TextField("", getSkin());
        newLoc.setMessageText("New location");
        newLoc.getStyle().font.getData().setScale(1.4f);
        newLoc.setHeight(60);

        TextButton addBtn = new TextButton("Add", getSkin());
        addBtn.getLabel().setFontScale(1.4f);
        addBtn.setHeight(60);
        addBtn.setWidth(140);

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

        // Layout: New location input
        Table inputRow = new Table(getSkin());
        inputRow.add(newLoc).width(320).height(60).padRight(15);
        inputRow.add(addBtn).width(140).height(60);

        // Layout: Save/Cancel
        TextButton saveBtn = new TextButton("Save", getSkin());
        saveBtn.getLabel().setFontScale(1.5f);
        saveBtn.setHeight(60);
        saveBtn.setWidth(160);

        TextButton cancelBtn = new TextButton("Cancel", getSkin());
        cancelBtn.getLabel().setFontScale(1.5f);
        cancelBtn.setHeight(60);
        cancelBtn.setWidth(160);

        Table buttonRow = new Table(getSkin());
        buttonRow.defaults().pad(5);
        buttonRow.add(saveBtn).width(160).height(60);
        buttonRow.add(cancelBtn).width(160).height(60);

        // Layout: content table
        Table ct = getContentTable();
        ct.pad(5);
        ct.add(scrollPane).width(500).maxHeight(400).row();
        ct.add(inputRow).padTop(1).row();
        ct.add(buttonRow).padTop(1).row();

        // Dialog logic
        button(saveBtn, true);
        button(cancelBtn, false);
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
        if ((Boolean) obj) {
            List<Location> updated = getAllLocations();
            lobbyController.updateLobbyLocations(updated);
        }
        Gdx.input.setOnscreenKeyboardVisible(false);
        hide();
    }
}
