package io.github.Spyfall.launcher;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import io.github.Spyfall.client.GameClient;
import io.github.Spyfall.services.AudioService;

public class GameLauncher {

    //Vi kan putte default settings her, men til å begynne med så lager den bare game client
    public static GameClient initGameClient(ScreenViewport viewport){
        AudioService audioService = AudioService.getInstance();
        audioService.loadMusic("background", "audio/music/683507__sergequadrado__sad-loop.wav");
        audioService.loadSound("click", "audio/sounffx/333916__lextrack__cat-meowing.mp3");
        return new GameClient(viewport);
    }
}
