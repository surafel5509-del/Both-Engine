package com.bothengine;

import android.app.Activity;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.bothengine.core.Engine;
import com.bothengine.core.EngineConfig;
import com.bothengine.core.EngineApplication;
import com.bothengine.rendering.RenderManager;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;



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
    private volatile boolean glSurfaceViewResumable;
    private volatile boolean surfaceReady;
    
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
                    .setContext(getApplicationContext())
                    .setTargetFPS(60)
                    .setEnablePhysics(true)
                    .setEnableAudio(true)
                    .setEnableLogging(true)
                    .build();
                engine = new Engine(config);
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

            // ORDER MATTERS. GLSurfaceView.setRenderMode() and onResume() both
            // dereference mGLThread, which only exists after setRenderer() has
            // been called. Calling setRenderMode() first throws a
            // NullPointerException and the activity never gets a content view.
            if (engine != null) {
                renderManager = engine.getRenderManager();
            }
            if (renderManager != null) {
                renderManager.initialize(glSurfaceView);
            }
            if (renderManager == null || renderManager.getRenderer() == null) {
                // Never leave a bare GLSurfaceView behind: attach a fallback
                // renderer so the GL thread exists and lifecycle calls are safe.
                Log.w(TAG, "No RenderManager renderer available, attaching fallback renderer");
                glSurfaceView.setRenderer(new FallbackRenderer());
            }
            glSurfaceViewResumable = true;

            // Safe now: the GL thread has been created by setRenderer().
            glSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);

            // Set content view
            setContentView(glSurfaceView);
            
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

        // GLSurfaceView.onResume() throws NullPointerException when no renderer
        // was ever set (no GL thread). Only resume a fully configured view.
        try {
            if (glSurfaceView != null && glSurfaceViewResumable) {
                glSurfaceView.onResume();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to resume surface: " + e.getMessage(), e);
        }

        try {
            if (renderManager != null && renderManager.getRenderer() != null) {
                renderManager.getRenderer().resume();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to resume renderer: " + e.getMessage(), e);
        }
        
        if (!gameRunning) {
            gameRunning = true;
        }
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        
        Log.i(TAG, "GameActivity onPause");

        try {
            if (glSurfaceView != null && glSurfaceViewResumable) {
                glSurfaceView.onPause();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to pause surface: " + e.getMessage(), e);
        }

        try {
            if (renderManager != null && renderManager.getRenderer() != null) {
                renderManager.getRenderer().pause();
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to pause renderer: " + e.getMessage(), e);
        }
        
        gameRunning = false;
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        Log.i(TAG, "GameActivity onDestroy");
        
        stopGame();

        try {
            if (glSurfaceView != null && glSurfaceViewResumable) {
                glSurfaceView.onPause();
            }
        } catch (Exception e) {
            Log.w(TAG, "Error releasing surface: " + e.getMessage(), e);
        }
        glSurfaceViewResumable = false;
        glSurfaceView = null;
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
     * Minimal renderer used only when the engine has no RenderManager.
     * Its sole purpose is to create a valid GL thread so that
     * GLSurfaceView.onResume()/onPause() cannot throw.
     */
    private static final class FallbackRenderer implements GLSurfaceView.Renderer {
        private static final String TAG = "FallbackRenderer";

        private final com.bothengine.rendering.SceneRenderer scene =
            new com.bothengine.rendering.SceneRenderer();
        private int width = 1;
        private int height = 1;
        private long lastFrameNanos;

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            Log.i(TAG, "Fallback surface created");
            scene.initialize();
            lastFrameNanos = System.nanoTime();
        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {
            this.width = width;
            this.height = height;
            GLES20.glViewport(0, 0, width, height);
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            long now = System.nanoTime();
            float delta = lastFrameNanos == 0 ? 0.016f : (now - lastFrameNanos) / 1_000_000_000f;
            lastFrameNanos = now;
            if (delta > 0.1f) {
                delta = 0.1f;
            }
            scene.drawFrame(delta, width, height);
        }
    }

    /**
     * Report a fatal startup error without killing the app: the screen stays
     * up showing the reason, which makes a remote crash far easier to diagnose.
     */
    private void showErrorAndFinish(String message) {
        Log.e(TAG, "Fatal error: " + message);
        try {
            android.widget.TextView view = new android.widget.TextView(this);
            view.setBackgroundColor(0xFF101018);
            view.setTextColor(0xFFFF6B6B);
            view.setTextSize(15f);
            view.setPadding(48, 96, 48, 48);
            view.setText("Both Engine could not start the renderer.\n\n"
                + message
                + "\n\nThe engine is still running; press Back to exit.");
            setContentView(view);
        } catch (Throwable t) {
            Log.e(TAG, "Could not display error screen: " + t.getMessage(), t);
            finish();
        }
    }
}
