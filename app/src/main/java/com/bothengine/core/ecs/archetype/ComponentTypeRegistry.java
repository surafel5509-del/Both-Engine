package com.bothengine.core.ecs.archetype;

import com.bothengine.core.ecs.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * ComponentTypeRegistry
 * Registry for component type IDs used by the archetype system
 * Each component type gets a unique integer ID for fast lookups
 */
public class ComponentTypeRegistry {
    private static final Map<Class<? extends Component>, Integer> typeToId = new HashMap<>();
    private static final Map<Integer, Class<? extends Component>> idToType = new HashMap<>();
    private static int nextTypeId = 0;
    
    /**
     * Register a component type and get its ID
     */
    public static int register(Class<? extends Component> componentType) {
        Integer id = typeToId.get(componentType);
        if (id != null) {
            return id;
        }
        
        id = nextTypeId++;
        typeToId.put(componentType, id);
        idToType.put(id, componentType);
        return id;
    }
    
    /**
     * Get the type ID for a component type
     */
    public static int getTypeId(Class<? extends Component> componentType) {
        Integer id = typeToId.get(componentType);
        if (id == null) {
            id = register(componentType);
        }
        return id;
    }
    
    /**
     * Get the component type for a type ID
     */
    public static Class<? extends Component> getType(int typeId) {
        return idToType.get(typeId);
    }
    
    /**
     * Check if a component type is registered
     */
    public static boolean isRegistered(Class<? extends Component> componentType) {
        return typeToId.containsKey(componentType);
    }
    
    /**
     * Get all registered component types
     */
    public static Map<Class<? extends Component>, Integer> getAllTypes() {
        return new HashMap<>(typeToId);
    }
    
    /**
     * Get the total number of registered component types
     */
    public static int getTypeCount() {
        return typeToId.size();
    }
    
    /**
     * Clear all registered types
     */
    public static void clear() {
        typeToId.clear();
        idToType.clear();
        nextTypeId = 0;
    }
}
