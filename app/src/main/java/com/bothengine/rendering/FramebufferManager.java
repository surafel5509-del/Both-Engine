package com.bothengine.rendering;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * FramebufferManager
 * Manages all framebuffers used for post-processing, shadows, etc.
 */
public class FramebufferManager {
    private static final String TAG = "FramebufferManager";
    
    private final Map<String, Framebuffer> framebuffers;
    private Framebuffer mainFramebuffer;
    private Framebuffer shadowFramebuffer;
    private Framebuffer postProcessFramebuffer;
    private Framebuffer gBuffer;
    
    private int shadowMapSize;
    private int postProcessWidth;
    private int postProcessHeight;
    
    public FramebufferManager() {
        this.framebuffers = new HashMap<>();
        this.mainFramebuffer = null;
        this.shadowFramebuffer = null;
        this.postProcessFramebuffer = null;
        this.gBuffer = null;
        this.shadowMapSize = 2048;
        this.postProcessWidth = 1;
        this.postProcessHeight = 1;
    }
    
    /**
     * Initialize the framebuffer manager
     */
    public void initialize(int width, int height) {
        this.postProcessWidth = width;
        this.postProcessHeight = height;
        
        Log.i(TAG, "FramebufferManager initialized: " + width + "x" + height);
    }
    
    /**
     * Create or get a framebuffer by name
     */
    public Framebuffer getFramebuffer(String name, int width, int height, Framebuffer.FramebufferType type) {
        Framebuffer framebuffer = framebuffers.get(name);
        if (framebuffer == null || framebuffer.getWidth() != width || framebuffer.getHeight() != height) {
            framebuffer = new Framebuffer();
            if (framebuffer.create(width, height, type)) {
                framebuffers.put(name, framebuffer);
            } else {
                return null;
            }
        }
        return framebuffer;
    }
    
    /**
     * Get or create the main framebuffer
     */
    public Framebuffer getMainFramebuffer() {
        if (mainFramebuffer == null) {
            mainFramebuffer = getFramebuffer("Main", postProcessWidth, postProcessHeight, 
                Framebuffer.FramebufferType.COLOR_DEPTH);
        }
        return mainFramebuffer;
    }
    
    /**
     * Get or create the shadow framebuffer
     */
    public Framebuffer getShadowFramebuffer() {
        if (shadowFramebuffer == null) {
            shadowFramebuffer = getFramebuffer("Shadow", shadowMapSize, shadowMapSize,
                Framebuffer.FramebufferType.DEPTH);
        }
        return shadowFramebuffer;
    }
    
    /**
     * Get or create the post-process framebuffer
     */
    public Framebuffer getPostProcessFramebuffer() {
        if (postProcessFramebuffer == null) {
            postProcessFramebuffer = getFramebuffer("PostProcess", postProcessWidth, postProcessHeight,
                Framebuffer.FramebufferType.COLOR_DEPTH);
        }
        return postProcessFramebuffer;
    }
    
    /**
     * Get or create the G-buffer for deferred rendering
     */
    public Framebuffer getGBuffer() {
        if (gBuffer == null) {
            // G-buffer typically has multiple color attachments
            // For now, we'll use a simple framebuffer
            gBuffer = getFramebuffer("GBuffer", postProcessWidth, postProcessHeight,
                Framebuffer.FramebufferType.COLOR_DEPTH);
        }
        return gBuffer;
    }
    
    /**
     * Update viewport size
     */
    public void setViewportSize(int width, int height) {
        if (width == postProcessWidth && height == postProcessHeight) {
            return;
        }
        
        this.postProcessWidth = width;
        this.postProcessHeight = height;
        
        // Recreate framebuffers with new size
        if (mainFramebuffer != null) {
            mainFramebuffer.resize(width, height);
        }
        if (postProcessFramebuffer != null) {
            postProcessFramebuffer.resize(width, height);
        }
        if (gBuffer != null) {
            gBuffer.resize(width, height);
        }
        
        Log.i(TAG, "Viewport resized: " + width + "x" + height);
    }
    
    /**
     * Set shadow map size
     */
    public void setShadowMapSize(int size) {
        this.shadowMapSize = size;
        if (shadowFramebuffer != null) {
            shadowFramebuffer.resize(size, size);
        }
    }
    
    /**
     * Get shadow map size
     */
    public int getShadowMapSize() {
        return shadowMapSize;
    }
    
    /**
     * Begin rendering to a framebuffer
     */
    public void beginRender(String name) {
        Framebuffer framebuffer = framebuffers.get(name);
        if (framebuffer != null) {
            framebuffer.bind();
            framebuffer.clear();
        }
    }
    
    /**
     * End rendering to a framebuffer
     */
    public void endRender() {
        Framebuffer.unbind();
    }
    
    /**
     * Get a framebuffer by name
     */
    public Framebuffer getFramebuffer(String name) {
        return framebuffers.get(name);
    }
    
    /**
     * Check if a framebuffer exists
     */
    public boolean hasFramebuffer(String name) {
        return framebuffers.containsKey(name);
    }
    
    /**
     * Delete a framebuffer by name
     */
    public void deleteFramebuffer(String name) {
        Framebuffer framebuffer = framebuffers.remove(name);
        if (framebuffer != null) {
            framebuffer.delete();
        }
    }
    
    /**
     * Delete all framebuffers
     */
    public void deleteAll() {
        for (Framebuffer framebuffer : framebuffers.values()) {
            framebuffer.delete();
        }
        framebuffers.clear();
        mainFramebuffer = null;
        shadowFramebuffer = null;
        postProcessFramebuffer = null;
        gBuffer = null;
        
        Log.i(TAG, "All framebuffers deleted");
    }
    
    /**
     * Shutdown the framebuffer manager
     */
    public void shutdown() {
        deleteAll();
        Log.i(TAG, "FramebufferManager shutdown");
    }
    
    @Override
    public String toString() {
        return "FramebufferManager{" +
                "framebuffers=" + framebuffers.size() +
                ", shadowMapSize=" + shadowMapSize +
                ", viewport=" + postProcessWidth + "x" + postProcessHeight +
                '}';
    }
}
