
package io.github.Spyfall.view.game.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;
import io.github.Spyfall.services.AudioService;

/**
 * Displays a scrollable list of players with large vote buttons.
 */
public class PlayersList extends GameComponent {

    private List<String> players;
    private final String currentUsername;
    private final GameplayController controller;
    private final AudioService audioService;

    private final Map<String, Label> playerLabels = new HashMap<>();
    private final Map<String, TextButton> voteButtons = new HashMap<>();
    private String currentVotedPlayer = null;

    public PlayersList(Skin skin, String currentUsername, GameplayController controller, AudioService audioService) {
        super(skin);
        this.controller = controller;
        this.audioService = audioService;
        this.currentUsername = currentUsername;
    }

    @Override
    protected void create() {
        rootTable.top().pad(10);

        Label header = new Label("Players", skin);
        header.setFontScale(1.4f);
        header.setAlignment(Align.center);
        rootTable.add(header).colspan(2).padBottom(20).center().row();
    }

    public void setPlayers(List<String> players) {
        this.players = players;
        buildPlayerList();
    }

    private void buildPlayerList() {
        Table playerTable = new Table();
        playerTable.top();
        playerTable.defaults().pad(10).center();

        if (players == null || players.isEmpty())
            return;

        for (String playerName : players) {
            final String player = playerName;

            Label playerLabel = new Label(player, skin);
            playerLabel.setFontScale(1.5f);
            playerLabel.setColor(Color.WHITE);

            boolean isCurrentPlayer = player.equals(currentUsername);
            if (isCurrentPlayer) {
                playerLabel.setText("→ " + player);
                playerLabel.setColor(Color.YELLOW);
                playerTable.add(playerLabel).colspan(2).left().padBottom(20).row();
                continue;
            }

            TextButton voteButton = new TextButton("Vote", skin);
            voteButton.getLabel().setFontScale(1.5f);
            voteButton.getLabelCell().pad(10);
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

            playerTable.add(playerLabel).width(300).left();
            playerTable.add(voteButton).width(180).height(80).right().row();
        }

        ScrollPane scrollPane = new ScrollPane(playerTable, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setForceScroll(false, true);
        scrollPane.setSmoothScrolling(true);
        scrollPane.setScrollingDisabled(true, false); // only vertical scroll
        scrollPane.getStyle().background = null;

        rootTable.add(scrollPane).colspan(2).expand().fill().row();
    }

    public void markPlayerVoted(String playerName) {
        if (currentVotedPlayer != null && !currentVotedPlayer.equals(playerName)) {
            Label prevLabel = playerLabels.get(currentVotedPlayer);
            if (prevLabel != null)
                prevLabel.setColor(Color.WHITE);

            TextButton prevButton = voteButtons.get(currentVotedPlayer);
            if (prevButton != null) {
                prevButton.setDisabled(false);
                prevButton.getLabel().setColor(Color.WHITE);
            }
        }

        currentVotedPlayer = playerName;

        Label label = playerLabels.get(playerName);
        if (label != null)
            label.setColor(Color.GRAY);

        TextButton button = voteButtons.get(playerName);
        if (button != null) {
            button.setDisabled(true);
            button.getLabel().setColor(Color.GRAY);
        }
    }

    @Override
    public void update() {
        // Optional animations or live updates could go here
    }
}
