package com.bothengine.scripting;

import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * ScriptManager
 * Manages loading, compilation, and execution of scripts
 * Supports Java-based scripting
 */
public class ScriptManager {
    private static final String TAG = "ScriptManager";
    
    private static ScriptManager instance;
    
    private Map<String, Script> scripts;
    private Map<String, Class<? extends Script>> scriptClasses;
    private String scriptPath;
    
    private ScriptManager() {
        this.scripts = new HashMap<>();
        this.scriptClasses = new HashMap<>();
        this.scriptPath = "assets/scripts";
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized ScriptManager getInstance() {
        if (instance == null) {
            instance = new ScriptManager();
        }
        return instance;
    }
    
    /**
     * Initialize the script manager
     */
    public void initialize(String scriptPath) {
        this.scriptPath = scriptPath;
        
        // Scan for script classes
        scanScripts();
        
        Log.i(TAG, "ScriptManager initialized");
    }
    
    /**
     * Initialize with default path
     */
    public void initialize() {
        initialize("assets/scripts");
    }
    
    /**
     * Scan for script classes in the classpath
     */
    private void scanScripts() {
        // In a real implementation, this would scan the classpath
        // For now, we'll manually register known script classes
    }
    
    /**
     * Register a script class
     */
    public void registerScriptClass(String name, Class<? extends Script> scriptClass) {
        scriptClasses.put(name, scriptClass);
        Log.d(TAG, "Registered script class: " + name);
    }
    
    /**
     * Load a script by name
     */
    public Script loadScript(String name) {
        // Check if already loaded
        Script script = scripts.get(name);
        if (script != null) {
            return script;
        }
        
        // Check if we have a registered class
        Class<? extends Script> scriptClass = scriptClasses.get(name);
        if (scriptClass != null) {
            try {
                Constructor<? extends Script> constructor = scriptClass.getConstructor();
                script = constructor.newInstance();
                scripts.put(name, script);
                Log.d(TAG, "Loaded script: " + name);
                return script;
            } catch (Exception e) {
                Log.e(TAG, "Error creating script: " + name + " - " + e.getMessage());
            }
        }
        
        // Try to load from file
        script = loadScriptFromFile(name);
        if (script != null) {
            scripts.put(name, script);
            return script;
        }
        
        Log.w(TAG, "Script not found: " + name);
        return null;
    }
    
    /**
     * Load a script from file
     */
    private Script loadScriptFromFile(String name) {
        // Try different file extensions
        String[] extensions = {".java", ".class"};
        
        for (String extension : extensions) {
            File file = new File(scriptPath, name + extension);
            if (file.exists()) {
                // Load and compile the script
                return compileScript(file);
            }
        }
        
        return null;
    }
    
    /**
     * Compile a script file
     */
    private Script compileScript(File file) {
        // In a real implementation, this would compile the script
        // For now, we'll return null
        return null;
    }
    
    /**
     * Unload a script
     */
    public void unloadScript(String name) {
        Script script = scripts.remove(name);
        if (script != null) {
            script.onShutdown();
            Log.d(TAG, "Unloaded script: " + name);
        }
    }
    
    /**
     * Unload all scripts
     */
    public void unloadAllScripts() {
        for (Script script : scripts.values()) {
            script.onShutdown();
        }
        scripts.clear();
        Log.i(TAG, "Unloaded all scripts");
    }
    
    /**
     * Reload all scripts
     */
    public void reloadAllScripts() {
        unloadAllScripts();
        scanScripts();
        
        // Reload all entities' scripts
        // This would be handled by the entity manager
        
        Log.i(TAG, "Reloaded all scripts");
    }
    
    /**
     * Get a loaded script by name
     */
    public Script getScript(String name) {
        return scripts.get(name);
    }
    
    /**
     * Check if a script is loaded
     */
    public boolean isScriptLoaded(String name) {
        return scripts.containsKey(name);
    }
    
    /**
     * Get all loaded script names
     */
    public String[] getLoadedScriptNames() {
        return scripts.keySet().toArray(new String[0]);
    }
    
    /**
     * Create a new script instance
     */
    public Script createScript(String className) {
        Class<? extends Script> scriptClass = scriptClasses.get(className);
        if (scriptClass != null) {
            try {
                Constructor<? extends Script> constructor = scriptClass.getConstructor();
                return constructor.newInstance();
            } catch (Exception e) {
                Log.e(TAG, "Error creating script instance: " + className + " - " + e.getMessage());
            }
        }
        return null;
    }
    
    // Getters and setters
    
    public String getScriptPath() {
        return scriptPath;
    }
    
    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }
    
    public Map<String, Script> getScripts() {
        return new HashMap<>(scripts);
    }
    
    public Map<String, Class<? extends Script>> getScriptClasses() {
        return new HashMap<>(scriptClasses);
    }
    
    /**
     * Shutdown the script manager
     */
    public void shutdown() {
        unloadAllScripts();
        scriptClasses.clear();
        instance = null;
        
        Log.i(TAG, "ScriptManager shutdown");
    }
    
    @Override
    public String toString() {
        return "ScriptManager{" +
                "scriptPath='" + scriptPath + '\'' +
                ", scripts=" + scripts.size() +
                ", scriptClasses=" + scriptClasses.size() +
                '}';
    }
}
