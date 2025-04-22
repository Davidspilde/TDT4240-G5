
package io.github.Spyfall.view.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.Spyfall.client.AssetLoader;
import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;

public class MainMenuTable extends Table {

    // Layout constants
    private final float BUTTON_WIDTH = 340f;
    private final float BUTTON_HEIGHT = 80f;
    private final float BUTTON_FONT_SCALE = 1.6f;
    private final float BUTTON_SPACING = 35f;

    private final float LOGO_WIDTH = 400f;
    private final float LOGO_HEIGHT = 170f;
    private final float LOGO_SPACING = 50f;

    public MainMenuTable(Skin skin, AudioService audioService, MainMenuController controller, Stage stage) {
        super(skin);
        setFillParent(true);
        top();

        // Set background image
        setBackground(new TextureRegionDrawable(new TextureRegion(AssetLoader.mainBackground)));

        // Logo section
        Image logo = new Image(new Texture("logo-Photoroom.png"));
        add(logo)
                .prefWidth(LOGO_WIDTH)
                .prefHeight(LOGO_HEIGHT)
                .padBottom(LOGO_SPACING)
                .row();

        // "Create Lobby" button
        TextButton createBtn = new TextButton("Create Lobby", skin);
        createBtn.getLabel().setFontScale(BUTTON_FONT_SCALE);
        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                new CreateLobbyDialog(skin, audioService, controller, stage);
            }
        });
        add(createBtn)
                .prefWidth(BUTTON_WIDTH)
                .prefHeight(BUTTON_HEIGHT)
                .padBottom(BUTTON_SPACING)
                .row();

        // "Join Lobby" button
        TextButton joinBtn = new TextButton("Join Lobby", skin);
        joinBtn.getLabel().setFontScale(BUTTON_FONT_SCALE);
        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                new JoinLobbyDialog(skin, audioService, controller, stage);
            }
        });
        add(joinBtn)
                .prefWidth(BUTTON_WIDTH)
                .prefHeight(BUTTON_HEIGHT)
                .padBottom(BUTTON_SPACING)
                .row();

        // "How to play" button
        TextButton howBtn = new TextButton("How to play", skin);
        howBtn.getLabel().setFontScale(BUTTON_FONT_SCALE);
        howBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                controller.onHowToPlay();
            }
        });
        add(howBtn)
                .prefWidth(BUTTON_WIDTH)
                .prefHeight(BUTTON_HEIGHT)
                .row();
    }
}
