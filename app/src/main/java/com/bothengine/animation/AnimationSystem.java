package com.bothengine.animation;

import android.util.Log;

import com.bothengine.components.SpriteRendererComponent;
import com.bothengine.components.TransformComponent;
import com.bothengine.core.EngineConfig;
import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;

import java.util.List;

/**
 * AnimationSystem
 * System for updating all animation components in the ECS
 * Handles both skeletal animation and sprite animation
 */
public class AnimationSystem {
    private static final String TAG = "AnimationSystem";
    
    private final EngineConfig config;
    private EntityManager entityManager;
    private boolean enabled;
    
    public AnimationSystem(EngineConfig config) {
        this.config = config;
        this.entityManager = null;
        this.enabled = true;
        
        Log.i(TAG, "AnimationSystem initialized");
    }
    
    /**
     * Initialize the system
     */
    public void initialize(EntityManager entityManager) {
        this.entityManager = entityManager;
        Log.i(TAG, "AnimationSystem initialized with EntityManager");
    }
    
    /**
     * Update all animation components
     */
    public void update(float deltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Update all entities with AnimatorComponent
        List<Entity> entities = entityManager.getEntitiesWithComponent(AnimatorComponent.class);
        
        for (Entity entity : entities) {
            if (entity.isActive()) {
                AnimatorComponent animator = entity.getComponent(AnimatorComponent.class);
                if (animator != null && animator.isEnabled()) {
                    updateAnimator(animator, deltaTime);
                }
            }
        }
        
        // Update all entities with SpriteAnimationComponent
        entities = entityManager.getEntitiesWithComponent(SpriteAnimationComponent.class);
        
        for (Entity entity : entities) {
            if (entity.isActive()) {
                SpriteAnimationComponent spriteAnimator = entity.getComponent(SpriteAnimationComponent.class);
                if (spriteAnimator != null && spriteAnimator.isEnabled()) {
                    updateSpriteAnimator(spriteAnimator, deltaTime);
                }
            }
        }
    }
    
    /**
     * Update an animator component
     */
    private void updateAnimator(AnimatorComponent animator, float deltaTime) {
        // Update the animation controller
        animator.getController().update(deltaTime);
        
        // The AnimatorComponent.onUpdate will handle applying animation to transform
    }
    
    /**
     * Update a sprite animation component
     */
    private void updateSpriteAnimator(SpriteAnimationComponent spriteAnimator, float deltaTime) {
        // Update the sprite animation
        spriteAnimator.getAnimation().update(deltaTime);
        
        // Apply animation to sprite renderer
        applySpriteAnimation(spriteAnimator);
    }
    
    /**
     * Apply sprite animation to the sprite renderer
     */
    private void applySpriteAnimation(SpriteAnimationComponent spriteAnimator) {
        Entity entity = spriteAnimator.getEntity();
        if (entity == null) return;
        
        SpriteRendererComponent spriteRenderer = entity.getComponent(SpriteRendererComponent.class);
        if (spriteRenderer == null) return;
        
        SpriteAnimation spriteAnimation = spriteAnimator.getAnimation();
        SpriteAnimation.SpriteFrame currentFrame = spriteAnimation.getCurrentFrame();
        
        if (currentFrame != null) {
            // Update sprite texture coordinates
            spriteRenderer.setUvCoords(currentFrame.getTexCoords());
            
            // If the frame has a different texture, update it
            if (currentFrame.getTexture() != null && currentFrame.getTexture() != spriteRenderer.getTexture()) {
                spriteRenderer.setTexture(currentFrame.getTexture());
            }
        }
    }
    
    /**
     * Fixed update for physics-based animations
     */
    public void fixedUpdate(float fixedDeltaTime) {
        if (!enabled || entityManager == null) return;
        
        // Fixed update can be used for physics-based animations
        // or animations that need to be frame-rate independent
    }
    
    /**
     * Shutdown the system
     */
    public void shutdown() {
        entityManager = null;
        enabled = false;
        Log.i(TAG, "AnimationSystem shutdown");
    }
    
    /**
     * Play an animation on an entity
     */
    public void playAnimation(Entity entity, String animationName) {
        if (entity == null) return;
        
        AnimatorComponent animator = entity.getComponent(AnimatorComponent.class);
        if (animator != null) {
            animator.play(animationName);
        }
    }
    
    /**
     * Play an animation on an entity with crossfade
     */
    public void crossFadeAnimation(Entity entity, String animationName, float transitionDuration) {
        if (entity == null) return;
        
        AnimatorComponent animator = entity.getComponent(AnimatorComponent.class);
        if (animator != null) {
            animator.crossFade(animationName, transitionDuration);
        }
    }
    
    /**
     * Stop animation on an entity
     */
    public void stopAnimation(Entity entity) {
        if (entity == null) return;
        
        AnimatorComponent animator = entity.getComponent(AnimatorComponent.class);
        if (animator != null) {
            animator.stop();
        }
    }
    
    /**
     * Check if an entity is playing an animation
     */
    public boolean isPlayingAnimation(Entity entity, String animationName) {
        if (entity == null) return false;
        
        AnimatorComponent animator = entity.getComponent(AnimatorComponent.class);
        if (animator != null) {
            return animationName.equals(animator.getCurrentClipName());
        }
        return false;
    }
    
    // Getters and setters
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
}
