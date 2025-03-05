package io.github.Spyfall.ecs;

import java.util.List;
import java.util.Set;

public interface System {
    //Add deltatime in future here if needed
    void update(List<Entity> entities);
}
