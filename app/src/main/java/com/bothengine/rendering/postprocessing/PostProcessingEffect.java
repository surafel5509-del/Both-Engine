package com.bothengine.rendering.postprocessing;

import com.bothengine.rendering.Framebuffer;
import com.bothengine.rendering.shaders.Shader;

/**
 * PostProcessingEffect
 * Base class for all post-processing effects
 */
public abstract class PostProcessingEffect {
    private String name;
    private boolean enabled;
    private int priority;
    private PostProcessingManager manager;
    private Shader shader;
    private Framebuffer inputFramebuffer;
    private Framebuffer outputFramebuffer;
    
    public PostProcessingEffect(String name) {
        this.name = name;
        this.enabled = true;
        this.priority = 0;
        this.manager = null;
    }
    
    /**
     * Initialize the effect
     */
    public void initialize() {
        // Override in subclasses
    }
    
    /**
     * Apply the effect
     */
    public void apply() {
        if (!enabled || shader == null || !shader.isCompiled()) {
            return;
        }
        
        // Bind shader
        shader.use();
        
        // Set up framebuffers
        if (inputFramebuffer != null) {
            inputFramebuffer.bind();
        }
        
        if (outputFramebuffer != null) {
            outputFramebuffer.bind();
        }
        
        // Apply effect
        onApply();
        
        // Cleanup
        Framebuffer.unbind();
    }
    
    /**
     * Called when applying the effect (override in subclasses)
     */
    protected abstract void onApply();
    
    /**
     * Set up the effect
     */
    public void setup() {
        // Override in subclasses
    }
    
    /**
     * Cleanup the effect
     */
    public void cleanup() {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    public PostProcessingManager getManager() {
        return manager;
    }
    
    public void setManager(PostProcessingManager manager) {
        this.manager = manager;
    }
    
    public Shader getShader() {
        return shader;
    }
    
    public void setShader(Shader shader) {
        this.shader = shader;
    }
    
    public Framebuffer getInputFramebuffer() {
        return inputFramebuffer;
    }
    
    public void setInputFramebuffer(Framebuffer inputFramebuffer) {
        this.inputFramebuffer = inputFramebuffer;
    }
    
    public Framebuffer getOutputFramebuffer() {
        return outputFramebuffer;
    }
    
    public void setOutputFramebuffer(Framebuffer outputFramebuffer) {
        this.outputFramebuffer = outputFramebuffer;
    }
    
    @Override
    public String toString() {
        return "PostProcessingEffect{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", priority=" + priority +
                '}';
    }
}
