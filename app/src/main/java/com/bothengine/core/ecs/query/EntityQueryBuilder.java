package com.bothengine.core.ecs.query;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.archetype.ArchetypeManager;

/**
 * EntityQueryBuilder
 * Fluent builder for creating entity queries
 */
public class EntityQueryBuilder {
    private final ArchetypeManager archetypeManager;
    
    public EntityQueryBuilder(ArchetypeManager archetypeManager) {
        this.archetypeManager = archetypeManager;
    }
    
    /**
     * Start a new query
     */
    public EntityQuery query() {
        return new EntityQuery(archetypeManager);
    }
    
    /**
     * Query entities with ALL of the specified components
     */
    @SafeVarargs
    public final EntityQuery withAll(Class<? extends Component>... components) {
        return EntityQuery.withAll(archetypeManager, components);
    }
    
    /**
     * Query entities with ANY of the specified components
     */
    @SafeVarargs
    public final EntityQuery withAny(Class<? extends Component>... components) {
        return EntityQuery.withAny(archetypeManager, components);
    }
    
    /**
     * Query entities with a specific component
     */
    public EntityQuery with(Class<? extends Component> component) {
        return withAll(component);
    }
    
    /**
     * Query entities without a specific component
     */
    public EntityQuery without(Class<? extends Component> component) {
        return new EntityQuery(archetypeManager).withNone(component);
    }
}
