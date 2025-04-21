package io.github.Spyfall.view.game.ui;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Component for game control buttons
 */
public class GameControls extends GameComponent {

    public interface GameControlListener {
        void onEndGameClicked();
        void onLeaveGameClicked();
    }

    private TextButton endGameButton;
    private TextButton leaveGameButton;
    private GameControlListener listener;
    private boolean isHost;
    
    public GameControls(Skin skin, boolean isHost) {
        super(skin);
        this.isHost = isHost;
    }

    @Override
    protected void create() {
        // end game (only host)
        endGameButton = new TextButton("End Game", skin);
        endGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onEndGameClicked();
                }
            }
        });
        endGameButton.setVisible(isHost);
        
        leaveGameButton = new TextButton("Leave Game", skin);
        leaveGameButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (listener != null) {
                    listener.onLeaveGameClicked();
                }
            }
        });

        rootTable.add(endGameButton).padRight(20);
        rootTable.add(leaveGameButton);
    }

    public void setListener(GameControlListener listener) {
        this.listener = listener;
    }
    
    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
        endGameButton.setVisible(isHost);
    }
    
    @Override
    public void update() {
        // don't need updates here
    }
}
