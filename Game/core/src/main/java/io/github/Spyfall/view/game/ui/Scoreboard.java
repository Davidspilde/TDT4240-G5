package io.github.Spyfall.view.game.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * Component for displaying the scoreboard
 */
public class Scoreboard extends GameComponent {
    private HashMap<String, Integer> scoreboard;
    private String currentUsername;
    private String title = "SCOREBOARD";
    private float contentWidth;

    public Scoreboard(Skin skin, String currentUsername, float contentWidth) {
        super(skin);
        this.currentUsername = currentUsername;
        this.contentWidth = contentWidth;
    }

    @Override
    protected void create() {
        rootTable.top();
    }
    
    public void setScoreboard(HashMap<String, Integer> scoreboard) {
        this.scoreboard = scoreboard;
        update();
    }
    
    public void setTitle(String title) {
        this.title = title;
        update();
    }

    @Override
    public void update() {
        rootTable.clear();
        
        if (scoreboard == null || scoreboard.isEmpty()) {
            return;
        }

        float playerColWidth = contentWidth * 0.6f;
        float scoreColWidth = contentWidth * 0.2f;
        float rankColWidth = contentWidth * 0.1f;

        Label titleLabel = new Label(title, skin);
        titleLabel.setAlignment(Align.center);
        rootTable.add(titleLabel).colspan(2).padBottom(10).row();

        Label rankHeader = new Label("#", skin);
        Label playerHeader = new Label("Player", skin);
        Label scoreHeader = new Label("Score", skin);
        rootTable.add(rankHeader).width(rankColWidth).left().padBottom(8);
        rootTable.add(playerHeader).width(playerColWidth).left().padBottom(8);
        rootTable.add(scoreHeader).width(scoreColWidth).right().padBottom(8).row();

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

            if (entry.getKey().equals(currentUsername)) {
                playerLabel.setText("→ " + entry.getKey());
                playerLabel.setColor(Color.YELLOW);
                scoreLabel.setColor(Color.YELLOW);
                rankLabel.setColor(Color.YELLOW);
            }

            boolean isTied = (i < sortedEntries.size() - 1) && 
                        entry.getValue().equals(sortedEntries.get(i+1).getValue());

            if (isTied) {
                rankText += "*"; // tie
                rankLabel.setText(rankText);
            }
            
            rootTable.add(rankLabel).width(rankColWidth).left().padBottom(5);
            rootTable.add(playerLabel).width(playerColWidth).left().fillX().padBottom(5);
            rootTable.add(scoreLabel).width(scoreColWidth).right().padBottom(5).row();
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
