
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.AudioService;

import java.util.ArrayList;
import java.util.List;

public class LocationRow extends Table {

    // Layout constants
    private final float FIELD_HEIGHT = 60f;
    private final float BUTTON_SIZE = 60f;
    private final float FONT_SCALE_LARGE = 1.6f;
    private final float FONT_SCALE_MEDIUM = 1.4f;
    private final float ROLE_FIELD_HEIGHT = 55f;
    private final float ADD_ROLE_BUTTON_WIDTH = 200f;
    private final float ADD_ROLE_BUTTON_HEIGHT = 55f;
    private final float SCROLL_WIDTH = 400f;
    private final float SCROLL_HEIGHT = 140f;

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

        padBottom(5);
        defaults().pad(8);

        // Name input
        nameField = new TextField(location.getName(), skin);
        nameField.setMessageText("Location name");
        nameField.getStyle().font.getData().setScale(FONT_SCALE_LARGE);
        nameField.setHeight(FIELD_HEIGHT);

        // Expand/collapse toggle
        expandButton = new TextButton("-", skin);
        expandButton.getLabel().setFontScale(FONT_SCALE_LARGE);
        expandButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                toggleRoles();
            }
        });

        // Delete this location
        deleteButton = new TextButton("X", skin);
        deleteButton.getLabel().setFontScale(FONT_SCALE_LARGE);
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                parentDialog.removeLocationRow(LocationRow.this);
                pack();
            }
        });

        // Top row layout
        Table top = new Table(skin);
        top.defaults().pad(8);
        top.add(expandButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(10);
        top.add(nameField).expandX().fillX().padRight(10).height(FIELD_HEIGHT);
        top.add(deleteButton).width(BUTTON_SIZE).height(BUTTON_SIZE);

        // Prepare role containers
        rolesTable = new Table(skin);
        rolesListTable = new Table(skin);

        for (String role : location.getRoles()) {
            roleFields.add(createRoleField(role));
        }

        add(top).expandX().fillX().row();
    }

    private void toggleRoles() {
        expanded = !expanded;
        expandButton.setText(expanded ? "|" : "-");

        clearChildren();

        Table top = new Table(skin);
        top.defaults().pad(8);
        top.add(expandButton).width(BUTTON_SIZE).height(BUTTON_SIZE).padRight(10);
        top.add(nameField).expandX().fillX().padRight(10).height(FIELD_HEIGHT);
        top.add(deleteButton).width(BUTTON_SIZE).height(BUTTON_SIZE);
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
            removeBtn.getLabel().setFontScale(FONT_SCALE_MEDIUM);
            removeBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent e, float x, float y) {
                    audioService.playSound("click");
                    roleFields.remove(tf);
                    rebuildRolesUI();
                    parentDialog.pack();
                }
            });

            row.add(tf).expandX().fillX().height(ROLE_FIELD_HEIGHT).padRight(10);
            row.add(removeBtn).width(ROLE_FIELD_HEIGHT).height(ROLE_FIELD_HEIGHT);
            rolesListTable.add(row).expandX().fillX().padBottom(8).row();
        }

        ScrollPane scrollPane = new ScrollPane(rolesListTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setScrollingDisabled(true, false);

        rolesTable.add(scrollPane).width(SCROLL_WIDTH).height(SCROLL_HEIGHT).padBottom(15).row();

        TextButton addRoleBtn = new TextButton("Add Role", skin);
        addRoleBtn.getLabel().setFontScale(FONT_SCALE_LARGE);
        addRoleBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                roleFields.add(createRoleField(""));
                rebuildRolesUI();
                parentDialog.pack();
            }
        });

        rolesTable.add(addRoleBtn).width(ADD_ROLE_BUTTON_WIDTH).height(ADD_ROLE_BUTTON_HEIGHT).padTop(5);
    }

    private TextField createRoleField(String text) {
        TextField tf = new TextField(text, skin);
        tf.setMessageText("Role name");
        tf.getStyle().font.getData().setScale(FONT_SCALE_MEDIUM);
        tf.setHeight(ROLE_FIELD_HEIGHT);
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
