package io.github.Spyfall.launcher;

import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import io.github.Spyfall.client.GameClient;

public class GameLauncher {

    //Vi kan putte default settings her, men til å begynne med så lager den bare game client
    public static GameClient initGameClient(ScreenViewport viewport){
        return new GameClient(viewport);
    }
}
