package io.github.Spyfall.ecs;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.Spyfall.ecs.systems.RenderingSystem;

public class ECSManager {

    private static ECSManager instance;
    private List<Entity> entities;
    private Set<System> systems;
    private System renderSystem;

    private ECSManager(){
        this.entities = new ArrayList<Entity>();
    }

    public static ECSManager getInstance(){
        return (instance == null) ? (instance = new ECSManager()) : instance;
    }

    public void update(){
        if(systems!=null) {
            for (System system : systems) {
                system.update(entities);
            }
        }
        //ensuring that render runs last
        renderSystem.update(entities);
    }

    public void clearEntities() {
        if(entities != null)entities.clear();
    }

    public void addEntity(Entity entity){
        entities.add(entity);
    }

    public void clearSystems(){
        systems.clear();
    }

    public void addSystem(System system){
        if(system instanceof RenderingSystem){
            renderSystem = system;
        }
        else{
            systems.add(system);
        }
    }
}
