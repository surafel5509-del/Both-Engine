package com.bothengine.editor;

import android.util.Log;

import com.bothengine.core.Engine;
import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;
import com.bothengine.scene.Scene;
import com.bothengine.scene.SceneManager;

import java.util.ArrayList;
import java.util.List;

/**
 * EditorManager
 * Main editor controller that manages all editor windows and tools
 * Similar to Unity's EditorApplication
 */
public class EditorManager {
    private static final String TAG = "EditorManager";
    
    private static EditorManager instance;
    
    private Engine engine;
    private SceneManager sceneManager;
    private EntityManager entityManager;
    
    private List<EditorWindow> windows;
    private EditorWindow focusedWindow;
    private EditorTool currentTool;
    
    private boolean isPlaying;
    private boolean isPaused;
    private boolean isEditing;
    
    private EditorCamera editorCamera;
    private EditorSettings settings;
    
    private EditorManager() {
        this.windows = new ArrayList<>();
        this.focusedWindow = null;
        this.currentTool = EditorTool.SELECT;
        this.isPlaying = false;
        this.isPaused = false;
        this.isEditing = true;
        this.editorCamera = null;
        this.settings = new EditorSettings();
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized EditorManager getInstance() {
        if (instance == null) {
            instance = new EditorManager();
        }
        return instance;
    }
    
    /**
     * Initialize the editor manager
     */
    public void initialize(Engine engine) {
        this.engine = engine;
        this.sceneManager = engine.getSceneManager();
        this.entityManager = engine.getSceneManager().getEntityManager();
        
        // Create default editor camera
        this.editorCamera = new EditorCamera();
        this.editorCamera.initialize();
        
        Log.i(TAG, "EditorManager initialized");
    }
    
    /**
     * Update the editor
     */
    public void update(float deltaTime) {
        if (!isEditing) return;
        
        // Update all editor windows
        for (EditorWindow window : windows) {
            if (window.isVisible()) {
                window.update(deltaTime);
            }
        }
        
        // Update editor camera
        if (editorCamera != null) {
            editorCamera.update(deltaTime);
        }
        
        // Update current tool
        if (currentTool != null) {
            currentTool.update(deltaTime);
        }
    }
    
    /**
     * Render the editor UI
     */
    public void render() {
        if (!isEditing) return;
        
        // Render all editor windows
        for (EditorWindow window : windows) {
            if (window.isVisible()) {
                window.render();
            }
        }
    }
    
    /**
     * Add an editor window
     */
    public void addWindow(EditorWindow window) {
        if (!windows.contains(window)) {
            windows.add(window);
            window.setEditorManager(this);
        }
    }
    
    /**
     * Remove an editor window
     */
    public void removeWindow(EditorWindow window) {
        windows.remove(window);
        
        if (focusedWindow == window) {
            focusedWindow = null;
        }
    }
    
    /**
     * Get a window by type
     */
    @SuppressWarnings("unchecked")
    public <T extends EditorWindow> T getWindow(Class<T> windowClass) {
        for (EditorWindow window : windows) {
            if (windowClass.isInstance(window)) {
                return (T) window;
            }
        }
        return null;
    }
    
    /**
     * Set the focused window
     */
    public void setFocusedWindow(EditorWindow window) {
        if (focusedWindow != null) {
            focusedWindow.onFocusLost();
        }
        
        focusedWindow = window;
        
        if (focusedWindow != null) {
            focusedWindow.onFocusGained();
        }
    }
    
    /**
     * Set the current tool
     */
    public void setCurrentTool(EditorTool.Type tool) {
        if (currentTool != null) {
            currentTool.onDisable();
        }

        currentTool = createTool(tool);

        if (currentTool != null) {
            currentTool.onEnable();
        }
    }

    /**
     * Instantiate the concrete tool implementation for a tool type.
     */
    private static EditorTool createTool(EditorTool.Type type) {
        if (type == null) return null;
        switch (type) {
            case MOVE:
                return new EditorTool.MoveTool();
            case ROTATE:
                return new EditorTool.RotateTool();
            case SCALE:
                return new EditorTool.ScaleTool();
            case SELECT:
            default:
                return new EditorTool.SelectTool();
        }
    }
    
    /**
     * Enter play mode
     */
    public void enterPlayMode() {
        if (isPlaying) return;
        
        isPlaying = true;
        isPaused = false;
        isEditing = false;
        
        // Save current scene state
        Scene currentScene = sceneManager.getCurrentScene();
        if (currentScene != null) {
            currentScene.saveState();
        }
        
        Log.i(TAG, "Entered play mode");
    }
    
    /**
     * Exit play mode
     */
    public void exitPlayMode() {
        if (!isPlaying) return;
        
        isPlaying = false;
        isPaused = false;
        isEditing = true;
        
        // Restore scene state
        Scene currentScene = sceneManager.getCurrentScene();
        if (currentScene != null) {
            currentScene.restoreState();
        }
        
        Log.i(TAG, "Exited play mode");
    }
    
    /**
     * Toggle play/pause
     */
    public void togglePlayPause() {
        if (isPlaying) {
            isPaused = !isPaused;
        } else {
            enterPlayMode();
        }
    }
    
    /**
     * Step through the game one frame
     */
    public void step() {
        if (!isPlaying) {
            // Enter play mode temporarily for one frame
            boolean wasEditing = isEditing;
            isEditing = false;
            
            // Update game
            if (engine != null) {
                engine.update(1.0f / 60.0f); // Fixed timestep
            }
            
            isEditing = wasEditing;
        }
    }
    
    /**
     * Select an entity
     */
    public void selectEntity(Entity entity) {
        // Notify all windows that selection changed
        for (EditorWindow window : windows) {
            window.onSelectionChanged(entity);
        }
    }
    
    /**
     * Select multiple entities
     */
    public void selectEntities(List<Entity> entities) {
        for (EditorWindow window : windows) {
            window.onSelectionChanged(entities);
        }
    }
    
    /**
     * Clear selection
     */
    public void clearSelection() {
        for (EditorWindow window : windows) {
            window.onSelectionChanged((Entity) null);
        }
    }
    
    /**
     * Create a new entity
     */
    public Entity createEntity(String name) {
        if (entityManager == null) return null;
        
        Entity entity = entityManager.createEntity(name);
        selectEntity(entity);
        return entity;
    }
    
    /**
     * Delete selected entities
     */
    public void deleteSelected() {
        // This would be implemented by each window
    }
    
    /**
     * Undo the last action
     */
    public void undo() {
        // Implement undo/redo system
    }
    
    /**
     * Redo the last undone action
     */
    public void redo() {
        // Implement undo/redo system
    }
    
    /**
     * Save the current scene
     */
    public void saveScene() {
        if (sceneManager != null) {
            Scene currentScene = sceneManager.getCurrentScene();
            if (currentScene != null) {
                // Save scene to file
            }
        }
    }
    
    /**
     * Load a scene
     */
    public void loadScene(String sceneName) {
        if (sceneManager != null) {
            sceneManager.loadScene(sceneName);
        }
    }
    
    // Getters and setters
    
    /**
     * Input state used by the editor windows (may be null before initialize()).
     */
    public com.bothengine.input.InputManager getInput() {
        return engine != null ? engine.getInputManager() : null;
    }

    public Engine getEngine() {
        return engine;
    }
    
    public SceneManager getSceneManager() {
        return sceneManager;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public List<EditorWindow> getWindows() {
        return new ArrayList<>(windows);
    }
    
    public EditorWindow getFocusedWindow() {
        return focusedWindow;
    }
    
    public EditorTool getCurrentTool() {
        return currentTool;
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public boolean isPaused() {
        return isPaused;
    }
    
    public boolean isEditing() {
        return isEditing;
    }
    
    public EditorCamera getEditorCamera() {
        return editorCamera;
    }
    
    public EditorSettings getSettings() {
        return settings;
    }
    
    public void setSettings(EditorSettings settings) {
        this.settings = settings;
    }
    
    /**
     * Shutdown the editor manager
     */
    public void shutdown() {
        // Cleanup all windows
        for (EditorWindow window : windows) {
            window.shutdown();
        }
        windows.clear();
        
        // Cleanup editor camera
        if (editorCamera != null) {
            editorCamera.shutdown();
            editorCamera = null;
        }
        
        focusedWindow = null;
        currentTool = null;
        engine = null;
        sceneManager = null;
        entityManager = null;
        
        Log.i(TAG, "EditorManager shutdown");
    }
}
