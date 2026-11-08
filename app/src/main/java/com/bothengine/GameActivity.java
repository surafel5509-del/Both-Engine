package com.bothengine;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bothengine.core.Engine;
import com.bothengine.core.EngineConfig;
import com.bothengine.core.EngineApplication;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * GameActivity
 * Main activity for the Both Engine game
 */
public class GameActivity extends Activity implements GLSurfaceView.Renderer {
    private static final String TAG = "GameActivity";
    
    private GLSurfaceView glSurfaceView;
    private Engine engine;
    private EngineApplication application;
    
    // Game loop
    private boolean gameRunning;
    private long lastFrameTime;
    private float deltaTime;
    private float fixedDeltaTime;
    private float accumulator;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "GameActivity onCreate");
        
        // Request fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        // Get application and engine
        application = (EngineApplication) getApplication();
        engine = application.getEngine();
        
        // Initialize OpenGL
        initializeOpenGL();
        
        // Start game
        startGame();
    }
    
    /**
     * Initialize OpenGL
     */
    private void initializeOpenGL() {
        Log.i(TAG, "Initializing OpenGL");
        
        // Create GLSurfaceView
        glSurfaceView = new GLSurfaceView(this);
        
        // Set OpenGL ES version
        glSurfaceView.setEGLContextClientVersion(2);
        
        // Set renderer
        glSurfaceView.setRenderer(this);
        
        // Set render mode
        glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        
        // Set content view
        setContentView(glSurfaceView);
        
        Log.i(TAG, "OpenGL initialized");
    }
    
    /**
     * Start the game
     */
    private void startGame() {
        Log.i(TAG, "Starting game");
        
        gameRunning = true;
        lastFrameTime = System.nanoTime();
        deltaTime = 0;
        fixedDeltaTime = 1.0f / 60.0f; // 60 FPS
        accumulator = 0;
        
        // Start engine
        if (engine != null) {
            engine.start();
        }
    }
    
    /**
     * Stop the game
     */
    private void stopGame() {
        Log.i(TAG, "Stopping game");
        
        gameRunning = false;
        
        // Stop engine
        if (engine != null) {
            engine.stop();
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        Log.i(TAG, "GameActivity onResume");
        
        if (glSurfaceView != null) {
            glSurfaceView.onResume();
        }
        
        if (!gameRunning) {
            startGame();
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        Log.i(TAG, "GameActivity onPause");
        
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
        
        stopGame();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        Log.i(TAG, "GameActivity onDestroy");
        
        stopGame();
        
        if (glSurfaceView != null) {
            glSurfaceView = null;
        }
    }
    
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        Log.i(TAG, "Surface created");
        
        // Initialize engine render manager
        if (engine != null && engine.getRenderManager() != null) {
            engine.getRenderManager().initialize(glSurfaceView);
        }
    }
    
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.i(TAG, "Surface changed: " + width + "x" + height);
        
        // Update viewport
        if (engine != null && engine.getRenderManager() != null) {
            engine.getRenderManager().setViewportSize(width, height);
        }
        
        // Update camera aspect ratio
        if (engine != null && engine.getSceneManager() != null) {
            // Would update camera aspect ratio
        }
    }
    
    @Override
    public void onDrawFrame(GL10 gl) {
        if (!gameRunning) return;
        
        // Calculate delta time
        long currentTime = System.nanoTime();
        long elapsedNanos = currentTime - lastFrameTime;
        deltaTime = elapsedNanos / 1_000_000_000f; // Convert to seconds
        
        // Cap delta time to prevent spiral of death
        if (deltaTime > 0.1f) {
            deltaTime = 0.1f;
        }
        
        lastFrameTime = currentTime;
        
        // Update game
        updateGame(deltaTime);
        
        // Render game
        if (engine != null) {
            engine.update();
        }
    }
    
    /**
     * Update the game
     */
    private void updateGame(float deltaTime) {
        if (engine == null) return;
        
        // Fixed update loop
        accumulator += deltaTime;
        while (accumulator >= fixedDeltaTime) {
            engine.fixedUpdate(fixedDeltaTime);
            accumulator -= fixedDeltaTime;
        }
        
        // Variable update
        engine.update();
    }
    
    @Override
    public void onBackPressed() {
        Log.i(TAG, "Back button pressed");
        
        // Handle back button
        if (engine != null) {
            engine.pause();
        }
        
        super.onBackPressed();
    }
}
