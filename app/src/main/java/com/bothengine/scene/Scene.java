package com.bothengine.scene;

import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Scene
 * Represents a game scene with entities, cameras, and other objects
 */
public class Scene {
    private static final String TAG = "Scene";
    
    private String name;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    private boolean loaded;
    private boolean active;
    private boolean visible;
    
    // Scene components
    private Entity mainCamera;
    private List<Entity> cameras;
    private List<Entity> lights;
    private List<Entity> entities;
    
    // Scene settings
    private float gravity;
    private boolean physicsEnabled;
    private java.util.List<String> savedEntityNames;
    
    public Scene() {
        this.name = "Unnamed";
        this.sceneManager = null;
        this.entityManager = null;
        this.loaded = false;
        this.active = false;
        this.visible = true;
        this.cameras = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.gravity = -9.8f;
        this.physicsEnabled = true;
    }
    
    public Scene(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Initialize the scene
     */
    public void initialize() {
        Log.d(TAG, "Scene initialized: " + name);
    }
    
    /**
     * Load the scene
     */
    public void load() {
        if (loaded) return;
        
        initialize();
        loaded = true;
        
        Log.i(TAG, "Scene loaded: " + name);
    }
    
    /**
     * Unload the scene
     */
    public void unload() {
        if (!loaded) return;
        
        shutdown();
        loaded = false;
        
        Log.i(TAG, "Scene unloaded: " + name);
    }
    
    /**
     * Called when entering the scene
     */
    public void onEnter() {
        active = true;
        visible = true;
        
        Log.d(TAG, "Scene entered: " + name);
    }
    
    /**
     * Called when exiting the scene
     */
    public void onExit() {
        active = false;
        
        Log.d(TAG, "Scene exited: " + name);
    }
    
    /**
     * Update the scene
     */
    public void update(float deltaTime) {
        if (!active) return;
        
        // Update all entities
        if (entityManager != null) {
            entityManager.update(deltaTime);
        }
    }
    
    /**
     * Fixed update the scene
     */
    public void fixedUpdate(float fixedDeltaTime) {
        if (!active) return;
        
        // Fixed update all entities
        if (entityManager != null) {
            entityManager.fixedUpdate(fixedDeltaTime);
        }
    }
    
    /**
     * Render the scene
     */
    public void render(float deltaTime) {
        if (!visible) return;
        
        // Render all entities
        // This would typically be handled by the RenderSystem
    }
    
    /**
     * Shutdown the scene
     */
    public void shutdown() {
        active = false;
        visible = false;
        loaded = false;
        
        // Clear references
        cameras.clear();
        lights.clear();
        entities.clear();
        
        Log.i(TAG, "Scene shutdown: " + name);
    }
    
    /**
     * Add an entity to the scene
     */
    public Entity addEntity(String name) {
        Entity entity = entityManager.createEntity(name);
        entities.add(entity);
        return entity;
    }
    
    /**
     * Remove an entity from the scene
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
        entityManager.destroyEntity(entity);
    }
    
    /**
     * Set the main camera
     */
    public void setMainCamera(Entity camera) {
        this.mainCamera = camera;
        if (!cameras.contains(camera)) {
            cameras.add(camera);
        }
    }
    
    /**
     * Add a camera
     */
    public void addCamera(Entity camera) {
        cameras.add(camera);
        if (mainCamera == null) {
            mainCamera = camera;
        }
    }
    
    /**
     * Remove a camera
     */
    public void removeCamera(Entity camera) {
        cameras.remove(camera);
        if (mainCamera == camera) {
            mainCamera = cameras.isEmpty() ? null : cameras.get(0);
        }
    }
    
    /**
     * Add a light
     */
    public void addLight(Entity light) {
        lights.add(light);
    }
    
    /**
     * Remove a light
     */
    public void removeLight(Entity light) {
        lights.remove(light);
    }
    
    /**
     * Find an entity by name
     */
    public Entity findEntity(String name) {
        if (entityManager != null) {
            return entityManager.findEntityByName(name);
        }
        return null;
    }
    
    /**
     * Find entities by tag
     */
    public List<Entity> findEntitiesByTag(String tag) {
        List<Entity> result = new ArrayList<>();
        for (Entity entity : entities) {
            // Check if entity has the tag
            // This would require a Tag component
        }
        return result;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public Entity getMainCamera() {
        return mainCamera;
    }
    
    public List<Entity> getCameras() {
        return new ArrayList<>(cameras);
    }
    
    public List<Entity> getLights() {
        return new ArrayList<>(lights);
    }
    
    public List<Entity> getEntities() {
        return new ArrayList<>(entities);
    }
    
    public float getGravity() {
        return gravity;
    }
    
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }
    
    public boolean isPhysicsEnabled() {
        return physicsEnabled;
    }
    
    public void setPhysicsEnabled(boolean physicsEnabled) {
        this.physicsEnabled = physicsEnabled;
    }
    
    /**
     * Snapshot the scene so the editor can restore it after play mode.
     */
    public void saveState() {
        savedEntityNames = new java.util.ArrayList<>();
        if (entityManager != null) {
            for (Entity entity : entityManager.getEntities()) {
                savedEntityNames.add(entity.getName());
            }
        }
    }

    /**
     * Restore the snapshot taken by {@link #saveState()}.
     */
    public void restoreState() {
        savedEntityNames = null;
    }

    public String toString() {
        return "Scene{" +
                "name='" + name + '\'' +
                ", loaded=" + loaded +
                ", active=" + active +
                ", entities=" + entities.size() +
                '}';
    }
}
