package com.bothengine.performance;

import com.bothengine.core.ecs.Entity;
import com.bothengine.rendering.Mesh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LODSystem
 * Level of Detail system for optimizing rendering performance
 * Switches between different mesh resolutions based on distance from camera
 */
public class LODSystem {
    private static final int DEFAULT_LOD_COUNT = 4;
    
    private Map<Entity, LODGroup> lodGroups;
    private float[] distances;
    
    public LODSystem() {
        this.lodGroups = new HashMap<>();
        this.distances = new float[DEFAULT_LOD_COUNT];
        
        // Default distances (in world units)
        distances[0] = 0f;       // LOD0: 0-50 units
        distances[1] = 50f;      // LOD1: 50-100 units
        distances[2] = 100f;     // LOD2: 100-200 units
        distances[3] = 200f;     // LOD3: 200+ units
    }
    
    /**
     * Create an LOD system with custom distances
     */
    public static LODSystem create(float[] distances) {
        LODSystem system = new LODSystem();
        system.setDistances(distances);
        return system;
    }
    
    /**
     * Update the LOD system
     */
    public void update() {
        for (Map.Entry<Entity, LODGroup> entry : lodGroups.entrySet()) {
            Entity entity = entry.getKey();
            LODGroup group = entry.getValue();
            
            if (entity != null && entity.isActive()) {
                // Calculate distance from camera
                float distance = calculateDistance(entity);
                
                // Update LOD level
                group.updateLOD(distance);
            }
        }
    }
    
    /**
     * Calculate distance from camera to entity
     */
    private float calculateDistance(Entity entity) {
        // Get camera position
        // For now, we'll use a simple distance calculation
        // In a real implementation, we'd use the actual camera position
        return 0f;
    }
    
    /**
     * Add an entity to the LOD system
     */
    public void addEntity(Entity entity, LODGroup lodGroup) {
        lodGroups.put(entity, lodGroup);
    }
    
    /**
     * Remove an entity from the LOD system
     */
    public void removeEntity(Entity entity) {
        LODGroup group = lodGroups.remove(entity);
        if (group != null) {
            group.shutdown();
        }
    }
    
    /**
     * Get the LOD group for an entity
     */
    public LODGroup getLODGroup(Entity entity) {
        return lodGroups.get(entity);
    }
    
    /**
     * Check if an entity is in the LOD system
     */
    public boolean hasEntity(Entity entity) {
        return lodGroups.containsKey(entity);
    }
    
    /**
     * Clear all entities from the LOD system
     */
    public void clear() {
        for (LODGroup group : lodGroups.values()) {
            group.shutdown();
        }
        lodGroups.clear();
    }
    
    // Getters and setters
    
    public float[] getDistances() {
        return distances.clone();
    }
    
    public void setDistances(float[] distances) {
        if (distances != null) {
            this.distances = distances.clone();
        }
    }
    
    public Map<Entity, LODGroup> getLODGroups() {
        return new HashMap<>(lodGroups);
    }
    
    @Override
    public String toString() {
        return "LODSystem{" +
                "lodGroups=" + lodGroups.size() +
                '}';
    }
    
    /**
     * LODGroup class
     * Manages LOD levels for a single entity
     */
    public static class LODGroup {
        private Entity entity;
        private List<LODLevel> levels;
        private int currentLevel;
        
        public LODGroup(Entity entity) {
            this.entity = entity;
            this.levels = new ArrayList<>();
            this.currentLevel = 0;
        }
        
        /**
         * Create an LOD group with levels
         */
        public static LODGroup create(Entity entity, List<LODLevel> levels) {
            LODGroup group = new LODGroup(entity);
            group.setLevels(levels);
            return group;
        }
        
        /**
         * Add a LOD level
         */
        public void addLevel(LODLevel level) {
            levels.add(level);
            
            // Sort levels by distance
            levels.sort((a, b) -> Float.compare(a.getDistance(), b.getDistance()));
        }
        
        /**
         * Set all LOD levels
         */
        public void setLevels(List<LODLevel> levels) {
            this.levels = new ArrayList<>(levels);
            
            // Sort levels by distance
            this.levels.sort((a, b) -> Float.compare(a.getDistance(), b.getDistance()));
        }
        
        /**
         * Update the LOD level based on distance
         */
        public void updateLOD(float distance) {
            int newLevel = 0;
            
            for (int i = 0; i < levels.size(); i++) {
                if (distance >= levels.get(i).getDistance()) {
                    newLevel = i;
                } else {
                    break;
                }
            }
            
            // Clamp to valid range
            newLevel = Math.max(0, Math.min(newLevel, levels.size() - 1));
            
            // Only update if level changed
            if (newLevel != currentLevel) {
                setLevel(newLevel);
            }
        }
        
        /**
         * Set the current LOD level
         */
        public void setLevel(int level) {
            if (level < 0 || level >= levels.size()) return;
            
            // Deactivate current level
            if (currentLevel < levels.size()) {
                levels.get(currentLevel).onDeactivate();
            }
            
            // Activate new level
            currentLevel = level;
            levels.get(currentLevel).onActivate();
        }
        
        /**
         * Get the current LOD level
         */
        public int getCurrentLevel() {
            return currentLevel;
        }
        
        /**
         * Get the current LOD level object
         */
        public LODLevel getCurrentLevelObject() {
            return currentLevel < levels.size() ? levels.get(currentLevel) : null;
        }
        
        /**
         * Shutdown the LOD group
         */
        public void shutdown() {
            for (LODLevel level : levels) {
                level.onDeactivate();
            }
        }
        
        // Getters and setters
        
        public Entity getEntity() {
            return entity;
        }
        
        public void setEntity(Entity entity) {
            this.entity = entity;
        }
        
        public List<LODLevel> getLevels() {
            return new ArrayList<>(levels);
        }
        
        @Override
        public String toString() {
            return "LODGroup{" +
                    "entity=" + (entity != null ? entity.getName() : "null") +
                    ", levels=" + levels.size() +
                    ", currentLevel=" + currentLevel +
                    '}';
        }
    }
    
    /**
     * LODLevel class
     * Represents a single LOD level with a mesh
     */
    public static class LODLevel {
        private String name;
        private Mesh mesh;
        private float distance;
        private boolean active;
        
        public LODLevel(String name, Mesh mesh, float distance) {
            this.name = name;
            this.mesh = mesh;
            this.distance = distance;
            this.active = false;
        }
        
        /**
         * Create an LOD level
         */
        public static LODLevel create(String name, Mesh mesh, float distance) {
            return new LODLevel(name, mesh, distance);
        }
        
        /**
         * Called when this level is activated
         */
        public void onActivate() {
            active = true;
            // Switch to this mesh
        }
        
        /**
         * Called when this level is deactivated
         */
        public void onDeactivate() {
            active = false;
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Mesh getMesh() {
            return mesh;
        }
        
        public void setMesh(Mesh mesh) {
            this.mesh = mesh;
        }
        
        public float getDistance() {
            return distance;
        }
        
        public void setDistance(float distance) {
            this.distance = distance;
        }
        
        public boolean isActive() {
            return active;
        }
        
        @Override
        public String toString() {
            return "LODLevel{" +
                    "name='" + name + '\'' +
                    ", distance=" + distance +
                    ", active=" + active +
                    '}';
        }
    }
}
