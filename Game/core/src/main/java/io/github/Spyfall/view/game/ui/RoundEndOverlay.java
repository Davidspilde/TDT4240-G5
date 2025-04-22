
package io.github.Spyfall.view.game.ui;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * Round end overlay component
 */
public class RoundEndOverlay extends GameComponent {

    public interface RoundEndListener {
        void onNextRoundClicked();
    }

    // Layout constants
    private final float TITLE_FONT_SCALE = 1.5f;
    private final float PADDING = 30f;
    private final float ELEMENT_SPACING = 10f;

    private Texture bgTexture;
    private Label titleLabel;
    private Label reasonLabel;
    private Label spyLabel;
    private Label locationLabel;
    private TextButton nextRoundButton;
    private Scoreboard scoreboard;
    private RoundEndListener listener;

    private final String currentUsername;
    private final boolean isHost;
    private final float contentWidth;

    public RoundEndOverlay(Skin skin, String currentUsername, boolean isHost, float contentWidth) {
        super(skin);
        this.currentUsername = currentUsername;
        this.isHost = isHost;
        this.contentWidth = contentWidth;
    }

    @Override
    protected void create() {
        // Initialize scoreboard
        this.scoreboard = new Scoreboard(skin, currentUsername, contentWidth);
        rootTable.setFillParent(true);

        // Create semi-transparent black background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.8f);
        pixmap.fill();
        bgTexture = new Texture(pixmap);
        pixmap.dispose();
        rootTable.setBackground(new TextureRegionDrawable(new TextureRegion(bgTexture)));

        // Main content container
        Table contentTable = new Table();
        contentTable.pad(PADDING);
        contentTable.defaults().pad(ELEMENT_SPACING).align(Align.center);

        titleLabel = new Label("ROUND ENDED", skin);
        titleLabel.setFontScale(TITLE_FONT_SCALE);
        titleLabel.setAlignment(Align.center);

        reasonLabel = new Label("", skin);
        reasonLabel.setWrap(true);
        reasonLabel.setAlignment(Align.center);

        spyLabel = new Label("", skin);
        spyLabel.setWrap(true);

        locationLabel = new Label("", skin);
        locationLabel.setWrap(true);

        nextRoundButton = new TextButton("Next Round", skin);
        nextRoundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onNextRoundClicked();
                }
            }
        });
        nextRoundButton.setVisible(isHost);

        // Assemble content
        contentTable.add(titleLabel).fillX().row();
        contentTable.add(reasonLabel).fillX().padBottom(15).row();
        contentTable.add(spyLabel).fillX().padTop(10).row();
        contentTable.add(locationLabel).fillX().padTop(5).row();
        contentTable.add(scoreboard.getActor()).fillX().padTop(20).row();
        contentTable.add(nextRoundButton).padTop(20);

        rootTable.add(contentTable).expand().fill().maxWidth(contentWidth);
    }

    /**
     * Sets the round data and updates the display.
     */
    public void setRoundEndData(int roundNumber, String reason, String spy,
            String location, HashMap<String, Integer> scoreboard) {
        titleLabel.setText("ROUND " + roundNumber + " ENDED");

        reasonLabel.setText(reason != null ? reason : "");
        reasonLabel.setVisible(reason != null && !reason.isEmpty());

        spyLabel.setText("The Spy was: " + (spy != null ? spy : "Unknown"));
        locationLabel.setText("Location: " + (location != null ? location : "Unknown"));

        HashMap<String, Integer> safeScoreboard = scoreboard != null ? scoreboard : new HashMap<>();
        this.scoreboard.setScoreboard(safeScoreboard);

        update();
    }

    /**
     * Assign a listener for the Next Round button.
     */
    public void setListener(RoundEndListener listener) {
        this.listener = listener;
    }

    @Override
    public void update() {
        nextRoundButton.setVisible(isHost);
    }

    @Override
    public void dispose() {
        if (bgTexture != null) {
            bgTexture.dispose();
        }
    }
}
