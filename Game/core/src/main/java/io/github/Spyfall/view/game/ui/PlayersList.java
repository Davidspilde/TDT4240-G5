package io.github.Spyfall.view.game.ui;

import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import io.github.Spyfall.controller.GameplayController;

/**
 * Component for displaying players list with vote buttons
 */
public class PlayersList extends GameComponent {

    public interface PlayerActionListener {
        void onVotePlayer(String playerName);
    }

    private List<String> players;
    private String currentUsername;
    private boolean showVoteButtons = true;
    private PlayerActionListener listener;

    /**
     * Create players list component
     */
    public PlayersList(Skin skin, String currentUsername) {
        super(skin);
        this.currentUsername = currentUsername;
    }

    @Override
    protected void create() {
        // Initialize empty table
        rootTable.top();
        
        // Add header
        Label playersHeader = new Label("Players", skin);
        playersHeader.setAlignment(Align.center);
        playersHeader.setFontScale(1.2f);
        rootTable.add(playersHeader).colspan(2).padBottom(20).row();
    }

    /**
     * Set the list of players to display
     */
    public void setPlayers(List<String> players) {
        this.players = players;
        update();
    }
    
    /**
     * Control whether to show vote buttons
     */
    public void setShowVoteButtons(boolean show) {
        this.showVoteButtons = show;
        update();
    }
    
    /**
     * Set action listener
     */
    public void setListener(PlayerActionListener listener) {
        this.listener = listener;
    }
    
    /**
     * Convenience method to set controller directly
     */
    public void setController(GameplayController controller) {
        setListener(new PlayerActionListener() {
            @Override
            public void onVotePlayer(String playerName) {
                controller.votePlayer(playerName);
            }
        });
    }

    @Override
    public void update() {

        int childCount = rootTable.getChildren().size;
        if (childCount > 2) { // label + colspan
            rootTable.clearChildren();
            
            Label playersHeader = new Label("Players", skin);
            playersHeader.setAlignment(Align.center);
            playersHeader.setFontScale(1.2f);
            rootTable.add(playersHeader).colspan(2).padBottom(20).row();
        }

        // no players to show
        if (players == null || players.isEmpty()) {
            return;
        }

        for (String playerName : players) {
            Label playerLabel = new Label(playerName, skin);
            
            boolean isCurrentPlayer = playerName.equals(currentUsername);
            if (isCurrentPlayer) {
                playerLabel.setText("→ " + playerName);
                playerLabel.setColor(Color.YELLOW);
            }
            
            if (showVoteButtons) {
                TextButton voteButton = new TextButton("Vote", skin);
                
                if (isCurrentPlayer) {
                    voteButton.setDisabled(true);
                    voteButton.getLabel().setColor(Color.GRAY);
                }
                
                voteButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        if (!isCurrentPlayer && listener != null) {
                            listener.onVotePlayer(playerName);
                        }
                    }
                });
                
                rootTable.add(playerLabel).width(150).padRight(10).left();
                rootTable.add(voteButton).width(80).right().row();
            } else {
                rootTable.add(playerLabel).colspan(2).left().row();
            }
        }


    }



}
