package com.bothengine.core;

import android.util.Log;

import com.bothengine.assets.AssetManager;
import com.bothengine.input.InputManager;
import com.bothengine.rendering.RenderManager;
import com.bothengine.scene.SceneManager;
import com.bothengine.systems.PhysicsSystem;
import com.bothengine.systems.RenderSystem;
import com.bothengine.systems.UpdateSystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Both Engine Core
 * Main game engine class that manages all subsystems
 */
public class Engine {
    private static final String TAG = "Engine";
    
    private final EngineConfig config;
    private final AssetManager assetManager;
    private final InputManager inputManager;
    private final RenderManager renderManager;
    private final SceneManager sceneManager;
    private final PhysicsSystem physicsSystem;
    private final UpdateSystem updateSystem;
    private final RenderSystem renderSystem;
    
    private boolean isRunning;
    private boolean isPaused;
    private long lastFrameTime;
    private float deltaTime;
    private float totalTime;
    private int frameCount;
    private float fps;
    private long lastFpsUpdate;
    
    private final List<EngineListener> listeners = new ArrayList<>();
    
    public Engine(EngineConfig config) {
        this.config = config;
        this.isRunning = false;
        this.isPaused = false;
        
        Log.i(TAG, "Initializing Both Engine...");
        
        // Initialize subsystems
        this.assetManager = new AssetManager(config);
        this.inputManager = new InputManager(config);
        this.renderManager = new RenderManager(config);
        this.sceneManager = new SceneManager(config);
        this.physicsSystem = new PhysicsSystem(config);
        this.updateSystem = new UpdateSystem(config);
        this.renderSystem = new RenderSystem(config);
        
        // Set engine reference in render manager
        this.renderManager.setEngine(this);
        
        Log.i(TAG, "Both Engine Initialized Successfully");
    }
    
    /**
     * Start the game engine
     */
    public void start() {
        if (isRunning) return;
        
        isRunning = true;
        isPaused = false;
        lastFrameTime = System.nanoTime();
        lastFpsUpdate = lastFrameTime;
        frameCount = 0;
        fps = 0;
        totalTime = 0;
        
        Log.i(TAG, "Engine Started");
        notifyEngineStarted();
    }
    
    /**
     * Stop the game engine
     */
    public void stop() {
        if (!isRunning) return;
        
        isRunning = false;
        Log.i(TAG, "Engine Stopped");
        notifyEngineStopped();
    }
    
    /**
     * Pause the game engine
     */
    public void pause() {
        if (isPaused) return;
        isPaused = true;
        Log.i(TAG, "Engine Paused");
        notifyEnginePaused();
    }
    
    /**
     * Resume the game engine
     */
    public void resume() {
        if (!isPaused) return;
        isPaused = false;
        lastFrameTime = System.nanoTime();
        Log.i(TAG, "Engine Resumed");
        notifyEngineResumed();
    }
    
    /**
     * Shutdown the game engine
     */
    public void shutdown() {
        stop();
        
        // Shutdown subsystems
        renderSystem.shutdown();
        updateSystem.shutdown();
        physicsSystem.shutdown();
        sceneManager.shutdown();
        renderManager.shutdown();
        inputManager.shutdown();
        assetManager.shutdown();
        
        Log.i(TAG, "Engine Shutdown Complete");
        notifyEngineShutdown();
    }
    
    /**
     * Update the engine - called once per frame
     */
    public void update() {
        if (!isRunning || isPaused) return;
        
        long currentTime = System.nanoTime();
        long elapsedNanos = currentTime - lastFrameTime;
        deltaTime = elapsedNanos / 1_000_000_000f; // Convert to seconds
        
        // Cap delta time to prevent spiral of death
        if (deltaTime > 0.1f) {
            deltaTime = 0.1f;
        }
        
        totalTime += deltaTime;
        frameCount++;
        
        // Update FPS counter every second
        if (currentTime - lastFpsUpdate >= 1_000_000_000) {
            fps = frameCount;
            frameCount = 0;
            lastFpsUpdate = currentTime;
        }
        
        lastFrameTime = currentTime;
        
        // Update input
        inputManager.update(deltaTime);
        
        // Update physics
        if (config.isPhysicsEnabled()) {
            physicsSystem.update(deltaTime);
        }
        
        // Update game logic
        updateSystem.update(deltaTime);
        
        // Update scene
        sceneManager.update(deltaTime);
        
        // Render
        renderSystem.render(deltaTime);
        
        // Notify listeners
        notifyFrameUpdated(deltaTime);
    }
    
    /**
     * Fixed update - called at fixed intervals for physics
     */
    public void fixedUpdate(float fixedDeltaTime) {
        if (!isRunning || isPaused) return;
        
        if (config.isPhysicsEnabled()) {
            physicsSystem.fixedUpdate(fixedDeltaTime);
        }
        
        updateSystem.fixedUpdate(fixedDeltaTime);
        sceneManager.fixedUpdate(fixedDeltaTime);
    }
    
    // Getters
    public EngineConfig getConfig() {
        return config;
    }
    
    public AssetManager getAssetManager() {
        return assetManager;
    }
    
    public InputManager getInputManager() {
        return inputManager;
    }
    
    public RenderManager getRenderManager() {
        return renderManager;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }
    
    public UpdateSystem getUpdateSystem() {
        return updateSystem;
    }
    
    public RenderSystem getRenderSystem() {
        return renderSystem;
    }
    
    public boolean isRunning() {
        return isRunning;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public float getDeltaTime() {
        return deltaTime;
    }
    
    public float getTotalTime() {
        return totalTime;
    }
    
    public float getFPS() {
        return fps;
    }
    
    // Listener management
    public void addListener(EngineListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    public void removeListener(EngineListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyEngineStarted() {
        for (EngineListener listener : listeners) {
            listener.onEngineStarted(this);
        }
    }
    
    private void notifyEngineStopped() {
        for (EngineListener listener : listeners) {
            listener.onEngineStopped(this);
        }
    }
    
    private void notifyEnginePaused() {
        for (EngineListener listener : listeners) {
            listener.onEnginePaused(this);
        }
    }
    
    private void notifyEngineResumed() {
        for (EngineListener listener : listeners) {
            listener.onEngineResumed(this);
        }
    }
    
    private void notifyEngineShutdown() {
        for (EngineListener listener : listeners) {
            listener.onEngineShutdown(this);
        }
    }
    
    private void notifyFrameUpdated(float deltaTime) {
        for (EngineListener listener : listeners) {
            listener.onFrameUpdated(this, deltaTime);
        }
    }
    
    /**
     * Engine Listener Interface
     */
    public interface EngineListener {
        default void onEngineStarted(Engine engine) {}
        default void onEngineStopped(Engine engine) {}
        default void onEnginePaused(Engine engine) {}
        default void onEngineResumed(Engine engine) {}
        default void onEngineShutdown(Engine engine) {}
        default void onFrameUpdated(Engine engine, float deltaTime) {}
    }
}
