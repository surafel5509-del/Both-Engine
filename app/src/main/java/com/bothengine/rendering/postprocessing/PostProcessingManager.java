package com.bothengine.rendering.postprocessing;

import android.util.Log;

import com.bothengine.rendering.Framebuffer;
import com.bothengine.rendering.FramebufferManager;
import com.bothengine.rendering.GLES20Helper;
import com.bothengine.rendering.shaders.Shader;

import java.util.ArrayList;
import java.util.List;

/**
 * PostProcessingManager
 * Manages all post-processing effects for the game engine
 * Supports multiple effects chained together
 */
public class PostProcessingManager {
    private static final String TAG = "PostProcessingManager";
    
    private FramebufferManager framebufferManager;
    private List<PostProcessingEffect> effects;
    private boolean enabled;
    private Shader finalShader;
    
    public PostProcessingManager(FramebufferManager framebufferManager) {
        this.framebufferManager = framebufferManager;
        this.effects = new ArrayList<>();
        this.enabled = true;
    }
    
    /**
     * Initialize the post-processing manager
     */
    public void initialize() {
        Log.i(TAG, "PostProcessingManager initialized");
    }
    
    /**
     * Add a post-processing effect
     */
    public void addEffect(PostProcessingEffect effect) {
        if (effect != null && !effects.contains(effect)) {
            effects.add(effect);
            effect.setManager(this);
            Log.d(TAG, "Added effect: " + effect.getName());
        }
    }
    
    /**
     * Remove a post-processing effect
     */
    public void removeEffect(PostProcessingEffect effect) {
        if (effect != null) {
            effects.remove(effect);
            effect.setManager(null);
            Log.d(TAG, "Removed effect: " + effect.getName());
        }
    }
    
    /**
     * Remove effect by name
     */
    public void removeEffect(String name) {
        for (PostProcessingEffect effect : new ArrayList<>(effects)) {
            if (effect.getName().equals(name)) {
                removeEffect(effect);
            }
        }
    }
    
    /**
     * Clear all effects
     */
    public void clearEffects() {
        for (PostProcessingEffect effect : effects) {
            effect.setManager(null);
        }
        effects.clear();
        Log.i(TAG, "Cleared all effects");
    }
    
    /**
     * Get effect by name
     */
    public PostProcessingEffect getEffect(String name) {
        for (PostProcessingEffect effect : effects) {
            if (effect.getName().equals(name)) {
                return effect;
            }
        }
        return null;
    }
    
    /**
     * Get effect by index
     */
    public PostProcessingEffect getEffect(int index) {
        if (index >= 0 && index < effects.size()) {
            return effects.get(index);
        }
        return null;
    }
    
    /**
     * Get all effects
     */
    public List<PostProcessingEffect> getAllEffects() {
        return new ArrayList<>(effects);
    }
    
    /**
     * Apply post-processing effects
     * This renders the scene to a framebuffer, then applies effects, then renders to screen
     */
    public void apply() {
        if (!enabled || effects.isEmpty()) {
            return;
        }
        
        Framebuffer postProcessFbo = framebufferManager.getPostProcessFramebuffer();
        if (postProcessFbo == null) {
            return;
        }
        
        // Sort effects by priority
        effects.sort((a, b) -> Integer.compare(a.getPriority(), b.getPriority()));
        
        // Apply each effect
        for (PostProcessingEffect effect : effects) {
            if (effect.isEnabled()) {
                effect.apply();
            }
        }
    }
    
    /**
     * Render the final result to screen
     */
    public void renderToScreen() {
        if (finalShader != null) {
            finalShader.use();
            // Render quad with final shader
            renderFullscreenQuad();
        }
    }
    
    /**
     * Render a fullscreen quad
     */
    private void renderFullscreenQuad() {
        // Simple fullscreen quad rendering
        float[] vertices = {
            -1, -1, 0,
             1, -1, 0,
            -1,  1, 0,
             1,  1, 0
        };
        
        float[] texCoords = {
            0, 0,
            1, 0,
            0, 1,
            1, 1
        };
        
        short[] indices = {0, 1, 2, 2, 1, 3};
        
        // Create VBO
        int vbo = GLES20Helper.createVBO(vertices);
        int tbo = GLES20Helper.createVBO(texCoords);
        int ibo = GLES20Helper.createIBO(indices);
        
        // Bind and render
        GLES20Helper.enableVertexAttribArray(0, 3, 0, vbo);
        GLES20Helper.enableVertexAttribArray(1, 2, 0, tbo);
        
        GLES20Helper.bindIndexBuffer(ibo);
        GLES20Helper.drawElements(6);
        
        // Cleanup
        GLES20Helper.deleteVBO(vbo);
        GLES20Helper.deleteVBO(tbo);
        GLES20Helper.deleteIBO(ibo);
    }
    
    /**
     * Set the final shader for rendering to screen
     */
    public void setFinalShader(Shader shader) {
        this.finalShader = shader;
    }
    
    /**
     * Get the final shader
     */
    public Shader getFinalShader() {
        return finalShader;
    }
    
    /**
     * Enable or disable post-processing
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    /**
     * Check if post-processing is enabled
     */
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Get the framebuffer manager
     */
    public FramebufferManager getFramebufferManager() {
        return framebufferManager;
    }
    
    /**
     * Shutdown the post-processing manager
     */
    public void shutdown() {
        clearEffects();
        finalShader = null;
        framebufferManager = null;
        enabled = false;
        Log.i(TAG, "PostProcessingManager shutdown");
    }
    
    @Override
    public String toString() {
        return "PostProcessingManager{" +
                "effects=" + effects.size() +
                ", enabled=" + enabled +
                '}';
    }
}
