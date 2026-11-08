package com.bothengine.scene;

/**
 * SceneTransition
 * Base class for scene transitions
 */
public abstract class SceneTransition {
    private String name;
    private float duration;
    
    public SceneTransition() {
        this.name = "Transition";
        this.duration = 1.0f;
    }
    
    public SceneTransition(String name) {
        this.name = name;
    }
    
    /**
     * Called when the transition starts
     */
    public void onStart(Scene from, Scene to) {
        // Override in subclasses
    }
    
    /**
     * Called every frame during the transition
     */
    public void onUpdate(Scene from, Scene to, float progress) {
        // Override in subclasses
    }
    
    /**
     * Called when the transition completes
     */
    public void onComplete(Scene from, Scene to) {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public float getDuration() {
        return duration;
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
    }
}
