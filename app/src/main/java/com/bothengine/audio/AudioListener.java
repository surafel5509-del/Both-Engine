package com.bothengine.audio;

import com.bothengine.components.TransformComponent;
import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;
import com.bothengine.rendering.Vector3;

/**
 * AudioListener
 * ECS component that represents the audio listener in 3D space
 * There should be only one active listener at a time
 * Similar to Unity's AudioListener
 */
public class AudioListener extends Component {
    private Vector3 position;
    private Vector3 velocity;
    private float volume;
    
    public AudioListener() {
        this.position = new Vector3();
        this.velocity = new Vector3();
        this.volume = 1.0f;
    }
    
    /**
     * Create an audio listener
     */
    public static AudioListener create() {
        return new AudioListener();
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        
        // Register as the active listener
        AudioManager audioManager = AudioManager.getInstance();
        if (audioManager != null) {
            audioManager.setAudioListener(this);
        }
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        
        // Unregister as the active listener
        AudioManager audioManager = AudioManager.getInstance();
        if (audioManager != null && audioManager.getAudioListener() == this) {
            audioManager.setAudioListener(null);
        }
    }
    
    /**
     * Update the listener
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Update position from entity transform
        Entity entity = getEntity();
        if (entity != null) {
            TransformComponent transform = entity.getComponent(TransformComponent.class);
            if (transform != null) {
                position = new Vector3(transform.getPosition());
            }
        }
    }
    
    // Getters and setters
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    
    public Vector3 getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
    
    public float getVolume() {
        return volume;
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
    }
    
    @Override
    public String toString() {
        return "AudioListener{" +
                "position=" + position +
                ", volume=" + volume +
                '}';
    }
}
