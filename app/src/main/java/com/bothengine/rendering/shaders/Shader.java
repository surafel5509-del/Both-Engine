package com.bothengine.rendering.shaders;

import android.content.Context;
import android.util.Log;

import com.bothengine.rendering.GLES20Helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Shader
 * Base class for all shaders in the engine
 */
public abstract class Shader {
    private static final String TAG = "Shader";
    
    protected int programId;
    protected String vertexShaderSource;
    protected String fragmentShaderSource;
    protected boolean compiled;
    
    public Shader() {
        this.programId = 0;
        this.compiled = false;
    }
    
    /**
     * Load shader source from assets
     */
    public void loadFromAssets(Context context, String vertexPath, String fragmentPath) {
        try {
            vertexShaderSource = loadAsset(context, vertexPath);
            fragmentShaderSource = loadAsset(context, fragmentPath);
            Log.d(TAG, "Loaded shader sources: " + vertexPath + ", " + fragmentPath);
        } catch (IOException e) {
            Log.e(TAG, "Error loading shader sources", e);
        }
    }
    
    /**
     * Load a text file from assets
     */
    private String loadAsset(Context context, String path) throws IOException {
        StringBuilder result = new StringBuilder();
        InputStream inputStream = context.getAssets().open(path);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        
        while ((line = reader.readLine()) != null) {
            result.append(line).append("\n");
        }
        
        reader.close();
        inputStream.close();
        
        return result.toString();
    }
    
    /**
     * Compile the shader
     */
    public boolean compile() {
        if (vertexShaderSource == null || fragmentShaderSource == null) {
            Log.e(TAG, "Shader sources are null");
            return false;
        }
        
        try {
            programId = GLES20Helper.createShaderProgram(vertexShaderSource, fragmentShaderSource);
            if (programId == 0) {
                Log.e(TAG, "Failed to compile shader");
                return false;
            }
            
            compiled = true;
            Log.d(TAG, "Shader compiled successfully");
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error compiling shader", e);
            return false;
        }
    }
    
    /**
     * Use this shader
     */
    public void use() {
        if (!compiled) {
            Log.w(TAG, "Shader not compiled, cannot use");
            return;
        }
        GLES20Helper.checkGLError("Before using shader");
        android.opengl.GLES20.glUseProgram(programId);
        GLES20Helper.checkGLError("After using shader");
    }
    
    /**
     * Get the program ID
     */
    public int getProgramId() {
        return programId;
    }
    
    /**
     * Check if shader is compiled
     */
    public boolean isCompiled() {
        return compiled;
    }
    
    /**
     * Get attribute location
     */
    public int getAttributeLocation(String name) {
        if (!compiled) return -1;
        return android.opengl.GLES20.glGetAttribLocation(programId, name);
    }
    
    /**
     * Get uniform location
     */
    public int getUniformLocation(String name) {
        if (!compiled) return -1;
        return android.opengl.GLES20.glGetUniformLocation(programId, name);
    }
    
    /**
     * Set a uniform float
     */
    public void setUniform(String name, float value) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniform1f(location, value);
        }
    }
    
    /**
     * Set a uniform int
     */
    public void setUniform(String name, int value) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniform1i(location, value);
        }
    }
    
    /**
     * Set a uniform float array
     */
    public void setUniform(String name, float[] values) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniform1fv(location, values.length, values, 0);
        }
    }
    
    /**
     * Set a uniform vec2
     */
    public void setUniform(String name, float x, float y) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniform2f(location, x, y);
        }
    }
    
    /**
     * Set a uniform vec3
     */
    public void setUniform(String name, float x, float y, float z) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniform3f(location, x, y, z);
        }
    }
    
    /**
     * Set a uniform vec4
     */
    public void setUniform(String name, float x, float y, float z, float w) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniform4f(location, x, y, z, w);
        }
    }
    
    /**
     * Set a uniform mat4
     */
    public void setUniform(String name, float[] matrix) {
        int location = getUniformLocation(name);
        if (location != -1) {
            android.opengl.GLES20.glUniformMatrix4fv(location, 1, false, matrix, 0);
        }
    }
    
    /**
     * Delete the shader
     */
    public void delete() {
        if (programId != 0) {
            GLES20Helper.deleteShaderProgram(programId);
            programId = 0;
            compiled = false;
        }
    }
    
    /**
     * Abstract method to define vertex shader source
     */
    protected abstract String getVertexShaderSource();
    
    /**
     * Abstract method to define fragment shader source
     */
    protected abstract String getFragmentShaderSource();
    
    /**
     * Initialize the shader (load sources, compile, etc.)
     */
    public void initialize(Context context) {
        vertexShaderSource = getVertexShaderSource();
        fragmentShaderSource = getFragmentShaderSource();
        compile();
    }
}
