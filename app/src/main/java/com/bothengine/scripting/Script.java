package com.bothengine.scripting;

import com.bothengine.core.ecs.Entity;

/**
 * Script
 * Base class for all scripts
 * Similar to Unity's MonoBehaviour
 */
public abstract class Script {
    private String name;
    private Entity entity;
    private boolean enabled;
    
    public Script() {
        this.name = getClass().getSimpleName();
        this.entity = null;
        this.enabled = true;
    }
    
    /**
     * Create a script with a name
     */
    public Script(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Called when the script is initialized
     */
    public void onInitialize() {
        // Override in subclasses
    }
    
    /**
     * Called when the script is enabled
     */
    public void onEnable() {
        // Override in subclasses
    }
    
    /**
     * Called when the script is disabled
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
     * Called at fixed intervals
     */
    public void onFixedUpdate(float fixedDeltaTime) {
        // Override in subclasses
    }
    
    /**
     * Called when a collision occurs
     */
    public void onCollisionEnter(Object other) {
        // Override in subclasses
    }
    
    /**
     * Called when a collision ends
     */
    public void onCollisionExit(Object other) {
        // Override in subclasses
    }
    
    /**
     * Called when a trigger collision occurs
     */
    public void onTriggerEnter(Object other) {
        // Override in subclasses
    }
    
    /**
     * Called when a trigger collision ends
     */
    public void onTriggerExit(Object other) {
        // Override in subclasses
    }
    
    /**
     * Called when the script is shutdown
     */
    public void onShutdown() {
        // Override in subclasses
    }
    
    /**
     * Called when the entity is started
     */
    public void onStart() {
        // Override in subclasses
    }
    
    /**
     * Called when the entity is destroyed
     */
    public void onDestroy() {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Get a component from the entity
     */
    @SuppressWarnings("unchecked")
    public <T> T getComponent(Class<T> componentClass) {
        if (entity != null) {
            return entity.getComponent(componentClass);
        }
        return null;
    }
    
    /**
     * Check if the entity has a component
     */
    public boolean hasComponent(Class<?> componentClass) {
        return entity != null && entity.hasComponent(componentClass);
    }
    
    /**
     * Add a component to the entity
     */
    public <T> T addComponent(T component) {
        if (entity != null) {
            return entity.addComponent(component);
        }
        return null;
    }
    
    /**
     * Remove a component from the entity
     */
    public <T> T removeComponent(Class<T> componentClass) {
        if (entity != null) {
            return entity.removeComponent(componentClass);
        }
        return null;
    }
    
    /**
     * Find an entity by name
     */
    public Entity findEntity(String name) {
        if (entity != null && entity.getEntityManager() != null) {
            return entity.getEntityManager().findEntity(name);
        }
        return null;
    }
    
    /**
     * Log a message
     */
    public void log(String message) {
        System.out.println("[" + name + "] " + message);
    }
    
    /**
     * Log a warning
     */
    public void logWarning(String message) {
        System.err.println("[" + name + "] WARNING: " + message);
    }
    
    /**
     * Log an error
     */
    public void logError(String message) {
        System.err.println("[" + name + "] ERROR: " + message);
    }
    
    @Override
    public String toString() {
        return "Script{" +
                "name='" + name + '\'' +
                ", entity=" + (entity != null ? entity.getName() : "null") +
                ", enabled=" + enabled +
                '}';
    }
}
