package com.bothengine.systems;

import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;
import com.bothengine.components.MeshRendererComponent;
import com.bothengine.components.SpriteRendererComponent;
import com.bothengine.rendering.Camera;
import com.bothengine.rendering.MeshRenderer;
import com.bothengine.rendering.Renderer2D;
import com.bothengine.rendering.Renderer3D;
import com.bothengine.rendering.shaders.ShaderManager;

import java.util.List;

/**
 * RenderSystem
 * Handles rendering for all entities with renderable components
 */
public class RenderSystem {
    private static final String TAG = "RenderSystem";
    
    private final EngineConfig config;
    private EntityManager entityManager;
    private ShaderManager shaderManager;
    
    private Renderer2D renderer2D;
    private Renderer3D renderer3D;
    private GLSurfaceView glSurfaceView;
    private Camera mainCamera;
    
    private boolean enabled;
    private boolean rendering2D;
    private boolean rendering3D;
    
    public RenderSystem(EngineConfig config) {
        this.config = config;
        this.entityManager = null;
        this.shaderManager = null;
        this.renderer2D = null;
        this.renderer3D = null;
        this.glSurfaceView = null;
        this.mainCamera = null;
        this.enabled = true;
        this.rendering2D = true;
        this.rendering3D = true;
        
        Log.i(TAG, "RenderSystem initialized");
    }
    
    /**
     * Initialize the system
     */
    public void initialize(EntityManager entityManager, ShaderManager shaderManager, GLSurfaceView glSurfaceView) {
        this.entityManager = entityManager;
        this.shaderManager = shaderManager;
        this.glSurfaceView = glSurfaceView;
        
        // Initialize renderers
        this.renderer2D = new Renderer2D(config, shaderManager);
        this.renderer3D = new Renderer3D(config, shaderManager);
        
        // Initialize renderers
        renderer2D.initialize();
        
        // Create default camera
        this.mainCamera = new Camera(Camera.CameraType.PERSPECTIVE);
        renderer3D.initialize(mainCamera);
        
        Log.i(TAG, "RenderSystem initialized with renderers");
    }
    
    /**
     * Set the main camera
     */
    public void setMainCamera(Camera camera) {
        this.mainCamera = camera;
        if (renderer3D != null) {
            renderer3D.setCamera(camera);
        }
    }
    
    /**
     * Render all entities
     */
    public void render(float deltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Clear screen
        clearScreen();
        
        // Render 3D objects
        if (rendering3D) {
            render3D(deltaTime);
        }
        
        // Render 2D objects
        if (rendering2D) {
            render2D(deltaTime);
        }
    }
    
    /**
     * Clear the screen
     */
    private void clearScreen() {
        // This would be handled by the GLSurfaceView renderer
    }
    
    /**
     * Render 3D objects
     */
    private void render3D(float deltaTime) {
        if (renderer3D == null) return;
        
        // Get all entities with MeshRendererComponent
        List<Entity> entities = entityManager.getEntitiesWithComponent(MeshRendererComponent.class);
        
        for (Entity entity : entities) {
            if (entity.isVisible()) {
                MeshRendererComponent component = entity.getComponent(MeshRendererComponent.class);
                if (component != null && component.isEnabled()) {
                    // Get the mesh renderer
                    MeshRenderer renderer = component.getMeshRenderer();
                    if (renderer != null && renderer.isVisible()) {
                        // Set up for rendering
                        renderer3D.renderMesh(
                            renderer.getMesh(),
                            component.getTransformMatrix(),
                            renderer.getMaterial()
                        );
                    }
                }
            }
        }
        
        // Render the scene
        renderer3D.render(deltaTime);
    }
    
    /**
     * Render 2D objects
     */
    private void render2D(float deltaTime) {
        if (renderer2D == null) return;
        
        // Get all entities with SpriteRendererComponent
        List<Entity> entities = entityManager.getEntitiesWithComponent(SpriteRendererComponent.class);
        
        // Clear sprites from previous frame
        renderer2D.clearSprites();
        
        // Add sprites to renderer
        for (Entity entity : entities) {
            if (entity.isVisible()) {
                SpriteRendererComponent component = entity.getComponent(SpriteRendererComponent.class);
                if (component != null && component.isEnabled()) {
                    renderer2D.addSprite(component.getSprite());
                }
            }
        }
        
        // Render all 2D objects
        renderer2D.render(deltaTime);
    }
    
    /**
     * Shutdown the system
     */
    public void shutdown() {
        if (renderer2D != null) {
            renderer2D.shutdown();
            renderer2D = null;
        }
        
        if (renderer3D != null) {
            renderer3D.shutdown();
            renderer3D = null;
        }
        
        entityManager = null;
        shaderManager = null;
        glSurfaceView = null;
        mainCamera = null;
        enabled = false;
        
        Log.i(TAG, "RenderSystem shutdown");
    }
    
    // Getters and setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isRendering2D() {
        return rendering2D;
    }
    
    public void setRendering2D(boolean rendering2D) {
        this.rendering2D = rendering2D;
    }
    
    public boolean isRendering3D() {
        return rendering3D;
    }
    
    public void setRendering3D(boolean rendering3D) {
        this.rendering3D = rendering3D;
    }
    
    public Renderer2D getRenderer2D() {
        return renderer2D;
    }
    
    public Renderer3D getRenderer3D() {
        return renderer3D;
    }
}
