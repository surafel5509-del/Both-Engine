package com.bothengine.editor;

import com.bothengine.core.Engine;
import com.bothengine.core.ecs.Entity;
import com.bothengine.rendering.Camera;
import com.bothengine.rendering.Color;
import com.bothengine.rendering.Matrix4;
import com.bothengine.rendering.Mesh;
import com.bothengine.rendering.PrimitiveMesh;
import com.bothengine.rendering.Vector3;

import java.util.List;

/**
 * SceneView
 * Displays the scene from the editor camera perspective
 * Similar to Unity's Scene window
 */
public class SceneView extends EditorWindow {
    private Engine engine;
    private EditorCamera editorCamera;
    private boolean showGrid;
    private boolean showGizmos;
    private Color backgroundColor;
    
    // Viewport settings
    private int viewportX, viewportY, viewportWidth, viewportHeight;
    private float aspectRatio;
    
    // Grid settings
    private float gridSize;
    private int gridSubdivisions;
    private Color gridColor;
    private Color gridSubdivisionColor;
    
    // Selection
    private Entity selectedEntity;
    private List<Entity> selectedEntities;
    
    // Mesh for grid rendering
    private Mesh gridMesh;
    
    public SceneView() {
        super("Scene");
        this.engine = null;
        this.editorCamera = null;
        this.showGrid = true;
        this.showGizmos = true;
        this.backgroundColor = new Color(0.18f, 0.18f, 0.18f, 1f);
        this.viewportX = 0;
        this.viewportY = 0;
        this.viewportWidth = 800;
        this.viewportHeight = 600;
        this.aspectRatio = 16f / 9f;
        this.gridSize = 1f;
        this.gridSubdivisions = 10;
        this.gridColor = new Color(0.5f, 0.5f, 0.5f, 0.5f);
        this.gridSubdivisionColor = new Color(0.3f, 0.3f, 0.3f, 0.3f);
        this.selectedEntity = null;
        this.gridMesh = null;
    }
    
    /**
     * Initialize the scene view
     */
    @Override
    public void onInitialize() {
        super.onInitialize();
        
        if (getEditorManager() != null) {
            this.engine = getEditorManager().getEngine();
            this.editorCamera = getEditorManager().getEditorCamera();
        }
        
        // Calculate aspect ratio
        if (viewportHeight > 0) {
            aspectRatio = (float) viewportWidth / viewportHeight;
        }
        
        // Create grid mesh
        createGridMesh();
    }
    
    /**
     * Create the grid mesh
     */
    private void createGridMesh() {
        int gridSizeInt = (int) Math.ceil(gridSize);
        int halfSize = gridSizeInt / 2;
        
        // Create grid lines
        int vertexCount = (halfSize * 2 + 1) * 2 * 2; // X and Z lines
        float[] vertices = new float[vertexCount * 3];
        
        int index = 0;
        
        // X lines (red)
        for (int z = -halfSize; z <= halfSize; z++) {
            vertices[index++] = -halfSize;
            vertices[index++] = 0;
            vertices[index++] = z;
            
            vertices[index++] = halfSize;
            vertices[index++] = 0;
            vertices[index++] = z;
        }
        
        // Z lines (blue)
        for (int x = -halfSize; x <= halfSize; x++) {
            vertices[index++] = x;
            vertices[index++] = 0;
            vertices[index++] = -halfSize;
            
            vertices[index++] = x;
            vertices[index++] = 0;
            vertices[index++] = halfSize;
        }
        
        gridMesh = PrimitiveMesh.createLines("Grid", vertices, null);
    }
    
    /**
     * Update the scene view
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Update editor camera
        if (editorCamera != null) {
            editorCamera.update(deltaTime);
        }
        
        // Update viewport
        updateViewport();
    }
    
    /**
     * Render the scene view
     */
    @Override
    public void onRender() {
        super.onRender();
        
        if (editorCamera == null) return;
        
        // Set viewport
        setupViewport();
        
        // Clear screen
        clearScreen();
        
        // Setup camera
        setupCamera();
        
        // Render grid
        if (showGrid) {
            renderGrid();
        }
        
        // Render scene
        renderScene();
        
        // Render gizmos
        if (showGizmos) {
            renderGizmos();
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
     * Setup the camera
     */
    private void setupCamera() {
        Camera camera = editorCamera.getCamera();
        if (camera == null) return;
        
        // Set camera matrices
        // Use camera.getViewMatrix() and camera.getProjectionMatrix()
    }
    
    /**
     * Render the grid
     */
    private void renderGrid() {
        if (gridMesh == null) return;
        
        // Save current matrix
        // Matrix4 currentMatrix = getCurrentMatrix();
        
        // Scale grid to match grid size
        Matrix4 scaleMatrix = Matrix4.createIdentity();
        scaleMatrix.scale(new Vector3(gridSize, 1f, gridSize));
        
        // Set grid color
        // Set shader color to gridColor
        
        // Render grid mesh
        // gridMesh.render();
        
        // Render subdivision lines (smaller grid)
        if (gridSubdivisions > 0) {
            // Set subdivision color
            // Set shader color to gridSubdivisionColor
            
            // Scale down for subdivisions
            Matrix4 subScaleMatrix = Matrix4.createIdentity();
            subScaleMatrix.scale(new Vector3(gridSize / gridSubdivisions, 1f, gridSize / gridSubdivisions));
            
            // Render subdivision grid
            // gridMesh.render();
        }
        
        // Restore matrix
    }
    
    /**
     * Render the scene
     */
    private void renderScene() {
        if (engine == null) return;
        
        // Render all entities in the scene
        if (engine.getEntityManager() != null) {
            List<Entity> entities = engine.getEntityManager().getAllEntities();
            
            for (Entity entity : entities) {
                if (entity.isVisible()) {
                    renderEntity(entity);
                }
            }
        }
    }
    
    /**
     * Render an entity
     */
    private void renderEntity(Entity entity) {
        // Get mesh renderer component
        // Get sprite renderer component
        // Render based on components
    }
    
    /**
     * Render gizmos
     */
    private void renderGizmos() {
        // Render selection outline
        if (selectedEntity != null) {
            renderSelectionOutline(selectedEntity);
        }
        
        // Render transform gizmos for selected entities
        if (!selectedEntities.isEmpty()) {
            renderTransformGizmos();
        }
        
        // Render camera frustum
        renderCameraFrustums();
        
        // Render light gizmos
        renderLightGizmos();
    }
    
    /**
     * Render selection outline
     */
    private void renderSelectionOutline(Entity entity) {
        // Get entity bounds
        // Render wireframe box around entity
    }
    
    /**
     * Render transform gizmos
     */
    private void renderTransformGizmos() {
        // Get current tool
        EditorTool tool = getEditorManager() != null ? getEditorManager().getCurrentTool() : null;
        
        if (tool == null) return;
        
        // Render appropriate gizmo based on tool
        // For SELECT tool, render selection outline
        // For MOVE tool, render move gizmo
        // For ROTATE tool, render rotate gizmo
        // For SCALE tool, render scale gizmo
    }
    
    /**
     * Render camera frustums
     */
    private void renderCameraFrustums() {
        // Find all camera components and render their frustums
    }
    
    /**
     * Render light gizmos
     */
    private void renderLightGizmos() {
        // Find all light components and render their gizmos
    }
    
    /**
     * Update viewport dimensions
     */
    private void updateViewport() {
        if (viewportHeight > 0) {
            aspectRatio = (float) viewportWidth / viewportHeight;
        }
        
        // Update editor camera aspect ratio
        if (editorCamera != null && editorCamera.getCamera() != null) {
            editorCamera.getCamera().setAspectRatio(aspectRatio);
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
     * Called when selection changes
     */
    @Override
    public void onSelectionChanged(Entity entity) {
        selectedEntity = entity;
    }
    
    /**
     * Called when selection changes (multiple entities)
     */
    @Override
    public void onSelectionChanged(List<Entity> entities) {
        selectedEntities = entities;
        selectedEntity = entities.isEmpty() ? null : entities.get(0);
    }
    
    // Getters and setters
    
    public Engine getEngine() {
        return engine;
    }
    
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
    
    public EditorCamera getEditorCamera() {
        return editorCamera;
    }
    
    public void setEditorCamera(EditorCamera editorCamera) {
        this.editorCamera = editorCamera;
    }
    
    public boolean isShowGrid() {
        return showGrid;
    }
    
    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
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
    
    public float getGridSize() {
        return gridSize;
    }
    
    public void setGridSize(float gridSize) {
        this.gridSize = gridSize;
        createGridMesh();
    }
    
    public int getGridSubdivisions() {
        return gridSubdivisions;
    }
    
    public void setGridSubdivisions(int gridSubdivisions) {
        this.gridSubdivisions = gridSubdivisions;
    }
    
    public Color getGridColor() {
        return gridColor;
    }
    
    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }
    
    public Color getGridSubdivisionColor() {
        return gridSubdivisionColor;
    }
    
    public void setGridSubdivisionColor(Color gridSubdivisionColor) {
        this.gridSubdivisionColor = gridSubdivisionColor;
    }
    
    public Entity getSelectedEntity() {
        return selectedEntity;
    }
    
    public List<Entity> getSelectedEntities() {
        return selectedEntities;
    }
    
    @Override
    public String toString() {
        return "SceneView{" +
                "title='" + getTitle() + '\'' +
                ", viewportWidth=" + viewportWidth +
                ", viewportHeight=" + viewportHeight +
                ", aspectRatio=" + aspectRatio +
                ", showGrid=" + showGrid +
                ", showGizmos=" + showGizmos +
                '}';
    }
}
