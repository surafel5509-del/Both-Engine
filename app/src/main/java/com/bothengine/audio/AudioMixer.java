package com.bothengine.audio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AudioMixer
 * Manages audio mixing and effects
 * Similar to Unity's AudioMixer
 */
public class AudioMixer {
    private String name;
    private Map<String, AudioMixerGroup> groups;
    private List<AudioEffect> effects;
    
    private float masterVolume;
    private boolean enabled;
    
    public AudioMixer(String name) {
        this.name = name;
        this.groups = new HashMap<>();
        this.effects = new ArrayList<>();
        this.masterVolume = 1.0f;
        this.enabled = true;
    }
    
    /**
     * Create an audio mixer with name
     */
    public static AudioMixer create(String name) {
        return new AudioMixer(name);
    }
    
    /**
     * Create the master mixer
     */
    public static AudioMixer createMaster() {
        return new AudioMixer("Master");
    }
    
    /**
     * Add a mixer group
     */
    public void addGroup(AudioMixerGroup group) {
        groups.put(group.getName(), group);
    }
    
    /**
     * Get a mixer group by name
     */
    public AudioMixerGroup getGroup(String name) {
        return groups.get(name);
    }
    
    /**
     * Remove a mixer group
     */
    public void removeGroup(String name) {
        groups.remove(name);
    }
    
    /**
     * Add an audio effect
     */
    public void addEffect(AudioEffect effect) {
        if (!effects.contains(effect)) {
            effects.add(effect);
        }
    }
    
    /**
     * Remove an audio effect
     */
    public void removeEffect(AudioEffect effect) {
        effects.remove(effect);
    }
    
    /**
     * Process audio through the mixer
     */
    public float[] process(float[] audioData, int sampleRate, int channels) {
        if (!enabled) return audioData;
        
        // Apply master volume
        float[] processed = applyVolume(audioData, masterVolume);
        
        // Apply effects
        for (AudioEffect effect : effects) {
            processed = effect.apply(processed, sampleRate, channels);
        }
        
        return processed;
    }
    
    /**
     * Apply volume to audio data
     */
    private float[] applyVolume(float[] audioData, float volume) {
        float[] result = new float[audioData.length];
        for (int i = 0; i < audioData.length; i++) {
            result[i] = audioData[i] * volume;
        }
        return result;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Map<String, AudioMixerGroup> getGroups() {
        return new HashMap<>(groups);
    }
    
    public List<AudioEffect> getEffects() {
        return new ArrayList<>(effects);
    }
    
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolume(float masterVolume) {
        this.masterVolume = Math.max(0f, Math.min(1f, masterVolume));
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "AudioMixer{" +
                "name='" + name + '\'' +
                ", groups=" + groups.size() +
                ", effects=" + effects.size() +
                ", masterVolume=" + masterVolume +
                '}';
    }
    
    /**
     * AudioMixerGroup class
     * Group of audio sources that share the same mixer settings
     */
    public static class AudioMixerGroup {
        private String name;
        private AudioMixer mixer;
        private float volume;
        private float pitch;
        private boolean muted;
        private boolean solo;
        private boolean bypassEffects;
        
        public AudioMixerGroup(String name, AudioMixer mixer) {
            this.name = name;
            this.mixer = mixer;
            this.volume = 1.0f;
            this.pitch = 1.0f;
            this.muted = false;
            this.solo = false;
            this.bypassEffects = false;
        }
        
        /**
         * Create an audio mixer group
         */
        public static AudioMixerGroup create(String name, AudioMixer mixer) {
            return new AudioMixerGroup(name, mixer);
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public AudioMixer getMixer() {
            return mixer;
        }
        
        public void setMixer(AudioMixer mixer) {
            this.mixer = mixer;
        }
        
        public float getVolume() {
            return volume;
        }
        
        public void setVolume(float volume) {
            this.volume = Math.max(0f, Math.min(1f, volume));
        }
        
        public float getPitch() {
            return pitch;
        }
        
        public void setPitch(float pitch) {
            this.pitch = Math.max(0f, pitch);
        }
        
        public boolean isMuted() {
            return muted;
        }
        
        public void setMuted(boolean muted) {
            this.muted = muted;
        }
        
        public boolean isSolo() {
            return solo;
        }
        
        public void setSolo(boolean solo) {
            this.solo = solo;
        }
        
        public boolean isBypassEffects() {
            return bypassEffects;
        }
        
        public void setBypassEffects(boolean bypassEffects) {
            this.bypassEffects = bypassEffects;
        }
        
        @Override
        public String toString() {
            return "AudioMixerGroup{" +
                    "name='" + name + '\'' +
                    ", volume=" + volume +
                    ", pitch=" + pitch +
                    ", muted=" + muted +
                    '}';
        }
    }
    
    /**
     * AudioEffect class
     * Base class for audio effects
     */
    public static abstract class AudioEffect {
        private String name;
        private boolean enabled;
        private float wetMix;
        private float dryMix;
        
        public AudioEffect(String name) {
            this.name = name;
            this.enabled = true;
            this.wetMix = 0.5f;
            this.dryMix = 0.5f;
        }
        
        /**
         * Apply the effect to audio data
         */
        public abstract float[] apply(float[] audioData, int sampleRate, int channels);
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public boolean isEnabled() {
            return enabled;
        }
        
        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
        
        public float getWetMix() {
            return wetMix;
        }
        
        public void setWetMix(float wetMix) {
            this.wetMix = Math.max(0f, Math.min(1f, wetMix));
        }
        
        public float getDryMix() {
            return dryMix;
        }
        
        public void setDryMix(float dryMix) {
            this.dryMix = Math.max(0f, Math.min(1f, dryMix));
        }
        
        @Override
        public String toString() {
            return "AudioEffect{" +
                    "name='" + name + '\'' +
                    ", enabled=" + enabled +
                    '}';
        }
        
        /**
         * Reverb effect
         */
        public static class ReverbEffect extends AudioEffect {
            private float decayTime;
            private float diffusion;
            private float density;
            
            public ReverbEffect() {
                super("Reverb");
                this.decayTime = 1.0f;
                this.diffusion = 0.5f;
                this.density = 0.5f;
            }
            
            @Override
            public float[] apply(float[] audioData, int sampleRate, int channels) {
                // In a real implementation, this would apply reverb
                // For now, just return the input
                return audioData;
            }
            
            // Getters and setters
            
            public float getDecayTime() {
                return decayTime;
            }
            
            public void setDecayTime(float decayTime) {
                this.decayTime = decayTime;
            }
            
            public float getDiffusion() {
                return diffusion;
            }
            
            public void setDiffusion(float diffusion) {
                this.diffusion = diffusion;
            }
            
            public float getDensity() {
                return density;
            }
            
            public void setDensity(float density) {
                this.density = density;
            }
        }
        
        /**
         * Echo effect
         */
        public static class EchoEffect extends AudioEffect {
            private float delay;
            private float decay;
            
            public EchoEffect() {
                super("Echo");
                this.delay = 0.5f;
                this.decay = 0.5f;
            }
            
            @Override
            public float[] apply(float[] audioData, int sampleRate, int channels) {
                // In a real implementation, this would apply echo
                return audioData;
            }
            
            // Getters and setters
            
            public float getDelay() {
                return delay;
            }
            
            public void setDelay(float delay) {
                this.delay = delay;
            }
            
            public float getDecay() {
                return decay;
            }
            
            public void setDecay(float decay) {
                this.decay = decay;
            }
        }
        
        /**
         * Chorus effect
         */
        public static class ChorusEffect extends AudioEffect {
            private float rate;
            private float depth;
            private float delay;
            
            public ChorusEffect() {
                super("Chorus");
                this.rate = 0.5f;
                this.depth = 0.5f;
                this.delay = 0.02f;
            }
            
            @Override
            public float[] apply(float[] audioData, int sampleRate, int channels) {
                // In a real implementation, this would apply chorus
                return audioData;
            }
            
            // Getters and setters
            
            public float getRate() {
                return rate;
            }
            
            public void setRate(float rate) {
                this.rate = rate;
            }
            
            public float getDepth() {
                return depth;
            }
            
            public void setDepth(float depth) {
                this.depth = depth;
            }
            
            public float getDelay() {
                return delay;
            }
            
            public void setDelay(float delay) {
                this.delay = delay;
            }
        }
    }
}
