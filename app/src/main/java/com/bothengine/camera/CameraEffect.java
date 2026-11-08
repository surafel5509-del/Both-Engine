package com.bothengine.camera;

import com.bothengine.rendering.Camera;
import com.bothengine.rendering.Color;
import com.bothengine.rendering.Vector3;

/**
 * CameraEffect
 * Base class for camera effects
 * Can be applied to cameras to create various visual effects
 */
public class CameraEffect {
    private String name;
    private Camera camera;
    private boolean enabled;
    private float duration;
    private float timer;
    
    public CameraEffect(String name) {
        this.name = name;
        this.camera = null;
        this.enabled = false;
        this.duration = 0f;
        this.timer = 0f;
    }
    
    /**
     * Create a camera effect with name and duration
     */
    public static CameraEffect create(String name, float duration) {
        CameraEffect effect = new CameraEffect(name);
        effect.setDuration(duration);
        return effect;
    }
    
    /**
     * Initialize the effect
     */
    public void initialize(Camera camera) {
        this.camera = camera;
        onInitialize();
    }
    
    /**
     * Called when effect is initialized
     */
    protected void onInitialize() {
        // Override in subclasses
    }
    
    /**
     * Update the effect
     */
    public void update(float deltaTime) {
        if (!enabled) return;
        
        timer += deltaTime;
        
        if (duration > 0 && timer >= duration) {
            enabled = false;
            timer = 0f;
        }
        
        onUpdate(deltaTime);
    }
    
    /**
     * Called when effect is updated
     */
    protected void onUpdate(float deltaTime) {
        // Override in subclasses
    }
    
    /**
     * Start the effect
     */
    public void start() {
        enabled = true;
        timer = 0f;
        onStart();
    }
    
    /**
     * Called when effect starts
     */
    protected void onStart() {
        // Override in subclasses
    }
    
    /**
     * Stop the effect
     */
    public void stop() {
        enabled = false;
        timer = 0f;
        onStop();
    }
    
    /**
     * Called when effect stops
     */
    protected void onStop() {
        // Override in subclasses
    }
    
    /**
     * Reset the effect
     */
    public void reset() {
        timer = 0f;
        onReset();
    }
    
    /**
     * Called when effect is reset
     */
    protected void onReset() {
        // Override in subclasses
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public float getDuration() {
        return duration;
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
    }
    
    public float getTimer() {
        return timer;
    }
    
    public void setTimer(float timer) {
        this.timer = timer;
    }
    
    public float getProgress() {
        return duration > 0 ? Math.min(1f, timer / duration) : 0f;
    }
    
    @Override
    public String toString() {
        return "CameraEffect{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", duration=" + duration +
                ", timer=" + timer +
                '}';
    }
    
    /**
     * CameraShake effect
     * Shakes the camera for a specified duration and intensity
     */
    public static class CameraShake extends CameraEffect {
        private float intensity;
        private float frequency;
        private Vector3 originalPosition;
        
        public CameraShake() {
            super("CameraShake");
            this.intensity = 1f;
            this.frequency = 10f;
            this.originalPosition = new Vector3();
        }
        
        /**
         * Create a camera shake effect
         */
        public static CameraShake create(float duration, float intensity) {
            CameraShake shake = new CameraShake();
            shake.setDuration(duration);
            shake.setIntensity(intensity);
            return shake;
        }
        
        @Override
        protected void onInitialize() {
            super.onInitialize();
            if (camera != null) {
                originalPosition = camera.getPosition();
            }
        }
        
        @Override
        protected void onStart() {
            super.onStart();
            if (camera != null) {
                originalPosition = camera.getPosition();
            }
        }
        
        @Override
        protected void onUpdate(float deltaTime) {
            super.onUpdate(deltaTime);
            
            if (camera == null) return;
            
            // Calculate shake offset
            float time = timer * frequency;
            float shakeX = (float) (Math.sin(time * 1.3f) * Math.cos(time * 0.7f)) * intensity;
            float shakeY = (float) (Math.sin(time * 0.9f) * Math.cos(time * 1.1f)) * intensity;
            
            // Apply shake
            Vector3 shakeOffset = new Vector3(shakeX, shakeY, 0);
            camera.setPosition(originalPosition.add(shakeOffset));
        }
        
        @Override
        protected void onStop() {
            super.onStop();
            if (camera != null) {
                camera.setPosition(originalPosition);
            }
        }
        
        // Getters and setters
        
        public float getIntensity() {
            return intensity;
        }
        
        public void setIntensity(float intensity) {
            this.intensity = intensity;
        }
        
        public float getFrequency() {
            return frequency;
        }
        
        public void setFrequency(float frequency) {
            this.frequency = frequency;
        }
        
        @Override
        public String toString() {
            return "CameraShake{" +
                    "name='" + getName() + '\'' +
                    ", intensity=" + intensity +
                    ", frequency=" + frequency +
                    ", duration=" + getDuration() +
                    '}';
        }
    }
    
    /**
     * CameraZoom effect
     * Zooms the camera in or out
     */
    public static class CameraZoom extends CameraEffect {
        private float startFOV;
        private float targetFOV;
        private float originalFOV;
        
        public CameraZoom() {
            super("CameraZoom");
            this.startFOV = 60f;
            this.targetFOV = 90f;
            this.originalFOV = 60f;
        }
        
        /**
         * Create a camera zoom effect
         */
        public static CameraZoom create(float duration, float targetFOV) {
            CameraZoom zoom = new CameraZoom();
            zoom.setDuration(duration);
            zoom.setTargetFOV(targetFOV);
            return zoom;
        }
        
        @Override
        protected void onInitialize() {
            super.onInitialize();
            if (camera != null) {
                originalFOV = camera.getFieldOfView();
                startFOV = originalFOV;
            }
        }
        
        @Override
        protected void onStart() {
            super.onStart();
            if (camera != null) {
                startFOV = camera.getFieldOfView();
            }
        }
        
        @Override
        protected void onUpdate(float deltaTime) {
            super.onUpdate(deltaTime);
            
            if (camera == null) return;
            
            // Calculate zoom progress
            float t = getProgress();
            
            // Interpolate FOV
            float currentFOV = startFOV + (targetFOV - startFOV) * t;
            camera.setFieldOfView(currentFOV);
        }
        
        @Override
        protected void onStop() {
            super.onStop();
            if (camera != null) {
                camera.setFieldOfView(originalFOV);
            }
        }
        
        // Getters and setters
        
        public float getStartFOV() {
            return startFOV;
        }
        
        public void setStartFOV(float startFOV) {
            this.startFOV = startFOV;
        }
        
        public float getTargetFOV() {
            return targetFOV;
        }
        
        public void setTargetFOV(float targetFOV) {
            this.targetFOV = targetFOV;
        }
        
        @Override
        public String toString() {
            return "CameraZoom{" +
                    "name='" + getName() + '\'' +
                    ", startFOV=" + startFOV +
                    ", targetFOV=" + targetFOV +
                    ", duration=" + getDuration() +
                    '}';
        }
    }
    
    /**
     * CameraFade effect
     * Fades the camera view (simulated by changing clear color or post-processing)
     */
    public static class CameraFade extends CameraEffect {
        private Color startColor;
        private Color targetColor;
        private Color originalColor;
        
        public CameraFade() {
            super("CameraFade");
            this.startColor = new Color(0f, 0f, 0f, 0f);
            this.targetColor = new Color(0f, 0f, 0f, 1f);
            this.originalColor = new Color(0f, 0f, 0f, 0f);
        }
        
        /**
         * Create a camera fade effect
         */
        public static CameraFade create(float duration, Color targetColor) {
            CameraFade fade = new CameraFade();
            fade.setDuration(duration);
            fade.setTargetColor(targetColor);
            return fade;
        }
        
        @Override
        protected void onStart() {
            super.onStart();
            // Store original color
        }
        
        @Override
        protected void onUpdate(float deltaTime) {
            super.onUpdate(deltaTime);
            
            // Calculate fade progress
            float t = getProgress();
            
            // Interpolate color
            Color currentColor = Color.lerp(startColor, targetColor, t);
            
            // Apply fade (this would be implemented with post-processing)
        }
        
        @Override
        protected void onStop() {
            super.onStop();
            // Restore original color
        }
        
        // Getters and setters
        
        public Color getStartColor() {
            return startColor;
        }
        
        public void setStartColor(Color startColor) {
            this.startColor = startColor;
        }
        
        public Color getTargetColor() {
            return targetColor;
        }
        
        public void setTargetColor(Color targetColor) {
            this.targetColor = targetColor;
        }
        
        @Override
        public String toString() {
            return "CameraFade{" +
                    "name='" + getName() + '\'' +
                    ", targetColor=" + targetColor +
                    ", duration=" + getDuration() +
                    '}';
        }
    }
}
