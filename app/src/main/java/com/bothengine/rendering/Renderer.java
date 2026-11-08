package com.bothengine.rendering;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import com.bothengine.core.Engine;
import com.bothengine.core.EngineConfig;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Renderer
 * Handles OpenGL rendering for the game engine
 */
public class Renderer implements GLSurfaceView.Renderer {
    private static final String TAG = "Renderer";
    
    private final EngineConfig config;
    private Engine engine;
    private long lastFrameTime;
    private float deltaTime;
    private float fixedDeltaTime = 1.0f / 60.0f;
    private float accumulator;
    private boolean gameRunning = true;
    
    public Renderer(EngineConfig config) {
        this.config = config;
        Log.i(TAG, "Renderer created");
    }
    
    public void setEngine(Engine engine) {
        this.engine = engine;
        Log.i(TAG, "Engine set in Renderer");
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        try {
            Log.i(TAG, "Surface created");
            
            // Initialize OpenGL
            GLES20Helper.initialize(gl);
            
            // Clear screen
            clearScreen(gl);
            
            // Initialize engine if not already running
            if (engine != null && !engine.isRunning()) {
                engine.start();
                gameRunning = true;
                lastFrameTime = System.nanoTime();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onSurfaceCreated: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        try {
            Log.i(TAG, "Surface changed: " + width + "x" + height);
            
            // Set viewport
            gl.glViewport(0, 0, width, height);
            
            // Update engine render manager viewport
            if (engine != null && engine.getRenderManager() != null) {
                engine.getRenderManager().setViewportSize(width, height);
            }
            
            // Update camera aspect ratio
            if (engine != null && engine.getSceneManager() != null) {
                // Would update camera aspect ratio
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onSurfaceChanged: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        try {
            if (!gameRunning || engine == null) return;
            
            // Calculate delta time
            long currentTime = System.nanoTime();
            long elapsedNanos = currentTime - lastFrameTime;
            deltaTime = elapsedNanos / 1_000_000_000f; // Convert to seconds
            
            // Cap delta time to prevent spiral of death
            if (deltaTime > 0.1f) {
                deltaTime = 0.1f;
            }
            
            lastFrameTime = currentTime;
            
            // Fixed update loop
            accumulator += deltaTime;
            while (accumulator >= fixedDeltaTime) {
                if (engine != null) {
                    engine.fixedUpdate(fixedDeltaTime);
                }
                accumulator -= fixedDeltaTime;
            }
            
            // Clear screen
            clearScreen(gl);
            
            // Variable update and render
            if (engine != null) {
                engine.update();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onDrawFrame: " + e.getMessage(), e);
            gameRunning = false;
        }
    }
    
    /**
     * Clear the screen
     */
    private void clearScreen(GL10 gl) {
        try {
            gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        } catch (Exception e) {
            Log.e(TAG, "Error clearing screen: " + e.getMessage(), e);
        }
    }
    
    /**
     * Release renderer state.
     */
    public void shutdown() {
        gameRunning = false;
        engine = null;
        Log.i(TAG, "Renderer shutdown");
    }

    /**
     * Clear the colour/depth buffers of the current surface.
     */
    public void clearScreen() {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Advance and render one frame from outside onDrawFrame.
     */
    public void render(float deltaTime) {
        if (engine != null) {
            engine.update(deltaTime);
        }
    }

    public void setGameRunning(boolean running) {
        this.gameRunning = running;
    }
    
    public void pause() {
        gameRunning = false;
        if (engine != null) {
            engine.pause();
        }
    }
    
    public void resume() {
        gameRunning = true;
        lastFrameTime = System.nanoTime();
        if (engine != null) {
            engine.resume();
        }
    }
}
