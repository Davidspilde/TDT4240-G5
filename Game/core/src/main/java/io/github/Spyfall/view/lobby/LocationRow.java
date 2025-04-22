
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
        this.padBottom(5);
        this.defaults().pad(8);

        // Name field
        nameField = new TextField(location.getName(), skin);
        nameField.setMessageText("Location name");
        nameField.getStyle().font.getData().setScale(1.6f);
        nameField.setHeight(60);

        // Expand/collapse button
        expandButton = new TextButton("-", skin);
        expandButton.getLabel().setFontScale(1.6f);
        expandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                toggleRoles();
            }
        });

        // Delete button
        deleteButton = new TextButton("X", skin);
        deleteButton.getLabel().setFontScale(1.6f);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                parentDialog.removeLocationRow(LocationRow.this);
                pack();
            }
        });

        // Layout top row
        Table top = new Table(skin);
        top.defaults().pad(8);
        top.add(expandButton).width(60).height(60).padRight(10);
        top.add(nameField).expandX().fillX().padRight(10).height(60);
        top.add(deleteButton).width(60).height(60);

        rolesTable = new Table(skin);
        rolesListTable = new Table(skin);

        for (String role : location.getRoles()) {
            TextField tf = createRoleField(role);
            roleFields.add(tf);
        }

        add(top).expandX().fillX().row();
    }

    private void toggleRoles() {
        expanded = !expanded;
        expandButton.setText(expanded ? "|" : "-");

        clearChildren();

        Table top = new Table(skin);
        top.defaults().pad(8);
        top.add(expandButton).width(60).height(60).padRight(10);
        top.add(nameField).expandX().fillX().padRight(10).height(60);
        top.add(deleteButton).width(60).height(60);

        add(top).expandX().fillX().row();

        if (expanded) {
            rebuildRolesUI();
            add(rolesTable).expandX().fillX().padTop(10).row();
        }

        invalidateHierarchy();
        parentDialog.getContentTable().invalidateHierarchy();
        parentDialog.pack();
    }

    private void rebuildRolesUI() {
        rolesTable.clear();
        rolesListTable.clear();

        for (TextField tf : roleFields) {
            Table row = new Table(skin);
            row.defaults().pad(6);

            TextButton removeBtn = new TextButton("X", skin);
            removeBtn.getLabel().setFontScale(1.4f);
            removeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    audioService.playSound("click");
                    roleFields.remove(tf);
                    rebuildRolesUI();
                    parentDialog.pack();
                }
            });

            row.add(tf).expandX().fillX().height(55).padRight(10);
            row.add(removeBtn).width(55).height(55);
            rolesListTable.add(row).expandX().fillX().padBottom(8).row();
        }

        ScrollPane scrollPane = new ScrollPane(rolesListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        rolesTable.add(scrollPane).width(400).height(140).padBottom(15).row();

        TextButton addRoleBtn = new TextButton("Add Role", skin);
        addRoleBtn.getLabel().setFontScale(1.5f);
        addRoleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                TextField newField = createRoleField("");
                roleFields.add(newField);
                rebuildRolesUI();
                parentDialog.pack();
            }
        });

        rolesTable.add(addRoleBtn).width(200).height(55).padTop(5);
    }

    private TextField createRoleField(String text) {
        TextField tf = new TextField(text, skin);
        tf.setMessageText("Role name");
        tf.getStyle().font.getData().setScale(1.4f);
        tf.setHeight(55);
        return tf;
    }

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
