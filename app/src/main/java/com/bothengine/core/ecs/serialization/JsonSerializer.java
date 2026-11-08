package com.bothengine.core.ecs.serialization;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.List;
import java.util.Map;

/**
 * JsonSerializer
 * Simple JSON serialization utility using Gson
 * This provides a lightweight way to serialize/deserialize objects to JSON
 */
public class JsonSerializer {
    private static final Gson GSON = new GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .create();
    
    private static final Gson COMPACT_GSON = new GsonBuilder()
        .serializeNulls()
        .create();
    
    /**
     * Serialize an object to JSON with pretty printing
     */
    public static String toJson(Object object) {
        return GSON.toJson(object);
    }
    
    /**
     * Serialize an object to JSON without pretty printing
     */
    public static String toJsonCompact(Object object) {
        return COMPACT_GSON.toJson(object);
    }
    
    /**
     * Deserialize JSON to an object
     */
    public static <T> T fromJson(String json, Class<T> type) {
        return GSON.fromJson(json, type);
    }
    
    /**
     * Deserialize JSON to a list
     */
    public static <T> List<T> fromJsonList(String json, Class<T> type) {
        return GSON.fromJson(json, new ListTypeAdapter<>(type));
    }
    
    /**
     * Deserialize JSON to a map
     */
    public static <K, V> Map<K, V> fromJsonMap(String json, Class<K> keyType, Class<V> valueType) {
        return GSON.fromJson(json, new MapTypeAdapter<>(keyType, valueType));
    }
    
    /**
     * Parse JSON to a JsonElement for manual processing
     */
    public static JsonElement parse(String json) {
        return JsonParser.parseString(json);
    }
    
    /**
     * Check if JSON is valid
     */
    public static boolean isValid(String json) {
        try {
            JsonParser.parseString(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Pretty print JSON
     */
    public static String prettyPrint(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return GSON.toJson(element);
        } catch (Exception e) {
            return json;
        }
    }
    
    /**
     * Minify JSON
     */
    public static String minify(String json) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return COMPACT_GSON.toJson(element);
        } catch (Exception e) {
            return json;
        }
    }
    
    /**
     * Type adapter for lists
     */
    private static class ListTypeAdapter<T> implements com.google.gson.JsonDeserializer<List<T>> {
        private final Class<T> type;
        
        public ListTypeAdapter(Class<T> type) {
            this.type = type;
        }
        
        @Override
        public List<T> deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT, 
                                    com.google.gson.JsonDeserializationContext context) {
            return GSON.fromJson(json, new com.google.gson.reflect.TypeToken<List<T>>() {}.getType());
        }
    }
    
    /**
     * Type adapter for maps
     */
    private static class MapTypeAdapter<K, V> implements com.google.gson.JsonDeserializer<Map<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;
        
        public MapTypeAdapter(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }
        
        @Override
        public Map<K, V> deserialize(com.google.gson.JsonElement json, java.lang.reflect.Type typeOfT,
                                     com.google.gson.JsonDeserializationContext context) {
            return GSON.fromJson(json, new com.google.gson.reflect.TypeToken<Map<K, V>>() {}.getType());
        }
    }
}
