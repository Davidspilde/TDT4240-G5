package io.github.Spyfall.view.createGame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import io.github.Spyfall.model.GameModel;
import io.github.Spyfall.model.GameState;
import io.github.Spyfall.services.websocket.SendMessageService;

public class GameSettingsDialog extends Dialog {
    private TextField roundTimeField;
    private TextField spyCountField;
    private TextField roundLimitField;
    private TextField maxPlayersField;
    private SendMessageService sendMessageService;
    private String username;
    private String lobbyCode;
    private GameModel gameModel;

    public GameSettingsDialog(String title, Skin skin, String username, String lobbyCode) {
        super(title, skin);
        this.sendMessageService = SendMessageService.getInstance();
        this.gameModel = GameModel.getInstance();
        this.username = username;
        this.lobbyCode = lobbyCode;
        initDialog();
    }

    private void initDialog() {
        Table settingsTable = new Table();
        settingsTable.pad(10);

        // Round time input (1-10 minutes)
        settingsTable.add(new Label("Round Time (minutes):", getSkin())).left().pad(5).row();
        roundTimeField = new TextField("2", getSkin());
        roundTimeField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(roundTimeField).width(200).pad(5).row();

        // Spy count input (1-5 spies)
        settingsTable.add(new Label("Number of Spies:", getSkin())).left().pad(5).row();
        spyCountField = new TextField("1", getSkin());
        spyCountField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(spyCountField).width(200).pad(5).row();

        // Round limit input (1-10 rounds)
        settingsTable.add(new Label("Round Limit:", getSkin())).left().pad(5).row();
        roundLimitField = new TextField("3", getSkin());
        roundLimitField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(roundLimitField).width(200).pad(5).row();

        // Max players input (4-10 players)
        settingsTable.add(new Label("Maximum Players:", getSkin())).left().pad(5).row();
        maxPlayersField = new TextField("6", getSkin());
        maxPlayersField.setTextFieldFilter(new TextField.TextFieldFilter.DigitsOnlyFilter());
        settingsTable.add(maxPlayersField).width(200).pad(5).row();

        getContentTable().add(settingsTable);

        // Add buttons
        TextButton saveButton = new TextButton("Save", getSkin());
        TextButton cancelButton = new TextButton("Cancel", getSkin());
        TextButton startGameButton = new TextButton("Start Game", getSkin());

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

        startGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                startGame();
            }
        });

        button(saveButton);
        button(cancelButton);
        button(startGameButton);
    }

    private void saveSettings() {
        try {
            int roundTime = Integer.parseInt(roundTimeField.getText());
            int spyCount = Integer.parseInt(spyCountField.getText());
            int roundLimit = Integer.parseInt(roundLimitField.getText());
            int maxPlayers = Integer.parseInt(maxPlayersField.getText());

            // Need to make setting for spylastAttemptTime
            int spyLastAttemptTime = 60;

            // Validate input ranges
            roundTime = Math.max(1, Math.min(10, roundTime));
            spyCount = Math.max(1, Math.min(5, spyCount));
            roundLimit = Math.max(1, Math.min(10, roundLimit));
            maxPlayers = Math.max(4, Math.min(10, maxPlayers));

            sendMessageService.updateLobbyOptions(username, lobbyCode, roundLimit, spyCount, maxPlayers, roundTime * 60,
                    spyLastAttemptTime);
            hide();
        } catch (NumberFormatException e) {
            // Handle invalid input
            System.out.println("Invalid input: Please enter valid numbers");
        }
    }

    private void startGame() {
        // First save the settings
        saveSettings();

        // Send start game message
        sendMessageService.startGame(username, lobbyCode);

        // Update game state
        gameModel.setCurrentState(GameState.IN_GAME);

        // Hide the dialog
        hide();
    }
}
