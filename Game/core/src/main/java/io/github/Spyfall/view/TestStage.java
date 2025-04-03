package io.github.Spyfall.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class TestStage extends StageView {
    public TestStage(ScreenViewport viewport) {
        super(viewport);
        initTestStage();
    }

    private void initTestStage() {
        Gdx.input.setInputProcessor(stage);
    }
}
