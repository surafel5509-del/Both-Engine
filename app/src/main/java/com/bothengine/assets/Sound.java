package com.bothengine.assets;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Sound
 * Manages sound effects for the game engine
 */
public class Sound {
    private static final String TAG = "Sound";
    
    private static SoundPool soundPool;
    private static boolean initialized;
    private static Map<String, Integer> soundMap;
    
    private String name;
    private String path;
    private int soundId;
    private int streamId;
    private float volume;
    private float pitch;
    private float pan;
    private boolean looping;
    private boolean playing;
    private int priority;
    
    public Sound(Context context, String path) {
        this.path = path;
        this.name = path.substring(path.lastIndexOf('/') + 1);
        this.soundId = -1;
        this.streamId = -1;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.pan = 0.0f;
        this.looping = false;
        this.playing = false;
        this.priority = 1;
        
        // Initialize sound pool if needed
        initializeSoundPool(context);
        
        // Load sound
        load(context);
    }
    
    /**
     * Initialize the sound pool
     */
    private static void initializeSoundPool(Context context) {
        if (initialized) return;
        
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_GAME)
            .build();
        
        soundPool = new SoundPool.Builder()
            .setMaxStreams(16)
            .setAudioAttributes(audioAttributes)
            .build();
        
        soundMap = new HashMap<>();
        initialized = true;
        
        Log.i(TAG, "SoundPool initialized");
    }
    
    /**
     * Load the sound
     */
    private void load(Context context) {
        if (soundPool == null) {
            Log.e(TAG, "SoundPool not initialized");
            return;
        }
        
        try {
            // Get resource ID
            String soundName = name.substring(0, name.lastIndexOf('.'));
            int resId = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
            
            if (resId == 0) {
                Log.e(TAG, "Sound resource not found: " + soundName);
                return;
            }
            
            soundId = soundPool.load(context, resId, priority);
            soundMap.put(path, soundId);
            
            Log.d(TAG, "Sound loaded: " + name + " (ID: " + soundId + ")");
        } catch (Exception e) {
            Log.e(TAG, "Error loading sound: " + path, e);
        }
    }
    
    /**
     * Play the sound
     */
    public int play() {
        if (soundPool == null || soundId <= 0) {
            Log.w(TAG, "Cannot play sound: not loaded");
            return -1;
        }
        
        streamId = soundPool.play(
            soundId,
            volume,
            volume,
            priority,
            looping ? -1 : 0,
            pitch
        );
        
        playing = streamId >= 0;
        Log.d(TAG, "Sound played: " + name + " (Stream ID: " + streamId + ")");
        return streamId;
    }
    
    /**
     * Play the sound with custom parameters
     */
    public int play(float volume, float pitch, float pan, boolean looping) {
        this.volume = volume;
        this.pitch = pitch;
        this.pan = pan;
        this.looping = looping;
        return play();
    }
    
    /**
     * Stop the sound
     */
    public void stop() {
        if (soundPool != null && streamId >= 0) {
            soundPool.stop(streamId);
            streamId = -1;
            playing = false;
            Log.d(TAG, "Sound stopped: " + name);
        }
    }
    
    /**
     * Stop all instances of this sound
     */
    public void stopAll() {
        if (soundPool != null && soundId >= 0) {
            soundPool.stop(soundId);
            Log.d(TAG, "All instances of sound stopped: " + name);
        }
    }
    
    /**
     * Pause the sound
     */
    public void pause() {
        if (soundPool != null && streamId >= 0) {
            soundPool.pause(streamId);
            playing = false;
            Log.d(TAG, "Sound paused: " + name);
        }
    }
    
    /**
     * Resume the sound
     */
    public void resume() {
        if (soundPool != null && streamId >= 0) {
            soundPool.resume(streamId);
            playing = true;
            Log.d(TAG, "Sound resumed: " + name);
        }
    }
    
    /**
     * Set the volume
     */
    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }
    
    /**
     * Set the pitch
     */
    public void setPitch(float pitch) {
        this.pitch = Math.max(0.5f, Math.min(2, pitch));
    }
    
    /**
     * Set the pan
     */
    public void setPan(float pan) {
        this.pan = Math.max(-1, Math.min(1, pan));
    }
    
    /**
     * Set whether the sound should loop
     */
    public void setLooping(boolean looping) {
        this.looping = looping;
    }
    
    /**
     * Release the sound
     */
    public void release() {
        if (soundPool != null && soundId >= 0) {
            soundPool.unload(soundId);
            soundId = -1;
            Log.d(TAG, "Sound unloaded: " + name);
        }
    }
    
    /**
     * Check if the sound is loaded
     */
    public boolean isLoaded() {
        return soundId > 0;
    }
    
    /**
     * Check if the sound is playing
     */
    public boolean isPlaying() {
        return streamId >= 0 && playing;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPath() {
        return path;
    }
    
    public int getSoundId() {
        return soundId;
    }
    
    public int getStreamId() {
        return streamId;
    }
    
    public float getVolume() {
        return volume;
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public float getPan() {
        return pan;
    }
    
    public boolean isLooping() {
        return looping;
    }
    
    public int getPriority() {
        return priority;
    }
    
    public void setPriority(int priority) {
        this.priority = priority;
    }
    
    @Override
    public String toString() {
        return "Sound{" +
                "name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", soundId=" + soundId +
                ", loaded=" + isLoaded() +
                '}';
    }
    
    /**
     * Shutdown the sound system
     */
    public static void shutdown() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        if (soundMap != null) {
            soundMap.clear();
        }
        initialized = false;
        Log.i(TAG, "Sound system shutdown");
    }
}
