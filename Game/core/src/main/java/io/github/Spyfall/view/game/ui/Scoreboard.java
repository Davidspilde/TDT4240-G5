
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

    // Layout constants
    private final float TITLE_FONT_SCALE = 1.2f;
    private final float HEADER_SPACING = 10f;
    private final float ROW_SPACING = 8f;
    private final float TIE_LABEL_FONT_SCALE = 0.8f;

    private HashMap<String, Integer> scoreboard;
    private final String currentUsername;
    private String title = "SCOREBOARD";
    private final float contentWidth;
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

    public void setHighlightWinner(boolean highlight) {
        this.highlightWinner = highlight;
        update();
    }

    @Override
    public void update() {
        rootTable.clear();

        // Title
        Label titleLabel = new Label(title, skin);
        titleLabel.setAlignment(Align.center);
        rootTable.add(titleLabel).colspan(3).padBottom(HEADER_SPACING).fillX().row();

        if (scoreboard == null || scoreboard.isEmpty()) {
            Label noDataLabel = new Label("No scores available", skin);
            noDataLabel.setAlignment(Align.center);
            rootTable.add(noDataLabel).colspan(3).padBottom(HEADER_SPACING);
            return;
        }

        float width = rootTable.getWidth() > 0 ? rootTable.getWidth() : contentWidth;
        float rankColWidth = width * 0.15f;
        float playerColWidth = width * 0.55f;
        float scoreColWidth = width * 0.3f;

        // Header row
        Table headerTable = new Table();
        headerTable.defaults().expand().fill();

        headerTable.add(new Label("#", skin)).width(rankColWidth).padRight(10);
        headerTable.add(new Label("Player", skin)).width(playerColWidth).padRight(10).left();
        headerTable.add(new Label("Score", skin)).width(scoreColWidth).left();

        rootTable.add(headerTable).colspan(3).fillX().padBottom(HEADER_SPACING).row();

        List<Map.Entry<String, Integer>> sorted = new ArrayList<>(scoreboard.entrySet());
        sorted.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int currentRank = 1;
        Integer previousScore = null;
        boolean hasTies = false;

        for (int i = 0; i < sorted.size(); i++) {
            Map.Entry<String, Integer> entry = sorted.get(i);
            if (previousScore != null && entry.getValue().equals(previousScore)) {
                hasTies = true;
            } else {
                currentRank = i + 1;
            }

            previousScore = entry.getValue();

            String rankText = String.valueOf(currentRank);
            Label rankLabel = new Label(rankText, skin);
            Label playerLabel = new Label(entry.getKey(), skin);
            playerLabel.setWrap(true);
            Label scoreLabel = new Label(Integer.toString(entry.getValue()), skin);

            boolean isCurrent = entry.getKey().equals(currentUsername);
            boolean isWinner = i == 0;

            // Color logic for current user
            if (isCurrent) {
                playerLabel.setText("→ " + entry.getKey());
                playerLabel.setColor(Color.YELLOW);
                scoreLabel.setColor(Color.YELLOW);
                rankLabel.setColor(Color.YELLOW);
            }

            // Highlight winner if enabled
            if (highlightWinner && isWinner) {
                Color gold = new Color(1f, 0.84f, 0f, 1f);
                playerLabel.setText((isCurrent ? "→ " : "") + entry.getKey());
                playerLabel.setColor(gold);
                scoreLabel.setColor(gold);
                rankLabel.setColor(gold);
            }

            // Handle tied scores
            boolean isTied = (i < sorted.size() - 1) &&
                    entry.getValue().equals(sorted.get(i + 1).getValue());

            if (isTied) {
                rankLabel.setText(rankText + "*");
            }

            Table row = new Table();
            row.defaults().expand().fill();
            row.add(rankLabel).width(rankColWidth).left();
            row.add(playerLabel).width(playerColWidth).left().padLeft(10).padRight(10);
            row.add(scoreLabel).width(scoreColWidth).left();

            rootTable.add(row).colspan(3).fillX().padBottom(ROW_SPACING).row();
        }

        if (hasTies) {
            Label tieLabel = new Label("* Tied scores", skin);
            tieLabel.setFontScale(TIE_LABEL_FONT_SCALE);
            tieLabel.setColor(Color.LIGHT_GRAY);
            rootTable.add(tieLabel).colspan(3).padTop(10).left().row();
        }
    }
}
