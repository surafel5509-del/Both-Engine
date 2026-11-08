package com.bothengine.core.ecs.serialization;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EntitySerializer
 * Serializes entities and their components to/from various formats
 */
public class EntitySerializer {
    private final EntityManager entityManager;
    private final Map<Class<? extends Component>, ComponentSerializer<?>> componentSerializers;
    
    public EntitySerializer(EntityManager entityManager) {
        this.entityManager = entityManager;
        this.componentSerializers = new HashMap<>();
        
        // Register default serializers
        registerDefaultSerializers();
    }
    
    /**
     * Register default component serializers
     */
    private void registerDefaultSerializers() {
        // Default serializers will be registered by component types
        // that implement SerializableComponent
    }
    
    /**
     * Register a custom serializer for a component type
     */
    public <T extends Component> void registerSerializer(Class<T> componentType, ComponentSerializer<T> serializer) {
        componentSerializers.put(componentType, serializer);
    }
    
    /**
     * Get the serializer for a component type
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> ComponentSerializer<T> getSerializer(Class<T> componentType) {
        ComponentSerializer<?> serializer = componentSerializers.get(componentType);
        if (serializer != null) {
            return (ComponentSerializer<T>) serializer;
        }
        
        // Try to create a default serializer if component implements SerializableComponent
        try {
            if (SerializableComponent.class.isAssignableFrom(componentType)) {
                serializer = new DefaultComponentSerializer<>();
                componentSerializers.put(componentType, serializer);
                return (ComponentSerializer<T>) serializer;
            }
        } catch (Exception e) {
            // Ignore
        }
        
        return null;
    }
    
    /**
     * Serialize an entity to JSON
     */
    public String serializeToJson(Entity entity) {
        EntityData data = new EntityData();
        data.id = entity.getId();
        data.name = entity.getName();
        data.active = entity.isActive();
        data.visible = entity.isVisible();
        data.layer = entity.getLayer();
        data.zIndex = entity.getZIndex();
        
        // Serialize components
        for (Class<? extends Component> componentType : entity.getComponents().keySet()) {
            Component component = entity.getComponents().get(componentType);
            ComponentSerializer serializer = getSerializer(componentType);
            if (serializer != null) {
                String serialized = serializer.serialize(component);
                data.components.put(componentType.getName(), serialized);
            }
        }
        
        return JsonSerializer.toJson(data);
    }
    
    /**
     * Deserialize an entity from JSON
     */
    public Entity deserializeFromJson(String json, EntityManager targetManager) {
        EntityData data = JsonSerializer.fromJson(json, EntityData.class);
        
        // Create entity
        Entity entity = targetManager.createEntity(data.name);
        entity.setActive(data.active);
        entity.setVisible(data.visible);
        entity.setLayer(data.layer);
        entity.setZIndex(data.zIndex);
        
        // Deserialize components
        for (Map.Entry<String, String> entry : data.components.entrySet()) {
            String className = entry.getKey();
            String serialized = entry.getValue();
            
            try {
                Class<?> componentClass = Class.forName(className);
                if (Component.class.isAssignableFrom(componentClass)) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Component> componentType = (Class<? extends Component>) componentClass;
                    
                    ComponentSerializer<?> serializer = getSerializer(componentType);
                    if (serializer != null) {
                        Component component = serializer.deserialize(serialized);
                        entity.addComponent(component);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error deserializing component " + className + ": " + e.getMessage());
            }
        }
        
        return entity;
    }
    
    /**
     * Serialize all entities to JSON
     */
    public String serializeAllToJson() {
        List<Entity> entities = entityManager.getEntities();
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"entities\": [\n");
        
        for (int i = 0; i < entities.size(); i++) {
            if (i > 0) {
                json.append(",\n");
            }
            json.append("    ").append(serializeToJson(entities.get(i)));
        }
        
        json.append("\n  ]\n}");
        return json.toString();
    }
    
    /**
     * Serialize a scene to JSON
     */
    public String serializeSceneToJson(List<Entity> entities) {
        StringBuilder json = new StringBuilder();
        json.append("{\n  \"entities\": [\n");
        
        for (int i = 0; i < entities.size(); i++) {
            if (i > 0) {
                json.append(",\n");
            }
            json.append("    ").append(serializeToJson(entities.get(i)));
        }
        
        json.append("\n  ]\n}");
        return json.toString();
    }
    
    /**
     * Data class for entity serialization
     */
    public static class EntityData {
        public String id;
        public String name;
        public boolean active;
        public boolean visible;
        public int layer;
        public int zIndex;
        public Map<String, String> components = new HashMap<>();
    }
    
    /**
     * Component serializer interface
     */
    public interface ComponentSerializer<T extends Component> {
        String serialize(T component);
        T deserialize(String data);
    }
    
    /**
     * Default component serializer for SerializableComponent
     */
    public static class DefaultComponentSerializer<T extends Component & SerializableComponent> 
            implements ComponentSerializer<T> {
        @Override
        public String serialize(T component) {
            return component.serialize();
        }
        
        @Override
        public T deserialize(String data) {
            try {
                // This is a simplified implementation
                // In a real engine, we would use reflection or a factory
                throw new UnsupportedOperationException("Default deserializer not implemented");
            } catch (Exception e) {
                throw new RuntimeException("Failed to deserialize component", e);
            }
        }
    }
    
    /**
     * Marker interface for serializable components
     */
    public interface SerializableComponent {
        String serialize();
        void deserialize(String data);
    }
}
