package io.github.Spyfall.ecs.components;

import com.badlogic.gdx.math.Vector2;

public class TransformComponent implements Component{

    private Vector2 transform;

    public float getXTransform() {
        return transform.x;
    }

    public float getYTransform() {
        return transform.y;
    }

    public void setTransform(Vector2 transform) {
        this.transform = transform;
    }
}
