package io.github.Spyfall.view.game.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.VerticalGroup;
import com.badlogic.gdx.utils.Align;

/**
 * Component for displaying player information
 */
public class PlayerInfo extends GameComponent {

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
        infoGroup = new VerticalGroup();
        infoGroup.space(10);
        infoGroup.align(Align.center);
        
        roleLabel = new Label("", skin);
        roleLabel.setFontScale(1.2f);
        
        locationLabel = new Label("", skin);
        locationLabel.setFontScale(1.0f);
        
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
        
        if (isSpy) {
            locationLabel.setText("Find the location");
            locationLabel.setVisible(true);
        } else {
            locationLabel.setText("Location: " + location);
            locationLabel.setVisible(true);
        }
    }

}
