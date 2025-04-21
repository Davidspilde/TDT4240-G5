package io.github.Spyfall.view;

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

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.Location;
import io.github.Spyfall.services.websocket.SendMessageService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LocationsEditorDialog extends Dialog {
    private Skin skin;
    private String lobbyCode;
    private SendMessageService sendMsgService;
    private VerticalGroup locationsGroup;
    private List<Location> locations;

    public LocationsEditorDialog(Skin skin, String lobbyCode) {
        super("Edit Locations", skin);
        this.skin = skin;
        this.lobbyCode = lobbyCode;
        this.sendMsgService = SendMessageService.getInstance();
        this.locations = new ArrayList<>();

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

        // Add default locations with roles
        addLocation("Airplane", Arrays.asList("Pilot", "Flight Attendant", "Passenger", "Air Marshal"));
        addLocation("Bank", Arrays.asList("Bank Manager", "Teller", "Security Guard", "Customer"));
        addLocation("Beach", Arrays.asList("Lifeguard", "Surfer", "Ice Cream Vendor", "Tourist"));
        addLocation("Casino", Arrays.asList("Dealer", "Security Guard", "High Roller", "Slot Machine Player"));
        addLocation("Church", Arrays.asList("Priest", "Choir Member", "Worshipper", "Organist"));
        addLocation("Circus", Arrays.asList("Ringmaster", "Clown", "Acrobat", "Ticket Seller"));
        addLocation("Hotel", Arrays.asList("Receptionist", "Housekeeper", "Guest", "Security Guard"));
        addLocation("Hospital", Arrays.asList("Doctor", "Nurse", "Patient", "Surgeon"));
        addLocation("Library", Arrays.asList("Librarian", "Student", "Bookworm", "Security Guard"));
        addLocation("Movie Theater", Arrays.asList("Ticket Seller", "Usher", "Movie Goer", "Projectionist"));
        addLocation("Museum", Arrays.asList("Tour Guide", "Security Guard", "Visitor", "Curator"));
        addLocation("Restaurant", Arrays.asList("Chef", "Waiter", "Customer", "Host"));
        addLocation("School", Arrays.asList("Teacher", "Student", "Principal", "Janitor"));
        addLocation("Supermarket", Arrays.asList("Cashier", "Customer", "Manager", "Stock Clerk"));
        addLocation("Theater", Arrays.asList("Actor", "Director", "Stage Manager", "Audience Member"));

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
                    addLocation(locationName, Arrays.asList("Role 1", "Role 2", "Role 3", "Role 4"));
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
        TextButton saveButton = new TextButton("Save", skin);
        TextButton closeButton = new TextButton("Close", skin);

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Send updated locations to server
                sendMsgService.sendUpdateLocations(GameModel.getInstance().getUsername(), lobbyCode, locations);
                hide();
            }
        });

        closeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        button(saveButton);
        button(closeButton);
    }

    private void addLocation(String locationName, List<String> roles) {
        Table locationRow = new Table();
        Location location = new Location(locationName, new ArrayList<>(roles));
        locations.add(location);

        // Location name and buttons
        Label locationLabel = new Label(locationName, skin);
        TextButton editRolesButton = new TextButton("Edit Roles", skin);
        TextButton removeButton = new TextButton("X", skin);

        // Create roles dialog
        Dialog rolesDialog = new Dialog("Edit Roles - " + locationName, skin) {
            {
                Table rolesTable = new Table();
                VerticalGroup rolesGroup = new VerticalGroup();
                rolesGroup.space(5);

                // Add role fields
                for (int i = 0; i < roles.size(); i++) {
                    final int roleIndex = i;
                    TextField roleField = new TextField(roles.get(i), skin);
                    roleField.setMessageText("Enter role " + (i + 1));
                    roleField.addListener(new com.badlogic.gdx.scenes.scene2d.InputListener() {
                        @Override
                        public void exit(InputEvent event, float x, float y, int pointer, com.badlogic.gdx.scenes.scene2d.Actor toActor) {
                            roles.set(roleIndex, roleField.getText());
                            location.setRoles(new ArrayList<>(roles));
                        }
                    });
                    rolesGroup.addActor(roleField);
                }

                rolesTable.add(rolesGroup).pad(10);
                getContentTable().add(rolesTable);

                button("Close");
            }
        };

        editRolesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                rolesDialog.show(getStage());
            }
        });

        removeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                locations.remove(location);
                locationsGroup.removeActor(locationRow);
            }
        });

        locationRow.add(locationLabel).expandX().left();
        locationRow.add(editRolesButton).padRight(5);
        locationRow.add(removeButton).width(30);

        locationsGroup.addActor(locationRow);
    }
}
