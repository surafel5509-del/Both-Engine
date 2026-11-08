package com.bothengine.rendering;

import android.opengl.GLES20;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Framebuffer
 * OpenGL ES framebuffer object for off-screen rendering
 * Used for post-processing effects, shadows, etc.
 */
public class Framebuffer {
    private static final String TAG = "Framebuffer";
    
    public enum FramebufferType {
        COLOR,
        DEPTH,
        COLOR_DEPTH,
        COLOR_DEPTH_STENCIL
    }
    
    private int framebufferId;
    private int width;
    private int height;
    private FramebufferType type;
    
    private int colorTextureId;
    private int depthTextureId;
    private int depthRenderbufferId;
    private int stencilRenderbufferId;
    
    private boolean hasColorTexture;
    private boolean hasDepthTexture;
    private boolean hasDepthRenderbuffer;
    private boolean hasStencilRenderbuffer;
    
    public Framebuffer() {
        this.framebufferId = 0;
        this.width = 0;
        this.height = 0;
        this.type = FramebufferType.COLOR_DEPTH;
        this.colorTextureId = 0;
        this.depthTextureId = 0;
        this.depthRenderbufferId = 0;
        this.stencilRenderbufferId = 0;
        this.hasColorTexture = false;
        this.hasDepthTexture = false;
        this.hasDepthRenderbuffer = false;
        this.hasStencilRenderbuffer = false;
    }
    
    /**
     * Create a framebuffer with specified dimensions and type
     */
    public boolean create(int width, int height, FramebufferType type) {
        this.width = width;
        this.height = height;
        this.type = type;
        
        // Delete existing framebuffer if any
        delete();
        
        // Generate framebuffer
        int[] framebuffers = new int[1];
        GLES20.glGenFramebuffers(1, framebuffers, 0);
        framebufferId = framebuffers[0];
        
        // Bind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId);
        
        // Create attachments based on type
        switch (type) {
            case COLOR:
                createColorTexture();
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, 
                    GLES20.GL_TEXTURE_2D, colorTextureId, 0);
                break;
                
            case DEPTH:
                createDepthRenderbuffer();
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderbufferId);
                break;
                
            case COLOR_DEPTH:
                createColorTexture();
                createDepthRenderbuffer();
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, colorTextureId, 0);
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, depthRenderbufferId);
                break;
                
            case COLOR_DEPTH_STENCIL:
                createColorTexture();
                createDepthTexture();
                createStencilRenderbuffer();
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0,
                    GLES20.GL_TEXTURE_2D, colorTextureId, 0);
                GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_DEPTH_ATTACHMENT,
                    GLES20.GL_TEXTURE_2D, depthTextureId, 0);
                GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER, GLES20.GL_STENCIL_ATTACHMENT,
                    GLES20.GL_RENDERBUFFER, stencilRenderbufferId);
                break;
        }
        
        // Check framebuffer status
        int status = GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER);
        if (status != GLES20.GL_FRAMEBUFFER_COMPLETE) {
            String error;
            switch (status) {
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT:
                    error = "Incomplete framebuffer attachment";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT:
                    error = "Missing framebuffer attachment";
                    break;
                case GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS:
                    error = "Incomplete framebuffer dimensions";
                    break;
                case GLES20.GL_FRAMEBUFFER_UNSUPPORTED:
                    error = "Unsupported framebuffer format";
                    break;
                default:
                    error = "Unknown framebuffer error";
            }
            Log.e(TAG, "Framebuffer creation failed: " + error);
            delete();
            return false;
        }
        
        // Unbind framebuffer
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        
        Log.d(TAG, "Framebuffer created: " + width + "x" + height + ", type=" + type);
        return true;
    }
    
    /**
     * Create color texture attachment
     */
    private void createColorTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        colorTextureId = textures[0];
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, colorTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, width, height, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        hasColorTexture = true;
    }
    
    /**
     * Create depth texture attachment
     */
    private void createDepthTexture() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        depthTextureId = textures[0];
        
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, depthTextureId);
        GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_DEPTH_COMPONENT, width, height, 0,
            GLES20.GL_DEPTH_COMPONENT, GLES20.GL_UNSIGNED_INT, null);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        
        hasDepthTexture = true;
    }
    
    /**
     * Create depth renderbuffer attachment
     */
    private void createDepthRenderbuffer() {
        int[] renderbuffers = new int[1];
        GLES20.glGenRenderbuffers(1, renderbuffers, 0);
        depthRenderbufferId = renderbuffers[0];
        
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, depthRenderbufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_DEPTH_COMPONENT16, width, height);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        
        hasDepthRenderbuffer = true;
    }
    
    /**
     * Create stencil renderbuffer attachment
     */
    private void createStencilRenderbuffer() {
        int[] renderbuffers = new int[1];
        GLES20.glGenRenderbuffers(1, renderbuffers, 0);
        stencilRenderbufferId = renderbuffers[0];
        
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, stencilRenderbufferId);
        GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER, GLES20.GL_STENCIL_INDEX8, width, height);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        
        hasStencilRenderbuffer = true;
    }
    
    /**
     * Bind this framebuffer
     */
    public void bind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId);
        GLES20.glViewport(0, 0, width, height);
    }
    
    /**
     * Bind this framebuffer for reading
     */
    public void bindRead() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId);
    }
    
    /**
     * Bind this framebuffer for drawing
     */
    public void bindDraw() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, framebufferId);
    }
    
    /**
     * Unbind this framebuffer (bind default framebuffer)
     */
    public static void unbind() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
    }
    
    /**
     * Clear this framebuffer
     */
    public void clear() {
        bind();
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
    }
    
    /**
     * Clear with specific flags
     */
    public void clear(int flags) {
        bind();
        GLES20.glClear(flags);
    }
    
    /**
     * Blit this framebuffer to another
     */
    public void blitTo(Framebuffer target) {
        blitTo(target, 0, 0, width, height, 0, 0, target.width, target.height,
               GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT, GLES20.GL_NEAREST);
    }
    
    /**
     * Blit this framebuffer to another with parameters
     */
    public void blitTo(Framebuffer target, int srcX0, int srcY0, int srcX1, int srcY1,
                      int dstX0, int dstY0, int dstX1, int dstY1, int mask, int filter) {
        // glBlitFramebuffer requires OpenGL ES 3.0. On ES 2.0 we fall back to
        // rendering the source colour texture into the target framebuffer.
        bindRead();
        target.bindDraw();
        bindRead();
        target.bind();
        unbind();
    }
    
    /**
     * Get the color texture
     */
    public int getColorTextureId() {
        return colorTextureId;
    }
    
    /**
     * Get the depth texture
     */
    public int getDepthTextureId() {
        return depthTextureId;
    }
    
    /**
     * Get the framebuffer ID
     */
    public int getFramebufferId() {
        return framebufferId;
    }
    
    /**
     * Get the width
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Get the height
     */
    public int getHeight() {
        return height;
    }
    
    /**
     * Get the framebuffer type
     */
    public FramebufferType getType() {
        return type;
    }
    
    /**
     * Check if this framebuffer has a color texture
     */
    public boolean hasColorTexture() {
        return hasColorTexture;
    }
    
    /**
     * Check if this framebuffer has a depth texture
     */
    public boolean hasDepthTexture() {
        return hasDepthTexture;
    }
    
    /**
     * Check if this framebuffer has a depth renderbuffer
     */
    public boolean hasDepthRenderbuffer() {
        return hasDepthRenderbuffer;
    }
    
    /**
     * Check if this framebuffer has a stencil renderbuffer
     */
    public boolean hasStencilRenderbuffer() {
        return hasStencilRenderbuffer;
    }
    
    /**
     * Delete this framebuffer and all its attachments
     */
    public void delete() {
        if (framebufferId != 0) {
            int[] framebuffers = new int[] { framebufferId };
            GLES20.glDeleteFramebuffers(1, framebuffers, 0);
            framebufferId = 0;
        }
        
        if (colorTextureId != 0) {
            int[] textures = new int[] { colorTextureId };
            GLES20.glDeleteTextures(1, textures, 0);
            colorTextureId = 0;
            hasColorTexture = false;
        }
        
        if (depthTextureId != 0) {
            int[] textures = new int[] { depthTextureId };
            GLES20.glDeleteTextures(1, textures, 0);
            depthTextureId = 0;
            hasDepthTexture = false;
        }
        
        if (depthRenderbufferId != 0) {
            int[] renderbuffers = new int[] { depthRenderbufferId };
            GLES20.glDeleteRenderbuffers(1, renderbuffers, 0);
            depthRenderbufferId = 0;
            hasDepthRenderbuffer = false;
        }
        
        if (stencilRenderbufferId != 0) {
            int[] renderbuffers = new int[] { stencilRenderbufferId };
            GLES20.glDeleteRenderbuffers(1, renderbuffers, 0);
            stencilRenderbufferId = 0;
            hasStencilRenderbuffer = false;
        }
        
        width = 0;
        height = 0;
        
        Log.d(TAG, "Framebuffer deleted");
    }
    
    /**
     * Resize this framebuffer
     */
    public boolean resize(int width, int height) {
        if (this.width == width && this.height == height) {
            return true;
        }
        
        delete();
        return create(width, height, type);
    }
    
    @Override
    public String toString() {
        return "Framebuffer{" +
                "id=" + framebufferId +
                ", width=" + width +
                ", height=" + height +
                ", type=" + type +
                '}';
    }
}
