package com.bothengine.audio;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AudioManager
 * Main audio controller for the engine
 * Manages sound effects, music, and 3D audio
 * Similar to Unity's AudioManager
 */
public class AudioManager {
    private static final String TAG = "AudioManager";
    
    private static AudioManager instance;
    
    private Context context;
    private SoundPool soundPool;
    private Map<String, Integer> soundIds;
    private Map<String, AudioClip> audioClips;
    
    private List<AudioSource> activeSources;
    private AudioListener audioListener;
    
    private boolean enabled;
    private float masterVolume;
    private float musicVolume;
    private float sfxVolume;
    
    private int maxSoundSources;
    private int maxMusicSources;
    
    public AudioManager(Context context) {
        this.context = context;
        this.soundIds = new HashMap<>();
        this.audioClips = new HashMap<>();
        this.activeSources = new ArrayList<>();
        this.audioListener = null;
        this.enabled = true;
        this.masterVolume = 1.0f;
        this.musicVolume = 1.0f;
        this.sfxVolume = 1.0f;
        this.maxSoundSources = 32;
        this.maxMusicSources = 8;
        
        // Initialize SoundPool
        initializeSoundPool();
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized AudioManager getInstance() {
        return instance;
    }
    
    /**
     * Initialize the audio manager
     */
    public static void initialize(Context context) {
        if (instance == null) {
            instance = new AudioManager(context);
            Log.i(TAG, "AudioManager initialized");
        }
    }
    
    /**
     * Initialize SoundPool
     */
    private void initializeSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
            
            soundPool = new SoundPool.Builder()
                .setMaxStreams(maxSoundSources)
                .setAudioAttributes(audioAttributes)
                .build();
        } else {
            soundPool = new SoundPool(maxSoundSources, AudioManager.STREAM_MUSIC, 0);
        }
        
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            Log.d(TAG, "Sound loaded: " + sampleId + " status: " + status);
        });
    }
    
    /**
     * Load an audio clip
     */
    public AudioClip loadAudioClip(String name, int resourceId) {
        if (audioClips.containsKey(name)) {
            return audioClips.get(name);
        }
        
        AudioClip clip = new AudioClip(name, resourceId);
        int soundId = soundPool.load(context, resourceId, 1);
        
        soundIds.put(name, soundId);
        audioClips.put(name, clip);
        
        Log.d(TAG, "Loaded audio clip: " + name + " (ID: " + soundId + ")");
        return clip;
    }
    
    /**
     * Load an audio clip from file path
     */
    public AudioClip loadAudioClip(String name, String filePath) {
        if (audioClips.containsKey(name)) {
            return audioClips.get(name);
        }
        
        AudioClip clip = new AudioClip(name, filePath);
        // In a real implementation, we'd load from file
        // For now, we'll just create the clip
        audioClips.put(name, clip);
        
        Log.d(TAG, "Loaded audio clip from file: " + name);
        return clip;
    }
    
    /**
     * Play a sound effect
     */
    public int playSound(String name) {
        return playSound(name, 1.0f, 1.0f, 0, 0, 1.0f);
    }
    
    /**
     * Play a sound effect with parameters
     */
    public int playSound(String name, float volume, float pitch, int loop, int priority, float pan) {
        if (!enabled) return -1;
        
        Integer soundId = soundIds.get(name);
        if (soundId == null) {
            Log.w(TAG, "Sound not loaded: " + name);
            return -1;
        }
        
        float finalVolume = masterVolume * sfxVolume * volume;
        
        int streamId = soundPool.play(
            soundId,
            finalVolume,
            finalVolume,
            priority,
            loop,
            pitch
        );
        
        if (streamId > 0) {
            AudioSource source = new AudioSource(name, streamId, soundId, AudioSourceType.SFX);
            source.setVolume(volume);
            source.setPitch(pitch);
            source.setLoop(loop != 0);
            activeSources.add(source);
        }
        
        return streamId;
    }
    
    /**
     * Play music
     */
    public MediaPlayer playMusic(String name, boolean loop) {
        if (!enabled) return null;
        
        AudioClip clip = audioClips.get(name);
        if (clip == null) {
            Log.w(TAG, "Music not loaded: " + name);
            return null;
        }
        
        MediaPlayer mediaPlayer = MediaPlayer.create(context, clip.getResourceId());
        if (mediaPlayer == null) return null;
        
        mediaPlayer.setLooping(loop);
        mediaPlayer.setVolume(masterVolume * musicVolume);
        
        AudioSource source = new AudioSource(name, mediaPlayer, AudioSourceType.MUSIC);
        source.setLoop(loop);
        activeSources.add(source);
        
        mediaPlayer.start();
        
        mediaPlayer.setOnCompletionListener(mp -> {
            activeSources.remove(source);
            mp.release();
        });
        
        return mediaPlayer;
    }
    
    /**
     * Play music with fade in
     */
    public MediaPlayer playMusicWithFade(String name, boolean loop, float fadeDuration) {
        MediaPlayer mediaPlayer = playMusic(name, loop);
        if (mediaPlayer != null) {
            startFade(mediaPlayer, 0f, masterVolume * musicVolume, fadeDuration);
        }
        return mediaPlayer;
    }
    
    /**
     * Stop a sound by stream ID
     */
    public void stopSound(int streamId) {
        soundPool.stop(streamId);
        
        for (int i = 0; i < activeSources.size(); i++) {
            AudioSource source = activeSources.get(i);
            if (source.getStreamId() == streamId) {
                activeSources.remove(i);
                break;
            }
        }
    }
    
    /**
     * Stop all sounds
     */
    public void stopAllSounds() {
        for (AudioSource source : activeSources) {
            if (source.getType() == AudioSourceType.SFX) {
                soundPool.stop(source.getStreamId());
            }
        }
        activeSources.clear();
    }
    
    /**
     * Stop all music
     */
    public void stopAllMusic() {
        for (int i = 0; i < activeSources.size(); i++) {
            AudioSource source = activeSources.get(i);
            if (source.getType() == AudioSourceType.MUSIC && source.getMediaPlayer() != null) {
                source.getMediaPlayer().stop();
                source.getMediaPlayer().release();
                activeSources.remove(i);
                i--;
            }
        }
    }
    
    /**
     * Stop all audio
     */
    public void stopAll() {
        stopAllSounds();
        stopAllMusic();
    }
    
    /**
     * Pause all audio
     */
    public void pauseAll() {
        for (AudioSource source : activeSources) {
            if (source.getType() == AudioSourceType.MUSIC && source.getMediaPlayer() != null) {
                source.getMediaPlayer().pause();
            }
        }
    }
    
    /**
     * Resume all audio
     */
    public void resumeAll() {
        for (AudioSource source : activeSources) {
            if (source.getType() == AudioSourceType.MUSIC && source.getMediaPlayer() != null) {
                source.getMediaPlayer().start();
            }
        }
    }
    
    /**
     * Set 3D audio listener
     */
    public void setAudioListener(AudioListener listener) {
        this.audioListener = listener;
    }
    
    /**
     * Update 3D audio positions
     */
    public void update(float deltaTime) {
        if (audioListener == null) return;
        
        // Update 3D audio for all active sources
        for (AudioSource source : activeSources) {
            if (source.getType() == AudioSourceType.SFX_3D) {
                // Calculate 3D audio parameters based on listener and source positions
                // This would use the audio listener's position and the source's position
                // to calculate volume and pan for 3D effect
            }
        }
    }
    
    /**
     * Start a fade effect
     */
    private void startFade(MediaPlayer mediaPlayer, float fromVolume, float toVolume, float duration) {
        // In a real implementation, this would gradually change the volume
        // For now, we'll just set the final volume
        mediaPlayer.setVolume(toVolume, toVolume);
    }
    
    /**
     * Unload an audio clip
     */
    public void unloadAudioClip(String name) {
        Integer soundId = soundIds.remove(name);
        if (soundId != null) {
            soundPool.unload(soundId);
        }
        audioClips.remove(name);
    }
    
    /**
     * Unload all audio clips
     */
    public void unloadAll() {
        for (Integer soundId : soundIds.values()) {
            soundPool.unload(soundId);
        }
        soundIds.clear();
        audioClips.clear();
    }
    
    /**
     * Shutdown the audio manager
     */
    public void shutdown() {
        stopAll();
        unloadAll();
        
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
        
        instance = null;
        Log.i(TAG, "AudioManager shutdown");
    }
    
    // Getters and setters
    
    public Context getContext() {
        return context;
    }
    
    public SoundPool getSoundPool() {
        return soundPool;
    }
    
    public AudioListener getAudioListener() {
        return audioListener;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public float getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolume(float masterVolume) {
        this.masterVolume = Math.max(0f, Math.min(1f, masterVolume));
    }
    
    public float getMusicVolume() {
        return musicVolume;
    }
    
    public void setMusicVolume(float musicVolume) {
        this.musicVolume = Math.max(0f, Math.min(1f, musicVolume));
    }
    
    public float getSfxVolume() {
        return sfxVolume;
    }
    
    public void setSfxVolume(float sfxVolume) {
        this.sfxVolume = Math.max(0f, Math.min(1f, sfxVolume));
    }
    
    public int getMaxSoundSources() {
        return maxSoundSources;
    }
    
    public void setMaxSoundSources(int maxSoundSources) {
        this.maxSoundSources = maxSoundSources;
    }
    
    public int getMaxMusicSources() {
        return maxMusicSources;
    }
    
    public void setMaxMusicSources(int maxMusicSources) {
        this.maxMusicSources = maxMusicSources;
    }
    
    public List<AudioSource> getActiveSources() {
        return new ArrayList<>(activeSources);
    }
    
    public AudioClip getAudioClip(String name) {
        return audioClips.get(name);
    }
    
    @Override
    public String toString() {
        return "AudioManager{" +
                "enabled=" + enabled +
                ", masterVolume=" + masterVolume +
                ", musicVolume=" + musicVolume +
                ", sfxVolume=" + sfxVolume +
                ", audioClips=" + audioClips.size() +
                ", activeSources=" + activeSources.size() +
                '}';
    }
    
    /**
     * Audio source type enum
     */
    public enum AudioSourceType {
        SFX,
        SFX_3D,
        MUSIC
    }
}
