package com.bothengine.core.ecs.archetype;

import com.bothengine.core.ecs.Component;

import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Archetype
 * Represents a specific combination of component types for optimized ECS iteration
 * This is the core of a high-performance ECS system similar to Unity DOTS
 */
public class Archetype {
    private static final int MAX_COMPONENT_TYPES = 256;
    
    private final String name;
    private final Class<? extends Component>[] componentTypes;
    private final BitSet componentTypeMask;
    private final int id;
    private final int hashCode;
    
    // Cache for archetype lookups
    private static final Map<BitSet, Archetype> archetypeCache = new HashMap<>();
    private static int nextId = 0;
    
    /**
     * Create a new archetype with the specified component types
     */
    @SafeVarargs
    public static Archetype of(Class<? extends Component>... componentTypes) {
        // Sort component types for consistent hashing
        Arrays.sort(componentTypes, (a, b) -> a.getName().compareTo(b.getName()));
        
        // Create component type mask
        BitSet mask = new BitSet(MAX_COMPONENT_TYPES);
        for (Class<? extends Component> type : componentTypes) {
            int typeId = ComponentTypeRegistry.getTypeId(type);
            mask.set(typeId);
        }
        
        // Check cache
        Archetype cached = archetypeCache.get(mask);
        if (cached != null) {
            return cached;
        }
        
        // Create new archetype
        Archetype archetype = new Archetype(componentTypes, mask);
        archetypeCache.put(mask, archetype);
        return archetype;
    }
    
    /**
     * Get archetype by component type mask
     */
    public static Archetype fromMask(BitSet mask) {
        return archetypeCache.get(mask);
    }
    
    /**
     * Get or create archetype from component types
     */
    @SafeVarargs
    public static Archetype getOrCreate(Class<? extends Component>... componentTypes) {
        return of(componentTypes);
    }
    
    /**
     * Private constructor
     */
    private Archetype(Class<? extends Component>[] componentTypes, BitSet componentTypeMask) {
        this.id = nextId++;
        this.componentTypes = componentTypes;
        this.componentTypeMask = componentTypeMask;
        this.hashCode = componentTypeMask.hashCode();
        
        // Generate name
        StringBuilder nameBuilder = new StringBuilder("Archetype_");
        for (int i = 0; i < componentTypes.length; i++) {
            if (i > 0) nameBuilder.append("_");
            nameBuilder.append(componentTypes[i].getSimpleName());
        }
        this.name = nameBuilder.toString();
    }
    
    /**
     * Check if this archetype contains all the specified component types
     */
    @SafeVarargs
    public final boolean hasAll(Class<? extends Component>... types) {
        for (Class<? extends Component> type : types) {
            int typeId = ComponentTypeRegistry.getTypeId(type);
            if (!componentTypeMask.get(typeId)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if this archetype contains any of the specified component types
     */
    @SafeVarargs
    public final boolean hasAny(Class<? extends Component>... types) {
        for (Class<? extends Component> type : types) {
            int typeId = ComponentTypeRegistry.getTypeId(type);
            if (componentTypeMask.get(typeId)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if this archetype contains a specific component type
     */
    public boolean has(Class<? extends Component> componentType) {
        int typeId = ComponentTypeRegistry.getTypeId(componentType);
        return componentTypeMask.get(typeId);
    }
    
    /**
     * Get the component type at the specified index
     */
    public Class<? extends Component> getComponentType(int index) {
        return componentTypes[index];
    }
    
    /**
     * Get all component types
     */
    public Class<? extends Component>[] getComponentTypes() {
        return componentTypes;
    }
    
    /**
     * Get the number of component types
     */
    public int getComponentTypeCount() {
        return componentTypes.length;
    }
    
    /**
     * Get the component type mask
     */
    public BitSet getComponentTypeMask() {
        return componentTypeMask;
    }
    
    /**
     * Get the archetype ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Get the archetype name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Check if this archetype matches another
     */
    public boolean matches(Archetype other) {
        return this == other || componentTypeMask.equals(other.componentTypeMask);
    }
    
    /**
     * Clear the archetype cache
     */
    public static void clearCache() {
        archetypeCache.clear();
        nextId = 0;
    }
    
    @Override
    public int hashCode() {
        return hashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Archetype archetype = (Archetype) obj;
        return componentTypeMask.equals(archetype.componentTypeMask);
    }
    
    @Override
    public String toString() {
        return name + "(id=" + id + ", components=" + componentTypes.length + ")";
    }
}
