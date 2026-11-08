package com.bothengine.core.ecs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Entity
 * Represents a game object in the ECS architecture
 * An entity is just an ID and a collection of components
 */
public class Entity {
    private final String id;
    private final String name;
    private final Map<Class<? extends Component>, Component> components;
    private boolean active;
    private boolean visible;
    private Entity parent;
    private int layer;
    private int zIndex;
    
    public Entity(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.components = new HashMap<>();
        this.active = true;
        this.visible = true;
        this.parent = null;
        this.layer = 0;
        this.zIndex = 0;
    }
    
    public Entity() {
        this("Entity");
    }
    
    public String getId() {
        return id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Add a component to this entity
     */
    public <T extends Component> T addComponent(T component) {
        component.setEntity(this);
        components.put(component.getClass(), component);
        return component;
    }
    
    /**
     * Get a component from this entity
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentClass) {
        return (T) components.get(componentClass);
    }
    
    /**
     * Check if entity has a component
     */
    public boolean hasComponent(Class<? extends Component> componentClass) {
        return components.containsKey(componentClass);
    }
    
    /**
     * Remove a component from this entity
     */
    public <T extends Component> T removeComponent(Class<T> componentClass) {
        T component = getComponent(componentClass);
        if (component != null) {
            component.setEntity(null);
            components.remove(componentClass);
        }
        return component;
    }
    
    /**
     * Remove all components from this entity
     */
    public void removeAllComponents() {
        for (Component component : components.values()) {
            component.setEntity(null);
        }
        components.clear();
    }
    
    /**
     * Get all components
     */
    public Map<Class<? extends Component>, Component> getComponents() {
        return new HashMap<>(components);
    }
    
    /**
     * Get component count
     */
    public int getComponentCount() {
        return components.size();
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public Entity getParent() {
        return parent;
    }
    
    public void setParent(Entity parent) {
        this.parent = parent;
    }
    
    public int getLayer() {
        return layer;
    }
    
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    public int getZIndex() {
        return zIndex;
    }
    
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
    
    /**
     * Check if this entity has all the specified component types
     */
    public boolean hasComponents(Class<? extends Component>... componentClasses) {
        for (Class<? extends Component> componentClass : componentClasses) {
            if (!hasComponent(componentClass)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Destroy this entity
     */
    public void destroy() {
        removeAllComponents();
        active = false;
        visible = false;
    }
    
    @Override
    public String toString() {
        return "Entity{" + 
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", visible=" + visible +
                ", components=" + components.size() +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }
    
    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
