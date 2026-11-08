package com.bothengine.animation;

import com.bothengine.rendering.Vector3;
import com.bothengine.components.Quaternion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnimationClip
 * Stores animation data with keyframes for properties
 * Supports position, rotation, scale, and custom property animation
 */
public class AnimationClip {
    private String name;
    private float length;
    private float frameRate;
    private boolean loop;
    private boolean pingPong;
    
    private Map<String, PositionKeyframe[]> positionTracks;
    private Map<String, RotationKeyframe[]> rotationTracks;
    private Map<String, ScaleKeyframe[]> scaleTracks;
    private Map<String, FloatKeyframe[]> floatTracks;
    private Map<String, Vector3Keyframe[]> vector3Tracks;
    private Map<String, ColorKeyframe[]> colorTracks;
    
    public AnimationClip(String name) {
        this.name = name;
        this.length = 0f;
        this.frameRate = 30f;
        this.loop = true;
        this.pingPong = false;
        this.positionTracks = new HashMap<>();
        this.rotationTracks = new HashMap<>();
        this.scaleTracks = new HashMap<>();
        this.floatTracks = new HashMap<>();
        this.vector3Tracks = new HashMap<>();
        this.colorTracks = new HashMap<>();
    }
    
    /**
     * Create an animation clip with name and length
     */
    public static AnimationClip create(String name, float length) {
        AnimationClip clip = new AnimationClip(name);
        clip.setLength(length);
        return clip;
    }
    
    /**
     * Add a position track for a bone or object
     */
    public void addPositionTrack(String trackName, PositionKeyframe[] keyframes) {
        positionTracks.put(trackName, keyframes);
        updateLength();
    }
    
    /**
     * Add a rotation track for a bone or object
     */
    public void addRotationTrack(String trackName, RotationKeyframe[] keyframes) {
        rotationTracks.put(trackName, keyframes);
        updateLength();
    }
    
    /**
     * Add a scale track for a bone or object
     */
    public void addScaleTrack(String trackName, ScaleKeyframe[] keyframes) {
        scaleTracks.put(trackName, keyframes);
        updateLength();
    }
    
    /**
     * Add a float property track
     */
    public void addFloatTrack(String propertyName, FloatKeyframe[] keyframes) {
        floatTracks.put(propertyName, keyframes);
        updateLength();
    }
    
    /**
     * Add a Vector3 property track
     */
    public void addVector3Track(String propertyName, Vector3Keyframe[] keyframes) {
        vector3Tracks.put(propertyName, keyframes);
        updateLength();
    }
    
    /**
     * Add a color property track
     */
    public void addColorTrack(String propertyName, ColorKeyframe[] keyframes) {
        colorTracks.put(propertyName, keyframes);
        updateLength();
    }
    
    /**
     * Update the clip length based on all tracks
     */
    private void updateLength() {
        float maxLength = 0f;
        
        for (PositionKeyframe[] keyframes : positionTracks.values()) {
            if (keyframes.length > 0) {
                maxLength = Math.max(maxLength, keyframes[keyframes.length - 1].time);
            }
        }
        
        for (RotationKeyframe[] keyframes : rotationTracks.values()) {
            if (keyframes.length > 0) {
                maxLength = Math.max(maxLength, keyframes[keyframes.length - 1].time);
            }
        }
        
        for (ScaleKeyframe[] keyframes : scaleTracks.values()) {
            if (keyframes.length > 0) {
                maxLength = Math.max(maxLength, keyframes[keyframes.length - 1].time);
            }
        }
        
        for (FloatKeyframe[] keyframes : floatTracks.values()) {
            if (keyframes.length > 0) {
                maxLength = Math.max(maxLength, keyframes[keyframes.length - 1].time);
            }
        }
        
        for (Vector3Keyframe[] keyframes : vector3Tracks.values()) {
            if (keyframes.length > 0) {
                maxLength = Math.max(maxLength, keyframes[keyframes.length - 1].time);
            }
        }
        
        for (ColorKeyframe[] keyframes : colorTracks.values()) {
            if (keyframes.length > 0) {
                maxLength = Math.max(maxLength, keyframes[keyframes.length - 1].time);
            }
        }
        
        this.length = maxLength;
    }
    
    /**
     * Sample position at a given time
     */
    public Vector3 samplePosition(String trackName, float time) {
        PositionKeyframe[] keyframes = positionTracks.get(trackName);
        if (keyframes == null || keyframes.length == 0) {
            return null;
        }
        return sampleKeyframes(keyframes, time);
    }
    
    /**
     * Sample rotation at a given time
     */
    public Quaternion sampleRotation(String trackName, float time) {
        RotationKeyframe[] keyframes = rotationTracks.get(trackName);
        if (keyframes == null || keyframes.length == 0) {
            return null;
        }
        return sampleKeyframes(keyframes, time);
    }
    
    /**
     * Sample scale at a given time
     */
    public Vector3 sampleScale(String trackName, float time) {
        ScaleKeyframe[] keyframes = scaleTracks.get(trackName);
        if (keyframes == null || keyframes.length == 0) {
            return null;
        }
        return sampleKeyframes(keyframes, time);
    }
    
    /**
     * Sample float value at a given time
     */
    public float sampleFloat(String propertyName, float time) {
        FloatKeyframe[] keyframes = floatTracks.get(propertyName);
        if (keyframes == null || keyframes.length == 0) {
            return 0f;
        }
        return sampleKeyframes(keyframes, time);
    }
    
    /**
     * Sample Vector3 value at a given time
     */
    public Vector3 sampleVector3(String propertyName, float time) {
        Vector3Keyframe[] keyframes = vector3Tracks.get(propertyName);
        if (keyframes == null || keyframes.length == 0) {
            return null;
        }
        return sampleKeyframes(keyframes, time);
    }
    
    /**
     * Sample color value at a given time
     */
    public Color sampleColor(String propertyName, float time) {
        ColorKeyframe[] keyframes = colorTracks.get(propertyName);
        if (keyframes == null || keyframes.length == 0) {
            return null;
        }
        return sampleKeyframes(keyframes, time);
    }
    
    /**
     * Generic keyframe sampling with linear interpolation
     */
    @SuppressWarnings("unchecked")
    private <T extends Keyframe> T sampleKeyframes(Keyframe[] keyframes, float time) {
        if (keyframes.length == 1) {
            return (T) keyframes[0];
        }
        
        // Clamp time
        time = Math.max(0, Math.min(time, length));
        
        // Find the two keyframes to interpolate between
        int index = 0;
        for (int i = 0; i < keyframes.length - 1; i++) {
            if (time >= keyframes[i].time && time <= keyframes[i + 1].time) {
                index = i;
                break;
            }
        }
        
        // If time is beyond last keyframe, return last keyframe
        if (index >= keyframes.length - 1) {
            return (T) keyframes[keyframes.length - 1];
        }
        
        // Linear interpolation
        float t = (time - keyframes[index].time) / (keyframes[index + 1].time - keyframes[index].time);
        return (T) keyframes[index].interpolate(keyframes[index + 1], t);
    }
    
    /**
     * Check if this clip has a position track for the given name
     */
    public boolean hasPositionTrack(String trackName) {
        return positionTracks.containsKey(trackName);
    }
    
    /**
     * Check if this clip has a rotation track for the given name
     */
    public boolean hasRotationTrack(String trackName) {
        return rotationTracks.containsKey(trackName);
    }
    
    /**
     * Check if this clip has a scale track for the given name
     */
    public boolean hasScaleTrack(String trackName) {
        return scaleTracks.containsKey(trackName);
    }
    
    /**
     * Check if this clip has a float track for the given property
     */
    public boolean hasFloatTrack(String propertyName) {
        return floatTracks.containsKey(propertyName);
    }
    
    /**
     * Check if this clip has a Vector3 track for the given property
     */
    public boolean hasVector3Track(String propertyName) {
        return vector3Tracks.containsKey(propertyName);
    }
    
    /**
     * Check if this clip has a color track for the given property
     */
    public boolean hasColorTrack(String propertyName) {
        return colorTracks.containsKey(propertyName);
    }
    
    /**
     * Get all position track names
     */
    public List<String> getPositionTrackNames() {
        return new ArrayList<>(positionTracks.keySet());
    }
    
    /**
     * Get all rotation track names
     */
    public List<String> getRotationTrackNames() {
        return new ArrayList<>(rotationTracks.keySet());
    }
    
    /**
     * Get all scale track names
     */
    public List<String> getScaleTrackNames() {
        return new ArrayList<>(scaleTracks.keySet());
    }
    
    /**
     * Get all float track names
     */
    public List<String> getFloatTrackNames() {
        return new ArrayList<>(floatTracks.keySet());
    }
    
    /**
     * Get all Vector3 track names
     */
    public List<String> getVector3TrackNames() {
        return new ArrayList<>(vector3Tracks.keySet());
    }
    
    /**
     * Get all color track names
     */
    public List<String> getColorTrackNames() {
        return new ArrayList<>(colorTracks.keySet());
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public float getLength() {
        return length;
    }
    
    public void setLength(float length) {
        this.length = length;
    }
    
    public float getFrameRate() {
        return frameRate;
    }
    
    public void setFrameRate(float frameRate) {
        this.frameRate = frameRate;
    }
    
    public boolean isLoop() {
        return loop;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    public boolean isPingPong() {
        return pingPong;
    }
    
    public void setPingPong(boolean pingPong) {
        this.pingPong = pingPong;
    }
    
    @Override
    public String toString() {
        return "AnimationClip{" +
                "name='" + name + '\'' +
                ", length=" + length +
                ", frameRate=" + frameRate +
                ", loop=" + loop +
                ", pingPong=" + pingPong +
                ", positionTracks=" + positionTracks.size() +
                ", rotationTracks=" + rotationTracks.size() +
                ", scaleTracks=" + scaleTracks.size() +
                '}';
    }
    
    // Keyframe base class
    public static abstract class Keyframe<T> {
        public float time;
        public T value;
        
        public Keyframe(float time, T value) {
            this.time = time;
            this.value = value;
        }
        
        public abstract T interpolate(Keyframe<T> next, float t);
    }
    
    // Position keyframe
    public static class PositionKeyframe extends Keyframe<Vector3> {
        public PositionKeyframe(float time, Vector3 position) {
            super(time, position);
        }
        
        @Override
        public Vector3 interpolate(Keyframe<Vector3> next, float t) {
            PositionKeyframe nextFrame = (PositionKeyframe) next;
            return Vector3.lerp(value, nextFrame.value, t);
        }
    }
    
    // Rotation keyframe with spherical linear interpolation
    public static class RotationKeyframe extends Keyframe<Quaternion> {
        public RotationKeyframe(float time, Quaternion rotation) {
            super(time, rotation);
        }
        
        @Override
        public Quaternion interpolate(Keyframe<Quaternion> next, float t) {
            RotationKeyframe nextFrame = (RotationKeyframe) next;
            return Quaternion.slerp(value, nextFrame.value, t);
        }
    }
    
    // Scale keyframe
    public static class ScaleKeyframe extends Keyframe<Vector3> {
        public ScaleKeyframe(float time, Vector3 scale) {
            super(time, scale);
        }
        
        @Override
        public Vector3 interpolate(Keyframe<Vector3> next, float t) {
            ScaleKeyframe nextFrame = (ScaleKeyframe) next;
            return Vector3.lerp(value, nextFrame.value, t);
        }
    }
    
    // Float keyframe
    public static class FloatKeyframe extends Keyframe<Float> {
        public FloatKeyframe(float time, float value) {
            super(time, value);
        }
        
        @Override
        public Float interpolate(Keyframe<Float> next, float t) {
            FloatKeyframe nextFrame = (FloatKeyframe) next;
            return value + (nextFrame.value - value) * t;
        }
    }
    
    // Vector3 keyframe
    public static class Vector3Keyframe extends Keyframe<Vector3> {
        public Vector3Keyframe(float time, Vector3 value) {
            super(time, value);
        }
        
        @Override
        public Vector3 interpolate(Keyframe<Vector3> next, float t) {
            Vector3Keyframe nextFrame = (Vector3Keyframe) next;
            return Vector3.lerp(value, nextFrame.value, t);
        }
    }
    
    // Color keyframe (RGBA)
    public static class ColorKeyframe extends Keyframe<Color> {
        public ColorKeyframe(float time, Color color) {
            super(time, color);
        }
        
        @Override
        public Color interpolate(Keyframe<Color> next, float t) {
            ColorKeyframe nextFrame = (ColorKeyframe) next;
            return Color.lerp(value, nextFrame.value, t);
        }
    }
    
    // Color class for animation
    public static class Color {
        public float r, g, b, a;
        
        public Color(float r, float g, float b, float a) {
            this.r = r;
            this.g = g;
            this.b = b;
            this.a = a;
        }
        
        public Color(float r, float g, float b) {
            this(r, g, b, 1.0f);
        }
        
        public static Color lerp(Color a, Color b, float t) {
            return new Color(
                a.r + (b.r - a.r) * t,
                a.g + (b.g - a.g) * t,
                a.b + (b.b - a.b) * t,
                a.a + (b.a - a.a) * t
            );
        }
        
        @Override
        public String toString() {
            return "Color{r=" + r + ", g=" + g + ", b=" + b + ", a=" + a + "}";
        }
    }
}
