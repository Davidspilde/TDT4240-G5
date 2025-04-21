package io.github.Spyfall.view.stages.mainmenu;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import io.github.Spyfall.controller.MainMenuController;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.ui.UIConstants;

public class MainMenuTable extends Table {

    public MainMenuTable(Skin skin, AudioService audioService, MainMenuController controller, Stage stage) {
        super(skin);
        setFillParent(true);
        top();
        setBackground(new TextureRegionDrawable(new TextureRegion(new Texture("Background_city.png"))));

        // Logo
        Image logo = new Image(new Texture("logo-Photoroom.png"));
        add(logo)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.LOGO_HEIGHT_PERCENT, this))
                .padBottom(Value.percentHeight(UIConstants.VERTICAL_GAP_PERCENT, this))
                .row();

        // Create Lobby Button
        TextButton createBtn = new TextButton("Create Lobby", skin);
        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                new CreateLobbyDialog(skin, audioService, controller, stage);
            }
        });
        add(createBtn)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                .padBottom(Value.percentHeight(UIConstants.VERTICAL_GAP_PERCENT, this))
                .row();

        // Join Game Button
        TextButton joinBtn = new TextButton("Join Lobby", skin);
        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                new JoinLobbyDialog(skin, audioService, controller, stage);
            }
        });
        add(joinBtn)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                .padBottom(Value.percentHeight(UIConstants.VERTICAL_GAP_PERCENT, this))
                .row();

        // How to Play Button
        TextButton howBtn = new TextButton("How to play", skin);
        howBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent e, float x, float y) {
                audioService.playSound("click");
                controller.onHowToPlay();
            }
        });
        add(howBtn)
                .prefWidth(Value.percentWidth(UIConstants.BUTTON_WIDTH_PERCENT, this))
                .prefHeight(Value.percentHeight(UIConstants.BUTTON_HEIGHT_PERCENT, this))
                .row();
    }
}
