package com.bothengine.rendering;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

/**
 * GLES20Helper
 * Helper class for OpenGL ES 2.0 operations
 */
public class GLES20Helper {
    private static final String TAG = "GLES20Helper";
    
    /**
     * Clear the screen
     */
    public static void clearScreen() {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * Clear the screen with a specific color
     */
    public static void clearScreen(float r, float g, float b, float a) {
        GLES20.glClearColor(r, g, b, a);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * Enable depth testing
     */
    public static void enableDepthTesting(boolean enabled) {
        if (enabled) {
            GLES20.glEnable(GLES20.GL_DEPTH_TEST);
            GLES20.glDepthFunc(GLES20.GL_LEQUAL);
        } else {
            GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        }
    }
    
    /**
     * Enable culling
     */
    public static void enableCulling(boolean enabled) {
        if (enabled) {
            GLES20.glEnable(GLES20.GL_CULL_FACE);
            GLES20.glCullFace(GLES20.GL_BACK);
        } else {
            GLES20.glDisable(GLES20.GL_CULL_FACE);
        }
    }
    
    /**
     * Enable blending
     */
    public static void enableBlending(boolean enabled) {
        if (enabled) {
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        } else {
            GLES20.glDisable(GLES20.GL_BLEND);
        }
    }
    
    /**
     * Enable wireframe mode.
     * OpenGL ES 2.0 has no polygon mode, so this is a no-op placeholder kept for
     * API compatibility; renderers may switch to a line primitive mode instead.
     */
    public static void enableWireframe(boolean enabled) {
        Log.d(TAG, "Wireframe mode requested: " + enabled + " (unsupported on GLES 2.0)");
    }
    
    /**
     * Create a shader program
     */
    public static int createShaderProgram(String vertexShaderSource, String fragmentShaderSource) {
        int vertexShader = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        int fragmentShader = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);
        
        int program = GLES20.glCreateProgram();
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        
        // Check for linking errors
        int[] linkStatus = new int[1];
        GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
        if (linkStatus[0] == 0) {
            String error = GLES20.glGetProgramInfoLog(program);
            Log.e(TAG, "Shader program linking error: " + error);
            GLES20.glDeleteProgram(program);
            return 0;
        }
        
        // Delete shaders after linking
        GLES20.glDeleteShader(vertexShader);
        GLES20.glDeleteShader(fragmentShader);
        
        return program;
    }
    
    /**
     * Compile a shader
     */
    private static int compileShader(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        
        // Check for compilation errors
        int[] compileStatus = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
        if (compileStatus[0] == 0) {
            String error = GLES20.glGetShaderInfoLog(shader);
            Log.e(TAG, "Shader compilation error (" + (type == GLES20.GL_VERTEX_SHADER ? "Vertex" : "Fragment") + "): " + error);
            GLES20.glDeleteShader(shader);
            return 0;
        }
        
        return shader;
    }
    
    /**
     * Create a float buffer from an array
     */
    public static FloatBuffer createFloatBuffer(float[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(data);
        floatBuffer.position(0);
        return floatBuffer;
    }
    
    /**
     * Create a short buffer from an array
     */
    public static ShortBuffer createShortBuffer(short[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 2);
        byteBuffer.order(ByteOrder.nativeOrder());
        ShortBuffer shortBuffer = byteBuffer.asShortBuffer();
        shortBuffer.put(data);
        shortBuffer.position(0);
        return shortBuffer;
    }
    
    /**
     * Create a VBO (Vertex Buffer Object)
     */
    public static int createVBO(float[] vertices) {
        int[] vbo = new int[1];
        GLES20.glGenBuffers(1, vbo, 0);
        
        FloatBuffer vertexBuffer = createFloatBuffer(vertices);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo[0]);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, vertices.length * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        
        return vbo[0];
    }
    
    /**
     * Create an IBO (Index Buffer Object)
     */
    public static int createIBO(short[] indices) {
        int[] ibo = new int[1];
        GLES20.glGenBuffers(1, ibo, 0);
        
        ShortBuffer indexBuffer = createShortBuffer(indices);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo[0]);
        GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, indices.length * 2, indexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        
        return ibo[0];
    }
    
    /**
     * Create a VAO (Vertex Array Object) - Note: VAOs are not available in OpenGL ES 2.0
     * This is a helper for OpenGL ES 3.0+
     */
    public static int createVAO() {
        // Vertex array objects require OpenGL ES 3.0 / OES_vertex_array_object,
        // which this engine does not target yet.
        return 0;
    }
    
    /**
     * Delete a VBO
     */
    public static void deleteVBO(int vbo) {
        int[] buffers = new int[] { vbo };
        GLES20.glDeleteBuffers(1, buffers, 0);
    }
    
    /**
     * Delete an IBO
     */
    public static void deleteIBO(int ibo) {
        int[] buffers = new int[] { ibo };
        GLES20.glDeleteBuffers(1, buffers, 0);
    }
    
    /**
     * Delete a VAO
     */
    public static void deleteVAO(int vao) {
        // No-op: see createVAO().
    }
    
    /**
     * Delete a shader program
     */
    public static void deleteShaderProgram(int program) {
        GLES20.glDeleteProgram(program);
    }
    
    /**
     * Get the current OpenGL ES version
     */
    public static String getOpenGLVersion() {
        return GLES20.glGetString(GLES20.GL_VERSION);
    }
    
    /**
     * Get the OpenGL ES vendor
     */
    public static String getOpenGLVendor() {
        return GLES20.glGetString(GLES20.GL_VENDOR);
    }
    
    /**
     * Get the OpenGL ES renderer
     */
    public static String getOpenGLRenderer() {
        return GLES20.glGetString(GLES20.GL_RENDERER);
    }
    
    /**
     * Check for OpenGL errors
     */
    public static void checkGLError(String operation) {
        int error = GLES20.glGetError();
        if (error != GLES20.GL_NO_ERROR) {
            String errorString;
            switch (error) {
                case GLES20.GL_INVALID_ENUM:
                    errorString = "GL_INVALID_ENUM";
                    break;
                case GLES20.GL_INVALID_VALUE:
                    errorString = "GL_INVALID_VALUE";
                    break;
                case GLES20.GL_INVALID_OPERATION:
                    errorString = "GL_INVALID_OPERATION";
                    break;
                case GLES20.GL_OUT_OF_MEMORY:
                    errorString = "GL_OUT_OF_MEMORY";
                    break;
                case GLES20.GL_INVALID_FRAMEBUFFER_OPERATION:
                    errorString = "GL_INVALID_FRAMEBUFFER_OPERATION";
                    break;
                default:
                    errorString = "Unknown error";
            }
            Log.e(TAG, "OpenGL error after " + operation + ": " + errorString);
        }
    }
}
