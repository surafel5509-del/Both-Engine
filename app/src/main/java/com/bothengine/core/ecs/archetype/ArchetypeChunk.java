package com.bothengine.core.ecs.archetype;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;

import java.util.Arrays;

/**
 * ArchetypeChunk
 * A contiguous block of entities with the same archetype (component composition)
 * This enables cache-friendly iteration and better performance
 */
public class ArchetypeChunk {
    private static final int DEFAULT_CHUNK_SIZE = 1024;
    
    private final Archetype archetype;
    private final int chunkSize;
    private final Component[][] componentArrays;
    private final Entity[] entities;
    private int entityCount;
    private ArchetypeChunk nextChunk;
    
    /**
     * Create a new chunk for the specified archetype
     */
    public ArchetypeChunk(Archetype archetype) {
        this(archetype, DEFAULT_CHUNK_SIZE);
    }
    
    /**
     * Create a new chunk with a specific size
     */
    public ArchetypeChunk(Archetype archetype, int chunkSize) {
        this.archetype = archetype;
        this.chunkSize = chunkSize;
        this.entityCount = 0;
        this.nextChunk = null;
        
        // Create component arrays
        Class<? extends Component>[] componentTypes = archetype.getComponentTypes();
        this.componentArrays = new Component[componentTypes.length][];
        
        for (int i = 0; i < componentTypes.length; i++) {
            this.componentArrays[i] = new Component[chunkSize];
        }
        
        this.entities = new Entity[chunkSize];
    }
    
    /**
     * Add an entity to this chunk
     */
    public void addEntity(Entity entity, Component[] components) {
        if (entityCount >= chunkSize) {
            // Chunk is full, create a new one
            if (nextChunk == null) {
                nextChunk = new ArchetypeChunk(archetype, chunkSize);
            }
            nextChunk.addEntity(entity, components);
            return;
        }
        
        // Add entity
        entities[entityCount] = entity;
        
        // Add components
        for (int i = 0; i < componentArrays.length; i++) {
            componentArrays[i][entityCount] = components[i];
        }
        
        entityCount++;
    }
    
    /**
     * Remove an entity from this chunk
     */
    public void removeEntity(int index) {
        if (index < 0 || index >= entityCount) {
            return;
        }
        
        // Move last entity to this position
        if (index < entityCount - 1) {
            entities[index] = entities[entityCount - 1];
            for (int i = 0; i < componentArrays.length; i++) {
                componentArrays[i][index] = componentArrays[i][entityCount - 1];
            }
        }
        
        // Clear last position
        entities[entityCount - 1] = null;
        for (int i = 0; i < componentArrays.length; i++) {
            componentArrays[i][entityCount - 1] = null;
        }
        
        entityCount--;
    }
    
    /**
     * Get an entity at the specified index
     */
    public Entity getEntity(int index) {
        if (index < 0 || index >= entityCount) {
            return null;
        }
        return entities[index];
    }
    
    /**
     * Get a component of a specific type at the specified index
     */
    public <T extends Component> T getComponent(Class<T> componentType, int index) {
        if (index < 0 || index >= entityCount) {
            return null;
        }
        
        int componentIndex = archetype.getComponentTypeIndex(componentType);
        if (componentIndex < 0) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        T component = (T) componentArrays[componentIndex][index];
        return component;
    }
    
    /**
     * Get all entities in this chunk
     */
    public Entity[] getEntities() {
        return Arrays.copyOf(entities, entityCount);
    }
    
    /**
     * Get the component array for a specific component type
     */
    public <T extends Component> T[] getComponentArray(Class<T> componentType) {
        int componentIndex = archetype.getComponentTypeIndex(componentType);
        if (componentIndex < 0) {
            return null;
        }
        
        @SuppressWarnings("unchecked")
        T[] array = (T[]) componentArrays[componentIndex];
        return array;
    }
    
    /**
     * Get the number of entities in this chunk
     */
    public int getEntityCount() {
        return entityCount;
    }
    
    /**
     * Check if this chunk is full
     */
    public boolean isFull() {
        return entityCount >= chunkSize;
    }
    
    /**
     * Check if this chunk is empty
     */
    public boolean isEmpty() {
        return entityCount == 0;
    }
    
    /**
     * Get the chunk size (capacity)
     */
    public int getChunkSize() {
        return chunkSize;
    }
    
    /**
     * Get the archetype
     */
    public Archetype getArchetype() {
        return archetype;
    }
    
    /**
     * Get the next chunk in the chain
     */
    public ArchetypeChunk getNextChunk() {
        return nextChunk;
    }
    
    /**
     * Set the next chunk in the chain
     */
    public void setNextChunk(ArchetypeChunk nextChunk) {
        this.nextChunk = nextChunk;
    }
    
    /**
     * Clear this chunk
     */
    public void clear() {
        for (int i = 0; i < entities.length; i++) {
            entities[i] = null;
        }
        for (Component[] array : componentArrays) {
            Arrays.fill(array, null);
        }
        entityCount = 0;
        nextChunk = null;
    }
    
    @Override
    public String toString() {
        return "ArchetypeChunk{" +
                "archetype=" + archetype.getName() +
                ", entityCount=" + entityCount +
                ", chunkSize=" + chunkSize +
                '}';
    }
}
