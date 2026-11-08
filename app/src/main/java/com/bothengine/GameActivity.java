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
import com.bothengine.rendering.RenderManager;



/**
 * GameActivity
 * Main activity for the Both Engine game
 */
public class GameActivity extends Activity {
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
    private RenderManager renderManager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.i(TAG, "GameActivity onCreate");
        
        try {
            // Request fullscreen
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            
            // Get application and engine
            application = (EngineApplication) getApplication();
            if (application == null) {
                Log.e(TAG, "Application is null");
                showErrorAndFinish("Application initialization failed");
                return;
            }
            
            engine = application.getEngine();
            if (engine == null) {
                Log.e(TAG, "Engine is null, creating new engine");
                // Create engine if not initialized
                EngineConfig config = new EngineConfig.Builder()
                    .setContext(this)
                    .setTargetFPS(60)
                    .setEnablePhysics(true)
                    .setEnableAudio(true)
                    .setEnableLogging(true)
                    .build();
                engine = new Engine(config);
                application = new EngineApplication();
                application.onCreate();
            }
            
            // Initialize OpenGL
            initializeOpenGL();
            
            // Start game
            startGame();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate: " + e.getMessage(), e);
            showErrorAndFinish("Failed to initialize game: " + e.getMessage());
        }
    }
    
    /**
     * Initialize OpenGL
     */
    private void initializeOpenGL() {
        try {
            Log.i(TAG, "Initializing OpenGL");
            
            // Create GLSurfaceView
            glSurfaceView = new GLSurfaceView(this);
            if (glSurfaceView == null) {
                throw new RuntimeException("Failed to create GLSurfaceView");
            }
            
            // Set OpenGL ES version
            glSurfaceView.setEGLContextClientVersion(2);
            
            // Set render mode
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
            
            // Set content view
            setContentView(glSurfaceView);
            
            // Get render manager from engine
            if (engine != null) {
                renderManager = engine.getRenderManager();
                if (renderManager != null) {
                    renderManager.initialize(glSurfaceView);
                }
            }
            
            Log.i(TAG, "OpenGL initialized");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize OpenGL: " + e.getMessage(), e);
            showErrorAndFinish("OpenGL initialization failed: " + e.getMessage());
        }
    }
    
    /**
     * Start the game
     */
    private void startGame() {
        try {
            Log.i(TAG, "Starting game");
            
            if (engine == null) {
                throw new RuntimeException("Engine is null");
            }
            
            gameRunning = true;
            lastFrameTime = System.nanoTime();
            deltaTime = 0;
            fixedDeltaTime = 1.0f / 60.0f; // 60 FPS
            accumulator = 0;
            
            // Start engine
            engine.start();
            
            Log.i(TAG, "Game started successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to start game: " + e.getMessage(), e);
            showErrorAndFinish("Failed to start game: " + e.getMessage());
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
        
        if (renderManager != null && renderManager.getRenderer() != null) {
            renderManager.getRenderer().resume();
        }
        
        if (!gameRunning) {
            gameRunning = true;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        Log.i(TAG, "GameActivity onPause");
        
        if (glSurfaceView != null) {
            glSurfaceView.onPause();
        }
        
        if (renderManager != null && renderManager.getRenderer() != null) {
            renderManager.getRenderer().pause();
        }
        
        gameRunning = false;
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
    

    
    /**
     * Update the game - called from engine update loop
     */
    private void updateGame(float deltaTime) {
        if (engine == null) return;
        
        try {
            // Fixed update loop
            accumulator += deltaTime;
            while (accumulator >= fixedDeltaTime) {
                engine.fixedUpdate(fixedDeltaTime);
                accumulator -= fixedDeltaTime;
            }
            
            // Variable update
            engine.update();
        } catch (Exception e) {
            Log.e(TAG, "Error in updateGame: " + e.getMessage(), e);
            gameRunning = false;
        }
    }
    
    @Override
    public void onBackPressed() {
        try {
            Log.i(TAG, "Back button pressed");
            
            // Handle back button
            if (engine != null) {
                engine.pause();
            }
            
            super.onBackPressed();
        } catch (Exception e) {
            Log.e(TAG, "Error in onBackPressed: " + e.getMessage(), e);
            super.onBackPressed();
        }
    }
    
    /**
     * Show error message and finish activity
     */
    private void showErrorAndFinish(String message) {
        Log.e(TAG, "Fatal error: " + message);
        try {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, which) -> finish());
            builder.setCancelable(false);
            builder.show();
        } catch (Exception e) {
            // If we can't show dialog, just finish
            finish();
        }
    }
}
