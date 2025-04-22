package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;

import java.util.ArrayList;
import java.util.List;

public class LocationRow extends Table {
    private final Skin skin;
    private final TextButton expandButton;
    private final TextField nameField;
    private final TextButton deleteButton;
    private final Table rolesTable;
    private final Table rolesListTable;
    private final List<TextField> roleFields = new ArrayList<>();
    private final LocationsEditorDialog parentDialog;
    private final AudioService audioService;
    private boolean expanded = false;

    public LocationRow(Location location, Skin skin, LocationsEditorDialog parentDialog, AudioService audioService) {
        super(skin);
        this.skin = skin;
        this.audioService = audioService;
        this.parentDialog = parentDialog;
        this.padBottom(10);

        // Create name input field
        nameField = new TextField(location.getName(), skin);
        nameField.setMessageText("Location name");

        // Create expand/collapse button
        expandButton = new TextButton("-", skin);
        expandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                audioService.playSound("click");
                toggleRoles();
            }
        });
        // Create delete button
        deleteButton = new TextButton("X", skin);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {

                audioService.playSound("click");
                parentDialog.removeLocationRow(LocationRow.this);
                pack();
            }
        });

        // adds buttons
        Table top = new Table(skin);
        top.add(expandButton).width(30).padRight(5);
        top.add(nameField).expandX().fillX().padRight(5);

        top.add(deleteButton).width(30);
        // Initialize role container tables
        rolesTable = new Table(skin);
        rolesListTable = new Table(skin);

        // Prepopulate roleFields from model
        for (String role : location.getRoles()) {
            TextField tf = new TextField(role, skin);
            tf.setMessageText("Role name");
            roleFields.add(tf);
        }

        // Add initial layout to this row
        add(top).expandX().fillX().row();
    }

    private void toggleRoles() {
        expanded = !expanded;
        expandButton.setText(expanded ? "|" : "-");

        // Clear all child widgets of this LocationRow
        this.clearChildren();

        // Re-add the top row
        Table top = new Table(skin);
        top.add(expandButton).width(30).padRight(5);
        top.add(nameField).expandX().fillX();
        top.add(deleteButton).width(30);
        this.add(top).expandX().fillX().row();

        // Rebuild roles UI and show it
        if (expanded) {
            rebuildRolesUI();
            this.add(rolesTable).expandX().fillX().row();
        }

        // Recalculate layout sizes
        this.invalidateHierarchy();
        parentDialog.getContentTable().invalidateHierarchy();
        parentDialog.pack();
    }

    // Expands the roles for a location
    private void rebuildRolesUI() {
        rolesTable.clear();
        rolesListTable.clear();

        // Re-add all saved role fields
        for (TextField tf : roleFields) {
            Table row = new Table(skin);

            TextButton removeBtn = new TextButton("X", skin);
            removeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {

                    audioService.playSound("click");
                    roleFields.remove(tf);
                    rebuildRolesUI();
                    parentDialog.pack();
                }
            });

            row.add(tf).expandX().fillX().padRight(5);
            row.add(removeBtn).width(30);
            rolesListTable.add(row).expandX().fillX().padBottom(5).row();
        }

        // Scroll pane for roles
        ScrollPane scrollPane = new ScrollPane(rolesListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        rolesTable.add(scrollPane).width(300).height(100).padBottom(10).row();

        // Add-role button
        TextButton addRoleBtn = new TextButton("Add Role", skin);
        addRoleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {

                audioService.playSound("click");
                TextField newField = new TextField("", skin);
                newField.setMessageText("Role name");
                roleFields.add(newField);
                rebuildRolesUI();
                parentDialog.pack();
            }
        });

        rolesTable.add(addRoleBtn);
    }

    // Converts the current UI state into a Location model object.
    public Location toLocation() {
        List<String> roles = new ArrayList<>();
        for (TextField tf : roleFields) {
            String text = tf.getText().trim();
            if (!text.isEmpty()) {
                roles.add(text);
            }
        }
        return new Location(nameField.getText().trim(), roles);
    }
}
