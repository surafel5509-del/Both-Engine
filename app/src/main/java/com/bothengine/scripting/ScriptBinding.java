package com.bothengine.scripting;

import java.util.HashMap;
import java.util.Map;

/**
 * ScriptBinding
 * Binds Java classes and methods to scripts
 * Allows scripts to call Java methods and access Java objects
 */
public class ScriptBinding {
    private String name;
    private Map<String, Object> boundObjects;
    private Map<String, ScriptFunction> boundFunctions;
    
    public ScriptBinding(String name) {
        this.name = name;
        this.boundObjects = new HashMap<>();
        this.boundFunctions = new HashMap<>();
    }
    
    /**
     * Create a script binding with a name
     */
    public static ScriptBinding create(String name) {
        return new ScriptBinding(name);
    }
    
    /**
     * Bind an object with a name
     */
    public void bindObject(String name, Object object) {
        boundObjects.put(name, object);
    }
    
    /**
     * Get a bound object by name
     */
    public Object getBoundObject(String name) {
        return boundObjects.get(name);
    }
    
    /**
     * Check if an object is bound
     */
    public boolean isObjectBound(String name) {
        return boundObjects.containsKey(name);
    }
    
    /**
     * Unbind an object
     */
    public void unbindObject(String name) {
        boundObjects.remove(name);
    }
    
    /**
     * Bind a function with a name
     */
    public void bindFunction(String name, ScriptFunction function) {
        boundFunctions.put(name, function);
    }
    
    /**
     * Get a bound function by name
     */
    public ScriptFunction getBoundFunction(String name) {
        return boundFunctions.get(name);
    }
    
    /**
     * Check if a function is bound
     */
    public boolean isFunctionBound(String name) {
        return boundFunctions.containsKey(name);
    }
    
    /**
     * Unbind a function
     */
    public void unbindFunction(String name) {
        boundFunctions.remove(name);
    }
    
    /**
     * Call a bound function
     */
    public Object callFunction(String name, Object... args) {
        ScriptFunction function = boundFunctions.get(name);
        if (function != null) {
            return function.call(args);
        }
        return null;
    }
    
    /**
     * Get all bound object names
     */
    public String[] getBoundObjectNames() {
        return boundObjects.keySet().toArray(new String[0]);
    }
    
    /**
     * Get all bound function names
     */
    public String[] getBoundFunctionNames() {
        return boundFunctions.keySet().toArray(new String[0]);
    }
    
    /**
     * Clear all bindings
     */
    public void clear() {
        boundObjects.clear();
        boundFunctions.clear();
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, Object> getBoundObjects() {
        return new HashMap<>(boundObjects);
    }
    
    public Map<String, ScriptFunction> getBoundFunctions() {
        return new HashMap<>(boundFunctions);
    }
    
    @Override
    public String toString() {
        return "ScriptBinding{" +
                "name='" + name + '\'' +
                ", boundObjects=" + boundObjects.size() +
                ", boundFunctions=" + boundFunctions.size() +
                '}';
    }
    
    /**
     * ScriptFunction interface
     * Represents a function that can be called from a script
     */
    @FunctionalInterface
    public interface ScriptFunction {
        Object call(Object... args);
    }
    
    /**
     * Script binding manager
     * Manages multiple script bindings
     */
    public static class ScriptBindingManager {
        private static ScriptBindingManager instance;
        
        private Map<String, ScriptBinding> bindings;
        
        private ScriptBindingManager() {
            this.bindings = new HashMap<>();
        }
        
        /**
         * Get the singleton instance
         */
        public static synchronized ScriptBindingManager getInstance() {
            if (instance == null) {
                instance = new ScriptBindingManager();
            }
            return instance;
        }
        
        /**
         * Create a new binding
         */
        public ScriptBinding createBinding(String name) {
            ScriptBinding binding = new ScriptBinding(name);
            bindings.put(name, binding);
            return binding;
        }
        
        /**
         * Get a binding by name
         */
        public ScriptBinding getBinding(String name) {
            return bindings.get(name);
        }
        
        /**
         * Check if a binding exists
         */
        public boolean hasBinding(String name) {
            return bindings.containsKey(name);
        }
        
        /**
         * Remove a binding
         */
        public void removeBinding(String name) {
            bindings.remove(name);
        }
        
        /**
         * Clear all bindings
         */
        public void clear() {
            bindings.clear();
        }
        
        /**
         * Shutdown the binding manager
         */
        public void shutdown() {
            clear();
            instance = null;
        }
        
        // Getters and setters
        
        public Map<String, ScriptBinding> getBindings() {
            return new HashMap<>(bindings);
        }
        
        @Override
        public String toString() {
            return "ScriptBindingManager{" +
                    "bindings=" + bindings.size() +
                    '}';
        }
    }
}
