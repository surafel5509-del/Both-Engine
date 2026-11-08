package com.bothengine.rendering.shaders;

import android.content.Context;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * ShaderManager
 * Manages all shaders in the engine
 */
public class ShaderManager {
    private static final String TAG = "ShaderManager";
    
    private final Context context;
    private final Map<Class<? extends Shader>, Shader> shaders;
    
    public ShaderManager(Context context) {
        this.context = context;
        this.shaders = new HashMap<>();
    }
    
    /**
     * Get a shader instance
     */
    @SuppressWarnings("unchecked")
    public <T extends Shader> T getShader(Class<T> shaderClass) {
        Shader shader = shaders.get(shaderClass);
        if (shader == null) {
            try {
                shader = shaderClass.newInstance();
                shader.initialize(context);
                shaders.put(shaderClass, shader);
                Log.d(TAG, "Created shader: " + shaderClass.getSimpleName());
            } catch (Exception e) {
                Log.e(TAG, "Error creating shader: " + shaderClass.getSimpleName(), e);
            }
        }
        return (T) shader;
    }
    
    /**
     * Reload all shaders
     */
    public void reloadAll() {
        for (Map.Entry<Class<? extends Shader>, Shader> entry : shaders.entrySet()) {
            try {
                Shader shader = entry.getValue();
                shader.delete();
                shader = entry.getKey().newInstance();
                shader.initialize(context);
                shaders.put(entry.getKey(), shader);
                Log.d(TAG, "Reloaded shader: " + entry.getKey().getSimpleName());
            } catch (Exception e) {
                Log.e(TAG, "Error reloading shader: " + entry.getKey().getSimpleName(), e);
            }
        }
    }
    
    /**
     * Shutdown the shader manager
     */
    public void shutdown() {
        for (Shader shader : shaders.values()) {
            shader.delete();
        }
        shaders.clear();
        Log.i(TAG, "ShaderManager shutdown");
    }
    
    /**
     * Get the basic 2D shader
     */
    public Basic2DShader getBasic2DShader() {
        return getShader(Basic2DShader.class);
    }
    
    /**
     * Get the basic 3D shader
     */
    public Basic3DShader getBasic3DShader() {
        return getShader(Basic3DShader.class);
    }
}
