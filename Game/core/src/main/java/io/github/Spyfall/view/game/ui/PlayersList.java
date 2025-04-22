
package io.github.Spyfall.view.game.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.services.AudioService;

/**
 * Component for displaying players list with vote buttons.
 * Only one player can be voted for at a time.
 */
public class PlayersList extends GameComponent {

    private List<String> players;
    private String currentUsername;
    private GameplayController controller;
    private AudioService audioService;

    // Track player UI elements
    private final Map<String, Label> playerLabels = new HashMap<>();
    private final Map<String, TextButton> voteButtons = new HashMap<>();

    // Track the currently voted player
    private String currentVotedPlayer = null;

    public PlayersList(Skin skin, String currentUsername, GameplayController controller, AudioService audioService) {
        super(skin);
        this.audioService = audioService;
        this.controller = controller;
        this.currentUsername = currentUsername;
    }

    @Override
    protected void create() {
        rootTable.top();

        Label playersHeader = new Label("Players", skin);
        playersHeader.setAlignment(Align.center);
        playersHeader.setFontScale(1.2f);
        rootTable.add(playersHeader).colspan(2).padBottom(20).row();
    }

    /**
     * Sets the list of players and builds the UI once.
     */
    public void setPlayers(List<String> players) {
        this.players = players;
        buildPlayerList();
    }

    /**
     * Builds the player list UI. Each row contains the player's name
     * and a vote button, unless it's the current player.
     */
    private void buildPlayerList() {
        if (players == null || players.isEmpty())
            return;

        for (String playerName : players) {
            final String player = playerName;

            Label playerLabel = new Label(player, skin);
            playerLabel.setColor(Color.WHITE);

            boolean isCurrentPlayer = player.equals(currentUsername);
            if (isCurrentPlayer) {
                playerLabel.setText("→ " + player);
                playerLabel.setColor(Color.YELLOW);
                rootTable.add(playerLabel).colspan(2).left().row();
                continue;
            }

            TextButton voteButton = new TextButton("Vote", skin);

            voteButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    audioService.playSound("click");
                    controller.onVotePlayer(player);
                    markPlayerVoted(player);
                }
            });

            playerLabels.put(player, playerLabel);
            voteButtons.put(player, voteButton);

            rootTable.add(playerLabel).width(150).padRight(10).left();
            rootTable.add(voteButton).width(80).right().row();
        }
    }

    /**
     * Marks a player as voted for. Clears the highlight from the previous one.
     */
    public void markPlayerVoted(String playerName) {
        // Reset the previous vote (if any)
        if (currentVotedPlayer != null && !currentVotedPlayer.equals(playerName)) {
            Label previousLabel = playerLabels.get(currentVotedPlayer);
            if (previousLabel != null) {
                previousLabel.setColor(Color.WHITE);
            }

            TextButton previousButton = voteButtons.get(currentVotedPlayer);
            if (previousButton != null) {
                previousButton.setDisabled(false);
                previousButton.getLabel().setColor(Color.WHITE);
            }
        }

        currentVotedPlayer = playerName;

        // Highlight the newly voted player
        Label label = playerLabels.get(playerName);
        if (label != null) {
            label.setColor(Color.GRAY);
        }

        TextButton button = voteButtons.get(playerName);
        if (button != null) {
            button.setDisabled(true);
            button.getLabel().setColor(Color.GRAY);
        }
    }

    @Override
    public void update() {
        // No-op for now
    }
}
