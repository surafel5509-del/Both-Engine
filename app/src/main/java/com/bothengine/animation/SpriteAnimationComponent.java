package com.bothengine.animation;

import com.bothengine.core.ecs.Component;

/**
 * SpriteAnimationComponent
 * ECS component for 2D sprite animation
 * Works with SpriteRendererComponent to display animated sprites
 */
public class SpriteAnimationComponent extends Component {
    private SpriteAnimation animation;
    private String currentClipName;
    private boolean playing;
    private float speed;
    
    public SpriteAnimationComponent() {
        this.animation = null;
        this.currentClipName = null;
        this.playing = false;
        this.speed = 1.0f;
    }
    
    /**
     * Create a sprite animation component with a sprite animation
     */
    public static SpriteAnimationComponent create(SpriteAnimation animation) {
        SpriteAnimationComponent component = new SpriteAnimationComponent();
        component.setAnimation(animation);
        return component;
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
    }
    
    /**
     * Update the sprite animation
     */
    @Override
    public void onUpdate(float deltaTime) {
        if (!isEnabled() || animation == null) return;
        
        animation.setSpeed(speed);
        animation.update(deltaTime);
    }
    
    /**
     * Play an animation clip by name
     */
    public void play(String clipName) {
        if (animation == null) return;
        
        currentClipName = clipName;
        playing = true;
        animation.play(clipName);
    }
    
    /**
     * Play the default animation
     */
    public void play() {
        if (animation == null) return;
        
        playing = true;
        if (currentClipName != null) {
            animation.play(currentClipName);
        } else if (!animation.getClips().isEmpty()) {
            animation.play(animation.getClips().get(0).getName());
        }
    }
    
    /**
     * Stop the animation
     */
    public void stop() {
        if (animation == null) return;
        
        playing = false;
        animation.stop();
    }
    
    /**
     * Pause the animation
     */
    public void pause() {
        if (animation == null) return;
        
        playing = false;
        animation.pause();
    }
    
    /**
     * Resume the animation
     */
    public void resume() {
        if (animation == null) return;
        
        playing = true;
        animation.resume();
    }
    
    /**
     * Set the animation speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
        if (animation != null) {
            animation.setSpeed(speed);
        }
    }
    
    /**
     * Set the animation loop mode
     */
    public void setLoop(boolean loop) {
        if (animation != null) {
            animation.setLoop(loop);
        }
    }
    
    /**
     * Set the animation ping pong mode
     */
    public void setPingPong(boolean pingPong) {
        if (animation != null) {
            animation.setPingPong(pingPong);
        }
    }
    
    /**
     * Set the current frame index
     */
    public void setFrameIndex(int index) {
        if (animation != null) {
            animation.setCurrentFrameIndex(index);
        }
    }
    
    // Getters and setters
    
    public SpriteAnimation getAnimation() {
        return animation;
    }
    
    public void setAnimation(SpriteAnimation animation) {
        this.animation = animation;
    }
    
    public String getCurrentClipName() {
        return currentClipName;
    }
    
    public void setCurrentClipName(String currentClipName) {
        this.currentClipName = currentClipName;
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public SpriteAnimation.SpriteFrame getCurrentFrame() {
        return animation != null ? animation.getCurrentFrame() : null;
    }
    
    @Override
    public String toString() {
        return "SpriteAnimationComponent{" +
                "animation=" + (animation != null ? animation.getName() : "null") +
                ", currentClip='" + currentClipName + '\'' +
                ", playing=" + playing +
                ", speed=" + speed +
                '}';
    }
}
