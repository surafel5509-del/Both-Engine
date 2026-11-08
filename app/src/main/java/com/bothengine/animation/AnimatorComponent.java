package com.bothengine.animation;

import com.bothengine.components.TransformComponent;
import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Vector3;
import com.bothengine.components.Quaternion;

import java.util.HashMap;
import java.util.Map;

/**
 * AnimatorComponent
 * ECS component for animation playback
 * Applies animation data to TransformComponent and other properties
 */
public class AnimatorComponent extends Component {
    private AnimationController controller;
    private Map<String, String> parameterDefaults;
    private Map<String, Float> floatParameters;
    private Map<String, Boolean> boolParameters;
    private Map<String, Integer> intParameters;
    private Map<String, TriggerParameter> triggerParameters;
    
    private TransformComponent cachedTransform;
    private boolean applyRootMotion;
    private Vector3 rootMotionPosition;
    private Quaternion rootMotionRotation;
    
    public AnimatorComponent() {
        this.controller = new AnimationController();
        this.parameterDefaults = new HashMap<>();
        this.floatParameters = new HashMap<>();
        this.boolParameters = new HashMap<>();
        this.intParameters = new HashMap<>();
        this.triggerParameters = new HashMap<>();
        this.cachedTransform = null;
        this.applyRootMotion = false;
        this.rootMotionPosition = new Vector3();
        this.rootMotionRotation = new Quaternion();
    }
    
    /**
     * Create an animator component with an animation controller
     */
    public static AnimatorComponent create(AnimationController controller) {
        AnimatorComponent component = new AnimatorComponent();
        component.setController(controller);
        return component;
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        cachedTransform = getEntity().getComponent(TransformComponent.class);
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        cachedTransform = null;
    }
    
    /**
     * Update the animator
     */
    @Override
    public void onUpdate(float deltaTime) {
        if (!isEnabled()) return;
        
        // Update the animation controller
        controller.update(deltaTime);
        
        // Apply animation to transform
        applyAnimationToTransform();
        
        // Apply root motion if enabled
        if (applyRootMotion && cachedTransform != null) {
            applyRootMotion();
        }
        
        // Reset trigger parameters
        resetTriggers();
    }
    
    /**
     * Apply animation data to the transform component
     */
    private void applyAnimationToTransform() {
        if (cachedTransform == null) {
            cachedTransform = getEntity().getComponent(TransformComponent.class);
            if (cachedTransform == null) return;
        }
        
        AnimationController.AnimationState currentState = controller.getCurrentState();
        AnimationController.AnimationState nextState = controller.getNextState();
        
        if (currentState == null) return;
        
        AnimationClip clip = currentState.getClip();
        float normalizedTime = currentState.getNormalizedTime();
        float currentTime = currentState.getTime();
        
        // Sample animation at current time
        Vector3 position = clip.samplePosition("root", currentTime);
        Quaternion rotation = clip.sampleRotation("root", currentTime);
        Vector3 scale = clip.sampleScale("root", currentTime);
        
        // If in transition, blend with next state
        if (controller.isInTransition() && nextState != null) {
            AnimationClip nextClip = nextState.getClip();
            float nextTime = nextState.getTime();
            float blendFactor = controller.getTransitionProgress();
            
            Vector3 nextPosition = nextClip.samplePosition("root", nextTime);
            Quaternion nextRotation = nextClip.sampleRotation("root", nextTime);
            Vector3 nextScale = nextClip.sampleScale("root", nextTime);
            
            if (position != null && nextPosition != null) {
                position = Vector3.lerp(position, nextPosition, blendFactor);
            }
            if (rotation != null && nextRotation != null) {
                rotation = Quaternion.slerp(rotation, nextRotation, blendFactor);
            }
            if (scale != null && nextScale != null) {
                scale = Vector3.lerp(scale, nextScale, blendFactor);
            }
        }
        
        // Apply to transform
        if (position != null) {
            rootMotionPosition = position;
            if (!applyRootMotion) {
                cachedTransform.setPosition(position);
            }
        }
        if (rotation != null) {
            rootMotionRotation = rotation;
            if (!applyRootMotion) {
                cachedTransform.setRotation(rotation);
            }
        }
        if (scale != null) {
            cachedTransform.setScale(scale);
        }
        
        // Apply bone animations if available
        applyBoneAnimations(clip, currentTime, blendFactor);
    }
    
    /**
     * Apply bone animations
     */
    private void applyBoneAnimations(AnimationClip clip, float currentTime, float blendFactor) {
        // This would be implemented with a skeletal mesh system
        // For now, we just handle the root bone
    }
    
    /**
     * Apply root motion to the transform
     */
    private void applyRootMotion() {
        if (cachedTransform == null) return;
        
        // Store current position and rotation
        Vector3 currentPos = cachedTransform.getPosition();
        Quaternion currentRot = cachedTransform.getRotation();
        
        // Apply root motion delta
        cachedTransform.setPosition(currentPos.add(rootMotionPosition));
        cachedTransform.setRotation(currentRot.multiply(rootMotionRotation));
        
        // Reset root motion
        rootMotionPosition = new Vector3();
        rootMotionRotation = new Quaternion();
    }
    
    /**
     * Reset trigger parameters after they've been processed
     */
    private void resetTriggers() {
        for (Map.Entry<String, TriggerParameter> entry : triggerParameters.entrySet()) {
            if (entry.getValue().isSet()) {
                entry.getValue().reset();
            }
        }
    }
    
    /**
     * Play an animation by name
     */
    public void play(String stateName) {
        controller.play(stateName);
    }
    
    /**
     * Play an animation with crossfade
     */
    public void play(String stateName, float transitionDuration) {
        controller.play(stateName, transitionDuration);
    }
    
    /**
     * Crossfade to another animation
     */
    public void crossFade(String stateName, float transitionDuration) {
        controller.crossFade(stateName, transitionDuration);
    }
    
    /**
     * Stop all animations
     */
    public void stop() {
        controller.stop();
    }
    
    /**
     * Set a float parameter
     */
    public void setFloat(String name, float value) {
        floatParameters.put(name, value);
    }
    
    /**
     * Get a float parameter
     */
    public float getFloat(String name) {
        return floatParameters.getOrDefault(name, 0f);
    }
    
    /**
     * Set a bool parameter
     */
    public void setBool(String name, boolean value) {
        boolParameters.put(name, value);
    }
    
    /**
     * Get a bool parameter
     */
    public boolean getBool(String name) {
        return boolParameters.getOrDefault(name, false);
    }
    
    /**
     * Set an int parameter
     */
    public void setInteger(String name, int value) {
        intParameters.put(name, value);
    }
    
    /**
     * Get an int parameter
     */
    public int getInteger(String name) {
        return intParameters.getOrDefault(name, 0);
    }
    
    /**
     * Set a trigger parameter
     */
    public void setTrigger(String name) {
        TriggerParameter trigger = triggerParameters.get(name);
        if (trigger == null) {
            trigger = new TriggerParameter();
            triggerParameters.put(name, trigger);
        }
        trigger.set();
    }
    
    /**
     * Reset a trigger parameter
     */
    public void resetTrigger(String name) {
        TriggerParameter trigger = triggerParameters.get(name);
        if (trigger != null) {
            trigger.reset();
        }
    }
    
    /**
     * Check if a trigger was set
     */
    public boolean getTrigger(String name) {
        TriggerParameter trigger = triggerParameters.get(name);
        return trigger != null && trigger.isSet();
    }
    
    /**
     * Set the animation speed
     */
    public void setSpeed(float speed) {
        controller.setSpeed(speed);
    }
    
    /**
     * Get the animation speed
     */
    public float getSpeed() {
        return controller.getSpeed();
    }
    
    /**
     * Check if animation is playing
     */
    public boolean isPlaying() {
        return controller.isPlaying();
    }
    
    /**
     * Get the current animation clip name
     */
    public String getCurrentClipName() {
        AnimationClip clip = controller.getCurrentClip();
        return clip != null ? clip.getName() : null;
    }
    
    /**
     * Get the current normalized time
     */
    public float getNormalizedTime() {
        return controller.getNormalizedTime();
    }
    
    /**
     * Set the current normalized time
     */
    public void setNormalizedTime(float normalizedTime) {
        controller.setNormalizedTime(normalizedTime);
    }
    
    /**
     * Enable or disable root motion
     */
    public void setApplyRootMotion(boolean applyRootMotion) {
        this.applyRootMotion = applyRootMotion;
    }
    
    /**
     * Check if root motion is applied
     */
    public boolean isApplyRootMotion() {
        return applyRootMotion;
    }
    
    // Getters and setters
    
    public AnimationController getController() {
        return controller;
    }
    
    public void setController(AnimationController controller) {
        this.controller = controller;
    }
    
    public TransformComponent getCachedTransform() {
        return cachedTransform;
    }
    
    public void setCachedTransform(TransformComponent cachedTransform) {
        this.cachedTransform = cachedTransform;
    }
    
    @Override
    public String toString() {
        return "AnimatorComponent{" +
                "controller=" + controller +
                ", applyRootMotion=" + applyRootMotion +
                ", currentClip=" + getCurrentClipName() +
                '}';
    }
    
    // Trigger parameter class
    private static class TriggerParameter {
        private boolean set;
        
        public TriggerParameter() {
            this.set = false;
        }
        
        public void set() {
            this.set = true;
        }
        
        public void reset() {
            this.set = false;
        }
        
        public boolean isSet() {
            return set;
        }
    }
}
