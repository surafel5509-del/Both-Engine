package com.bothengine.core.ecs.query;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.archetype.Archetype;
import com.bothengine.core.ecs.archetype.ArchetypeChunk;
import com.bothengine.core.ecs.archetype.ArchetypeManager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * EntityQuery
 * Allows for efficient querying of entities based on component composition
 * This is optimized for the archetype-based ECS system
 */
public class EntityQuery {
    private final ArchetypeManager archetypeManager;
    private final Class<? extends Component>[] allOf;
    private final Class<? extends Component>[] anyOf;
    private final Class<? extends Component>[] noneOf;
    
    private List<Archetype> matchingArchetypes;
    private boolean cacheValid;
    
    /**
     * Create a new entity query
     */
    public EntityQuery(ArchetypeManager archetypeManager) {
        this.archetypeManager = archetypeManager;
        this.allOf = new Class[0];
        this.anyOf = new Class[0];
        this.noneOf = new Class[0];
        this.matchingArchetypes = new ArrayList<>();
        this.cacheValid = false;
    }
    
    /**
     * Create a query that matches entities with ALL of the specified components
     */
    @SafeVarargs
    public static EntityQuery withAll(ArchetypeManager manager, Class<? extends Component>... components) {
        EntityQuery query = new EntityQuery(manager);
        query.allOf = components;
        return query;
    }
    
    /**
     * Create a query that matches entities with ANY of the specified components
     */
    @SafeVarargs
    public static EntityQuery withAny(ArchetypeManager manager, Class<? extends Component>... components) {
        EntityQuery query = new EntityQuery(manager);
        query.anyOf = components;
        return query;
    }
    
    /**
     * Create a query that matches entities with ALL of 'allOf' AND ANY of 'anyOf' components
     */
    @SafeVarargs
    public final EntityQuery withAnyOf(Class<? extends Component>... components) {
        this.anyOf = components;
        this.cacheValid = false;
        return this;
    }
    
    /**
     * Add components that must ALL be present
     */
    @SafeVarargs
    public final EntityQuery withAll(Class<? extends Component>... components) {
        this.allOf = components;
        this.cacheValid = false;
        return this;
    }
    
    /**
     * Add components where ANY must be present
     */
    @SafeVarargs
    public final EntityQuery withAny(Class<? extends Component>... components) {
        this.anyOf = components;
        this.cacheValid = false;
        return this;
    }
    
    /**
     * Add components that must NONE be present
     */
    @SafeVarargs
    public final EntityQuery withNone(Class<? extends Component>... components) {
        this.noneOf = components;
        this.cacheValid = false;
        return this;
    }
    
    /**
     * Update the cache of matching archetypes
     */
    private void updateCache() {
        if (cacheValid) {
            return;
        }
        
        matchingArchetypes.clear();
        
        for (Archetype archetype : archetypeManager.getAllArchetypes()) {
            if (matches(archetype)) {
                matchingArchetypes.add(archetype);
            }
        }
        
        cacheValid = true;
    }
    
    /**
     * Check if an archetype matches this query
     */
    private boolean matches(Archetype archetype) {
        // Check allOf
        if (allOf.length > 0 && !archetype.hasAll(allOf)) {
            return false;
        }
        
        // Check anyOf
        if (anyOf.length > 0 && !archetype.hasAny(anyOf)) {
            return false;
        }
        
        // Check noneOf
        if (noneOf.length > 0 && archetype.hasAny(noneOf)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Get all entities matching this query
     */
    public List<Entity> getEntities() {
        updateCache();
        
        List<Entity> entities = new ArrayList<>();
        
        for (Archetype archetype : matchingArchetypes) {
            entities.addAll(archetypeManager.getEntities(archetype));
        }
        
        return entities;
    }
    
    /**
     * Get the count of entities matching this query
     */
    public int getEntityCount() {
        updateCache();
        
        int count = 0;
        for (Archetype archetype : matchingArchetypes) {
            count += archetypeManager.getEntities(archetype).size();
        }
        
        return count;
    }
    
    /**
     * Iterate over all entities matching this query
     */
    public void forEach(Consumer<Entity> action) {
        updateCache();
        
        for (Archetype archetype : matchingArchetypes) {
            for (Entity entity : archetypeManager.getEntities(archetype)) {
                action.accept(entity);
            }
        }
    }
    
    /**
     * Iterate over all entities matching this query with chunk information
     * This is more efficient as it processes entities in contiguous memory
     */
    public void forEachChunk(Consumer<ArchetypeChunk> chunkConsumer) {
        updateCache();
        
        for (Archetype archetype : matchingArchetypes) {
            for (ArchetypeChunk chunk : archetypeManager.getChunks(archetype)) {
                chunkConsumer.accept(chunk);
            }
        }
    }
    
    /**
     * Get the first entity matching this query
     */
    public Entity getFirstEntity() {
        updateCache();
        
        for (Archetype archetype : matchingArchetypes) {
            List<Entity> entities = archetypeManager.getEntities(archetype);
            if (!entities.isEmpty()) {
                return entities.get(0);
            }
        }
        
        return null;
    }
    
    /**
     * Check if any entities match this query
     */
    public boolean hasEntities() {
        return getEntityCount() > 0;
    }
    
    /**
     * Get the matching archetypes
     */
    public List<Archetype> getMatchingArchetypes() {
        updateCache();
        return new ArrayList<>(matchingArchetypes);
    }
    
    /**
     * Invalidate the cache (call when archetypes change)
     */
    public void invalidateCache() {
        cacheValid = false;
    }
    
    @Override
    public String toString() {
        return "EntityQuery{" +
                "allOf=" + java.util.Arrays.toString(allOf) +
                ", anyOf=" + java.util.Arrays.toString(anyOf) +
                ", noneOf=" + java.util.Arrays.toString(noneOf) +
                '}';
    }
}
