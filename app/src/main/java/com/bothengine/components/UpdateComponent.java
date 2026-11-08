package com.bothengine.components;

import com.bothengine.core.ecs.Component;

/**
 * UpdateComponent
 * Base class for components that need to be updated every frame
 */
public class UpdateComponent extends Component {
    private boolean started;
    
    public UpdateComponent() {
        this.started = false;
    }
    
    /**
     * Called when the component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        started = false;
    }
    
    /**
     * Called when the component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        started = false;
    }
    
    /**
     * Called when the entity is enabled
     */
    @Override
    public void onEnable() {
        super.onEnable();
        if (!started) {
            start();
            started = true;
        }
    }
    
    /**
     * Called when the entity is disabled
     */
    @Override
    public void onDisable() {
        super.onDisable();
        if (started) {
            stop();
            started = false;
        }
    }
    
    /**
     * Called once when the component starts
     */
    public void start() {
        // Override in subclasses
    }
    
    /**
     * Called once when the component stops
     */
    public void stop() {
        // Override in subclasses
    }
    
    /**
     * Called every frame
     */
    @Override
    public void onUpdate(float deltaTime) {
        if (started && isEnabled()) {
            update(deltaTime);
        }
    }
    
    /**
     * Called at fixed intervals
     */
    @Override
    public void onFixedUpdate(float fixedDeltaTime) {
        if (started && isEnabled()) {
            fixedUpdate(fixedDeltaTime);
        }
    }
    
    /**
     * Update method (override in subclasses)
     */
    public void update(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Fixed update method (override in subclasses)
     */
    public void fixedUpdate(float fixedDeltaTime) {
        // Override in subclasses
    }
}
