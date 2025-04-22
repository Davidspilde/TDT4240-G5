
package io.github.Spyfall.view.game.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * Timer component for countdown display
 */
public class Timer extends GameComponent {

    public interface TimerListener {
        void onTimerEnd();
    }

    // Layout and behavior constants
    private final float FONT_SCALE = 1.5f;
    private final float WARNING_THRESHOLD = 10f; // seconds

    private float timeRemaining;
    private boolean isRunning;
    private Label timerLabel;
    private TimerListener listener;

    public Timer(Skin skin) {
        super(skin);
    }

    @Override
    protected void create() {
        timerLabel = new Label("00:00", skin);
        timerLabel.setAlignment(Align.center);
        timerLabel.setFontScale(FONT_SCALE);
        rootTable.add(timerLabel);
    }

    /**
     * Starts the timer with a countdown from the given time (in seconds)
     */
    public void start(float seconds) {
        timeRemaining = seconds;
        isRunning = true;
        updateDisplay();
    }

    /**
     * Stops the timer
     */
    public void stop() {
        isRunning = false;
    }

    public void setListener(TimerListener listener) {
        this.listener = listener;
    }

    public void setText(String text) {
        timerLabel.setText(text);
    }

    @Override
    public void update() {
        if (!isRunning)
            return;

        timeRemaining -= Gdx.graphics.getDeltaTime();

        if (timeRemaining <= 0) {
            timeRemaining = 0;
            isRunning = false;

            if (listener != null) {
                listener.onTimerEnd();
            }
        }

        updateDisplay();
    }

    /**
     * Updates the label to show the current time, and changes color if time is low
     */
    private void updateDisplay() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);

        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));

        if (timeRemaining <= WARNING_THRESHOLD) {
            timerLabel.setColor(Color.RED);
        } else {
            timerLabel.setColor(Color.WHITE);
        }
    }

    public float getTimeRemaining() {
        return timeRemaining;
    }

    public boolean isRunning() {
        return isRunning;
    }
}
