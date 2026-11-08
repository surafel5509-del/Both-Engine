package com.bothengine.editor;

import com.bothengine.core.Engine;
import com.bothengine.rendering.Camera;
import com.bothengine.rendering.Color;

/**
 * GameView
 * Displays the game view (what the player sees)
 * Similar to Unity's Game window
 */
public class GameView extends EditorWindow {
    private Engine engine;
    private Camera gameCamera;
    private boolean showStats;
    private boolean showGizmos;
    private Color backgroundColor;
    
    // Viewport settings
    private int viewportX, viewportY, viewportWidth, viewportHeight;
    private float aspectRatio;
    
    // Stats
    private int fps;
    private float frameTime;
    private int triangleCount;
    private int drawCalls;
    
    public GameView() {
        super("Game");
        this.engine = null;
        this.gameCamera = null;
        this.showStats = true;
        this.showGizmos = true;
        this.backgroundColor = new Color(0f, 0f, 0f, 1f);
        this.viewportX = 0;
        this.viewportY = 0;
        this.viewportWidth = 800;
        this.viewportHeight = 600;
        this.aspectRatio = 16f / 9f;
        this.fps = 0;
        this.frameTime = 0f;
        this.triangleCount = 0;
        this.drawCalls = 0;
    }
    
    /**
     * Initialize the game view
     */
    @Override
    public void onInitialize() {
        super.onInitialize();
        
        if (getEditorManager() != null) {
            this.engine = getEditorManager().getEngine();
        }
        
        // Calculate aspect ratio
        if (viewportHeight > 0) {
            aspectRatio = (float) viewportWidth / viewportHeight;
        }
    }
    
    /**
     * Update the game view
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Update game camera if available
        if (engine != null && engine.isPlaying()) {
            // In play mode, use the active game camera
            if (engine.getSceneManager() != null && engine.getSceneManager().getCurrentScene() != null) {
                gameCamera = engine.getSceneManager().getCurrentScene().getMainCamera();
            }
        } else {
            // In edit mode, use the editor camera
            if (getEditorManager() != null && getEditorManager().getEditorCamera() != null) {
                gameCamera = getEditorManager().getEditorCamera().getCamera();
            }
        }
        
        // Update viewport
        updateViewport();
    }
    
    /**
     * Render the game view
     */
    @Override
    public void onRender() {
        super.onRender();
        
        if (engine == null) return;
        
        // Set viewport
        setupViewport();
        
        // Clear screen
        clearScreen();
        
        // Render game
        renderGame();
        
        // Render gizmos if enabled
        if (showGizmos) {
            renderGizmos();
        }
        
        // Render stats if enabled
        if (showStats) {
            renderStats();
        }
        
        // Restore viewport
        restoreViewport();
    }
    
    /**
     * Setup the viewport
     */
    private void setupViewport() {
        // Set OpenGL viewport
        // glViewport(viewportX, viewportY, viewportWidth, viewportHeight);
    }
    
    /**
     * Clear the screen
     */
    private void clearScreen() {
        // Clear color and depth buffers
        // glClearColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a);
        // glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * Render the game
     */
    private void renderGame() {
        if (engine == null) return;
        
        // Render the game using the engine's render system
        engine.render();
    }
    
    /**
     * Render gizmos
     */
    private void renderGizmos() {
        // Render selection outlines
        // Render transform gizmos
        // Render physics debug
    }
    
    /**
     * Render stats overlay
     */
    private void renderStats() {
        // Render FPS, frame time, triangle count, draw calls
    }
    
    /**
     * Update viewport dimensions
     */
    private void updateViewport() {
        if (viewportHeight > 0) {
            aspectRatio = (float) viewportWidth / viewportHeight;
        }
    }
    
    /**
     * Restore the viewport
     */
    private void restoreViewport() {
        // Restore default viewport
    }
    
    /**
     * Set the viewport size
     */
    public void setViewport(int x, int y, int width, int height) {
        this.viewportX = x;
        this.viewportY = y;
        this.viewportWidth = width;
        this.viewportHeight = height;
        updateViewport();
    }
    
    /**
     * Update stats
     */
    public void updateStats(int fps, float frameTime, int triangleCount, int drawCalls) {
        this.fps = fps;
        this.frameTime = frameTime;
        this.triangleCount = triangleCount;
        this.drawCalls = drawCalls;
    }
    
    /**
     * Get the game camera
     */
    public Camera getGameCamera() {
        return gameCamera;
    }
    
    /**
     * Set the game camera
     */
    public void setGameCamera(Camera gameCamera) {
        this.gameCamera = gameCamera;
    }
    
    // Getters and setters
    
    public Engine getEngine() {
        return engine;
    }
    
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    
    public boolean isShowStats() {
        return showStats;
    }
    
    public void setShowStats(boolean showStats) {
        this.showStats = showStats;
    }
    
    public boolean isShowGizmos() {
        return showGizmos;
    }
    
    public void setShowGizmos(boolean showGizmos) {
        this.showGizmos = showGizmos;
    }
    
    public Color getBackgroundColor() {
        return backgroundColor;
    }
    
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }
    
    public int getViewportX() {
        return viewportX;
    }
    
    public int getViewportY() {
        return viewportY;
    }
    
    public int getViewportWidth() {
        return viewportWidth;
    }
    
    public int getViewportHeight() {
        return viewportHeight;
    }
    
    public float getAspectRatio() {
        return aspectRatio;
    }
    
    public int getFps() {
        return fps;
    }
    
    public float getFrameTime() {
        return frameTime;
    }
    
    public int getTriangleCount() {
        return triangleCount;
    }
    
    public int getDrawCalls() {
        return drawCalls;
    }
    
    @Override
    public String toString() {
        return "GameView{" +
                "title='" + getTitle() + '\'' +
                ", viewportWidth=" + viewportWidth +
                ", viewportHeight=" + viewportHeight +
                ", aspectRatio=" + aspectRatio +
                ", fps=" + fps +
                '}';
    }
}
