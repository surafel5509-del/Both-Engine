package com.bothengine.scene;

/**
 * InstantTransition
 * Instant transition between scenes (no animation)
 */
public class InstantTransition extends SceneTransition {
    
    public InstantTransition() {
        super("Instant");
    }
    
    @Override
    public void onStart(Scene from, Scene to) {
        // Nothing to do
    }
    
    @Override
    public void onUpdate(Scene from, Scene to, float progress) {
        // Nothing to do
    }
    
    @Override
    public void onComplete(Scene from, Scene to) {
        // Just switch scenes
        if (from != null) {
            from.setVisible(false);
        }
        
        if (to != null) {
            to.setVisible(true);
        }
    }
}
