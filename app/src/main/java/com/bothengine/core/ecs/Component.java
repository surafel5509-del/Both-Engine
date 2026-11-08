package com.bothengine.core.ecs;

/**
 * Component
 * Base class for all ECS components
 * Components are pure data containers with no logic
 */
public abstract class Component {
    private Entity entity;
    private boolean enabled;
    
    public Component() {
        this.enabled = true;
    }
    
    /**
     * Called when component is added to an entity
     */
    public void onAddedToEntity() {
        // Override in subclasses
    }
    
    /**
     * Called when component is removed from an entity
     */
    public void onRemovedFromEntity() {
        // Override in subclasses
    }
    
    /**
     * Called when the entity is enabled
     */
    public void onEnable() {
        // Override in subclasses
    }
    
    /**
     * Called when the entity is disabled
     */
    public void onDisable() {
        // Override in subclasses
    }
    
    /**
     * Called every frame
     */
    public void onUpdate(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Called at fixed intervals (for physics, etc.)
     */
    public void onFixedUpdate(float fixedDeltaTime) {
        // Override in subclasses
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        if (this.entity != null && this.entity != entity) {
            onRemovedFromEntity();
        }
        this.entity = entity;
        if (entity != null) {
            onAddedToEntity();
        }
    }
    
    public boolean isEnabled() {
        return enabled && (entity == null || entity.isActive());
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Get the component type for filtering
     */
    public Class<? extends Component> getComponentType() {
        return getClass();
    }
}
