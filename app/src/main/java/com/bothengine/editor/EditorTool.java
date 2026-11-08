package com.bothengine.editor;

/**
 * EditorTool
 * Base class for all editor tools
 * Similar to Unity's Tool class
 */
public abstract class EditorTool {
    public static final int SELECT = 0;
    public static final int MOVE = 1;
    public static final int ROTATE = 2;
    public static final int SCALE = 3;
    public static final int RECTANGLE = 4;
    public static final int CIRCLE = 5;
    public static final int BRUSH = 6;
    
    private String name;
    private int iconId;
    private boolean enabled;
    
    public EditorTool(String name) {
        this.name = name;
        this.iconId = 0;
        this.enabled = false;
    }
    
    /**
     * Called when tool is enabled
     */
    public void onEnable() {
        enabled = true;
    }
    
    /**
     * Called when tool is disabled
     */
    public void onDisable() {
        enabled = false;
    }
    
    /**
     * Update the tool
     */
    public void update(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Called when mouse/touch down
     */
    public void onPointerDown(float x, float y) {
        // Override in subclasses
    }
    
    /**
     * Called when mouse/touch move
     */
    public void onPointerMove(float x, float y) {
        // Override in subclasses
    }
    
    /**
     * Called when mouse/touch up
     */
    public void onPointerUp(float x, float y) {
        // Override in subclasses
    }
    
    /**
     * Called when mouse/touch drag
     */
    public void onPointerDrag(float x, float y, float deltaX, float deltaY) {
        // Override in subclasses
    }
    
    /**
     * Render the tool gizmos
     */
    public void renderGizmos() {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getIconId() {
        return iconId;
    }
    
    public void setIconId(int iconId) {
        this.iconId = iconId;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "EditorTool{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                '}';
    }
    
    // Concrete tool implementations
    
    /**
     * Select tool for selecting entities
     */
    public static class SelectTool extends EditorTool {
        public SelectTool() {
            super("Select");
        }
        
        @Override
        public void onEnable() {
            super.onEnable();
        }
        
        @Override
        public void onPointerDown(float x, float y) {
            // Perform selection at position (x, y)
        }
    }
    
    /**
     * Move tool for translating entities
     */
    public static class MoveTool extends EditorTool {
        public MoveTool() {
            super("Move");
        }
        
        @Override
        public void onEnable() {
            super.onEnable();
        }
        
        @Override
        public void onPointerDrag(float x, float y, float deltaX, float deltaY) {
            // Move selected entities by delta
        }
        
        @Override
        public void renderGizmos() {
            // Render move gizmo
        }
    }
    
    /**
     * Rotate tool for rotating entities
     */
    public static class RotateTool extends EditorTool {
        public RotateTool() {
            super("Rotate");
        }
        
        @Override
        public void onEnable() {
            super.onEnable();
        }
        
        @Override
        public void onPointerDrag(float x, float y, float deltaX, float deltaY) {
            // Rotate selected entities by delta
        }
        
        @Override
        public void renderGizmos() {
            // Render rotate gizmo
        }
    }
    
    /**
     * Scale tool for scaling entities
     */
    public static class ScaleTool extends EditorTool {
        public ScaleTool() {
            super("Scale");
        }
        
        @Override
        public void onEnable() {
            super.onEnable();
        }
        
        @Override
        public void onPointerDrag(float x, float y, float deltaX, float deltaY) {
            // Scale selected entities by delta
        }
        
        @Override
        public void renderGizmos() {
            // Render scale gizmo
        }
    }
}
