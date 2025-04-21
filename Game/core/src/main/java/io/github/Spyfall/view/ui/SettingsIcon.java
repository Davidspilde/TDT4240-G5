
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

//Settings icon which can be added to different stages
public class SettingsIcon extends Table {

    public SettingsIcon(Skin skin, AudioService audioService, Stage stage) {
        super(skin);
        setFillParent(true);
        bottom().right();

        float ICON_SZ = UIConstants.ICON_SIZE_PERCENT;
        float PAD_SZ = UIConstants.PADDING_PERCENT;

        Image icon = new Image(new Texture("settings-logo.png"));
        icon.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                new SettingsDialog(skin, audioService, stage);
            }
        });

        add(icon)
                .prefWidth(Value.percentWidth(ICON_SZ, this))
                .prefHeight(Value.percentWidth(ICON_SZ, this))
                .pad(Value.percentWidth(PAD_SZ, this));
    }
}
