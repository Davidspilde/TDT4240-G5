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

    private float timeRemaining;
    private boolean isRunning;
    private Label timerLabel;
    private TimerListener listener;
    private float warningThreshold = 10f; // warning when 10 secs left
    
    public Timer(Skin skin) {
        super(skin);
    }

    @Override
    protected void create() {
        timerLabel = new Label("00:00", skin);
        timerLabel.setAlignment(Align.center);
        timerLabel.setFontScale(1.5f);
        rootTable.add(timerLabel);
    }

    public void start(float seconds) {
        timeRemaining = seconds;
        isRunning = true;
        updateDisplay();
    }

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
        if (!isRunning) return;
        
        timeRemaining -= Gdx.graphics.getDeltaTime();
        
        if (timeRemaining <= 0) {
            timeRemaining = 0;
            isRunning = false;
            
            // notify listener
            if (listener != null) {
                listener.onTimerEnd();
            }
        }
        updateDisplay();
    }

    private void updateDisplay() {
        int minutes = (int) (timeRemaining / 60);
        int seconds = (int) (timeRemaining % 60);
        
        String display = String.format("%02d:%02d", minutes, seconds);
        timerLabel.setText(display);
        
        if (timeRemaining <= warningThreshold) {
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
