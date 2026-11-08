package com.bothengine.rendering.postprocessing;

import android.opengl.GLES20;

import com.bothengine.rendering.Framebuffer;
import com.bothengine.rendering.FramebufferManager;
import com.bothengine.rendering.shaders.Shader;

/**
 * BloomEffect
 * Creates a bloom/glow effect by extracting bright pixels and blurring them
 */
public class BloomEffect extends PostProcessingEffect {
    
    private float threshold;
    private float intensity;
    private float blurRadius;
    private int blurPasses;
    
    // Framebuffers for ping-pong blurring
    private Framebuffer blurFramebuffer1;
    private Framebuffer blurFramebuffer2;
    
    public BloomEffect() {
        super("Bloom");
        this.threshold = 0.8f;
        this.intensity = 1.0f;
        this.blurRadius = 1.0f;
        this.blurPasses = 4;
    }
    
    @Override
    public void initialize() {
        // Create shader for threshold extraction
        String vertexShader = 
            "#version 100\n" +
            "attribute vec3 aPosition;\n" +
            "attribute vec2 aTexCoord;\n" +
            "varying vec2 vTexCoord;\n" +
            "void main() {\n" +
            "    gl_Position = vec4(aPosition, 1.0);\n" +
            "    vTexCoord = aTexCoord;\n" +
            "}";
        
        String thresholdFragmentShader = 
            "#version 100\n" +
            "precision mediump float;\n" +
            "varying vec2 vTexCoord;\n" +
            "uniform sampler2D uTexture;\n" +
            "uniform float uThreshold;\n" +
            "void main() {\n" +
            "    vec4 color = texture2D(uTexture, vTexCoord);\n" +
            "    float brightness = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
" +
            "    if (brightness > uThreshold) {\n" +
            "        gl_FragColor = color;\n" +
            "    } else {\n" +
            "        gl_FragColor = vec4(0.0);\n" +
            "    }\n" +
            "}";
        
        // Create shader for blur
        String blurFragmentShader = 
            "#version 100\n" +
            "precision mediump float;\n" +
            "varying vec2 vTexCoord;\n" +
            "uniform sampler2D uTexture;\n" +
            "uniform vec2 uDirection;\n" +
            "uniform float uRadius;\n" +
            "void main() {\n" +
            "    vec4 color = vec4(0.0);\n" +
            "    vec2 offset = uDirection * uRadius;\n" +
            "    color += texture2D(uTexture, vTexCoord - offset * 4.0) * 0.0162162162;
" +
            "    color += texture2D(uTexture, vTexCoord - offset * 3.0) * 0.0540540541;
" +
            "    color += texture2D(uTexture, vTexCoord - offset * 2.0) * 0.1216216216;
" +
            "    color += texture2D(uTexture, vTexCoord - offset * 1.0) * 0.1945945946;
" +
            "    color += texture2D(uTexture, vTexCoord) * 0.2270270270;
" +
            "    color += texture2D(uTexture, vTexCoord + offset * 1.0) * 0.1945945946;
" +
            "    color += texture2D(uTexture, vTexCoord + offset * 2.0) * 0.1216216216;
" +
            "    color += texture2D(uTexture, vTexCoord + offset * 3.0) * 0.0540540541;
" +
            "    color += texture2D(uTexture, vTexCoord + offset * 4.0) * 0.0162162162;
" +
            "    gl_FragColor = color;\n" +
            "}";
        
        // Create shader for final composition
        String compositeFragmentShader = 
            "#version 100\n" +
            "precision mediump float;\n" +
            "varying vec2 vTexCoord;\n" +
            "uniform sampler2D uSceneTexture;\n" +
            "uniform sampler2D uBloomTexture;\n" +
            "uniform float uIntensity;\n" +
            "void main() {\n" +
            "    vec4 sceneColor = texture2D(uSceneTexture, vTexCoord);\n" +
            "    vec4 bloomColor = texture2D(uBloomTexture, vTexCoord);\n" +
            "    gl_FragColor = sceneColor + bloomColor * uIntensity;\n" +
            "}";
        
        // For now, we'll use a simple shader
        // In a real implementation, we would use the shaders above
    }
    
    @Override
    protected void onApply() {
        if (getManager() == null) {
            return;
        }
        
        FramebufferManager fboManager = getManager().getFramebufferManager();
        
        // Get framebuffers
        Framebuffer sceneFbo = fboManager.getPostProcessFramebuffer();
        if (sceneFbo == null) {
            return;
        }
        
        // Create blur framebuffers if needed
        if (blurFramebuffer1 == null || blurFramebuffer1.getWidth() != sceneFbo.getWidth()) {
            blurFramebuffer1 = fboManager.getFramebuffer("BloomBlur1", sceneFbo.getWidth(), sceneFbo.getHeight(),
                Framebuffer.FramebufferType.COLOR);
            blurFramebuffer2 = fboManager.getFramebuffer("BloomBlur2", sceneFbo.getWidth(), sceneFbo.getHeight(),
                Framebuffer.FramebufferType.COLOR);
        }
        
        // Step 1: Extract bright pixels (threshold)
        extractBrightPixels(sceneFbo, blurFramebuffer1);
        
        // Step 2: Apply Gaussian blur (horizontal and vertical passes)
        applyBlur(blurFramebuffer1, blurFramebuffer2, true);  // Horizontal
        applyBlur(blurFramebuffer2, blurFramebuffer1, false); // Vertical
        
        // Step 3: Apply additional blur passes if needed
        for (int i = 1; i < blurPasses; i++) {
            applyBlur(blurFramebuffer1, blurFramebuffer2, true);
            applyBlur(blurFramebuffer2, blurFramebuffer1, false);
        }
        
        // Step 4: Composite with original scene
        composite(sceneFbo, blurFramebuffer1);
    }
    
    /**
     * Extract bright pixels above threshold
     */
    private void extractBrightPixels(Framebuffer source, Framebuffer destination) {
        // Bind destination framebuffer
        destination.bind();
        
        // Use threshold shader
        // This would use the threshold shader created in initialize()
        
        // Bind source texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, source.getColorTextureId());
        
        // Set shader uniforms
        // shader.setUniform("uTexture", 0);
        // shader.setUniform("uThreshold", threshold);
        
        // Render fullscreen quad
        renderFullscreenQuad();
    }
    
    /**
     * Apply Gaussian blur
     */
    private void applyBlur(Framebuffer source, Framebuffer destination, boolean horizontal) {
        destination.bind();
        
        // Bind source texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, source.getColorTextureId());
        
        // Set blur direction
        float directionX = horizontal ? 1.0f : 0.0f;
        float directionY = horizontal ? 0.0f : 1.0f;
        
        // shader.setUniform("uTexture", 0);
        // shader.setUniform("uDirection", directionX, directionY);
        // shader.setUniform("uRadius", blurRadius / source.getWidth());
        
        // Render fullscreen quad
        renderFullscreenQuad();
    }
    
    /**
     * Composite bloom with original scene
     */
    private void composite(Framebuffer scene, Framebuffer bloom) {
        // This would render to the final framebuffer or screen
        // For now, we'll just bind the scene framebuffer
        scene.bind();
        
        // Bind textures
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, scene.getColorTextureId());
        
        GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, bloom.getColorTextureId());
        
        // shader.setUniform("uSceneTexture", 0);
        // shader.setUniform("uBloomTexture", 1);
        // shader.setUniform("uIntensity", intensity);
        
        // Render fullscreen quad
        renderFullscreenQuad();
    }
    
    /**
     * Render a fullscreen quad
     */
    private void renderFullscreenQuad() {
        // This is a placeholder - the actual implementation is in PostProcessingManager
    }
    
    // Getters and setters for bloom parameters
    
    public float getThreshold() {
        return threshold;
    }
    
    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }
    
    public float getIntensity() {
        return intensity;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public float getBlurRadius() {
        return blurRadius;
    }
    
    public void setBlurRadius(float blurRadius) {
        this.blurRadius = blurRadius;
    }
    
    public int getBlurPasses() {
        return blurPasses;
    }
    
    public void setBlurPasses(int blurPasses) {
        this.blurPasses = blurPasses;
    }
    
    @Override
    public String toString() {
        return "BloomEffect{" +
                "threshold=" + threshold +
                ", intensity=" + intensity +
                ", blurRadius=" + blurRadius +
                ", blurPasses=" + blurPasses +
                '}';
    }
}
