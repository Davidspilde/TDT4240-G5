package io.github.Spyfall.view.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

/**
 * Component for displaying the scoreboard
 */
public class Scoreboard extends GameComponent {
    private HashMap<String, Integer> scoreboard;
    private String currentUsername;
    private String title = "SCOREBOARD";
    private float contentWidth;
    private boolean highlightWinner = false;

    public Scoreboard(Skin skin, String currentUsername, float contentWidth) {
        super(skin);
        this.currentUsername = currentUsername;
        this.contentWidth = contentWidth;
    }

    @Override
    protected void create() {
        rootTable.top();
        rootTable.defaults().expand().fill();
    }
    
    public void setScoreboard(HashMap<String, Integer> scoreboard) {
        this.scoreboard = scoreboard != null ? scoreboard : new HashMap<>();
        update();
    }
    
    public void setTitle(String title) {
        this.title = title;
        update();
    }

    /**
     * Set whether to highlight the winner with trophy icon and gold color
     */
    public void setHighlightWinner(boolean highlight) {
        this.highlightWinner = highlight;
        update();
    }

    @Override
    public void update() {
        rootTable.clear();
        
        if (scoreboard == null || scoreboard.isEmpty()) {
            Label titleLabel = new Label(title, skin);
            titleLabel.setAlignment(Align.center);
            rootTable.add(titleLabel).colspan(3).padBottom(10).row();
            
            Label noDataLabel = new Label("No scores available", skin);
            noDataLabel.setAlignment(Align.center);
            rootTable.add(noDataLabel).colspan(3).padBottom(10);
            return;
        }

        float availableWidth = rootTable.getWidth();
        if (availableWidth <= 0) {
            // If width isn't available yet, use the provided contentWidth as fallback
            availableWidth = contentWidth;
        }

        float rankColWidth = availableWidth * 0.15f;
        float playerColWidth = availableWidth * 0.55f;
        float scoreColWidth = availableWidth * 0.3f;

        Label titleLabel = new Label(title, skin);
        titleLabel.setAlignment(Align.center);
        rootTable.add(titleLabel).colspan(3).padBottom(10).fillX().row();

        Table headerTable = new Table();
        headerTable.defaults().expand().fill();

        Label rankHeader = new Label("#", skin);
        Label playerHeader = new Label("Player", skin);
        Label scoreHeader = new Label("Score", skin);

        headerTable.add(rankHeader).width(rankColWidth).padRight(10);
        headerTable.add(playerHeader).width(playerColWidth).padRight(10).left();
        headerTable.add(scoreHeader).width(scoreColWidth).left();

        rootTable.add(headerTable).colspan(3).fillX().padBottom(10).row();


        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(scoreboard.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        int currentRank = 1;
        Integer previousScore = null;

        for (int i = 0; i < sortedEntries.size(); i++) {
            Map.Entry<String, Integer> entry = sortedEntries.get(i);
            if (previousScore != null && entry.getValue().equals(previousScore)) {
                // use same rank
            } else {
                currentRank = i + 1;
            }

            previousScore = entry.getValue();

            String rankText = String.valueOf(currentRank);
            Label rankLabel = new Label(rankText, skin);
            Label playerLabel = new Label(entry.getKey(), skin);
            playerLabel.setWrap(true);
            Label scoreLabel = new Label(Integer.toString(entry.getValue()), skin);

            boolean isCurrentPlayer = entry.getKey().equals(currentUsername);
            boolean isWinner = i == 0;

            if (isCurrentPlayer) {
                playerLabel.setText("→ " + entry.getKey());
                playerLabel.setColor(Color.YELLOW);
                scoreLabel.setColor(Color.YELLOW);
                rankLabel.setColor(Color.YELLOW);
            }

            if (highlightWinner && isWinner) {
                playerLabel.setText("🏆 " + (isCurrentPlayer ? "→ " : "") + entry.getKey());
                
                Color goldColor = new Color(1f, 0.84f, 0f, 1f);
                playerLabel.setColor(goldColor);
                scoreLabel.setColor(goldColor);
                rankLabel.setColor(goldColor);
            }

            boolean isTied = (i < sortedEntries.size() - 1) && 
                        entry.getValue().equals(sortedEntries.get(i+1).getValue());

            if (isTied) {
                rankText += "*"; // tie
                rankLabel.setText(rankText);
            }

            Table rowTable = new Table();
            rowTable.defaults().expand().fill();
            
            // Center-align rank numbers
            rowTable.add(rankLabel).width(rankColWidth).left();
            
            // Add padding between columns for better readability
            rowTable.add(playerLabel).width(playerColWidth).left().padLeft(10).padRight(10);
            
            // Center-align scores
            rowTable.add(scoreLabel).width(scoreColWidth).left();
            
            // Add the entire row to the root table
            rootTable.add(rowTable).colspan(3).fillX().padBottom(8).row();
        }

        if (sortedEntries.size() > 1) {
            boolean hasTies = false;
            Integer lastScore = null;
            
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                if (lastScore != null && entry.getValue().equals(lastScore)) {
                    hasTies = true;
                    break;
                }
                lastScore = entry.getValue();
            }
            
            if (hasTies) {
                Label tieLabel = new Label("* Tied scores", skin);
                tieLabel.setFontScale(0.8f);
                tieLabel.setColor(Color.LIGHT_GRAY);
                rootTable.add(tieLabel).colspan(3).padTop(10).left().row();
            }
        }

    }

}
