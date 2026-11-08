package com.bothengine.core.ecs;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * EntityManager
 * Manages all entities in the game
 */
public class EntityManager {
    private static final String TAG = "EntityManager";
    
    private final List<Entity> entities;
    private final Map<String, Entity> entityMap;
    private final List<Entity> entitiesToAdd;
    private final List<Entity> entitiesToRemove;
    private int nextEntityId;
    
    public EntityManager() {
        this.entities = new CopyOnWriteArrayList<>();
        this.entityMap = new HashMap<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();
        this.nextEntityId = 0;
    }
    
    /**
     * Create a new entity
     */
    public Entity createEntity(String name) {
        Entity entity = new Entity(name);
        entity.setEntityManager(this);
        entitiesToAdd.add(entity);
        entityMap.put(entity.getId(), entity);
        Log.d(TAG, "Created entity: " + entity.getName() + " (" + entity.getId() + ")");
        return entity;
    }
    
    /**
     * Create a new entity with default name
     */
    public Entity createEntity() {
        return createEntity("Entity_" + nextEntityId++);
    }
    
    /**
     * Destroy an entity
     */
    public void destroyEntity(Entity entity) {
        if (entity == null) return;
        entitiesToRemove.add(entity);
        entityMap.remove(entity.getId());
        Log.d(TAG, "Destroyed entity: " + entity.getName() + " (" + entity.getId() + ")");
    }
    
    /**
     * Destroy entity by ID
     */
    public void destroyEntity(String entityId) {
        Entity entity = entityMap.get(entityId);
        if (entity != null) {
            destroyEntity(entity);
        }
    }
    
    /**
     * Destroy all entities
     */
    public void destroyAllEntities() {
        entitiesToRemove.addAll(entities);
        entityMap.clear();
        Log.d(TAG, "Destroyed all entities");
    }
    
    /**
     * Get entity by ID
     */
    public Entity getEntity(String entityId) {
        return entityMap.get(entityId);
    }
    
    /**
     * Get all entities
     */
    public List<Entity> getAllEntities() {
        return getEntities();
    }

    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }
    
    /**
     * Get entities with specific component
     */
    public List<Entity> getEntitiesWithComponent(Class<? extends Component> componentClass) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.hasComponent(componentClass)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    /**
     * Get entities with all specified components
     */
    public List<Entity> getEntitiesWithComponents(Class<? extends Component>... componentClasses) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.hasComponents(componentClasses)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    /**
     * Get entity count
     */
    public int getEntityCount() {
        return entities.size();
    }
    
    /**
     * Process pending additions and removals
     * Called at the end of each frame
     */
    public void processUpdates() {
        // Add new entities
        for (Entity entity : entitiesToAdd) {
            entities.add(entity);
        }
        entitiesToAdd.clear();
        
        // Remove destroyed entities
        for (Entity entity : entitiesToRemove) {
            entities.remove(entity);
            entity.destroy();
        }
        entitiesToRemove.clear();
    }
    
    /**
     * Update all entities
     */
    public void update(float deltaTime) {
        processUpdates();
        
        for (Entity entity : entities) {
            if (entity.isActive()) {
                // Update all components
                for (Component component : entity.getComponents().values()) {
                    if (component.isEnabled()) {
                        component.onUpdate(deltaTime);
                    }
                }
            }
        }
    }
    
    /**
     * Fixed update all entities
     */
    public void fixedUpdate(float fixedDeltaTime) {
        for (Entity entity : entities) {
            if (entity.isActive()) {
                for (Component component : entity.getComponents().values()) {
                    if (component.isEnabled()) {
                        component.onFixedUpdate(fixedDeltaTime);
                    }
                }
            }
        }
    }
    
    /**
     * Find entity by name
     */
    public Entity findEntityByName(String name) {
        for (Entity entity : entities) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }
    
    /**
     * Find entities by name pattern
     */
    public List<Entity> findEntitiesByName(String namePattern) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.getName().contains(namePattern)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    /**
     * Shutdown the entity manager
     */
    public void shutdown() {
        destroyAllEntities();
        entities.clear();
        entityMap.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        Log.i(TAG, "EntityManager shutdown");
    }
}
