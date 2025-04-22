
package io.github.Spyfall.view.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;

import io.github.Spyfall.services.AudioService;

/**
 * Floating settings icon that opens the SettingsDialog when clicked.
 * Can be added to any stage.
 */
public class SettingsIcon extends Table {

    // Layout constants
    private final float ICON_SIZE_PERCENT = 0.08f;
    private final float PADDING_PERCENT = 0.02f;

    public SettingsIcon(Skin skin, AudioService audioService, Stage stage) {
        super(skin);
        setFillParent(true);
        bottom().right(); // Position in bottom-right corner

        // Create and style the settings icon
        Image icon = new Image(new Texture("settings-logo.png"));
        icon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                new SettingsDialog(skin, audioService, stage);
            }
        });

        // Add the icon with responsive sizing and padding
        add(icon)
                .prefWidth(Value.percentWidth(ICON_SIZE_PERCENT, this))
                .prefHeight(Value.percentWidth(ICON_SIZE_PERCENT, this))
                .pad(Value.percentWidth(PADDING_PERCENT, this));
    }
}
