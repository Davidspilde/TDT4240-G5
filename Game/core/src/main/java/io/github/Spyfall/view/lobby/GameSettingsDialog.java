<<<<<<<< HEAD:Game/core/src/main/java/io/github/Spyfall/view/createGame/GameSettingsDialog.java
package io.github.Spyfall.view.createGame;
========
package io.github.Spyfall.view.stages.lobby;
>>>>>>>> main:Game/core/src/main/java/io/github/Spyfall/view/stages/lobby/GameSettingsDialog.java

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.Spyfall.controller.LobbyController;
import io.github.Spyfall.model.LobbyData;

public class GameSettingsDialog extends Dialog {
    private TextField roundTimeField;
    private TextField spyLastAttemptField;
    private TextField roundLimitField;
    private TextField maxPlayersField;
    private TextField locationLimitField;
    private LobbyController lobbyController;

    public GameSettingsDialog(Skin skin, LobbyController lobbyController) {
        super("", skin);
        this.lobbyController = lobbyController;
        initDialog();
    }

    private void initDialog() {

        LobbyData lobbyData = lobbyController.getLobbyData();
        // The Current settings
        String CurrentRoundTime = Integer.toString(lobbyData.getTimePerRound() / 60);
        String CurrentRoundLimit = Integer.toString(lobbyData.getRoundLimit());
        String currentMaxPlayers = Integer.toString(lobbyData.getMaxPlayers());
        String currentLocationLimit = Integer.toString(lobbyData.getLocationLimit());
        String currentSpyLastAttemptTime = Integer.toString(lobbyData.getSpyLastAttemptTime());

        Table settingsTable = new Table();
        settingsTable.pad(10);

        // Round time input
        settingsTable.add(new Label("Round Time (minutes):", getSkin())).left().pad(5).row();
        roundTimeField = new TextField(CurrentRoundTime, getSkin());
        roundTimeField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(roundTimeField).width(200).pad(5).row();

        // Spy last attempt time input
        settingsTable.add(new Label("Spy Last Attempt Time (Seconds):", getSkin())).left().pad(5).row();
        spyLastAttemptField = new TextField(currentSpyLastAttemptTime, getSkin());
        spyLastAttemptField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(spyLastAttemptField).width(200).pad(5).row();

        // Locations limit input
        settingsTable.add(new Label("Locations limit:", getSkin())).left().pad(5).row();
        locationLimitField = new TextField(currentLocationLimit, getSkin());
        locationLimitField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(locationLimitField).width(200).pad(5).row();

        // Round limit input
        settingsTable.add(new Label("Round Limit:", getSkin())).left().pad(5).row();
        roundLimitField = new TextField(CurrentRoundLimit, getSkin());
        roundLimitField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(roundLimitField).width(200).pad(5).row();

        // Max players input
        settingsTable.add(new Label("Maximum Players:", getSkin())).left().pad(5).row();
        maxPlayersField = new TextField(currentMaxPlayers, getSkin());
        maxPlayersField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(maxPlayersField).width(200).pad(5).row();

        getContentTable().add(settingsTable);

        // Add buttons
        TextButton saveButton = new TextButton("Save", getSkin());
        TextButton cancelButton = new TextButton("Cancel", getSkin());

        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                saveSettings();
            }
        });

        cancelButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                hide();
            }
        });

        button(saveButton);
        button(cancelButton);
    }

    private void saveSettings() {
        try {
            int roundTime = Integer.parseInt(roundTimeField.getText()) * 60;
            int spyLastAttemptTime = Integer.parseInt(spyLastAttemptField.getText());
            int roundLimit = Integer.parseInt(roundLimitField.getText());
            int maxPlayers = Integer.parseInt(maxPlayersField.getText());
            int locationLimit = Integer.parseInt(locationLimitField.getText());

            lobbyController.updateLobbyOptions(roundLimit, locationLimit, maxPlayers, roundTime,
                    spyLastAttemptTime);
            hide();
        } catch (NumberFormatException e) {
            // Handle invalid input
            System.out.println("Invalid input: Please enter valid numbers");
        }
    }
}
