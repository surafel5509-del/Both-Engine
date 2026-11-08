package com.bothengine.core;

import android.app.Application;
import android.util.Log;

/**
 * Both Engine Application
 * Main application class that initializes the game engine
 */
public class EngineApplication extends Application {
    private static final String TAG = "EngineApplication";
    
    private static EngineApplication instance;
    private Engine engine;
    private boolean initialized = false;
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
        try {
            Log.i(TAG, "Both Engine Application Started");
            
            // Initialize engine configuration
            EngineConfig config = new EngineConfig.Builder()
                .setContext(this)
                .setTargetFPS(60)
                .setEnablePhysics(true)
                .setEnableAudio(true)
                .setEnableLogging(true)
                .build();
            
            // Create engine instance
            engine = new Engine(config);
            initialized = true;
            
            Log.i(TAG, "Both Engine initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize engine: " + e.getMessage(), e);
            initialized = false;
        }
    }
    
    /**
     * Initialize engine if not already initialized
     */
    public void ensureInitialized(Context context) {
        if (!initialized) {
            try {
                Log.i(TAG, "Initializing engine on demand");
                EngineConfig config = new EngineConfig.Builder()
                    .setContext(context)
                    .setTargetFPS(60)
                    .setEnablePhysics(true)
                    .setEnableAudio(true)
                    .setEnableLogging(true)
                    .build();
                
                engine = new Engine(config);
                initialized = true;
                Log.i(TAG, "Engine initialized on demand");
            } catch (Exception e) {
                Log.e(TAG, "Failed to initialize engine on demand: " + e.getMessage(), e);
                initialized = false;
            }
        }
    }
    
    public static EngineApplication getInstance() {
        return instance;
    }
    
    public Engine getEngine() {
        return engine;
    }
    
    @Override
    public void onTerminate() {
        if (engine != null) {
            engine.shutdown();
        }
        super.onTerminate();
    }
}
