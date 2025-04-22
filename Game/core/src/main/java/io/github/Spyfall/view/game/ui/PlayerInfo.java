
package io.github.Spyfall.view.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

/**
 * Component for displaying player role and location.
 */
public class PlayerInfo extends GameComponent {

    // Layout constants
    private final float ROLE_FONT_SCALE = 1.2f;
    private final float LOCATION_FONT_SCALE = 1.0f;
    private final float SPACING = 10f;

    private Label roleLabel;
    private Label locationLabel;
    private VerticalGroup infoGroup;

    private String role = "";
    private String location = "";
    private boolean isSpy = false;

    public PlayerInfo(Skin skin) {
        super(skin);
    }

    @Override
    protected void create() {
        // Group for aligning the labels vertically
        infoGroup = new VerticalGroup();
        infoGroup.space(SPACING);
        infoGroup.align(Align.center);

        roleLabel = new Label("", skin);
        roleLabel.setFontScale(ROLE_FONT_SCALE);

        locationLabel = new Label("", skin);
        locationLabel.setFontScale(LOCATION_FONT_SCALE);

        infoGroup.addActor(roleLabel);
        infoGroup.addActor(locationLabel);

        rootTable.add(infoGroup).center();
    }

    public void setRole(String role) {
        this.role = role;
        this.isSpy = role.equalsIgnoreCase("spy");
        update();
    }

    public void setLocation(String location) {
        this.location = location;
        update();
    }

    @Override
    public void update() {
        roleLabel.setText("Role: " + role);
        roleLabel.setColor(Color.WHITE);

        // Show location or prompt depending on whether the player is the spy
        if (isSpy) {
            locationLabel.setText("Find the location");
        } else {
            locationLabel.setText("Location: " + location);
        }

        locationLabel.setVisible(true);
    }
}
