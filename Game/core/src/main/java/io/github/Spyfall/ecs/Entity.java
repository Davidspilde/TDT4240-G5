package io.github.Spyfall.ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import io.github.Spyfall.ecs.components.Component;
import io.github.Spyfall.ecs.components.PositionComponent;

public class Entity {

    private int uid;
    private static int nextUid = 1;
    private final Map<Class<? extends Component>, Component> components;

    public Entity() {
        components = new HashMap<>();
        this.uid = nextUid++;
    }

    public int getUid() {
        return uid;
    }

    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }

    public void addComponent(Component component) {
        components.put(component.getClass(), component);
    }

    public Component getComponent(Class<? extends Component> componentClass) {
        return components.get(componentClass);
    }

    public void removeComponent(Class<? extends Component> componentClass) {
        components.remove(componentClass);
    }
}
