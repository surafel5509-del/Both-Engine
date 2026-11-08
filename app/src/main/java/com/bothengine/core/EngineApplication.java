package com.bothengine.core;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
        
        installCrashLogger();
        logStartupDiagnostics();

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
     * Persist uncaught exceptions to a file so a crash can be diagnosed on a
     * real device without a USB cable.
     */
    private void installCrashLogger() {
        final Thread.UncaughtExceptionHandler previous =
            Thread.getDefaultUncaughtExceptionHandler();
        final Context appContext = getApplicationContext();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable throwable) {
                try {
                    StringWriter buffer = new StringWriter();
                    PrintWriter writer = new PrintWriter(buffer);
                    writer.println("Thread: " + thread.getName());
                    writer.println("Time: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
                        .format(new Date()));
                    throwable.printStackTrace(writer);
                    writer.flush();

                    String report = buffer.toString();
                    Log.e(TAG, "UNCAUGHT EXCEPTION\n" + report);

                    File target = new File(appContext.getFilesDir(), getLastCrashFileName());
                    Writer out = new FileWriter(target, false);
                    try {
                        out.write(report);
                    } finally {
                        out.close();
                    }
                } catch (Throwable ignored) {
                    // Nothing sensible we can do while the process is dying.
                }

                if (previous != null) {
                    previous.uncaughtException(thread, throwable);
                }
            }
        });
    }

    /**
     * Log the environment once so a startup problem can be diagnosed from a
     * logcat dump alone.
     */
    private void logStartupDiagnostics() {
        try {
            android.content.pm.PackageInfo info = getPackageManager()
                .getPackageInfo(getPackageName(), 0);
            Log.i(TAG, "Startup diagnostics: package=" + getPackageName()
                + " version=" + info.versionName + " (" + info.versionCode + ")"
                + " sdk=" + android.os.Build.VERSION.SDK_INT
                + " device=" + android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL
                + " gl=" + (getPackageManager().hasSystemFeature(
                    android.content.pm.PackageManager.FEATURE_OPENGLES_EXTENSION_PACK)
                    ? "ext-pack" : "es2"));
        } catch (Throwable t) {
            Log.w(TAG, "Startup diagnostics unavailable: " + t.getMessage());
        }

        File previous = new File(getFilesDir(), getLastCrashFileName());
        if (previous.exists()) {
            Log.w(TAG, "A previous session crashed; report kept at " + previous.getAbsolutePath());
        }
    }

    /** Name of the file holding the most recent crash report. */
    public static String getLastCrashFileName() {
        return "last_crash.txt";
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
