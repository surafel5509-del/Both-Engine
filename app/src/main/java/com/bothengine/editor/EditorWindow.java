package com.bothengine.editor;

import com.bothengine.core.ecs.Entity;

import java.util.List;

/**
 * EditorWindow
 * Base class for all editor windows
 * Similar to Unity's EditorWindow
 */
public abstract class EditorWindow {
    private String title;
    private int x, y, width, height;
    private boolean visible;
    private boolean minimized;
    private boolean docked;
    
    protected EditorManager editorManager;
    
    public EditorWindow(String title) {
        this.title = title;
        this.x = 0;
        this.y = 0;
        this.width = 300;
        this.height = 400;
        this.visible = true;
        this.minimized = false;
        this.docked = false;
        this.editorManager = null;
    }
    
    /**
     * Initialize the window
     */
    public void initialize() {
        onInitialize();
    }
    
    /**
     * Called when window is initialized
     */
    protected void onInitialize() {
        // Override in subclasses
    }
    
    /**
     * Update the window
     */
    public void update(float deltaTime) {
        if (!visible || minimized) return;
        onUpdate(deltaTime);
    }
    
    /**
     * Called when window is updated
     */
    protected void onUpdate(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Render the window
     */
    public void render() {
        if (!visible) return;
        
        if (!minimized) {
            onRender();
        }
    }
    
    /**
     * Called when window is rendered
     */
    protected void onRender() {
        // Override in subclasses
    }
    
    /**
     * Called when window gains focus
     */
    public void onFocusGained() {
        // Override in subclasses
    }
    
    /**
     * Called when window loses focus
     */
    public void onFocusLost() {
        // Override in subclasses
    }
    
    /**
     * Called when selection changes
     */
    public void onSelectionChanged(Entity entity) {
        // Override in subclasses
    }
    
    /**
     * Called when selection changes (multiple entities)
     */
    public void onSelectionChanged(List<Entity> entities) {
        // Override in subclasses
    }
    
    /**
     * Called when an entity is created
     */
    public void onEntityCreated(Entity entity) {
        // Override in subclasses
    }
    
    /**
     * Called when an entity is destroyed
     */
    public void onEntityDestroyed(Entity entity) {
        // Override in subclasses
    }
    
    /**
     * Called when scene changes
     */
    public void onSceneChanged() {
        // Override in subclasses
    }
    
    /**
     * Shutdown the window
     */
    public void shutdown() {
        onShutdown();
    }
    
    /**
     * Called when window is shutdown
     */
    protected void onShutdown() {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public boolean isMinimized() {
        return minimized;
    }
    
    public void setMinimized(boolean minimized) {
        this.minimized = minimized;
    }
    
    public boolean isDocked() {
        return docked;
    }
    
    public void setDocked(boolean docked) {
        this.docked = docked;
    }
    
    public EditorManager getEditorManager() {
        return editorManager;
    }
    
    public void setEditorManager(EditorManager editorManager) {
        this.editorManager = editorManager;
    }
    
    @Override
    public String toString() {
        return "EditorWindow{" +
                "title='" + title + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", visible=" + visible +
                '}';
    }
}
