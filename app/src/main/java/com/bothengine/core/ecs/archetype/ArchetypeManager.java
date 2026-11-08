package com.bothengine.core.ecs.archetype;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ArchetypeManager
 * Manages all archetypes and their chunks for optimized ECS queries
 * This is the main class for the high-performance archetype-based ECS system
 */
public class ArchetypeManager {
    private final Map<Archetype, ArchetypeChunk> archetypeToFirstChunk;
    private final Map<BitSet, Archetype> maskToArchetype;
    private final List<Archetype> allArchetypes;
    
    public ArchetypeManager() {
        this.archetypeToFirstChunk = new HashMap<>();
        this.maskToArchetype = new HashMap<>();
        this.allArchetypes = new ArrayList<>();
    }
    
    /**
     * Get or create an archetype for the specified component types
     */
    @SafeVarargs
    public final Archetype getArchetype(Class<? extends Component>... componentTypes) {
        Archetype archetype = Archetype.of(componentTypes);
        
        // Register mask mapping
        maskToArchetype.put(archetype.getComponentTypeMask(), archetype);
        
        // Add to all archetypes if new
        if (!allArchetypes.contains(archetype)) {
            allArchetypes.add(archetype);
        }
        
        return archetype;
    }
    
    /**
     * Get the first chunk for an archetype
     */
    public ArchetypeChunk getFirstChunk(Archetype archetype) {
        return archetypeToFirstChunk.get(archetype);
    }
    
    /**
     * Get or create the first chunk for an archetype
     */
    public ArchetypeChunk getOrCreateFirstChunk(Archetype archetype) {
        ArchetypeChunk chunk = archetypeToFirstChunk.get(archetype);
        if (chunk == null) {
            chunk = new ArchetypeChunk(archetype);
            archetypeToFirstChunk.put(archetype, chunk);
        }
        return chunk;
    }
    
    /**
     * Add an entity to the appropriate archetype chunk
     */
    public void addEntity(Entity entity, Component[] components) {
        Archetype archetype = getArchetypeFromComponents(components);
        ArchetypeChunk chunk = getOrCreateFirstChunk(archetype);
        
        // Find the last chunk in the chain
        while (chunk.getNextChunk() != null) {
            chunk = chunk.getNextChunk();
        }
        
        // Add to chunk
        chunk.addEntity(entity, components);
    }
    
    /**
     * Remove an entity from its archetype chunk
     */
    public void removeEntity(Entity entity, Archetype archetype) {
        ArchetypeChunk chunk = archetypeToFirstChunk.get(archetype);
        if (chunk == null) {
            return;
        }
        
        // Find and remove from chunk
        while (chunk != null) {
            for (int i = 0; i < chunk.getEntityCount(); i++) {
                if (chunk.getEntity(i) == entity) {
                    chunk.removeEntity(i);
                    return;
                }
            }
            chunk = chunk.getNextChunk();
        }
    }
    
    /**
     * Get archetype from an array of components
     */
    public Archetype getArchetypeFromComponents(Component[] components) {
        Class<? extends Component>[] types = new Class[components.length];
        for (int i = 0; i < components.length; i++) {
            types[i] = components[i].getClass();
        }
        return getArchetype(types);
    }
    
    /**
     * Get all chunks for a specific archetype
     */
    public List<ArchetypeChunk> getChunks(Archetype archetype) {
        List<ArchetypeChunk> chunks = new ArrayList<>();
        ArchetypeChunk chunk = archetypeToFirstChunk.get(archetype);
        
        while (chunk != null) {
            chunks.add(chunk);
            chunk = chunk.getNextChunk();
        }
        
        return chunks;
    }
    
    /**
     * Get all entities with a specific archetype
     */
    public List<Entity> getEntities(Archetype archetype) {
        List<Entity> entities = new ArrayList<>();
        ArchetypeChunk chunk = archetypeToFirstChunk.get(archetype);
        
        while (chunk != null) {
            for (int i = 0; i < chunk.getEntityCount(); i++) {
                entities.add(chunk.getEntity(i));
            }
            chunk = chunk.getNextChunk();
        }
        
        return entities;
    }
    
    /**
     * Get all entities that have all the specified component types
     */
    @SafeVarargs
    public final List<Entity> getEntitiesWith(Class<? extends Component>... componentTypes) {
        List<Entity> entities = new ArrayList<>();
        
        for (Archetype archetype : allArchetypes) {
            if (archetype.hasAll(componentTypes)) {
                entities.addAll(getEntities(archetype));
            }
        }
        
        return entities;
    }
    
    /**
     * Get all entities that have any of the specified component types
     */
    @SafeVarargs
    public final List<Entity> getEntitiesWithAny(Class<? extends Component>... componentTypes) {
        List<Entity> entities = new ArrayList<>();
        
        for (Archetype archetype : allArchetypes) {
            if (archetype.hasAny(componentTypes)) {
                entities.addAll(getEntities(archetype));
            }
        }
        
        return entities;
    }
    
    /**
     * Get all archetypes
     */
    public List<Archetype> getAllArchetypes() {
        return new ArrayList<>(allArchetypes);
    }
    
    /**
     * Get the total number of entities across all archetypes
     */
    public int getTotalEntityCount() {
        int count = 0;
        for (Archetype archetype : allArchetypes) {
            ArchetypeChunk chunk = archetypeToFirstChunk.get(archetype);
            while (chunk != null) {
                count += chunk.getEntityCount();
                chunk = chunk.getNextChunk();
            }
        }
        return count;
    }
    
    /**
     * Get the number of chunks for a specific archetype
     */
    public int getChunkCount(Archetype archetype) {
        int count = 0;
        ArchetypeChunk chunk = archetypeToFirstChunk.get(archetype);
        while (chunk != null) {
            count++;
            chunk = chunk.getNextChunk();
        }
        return count;
    }
    
    /**
     * Clear all archetypes and chunks
     */
    public void clear() {
        archetypeToFirstChunk.clear();
        maskToArchetype.clear();
        allArchetypes.clear();
        Archetype.clearCache();
        ComponentTypeRegistry.clear();
    }
    
    /**
     * Get statistics
     */
    public String getStats() {
        int totalEntities = getTotalEntityCount();
        int totalArchetypes = allArchetypes.size();
        int totalChunks = 0;
        
        for (Archetype archetype : allArchetypes) {
            totalChunks += getChunkCount(archetype);
        }
        
        return "ArchetypeManager Stats: " + 
               totalEntities + " entities, " + 
               totalArchetypes + " archetypes, " + 
               totalChunks + " chunks";
    }
}
