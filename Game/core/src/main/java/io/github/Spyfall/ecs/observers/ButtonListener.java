package io.github.Spyfall.ecs.observers;

import io.github.Spyfall.ecs.components.ButtonComponent.ButtonEnum;

public interface ButtonListener {
    public void onAction(ButtonEnum type);
}
