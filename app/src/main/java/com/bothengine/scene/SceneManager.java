package com.bothengine.scene;

import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SceneManager
 * Manages all scenes and scene transitions in the game
 */
public class SceneManager {
    private static final String TAG = "SceneManager";
    
    private final EngineConfig config;
    private final EntityManager entityManager;
    
    private Map<String, Scene> scenes;
    private Scene currentScene;
    private Scene nextScene;
    private boolean isTransitioning;
    private float transitionProgress;
    private float transitionDuration;
    private SceneTransition transition;
    
    public SceneManager(EngineConfig config) {
        this.config = config;
        this.entityManager = new EntityManager();
        this.scenes = new HashMap<>();
        this.currentScene = null;
        this.nextScene = null;
        this.isTransitioning = false;
        this.transitionProgress = 0;
        this.transitionDuration = 1.0f;
        this.transition = null;
        
        Log.i(TAG, "SceneManager initialized");
    }
    
    /**
     * Initialize the scene manager
     */
    public void initialize() {
        // Create default scene
        Scene defaultScene = new Scene("Default");
        addScene(defaultScene);
        setCurrentScene("Default");
    }
    
    /**
     * Add a scene
     */
    public void addScene(Scene scene) {
        if (scene == null) return;
        
        String name = scene.getName();
        if (scenes.containsKey(name)) {
            Log.w(TAG, "Scene already exists: " + name);
            return;
        }
        
        scenes.put(name, scene);
        scene.setSceneManager(this);
        scene.setEntityManager(entityManager);
        
        Log.d(TAG, "Scene added: " + name);
    }
    
    /**
     * Remove a scene
     */
    public void removeScene(String name) {
        Scene scene = scenes.remove(name);
        if (scene != null) {
            scene.shutdown();
            Log.d(TAG, "Scene removed: " + name);
        }
    }
    
    /**
     * Get a scene by name
     */
    public Scene getScene(String name) {
        return scenes.get(name);
    }
    
    /**
     * Set the current scene
     */
    public void setCurrentScene(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            Log.e(TAG, "Scene not found: " + name);
            return;
        }
        
        if (currentScene != null) {
            currentScene.onExit();
        }
        
        currentScene = scene;
        currentScene.onEnter();
        
        Log.i(TAG, "Current scene set: " + name);
    }
    
    /**
     * Load a scene asynchronously
     */
    public void loadScene(String name) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            Log.e(TAG, "Scene not found: " + name);
            return;
        }
        
        if (scene.isLoaded()) {
            setCurrentScene(name);
            return;
        }
        
        // Load scene
        scene.load();
        setCurrentScene(name);
    }
    
    /**
     * Transition to a new scene
     */
    public void transitionToScene(String name, SceneTransition transition, float duration) {
        Scene scene = scenes.get(name);
        if (scene == null) {
            Log.e(TAG, "Scene not found: " + name);
            return;
        }
        
        if (isTransitioning) {
            Log.w(TAG, "Already transitioning");
            return;
        }
        
        nextScene = scene;
        this.transition = transition;
        this.transitionDuration = duration;
        this.transitionProgress = 0;
        this.isTransitioning = true;
        
        if (transition != null) {
            transition.onStart(currentScene, nextScene);
        }
        
        Log.i(TAG, "Transitioning to scene: " + name);
    }
    
    /**
     * Transition to a new scene with default transition
     */
    public void transitionToScene(String name, float duration) {
        transitionToScene(name, new FadeTransition(), duration);
    }
    
    /**
     * Transition to a new scene immediately
     */
    public void transitionToScene(String name) {
        transitionToScene(name, new InstantTransition(), 0);
    }
    
    /**
     * Update the scene manager
     */
    public void update(float deltaTime) {
        if (currentScene != null) {
            currentScene.update(deltaTime);
        }
        
        // Update transition
        if (isTransitioning) {
            transitionProgress += deltaTime;
            
            if (transition != null) {
                transition.onUpdate(currentScene, nextScene, transitionProgress / transitionDuration);
            }
            
            if (transitionProgress >= transitionDuration) {
                completeTransition();
            }
        }
        
        // Update entity manager
        entityManager.processUpdates();
    }
    
    /**
     * Fixed update the scene manager
     */
    public void fixedUpdate(float fixedDeltaTime) {
        if (currentScene != null) {
            currentScene.fixedUpdate(fixedDeltaTime);
        }
        
        entityManager.fixedUpdate(fixedDeltaTime);
    }
    
    /**
     * Complete the scene transition
     */
    private void completeTransition() {
        if (transition != null) {
            transition.onComplete(currentScene, nextScene);
        }
        
        if (currentScene != null) {
            currentScene.onExit();
        }
        
        currentScene = nextScene;
        nextScene = null;
        
        if (currentScene != null) {
            currentScene.onEnter();
        }
        
        isTransitioning = false;
        transitionProgress = 0;
        transition = null;
        
        Log.i(TAG, "Scene transition complete");
    }
    
    /**
     * Render the current scene
     */
    public void render(float deltaTime) {
        if (currentScene != null) {
            currentScene.render(deltaTime);
        }
    }
    
    /**
     * Shutdown the scene manager
     */
    public void shutdown() {
        if (currentScene != null) {
            currentScene.shutdown();
            currentScene = null;
        }
        
        if (nextScene != null) {
            nextScene.shutdown();
            nextScene = null;
        }
        
        for (Scene scene : scenes.values()) {
            scene.shutdown();
        }
        scenes.clear();
        
        entityManager.shutdown();
        
        Log.i(TAG, "SceneManager shutdown");
    }
    
    // Getters and setters
    
    public EngineConfig getConfig() {
        return config;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public Scene getCurrentScene() {
        return currentScene;
    }
    
    public Scene getNextScene() {
        return nextScene;
    }
    
    public boolean isTransitioning() {
        return isTransitioning;
    }
    
    public float getTransitionProgress() {
        return transitionProgress;
    }
    
    public List<Scene> getAllScenes() {
        return new ArrayList<>(scenes.values());
    }
    
    public Entity createEntity(String name) {
        return entityManager.createEntity(name);
    }
    
    public void destroyEntity(Entity entity) {
        entityManager.destroyEntity(entity);
    }
    
    @Override
    public String toString() {
        return "SceneManager{" +
                "currentScene=" + (currentScene != null ? currentScene.getName() : "null") +
                ", scenes=" + scenes.size() +
                ", isTransitioning=" + isTransitioning +
                '}';
    }
}
