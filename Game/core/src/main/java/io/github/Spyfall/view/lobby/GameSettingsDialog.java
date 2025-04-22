
package io.github.Spyfall.view.lobby;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.LobbyData;
import io.github.Spyfall.services.AudioService;
import io.github.Spyfall.view.ui.UIConstants;

public class GameSettingsDialog extends Dialog {
    private int roundTimeMinutes;
    private int spyLastAttemptSeconds;
    private int roundLimit;
    private int maxPlayers;
    private int locationLimit;

    private final LobbyController lobbyController;
    private final AudioService audioService;

    public GameSettingsDialog(Skin skin, LobbyController lobbyController, Stage stage, AudioService audioService) {
        super("Game Settings", skin);
        this.audioService = audioService;
        this.lobbyController = lobbyController;

        Drawable dim = skin.newDrawable("white", UIConstants.transparentBlack);
        dim.setMinWidth(stage.getViewport().getWorldWidth());
        dim.setMinHeight(stage.getViewport().getWorldHeight());
        getStyle().stageBackground = dim;

        initDialog();
        pack();
    }

    private void initDialog() {
        LobbyData data = lobbyController.getLobbyData();

        // Load current values
        roundTimeMinutes = Math.max(1, data.getTimePerRound() / 60);
        spyLastAttemptSeconds = Math.max(5, data.getSpyLastAttemptTime());
        locationLimit = Math.max(1, data.getLocationLimit());
        roundLimit = Math.max(1, data.getRoundLimit());
        maxPlayers = Math.max(1, data.getMaxPlayers());

        Table settingsTable = new Table();
        settingsTable.pad(30);
        settingsTable.center().top();

        // Add spinner-like rows
        addControlRow(settingsTable, "Round Time (minutes):", () -> roundTimeMinutes, v -> roundTimeMinutes = v, 1);
        addControlRow(settingsTable, "Spy Last Attempt (seconds):", () -> spyLastAttemptSeconds,
                v -> spyLastAttemptSeconds = v, 5);
        addControlRow(settingsTable, "Location Limit:", () -> locationLimit, v -> locationLimit = v, 1);
        addControlRow(settingsTable, "Round Limit:", () -> roundLimit, v -> roundLimit = v, 1);
        addControlRow(settingsTable, "Max Players:", () -> maxPlayers, v -> maxPlayers = v, 1);

        getContentTable().add(settingsTable).center().padBottom(20).row();

        // Buttons
        TextButton saveBtn = new TextButton("Save", getSkin());
        saveBtn.getLabel().setFontScale(1.6f);
        saveBtn.setHeight(60);

        TextButton cancelBtn = new TextButton("Cancel", getSkin());
        cancelBtn.getLabel().setFontScale(1.6f);
        cancelBtn.setHeight(60);

        saveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                saveSettings();
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                audioService.playSound("click");
                hide();
            }
        });

        Table buttonRow = new Table();
        buttonRow.padTop(10);
        buttonRow.add(saveBtn).width(180).height(60).padRight(20);
        buttonRow.add(cancelBtn).width(180).height(60);

        getContentTable().add(buttonRow).center().row();
    }

    private void addControlRow(Table table, String labelText, ValueGetter getter, ValueSetter setter, int step) {
        Label label = new Label(labelText, getSkin());
        label.setFontScale(1.4f);
        label.setAlignment(Align.center);

        final Label valueLabel = new Label(Integer.toString(getter.get()), getSkin());
        valueLabel.setFontScale(1.4f);
        valueLabel.setAlignment(Align.center);

        // Wrap label in a table to vertically center it
        Table valueWrapper = new Table();
        valueWrapper.add(valueLabel).expand().center().fill().height(60).width(100);
        TextButton minus = new TextButton("-", getSkin());
        minus.getLabel().setFontScale(1.4f);
        minus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newValue = Math.max(1, getter.get() - step);
                setter.set(newValue);
                valueLabel.setText(Integer.toString(newValue));
                audioService.playSound("click");
            }
        });

        TextButton plus = new TextButton("+", getSkin());
        plus.getLabel().setFontScale(1.4f);
        plus.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int newValue = getter.get() + step;
                setter.set(newValue);
                valueLabel.setText(Integer.toString(newValue));
                audioService.playSound("click");
            }
        });

        Table row = new Table();
        row.add(minus).width(60).height(60).padRight(10);
        row.add(valueWrapper).width(100).padRight(10);
        row.add(plus).width(60).height(60);

        table.add(label).colspan(3).padBottom(8).center().row();
        table.add(row).colspan(3).center().padBottom(20).row();
    }

    private void saveSettings() {
        lobbyController.updateLobbyOptions(
                roundLimit,
                locationLimit,
                maxPlayers,
                roundTimeMinutes * 60,
                spyLastAttemptSeconds);
        hide();
    }

    private interface ValueGetter {
        int get();
    }

    private interface ValueSetter {
        void set(int value);
    }
}
