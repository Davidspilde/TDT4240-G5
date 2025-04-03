package io.github.Spyfall.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TestStage extends StageController {
    public TestStage(ScreenViewport viewport) {
        super(viewport);
        initTestStage();
    }

    private void initTestStage() {
        Gdx.input.setInputProcessor(stage);
    }
}
