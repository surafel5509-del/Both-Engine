package com.bothengine.rendering;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bothengine.core.Engine;
import com.bothengine.core.EngineConfig;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * RenderManager
 * Manages all rendering operations for the game engine
 */
public class RenderManager {
    private static final String TAG = "RenderManager";
    
    private final EngineConfig config;
    private final Context context;
    
    private GLSurfaceView glSurfaceView;
    private Renderer renderer;
    private Camera mainCamera;
    private Camera uiCamera;
    private Engine engine;
    
    private int viewportWidth;
    private int viewportHeight;
    private float aspectRatio;
    
    private boolean wireframeMode;
    private boolean cullingEnabled;
    private boolean depthTestingEnabled;
    private boolean blendingEnabled;
    
    public RenderManager(EngineConfig config) {
        this.config = config;
        this.context = config.getContext();
        this.viewportWidth = 1;
        this.viewportHeight = 1;
        this.aspectRatio = 1.0f;
        this.wireframeMode = false;
        this.cullingEnabled = true;
        this.depthTestingEnabled = true;
        this.blendingEnabled = true;
        this.engine = null;
        
        Log.i(TAG, "RenderManager initialized");
    }
    
    /**
     * Set the engine reference
     */
    public void setEngine(Engine engine) {
        this.engine = engine;
        if (renderer != null) {
            renderer.setEngine(engine);
        }
    }
    
    /**
     * Initialize the render manager with a GLSurfaceView
     */
    public void initialize(GLSurfaceView glSurfaceView) {
        try {
            this.glSurfaceView = glSurfaceView;
            
            // Create renderer
            this.renderer = new Renderer(config);
            if (engine != null) {
                renderer.setEngine(engine);
            }
            glSurfaceView.setRenderer(renderer);
            
            // Set render mode
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            
            // Create cameras
            this.mainCamera = new Camera(Camera.CameraType.PERSPECTIVE);
            this.uiCamera = new Camera(Camera.CameraType.ORTHOGRAPHIC);
            
            Log.i(TAG, "RenderManager initialized with GLSurfaceView");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize RenderManager: " + e.getMessage(), e);
        }
    }
    
    /**
     * Set the main camera
     */
    public void setMainCamera(Camera camera) {
        this.mainCamera = camera;
    }
    
    /**
     * Get the main camera
     */
    public Camera getMainCamera() {
        return mainCamera;
    }
    
    /**
     * Get the UI camera
     */
    public Camera getUICamera() {
        return uiCamera;
    }
    
    /**
     * Get the renderer
     */
    public Renderer getRenderer() {
        return renderer;
    }
    
    /**
     * Update viewport size
     */
    public void setViewportSize(int width, int height) {
        this.viewportWidth = width;
        this.viewportHeight = height;
        this.aspectRatio = (float) width / height;
        
        if (mainCamera != null) {
            mainCamera.setAspectRatio(aspectRatio);
        }
        if (uiCamera != null) {
            uiCamera.setAspectRatio(aspectRatio);
        }
        
        Log.d(TAG, "Viewport size updated: " + width + "x" + height);
    }
    
    /**
     * Get viewport width
     */
    public int getViewportWidth() {
        return viewportWidth;
    }
    
    /**
     * Get viewport height
     */
    public int getViewportHeight() {
        return viewportHeight;
    }
    
    /**
     * Get aspect ratio
     */
    public float getAspectRatio() {
        return aspectRatio;
    }
    
    /**
     * Enable or disable wireframe mode
     */
    public void setWireframeMode(boolean enabled) {
        this.wireframeMode = enabled;
    }
    
    /**
     * Check if wireframe mode is enabled
     */
    public boolean isWireframeMode() {
        return wireframeMode;
    }
    
    /**
     * Enable or disable culling
     */
    public void setCullingEnabled(boolean enabled) {
        this.cullingEnabled = enabled;
    }
    
    /**
     * Check if culling is enabled
     */
    public boolean isCullingEnabled() {
        return cullingEnabled;
    }
    
    /**
     * Enable or disable depth testing
     */
    public void setDepthTestingEnabled(boolean enabled) {
        this.depthTestingEnabled = enabled;
    }
    
    /**
     * Check if depth testing is enabled
     */
    public boolean isDepthTestingEnabled() {
        return depthTestingEnabled;
    }
    
    /**
     * Enable or disable blending
     */
    public void setBlendingEnabled(boolean enabled) {
        this.blendingEnabled = enabled;
    }
    
    /**
     * Check if blending is enabled
     */
    public boolean isBlendingEnabled() {
        return blendingEnabled;
    }
    
    /**
     * Clear the screen
     */
    public void clearScreen() {
        if (renderer != null) {
            renderer.clearScreen();
        }
    }
    
    /**
     * Render all objects
     */
    public void render(float deltaTime) {
        if (renderer != null) {
            renderer.render(deltaTime);
        }
    }
    
    /**
     * Shutdown the render manager
     */
    public void shutdown() {
        if (renderer != null) {
            renderer.shutdown();
        }
        mainCamera = null;
        uiCamera = null;
        glSurfaceView = null;
        Log.i(TAG, "RenderManager shutdown");
    }
    
}
