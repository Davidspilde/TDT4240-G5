package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.services.websocket.SendMessageService;

public class LocationsEditorDialog extends Dialog {
    private Skin skin;
    private SendMessageService sendMsgService;
    private VerticalGroup locationsGroup;

    public LocationsEditorDialog(Skin skin) {
        super("Edit Locations", skin);
        this.skin = skin;
        this.sendMsgService = SendMessageService.getInstance();

        initDialog();
    }

    private void initDialog() {
        // Create locations table
        Table locationsTable = new Table();
        locationsTable.pad(10);

        // Create locations group for scrolling
        locationsGroup = new VerticalGroup();
        locationsGroup.space(10);
        locationsGroup.align(Align.center);

        // Add default locations
        addLocation("Airplane");
        addLocation("Bank");
        addLocation("Beach");
        addLocation("Casino");
        addLocation("Church");
        addLocation("Circus");
        addLocation("Hotel");
        addLocation("Hospital");
        addLocation("Library");
        addLocation("Movie Theater");
        addLocation("Museum");
        addLocation("Restaurant");
        addLocation("School");
        addLocation("Supermarket");
        addLocation("Theater");

        // Create scroll pane for locations
        ScrollPane locationsScrollPane = new ScrollPane(locationsGroup, skin);
        locationsScrollPane.setFadeScrollBars(false);
        locationsScrollPane.setScrollbarsVisible(true);
        locationsScrollPane.setScrollbarsOnTop(true);

        // Add new location section
        Table addLocationTable = new Table();
        TextField newLocationField = new TextField("", skin);
        newLocationField.setMessageText("Enter new location");
        TextButton addButton = new TextButton("Add", skin);

        addButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                String locationName = newLocationField.getText().trim();
                if (!locationName.isEmpty()) {
                    addLocation(locationName);
                    newLocationField.setText("");
                }
            }
        });

        addLocationTable.add(newLocationField).width(200).padRight(10);
        addLocationTable.add(addButton);

        // Add components to locations table
        locationsTable.add(locationsScrollPane).width(300).height(300).padBottom(10).row();
        locationsTable.add(addLocationTable).padTop(10);

        // Add locations table to dialog
        getContentTable().add(locationsTable).pad(10);

        // Add buttons
        TextButton closeButton = new TextButton("Close", skin);

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        button(closeButton);
    }

    private void addLocation(String locationName) {
        Table locationRow = new Table();

        Label locationLabel = new Label(locationName, skin);
        TextButton removeButton = new TextButton("X", skin);

        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                locationsGroup.removeActor(locationRow);
            }
        });

        locationRow.add(locationLabel).expandX().left();
        locationRow.add(removeButton).width(30);

        locationsGroup.addActor(locationRow);
    }
}
