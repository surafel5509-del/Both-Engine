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
    
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        
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
