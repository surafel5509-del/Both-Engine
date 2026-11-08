package com.bothengine.systems;

import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;
import com.bothengine.components.UpdateComponent;

import java.util.List;

/**
 * UpdateSystem
 * Handles update logic for all entities with UpdateComponent
 */
public class UpdateSystem {
    private static final String TAG = "UpdateSystem";
    
    private final EngineConfig config;
    private EntityManager entityManager;
    private boolean enabled;
    
    public UpdateSystem(EngineConfig config) {
        this.config = config;
        this.entityManager = null;
        this.enabled = true;
        
        Log.i(TAG, "UpdateSystem initialized");
    }
    
    /**
     * Initialize the system
     */
    public void initialize(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    /**
     * Update all entities with UpdateComponent
     */
    public void update(float deltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Get all entities with UpdateComponent
        List<Entity> entities = entityManager.getEntitiesWithComponent(UpdateComponent.class);
        
        for (Entity entity : entities) {
            if (entity.isActive()) {
                UpdateComponent updateComponent = entity.getComponent(UpdateComponent.class);
                if (updateComponent != null && updateComponent.isEnabled()) {
                    updateComponent.update(deltaTime);
                }
            }
        }
    }
    
    /**
     * Fixed update all entities with UpdateComponent
     */
    public void fixedUpdate(float fixedDeltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Get all entities with UpdateComponent
        List<Entity> entities = entityManager.getEntitiesWithComponent(UpdateComponent.class);
        
        for (Entity entity : entities) {
            if (entity.isActive()) {
                UpdateComponent updateComponent = entity.getComponent(UpdateComponent.class);
                if (updateComponent != null && updateComponent.isEnabled()) {
                    updateComponent.fixedUpdate(fixedDeltaTime);
                }
            }
        }
    }
    
    /**
     * Shutdown the system
     */
    public void shutdown() {
        entityManager = null;
        enabled = false;
        Log.i(TAG, "UpdateSystem shutdown");
    }
    
    // Getters and setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
