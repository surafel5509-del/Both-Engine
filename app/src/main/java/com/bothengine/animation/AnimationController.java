package com.bothengine.animation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnimationController
 * Manages animation playback, blending, and transitions
 * Similar to Unity's Animator Controller
 */
public class AnimationController {
    private static final String TAG = "AnimationController";
    
    private Map<String, AnimationClip> clips;
    private Map<String, AnimationState> states;
    private AnimationState currentState;
    private AnimationState nextState;
    private float transitionTime;
    private float transitionDuration;
    private boolean inTransition;
    
    private float speed;
    private boolean playing;
    private float currentTime;
    
    private List<AnimationEventListener> listeners;
    
    public AnimationController() {
        this.clips = new HashMap<>();
        this.states = new HashMap<>();
        this.currentState = null;
        this.nextState = null;
        this.transitionTime = 0f;
        this.transitionDuration = 0.3f;
        this.inTransition = false;
        this.speed = 1.0f;
        this.playing = false;
        this.currentTime = 0f;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Add an animation clip
     */
    public void addClip(AnimationClip clip) {
        clips.put(clip.getName(), clip);
        states.put(clip.getName(), new AnimationState(clip));
    }
    
    /**
     * Add an animation clip with a custom state name
     */
    public void addClip(String stateName, AnimationClip clip) {
        clips.put(stateName, clip);
        states.put(stateName, new AnimationState(clip));
    }
    
    /**
     * Remove an animation clip
     */
    public void removeClip(String clipName) {
        clips.remove(clipName);
        states.remove(clipName);
    }
    
    /**
     * Get an animation clip by name
     */
    public AnimationClip getClip(String clipName) {
        return clips.get(clipName);
    }
    
    /**
     * Play an animation by name
     */
    public void play(String stateName) {
        play(stateName, 0f, 1.0f);
    }
    
    /**
     * Play an animation with crossfade
     */
    public void play(String stateName, float transitionDuration) {
        play(stateName, 0f, transitionDuration);
    }
    
    /**
     * Play an animation with normalized time and transition
     */
    public void play(String stateName, float normalizedTime, float transitionDuration) {
        AnimationState newState = states.get(stateName);
        if (newState == null) {
            return;
        }
        
        if (currentState != null && currentState.getClip() == newState.getClip()) {
            // Same clip, just restart
            currentState.setNormalizedTime(normalizedTime);
            currentState.setTime(normalizedTime * currentState.getClip().getLength());
            return;
        }
        
        // Start transition
        if (currentState != null && transitionDuration > 0) {
            nextState = newState;
            nextState.setNormalizedTime(normalizedTime);
            nextState.setTime(normalizedTime * nextState.getClip().getLength());
            this.transitionDuration = transitionDuration;
            this.transitionTime = 0f;
            this.inTransition = true;
        } else {
            // Instant switch
            currentState = newState;
            currentState.setNormalizedTime(normalizedTime);
            currentState.setTime(normalizedTime * currentState.getClip().getLength());
            this.inTransition = false;
        }
        
        playing = true;
        fireAnimationStarted(stateName);
    }
    
    /**
     * Crossfade to another animation
     */
    public void crossFade(String stateName, float transitionDuration) {
        crossFade(stateName, transitionDuration, 0f);
    }
    
    /**
     * Crossfade to another animation with queue offset
     */
    public void crossFade(String stateName, float transitionDuration, float queueOffset) {
        AnimationState newState = states.get(stateName);
        if (newState == null) {
            return;
        }
        
        nextState = newState;
        nextState.setNormalizedTime(0f);
        nextState.setTime(0f);
        nextState.setQueueOffset(queueOffset);
        this.transitionDuration = transitionDuration;
        this.transitionTime = 0f;
        this.inTransition = true;
    }
    
    /**
     * Stop all animations
     */
    public void stop() {
        playing = false;
        currentTime = 0f;
        if (currentState != null) {
            currentState.setTime(0f);
            currentState.setNormalizedTime(0f);
        }
        inTransition = false;
        nextState = null;
    }
    
    /**
     * Pause the current animation
     */
    public void pause() {
        playing = false;
    }
    
    /**
     * Resume the current animation
     */
    public void resume() {
        playing = true;
    }
    
    /**
     * Update the animation controller
     */
    public void update(float deltaTime) {
        if (!playing) return;
        
        deltaTime *= speed;
        
        if (inTransition) {
            handleTransition(deltaTime);
        } else if (currentState != null) {
            updateCurrentState(deltaTime);
        }
    }
    
    /**
     * Handle animation transition
     */
    private void handleTransition(float deltaTime) {
        transitionTime += deltaTime;
        
        if (transitionTime >= transitionDuration) {
            // Transition complete
            inTransition = false;
            currentState = nextState;
            currentState.setTime(currentState.getQueueOffset());
            currentState.setNormalizedTime(currentState.getQueueOffset() / currentState.getClip().getLength());
            nextState = null;
            transitionTime = 0f;
            
            if (currentState != null) {
                fireAnimationStarted(currentState.getClip().getName());
            }
        }
    }
    
    /**
     * Update the current animation state
     */
    private void updateCurrentState(float deltaTime) {
        currentTime += deltaTime;
        currentState.setTime(currentState.getTime() + deltaTime);
        
        AnimationClip clip = currentState.getClip();
        float clipLength = clip.getLength();
        
        // Handle animation wrap mode
        if (currentState.getTime() >= clipLength) {
            if (clip.isLoop()) {
                if (clip.isPingPong()) {
                    // Ping pong mode
                    currentState.setDirection(-currentState.getDirection());
                    currentState.setTime(clipLength - (currentState.getTime() - clipLength));
                } else {
                    // Loop mode
                    currentState.setTime(currentState.getTime() - clipLength);
                }
                fireAnimationLooped(clip.getName());
            } else {
                // Clamp to end
                currentState.setTime(clipLength);
                playing = false;
                fireAnimationEnded(clip.getName());
            }
        } else if (currentState.getTime() < 0) {
            if (clip.isLoop()) {
                if (clip.isPingPong()) {
                    // Ping pong mode
                    currentState.setDirection(-currentState.getDirection());
                    currentState.setTime(-currentState.getTime());
                } else {
                    // Loop mode
                    currentState.setTime(clipLength + currentState.getTime());
                }
            } else {
                // Clamp to start
                currentState.setTime(0f);
            }
        }
        
        // Update normalized time
        currentState.setNormalizedTime(currentState.getTime() / clipLength);
    }
    
    /**
     * Get the current animation state
     */
    public AnimationState getCurrentState() {
        return currentState;
    }
    
    /**
     * Get the next animation state (during transition)
     */
    public AnimationState getNextState() {
        return nextState;
    }
    
    /**
     * Check if currently in transition
     */
    public boolean isInTransition() {
        return inTransition;
    }
    
    /**
     * Get transition progress (0-1)
     */
    public float getTransitionProgress() {
        if (!inTransition || transitionDuration <= 0) {
            return 1f;
        }
        return Math.min(1f, transitionTime / transitionDuration);
    }
    
    /**
     * Get the current animation clip
     */
    public AnimationClip getCurrentClip() {
        return currentState != null ? currentState.getClip() : null;
    }
    
    /**
     * Get the current animation time
     */
    public float getCurrentTime() {
        return currentTime;
    }
    
    /**
     * Get the current normalized time (0-1)
     */
    public float getNormalizedTime() {
        return currentState != null ? currentState.getNormalizedTime() : 0f;
    }
    
    /**
     * Set the current normalized time
     */
    public void setNormalizedTime(float normalizedTime) {
        if (currentState != null) {
            currentState.setNormalizedTime(normalizedTime);
            currentState.setTime(normalizedTime * currentState.getClip().getLength());
            currentTime = currentState.getTime();
        }
    }
    
    /**
     * Set the animation speed
     */
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    /**
     * Get the animation speed
     */
    public float getSpeed() {
        return speed;
    }
    
    /**
     * Check if animation is playing
     */
    public boolean isPlaying() {
        return playing;
    }
    
    /**
     * Get all clip names
     */
    public List<String> getClipNames() {
        return new ArrayList<>(clips.keySet());
    }
    
    /**
     * Get all state names
     */
    public List<String> getStateNames() {
        return new ArrayList<>(states.keySet());
    }
    
    /**
     * Add an animation event listener
     */
    public void addListener(AnimationEventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove an animation event listener
     */
    public void removeListener(AnimationEventListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Fire animation started event
     */
    private void fireAnimationStarted(String clipName) {
        for (AnimationEventListener listener : listeners) {
            listener.onAnimationStarted(clipName);
        }
    }
    
    /**
     * Fire animation ended event
     */
    private void fireAnimationEnded(String clipName) {
        for (AnimationEventListener listener : listeners) {
            listener.onAnimationEnded(clipName);
        }
    }
    
    /**
     * Fire animation looped event
     */
    private void fireAnimationLooped(String clipName) {
        for (AnimationEventListener listener : listeners) {
            listener.onAnimationLooped(clipName);
        }
    }
    
    @Override
    public String toString() {
        return "AnimationController{" +
                "clips=" + clips.size() +
                ", currentState=" + (currentState != null ? currentState.getClip().getName() : "null") +
                ", playing=" + playing +
                ", speed=" + speed +
                '}';
    }
    
    // Animation state class
    public static class AnimationState {
        private AnimationClip clip;
        private float time;
        private float normalizedTime;
        private float speed;
        private int direction;
        private float queueOffset;
        
        public AnimationState(AnimationClip clip) {
            this.clip = clip;
            this.time = 0f;
            this.normalizedTime = 0f;
            this.speed = 1.0f;
            this.direction = 1;
            this.queueOffset = 0f;
        }
        
        public AnimationClip getClip() {
            return clip;
        }
        
        public void setClip(AnimationClip clip) {
            this.clip = clip;
        }
        
        public float getTime() {
            return time;
        }
        
        public void setTime(float time) {
            this.time = time;
        }
        
        public float getNormalizedTime() {
            return normalizedTime;
        }
        
        public void setNormalizedTime(float normalizedTime) {
            this.normalizedTime = normalizedTime;
        }
        
        public float getSpeed() {
            return speed;
        }
        
        public void setSpeed(float speed) {
            this.speed = speed;
        }
        
        public int getDirection() {
            return direction;
        }
        
        public void setDirection(int direction) {
            this.direction = direction;
        }
        
        public float getQueueOffset() {
            return queueOffset;
        }
        
        public void setQueueOffset(float queueOffset) {
            this.queueOffset = queueOffset;
        }
    }
    
    // Animation event listener interface
    public interface AnimationEventListener {
        void onAnimationStarted(String clipName);
        void onAnimationEnded(String clipName);
        void onAnimationLooped(String clipName);
    }
}
