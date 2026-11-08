package com.bothengine.scene;

/**
 * FadeTransition
 * Fade transition between scenes
 */
public class FadeTransition extends SceneTransition {
    
    public FadeTransition() {
        super("Fade");
    }
    
    @Override
    public void onStart(Scene from, Scene to) {
        // Start fading out the current scene
        if (from != null) {
            // Would typically fade out the scene
        }
        
        // Start fading in the next scene
        if (to != null) {
            // Would typically start loading the next scene
        }
    }
    
    @Override
    public void onUpdate(Scene from, Scene to, float progress) {
        // Update fade progress
        // from would fade out: 1 - progress
        // to would fade in: progress
    }
    
    @Override
    public void onComplete(Scene from, Scene to) {
        // Complete the transition
        if (from != null) {
            from.setVisible(false);
        }
        
        if (to != null) {
            to.setVisible(true);
        }
    }
}
