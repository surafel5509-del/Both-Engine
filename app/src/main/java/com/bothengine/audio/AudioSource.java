package com.bothengine.audio;

import android.media.MediaPlayer;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Vector3;

/**
 * AudioSource
 * ECS component for playing audio from an entity
 * Similar to Unity's AudioSource
 */
public class AudioSource extends Component {
    private String clipName;
    private AudioClip audioClip;
    private int streamId;
    private MediaPlayer mediaPlayer;
    
    private AudioManager.AudioSourceType type;
    
    private boolean playOnStart;
    private boolean loop;
    private float volume;
    private float pitch;
    private float pan;
    private boolean mute;
    
    // 3D audio settings
    private boolean is3D;
    private Vector3 position;
    private float minDistance;
    private float maxDistance;
    private float rolloffFactor;
    
    // Playback state
    private boolean isPlaying;
    private float time;
    
    public AudioSource() {
        this.clipName = "";
        this.audioClip = null;
        this.streamId = -1;
        this.mediaPlayer = null;
        this.type = AudioManager.AudioSourceType.SFX;
        this.playOnStart = false;
        this.loop = false;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.pan = 0f;
        this.mute = false;
        this.is3D = false;
        this.position = new Vector3();
        this.minDistance = 1f;
        this.maxDistance = 50f;
        this.rolloffFactor = 1f;
        this.isPlaying = false;
        this.time = 0f;
    }
    
    /**
     * Create an audio source with a clip name
     */
    public static AudioSource create(String clipName) {
        AudioSource source = new AudioSource();
        source.setClipName(clipName);
        return source;
    }
    
    /**
     * Create a 3D audio source
     */
    public static AudioSource create3D(String clipName) {
        AudioSource source = create(clipName);
        source.set3D(true);
        return source;
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        
        // Load audio clip if name is set
        if (clipName != null && !clipName.isEmpty()) {
            AudioManager audioManager = AudioManager.getInstance();
            if (audioManager != null) {
                audioClip = audioManager.getAudioClip(clipName);
            }
        }
    }
    
    /**
     * Called when the entity is started
     */
    @Override
    public void onEnable() {
        super.onEnable();
        
        if (playOnStart && !isPlaying) {
            play();
        }
    }
    
    /**
     * Called when the entity is disabled
     */
    @Override
    public void onDisable() {
        super.onDisable();
        
        if (isPlaying) {
            stop();
        }
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        
        stop();
    }
    
    /**
     * Play the audio
     */
    public void play() {
        if (audioClip == null && clipName != null && !clipName.isEmpty()) {
            AudioManager audioManager = AudioManager.getInstance();
            if (audioManager != null) {
                audioClip = audioManager.getAudioClip(clipName);
            }
        }
        
        if (audioClip == null) return;
        
        AudioManager audioManager = AudioManager.getInstance();
        if (audioManager == null) return;
        
        if (mute) return;
        
        // Stop current playback
        stop();
        
        // Determine type
        if (audioClip.is3D() || is3D) {
            type = AudioManager.AudioSourceType.SFX_3D;
        } else if (audioClip.getLength() > 10f) {
            // Long audio = music
            type = AudioManager.AudioSourceType.MUSIC;
        } else {
            type = AudioManager.AudioSourceType.SFX;
        }
        
        // Play based on type
        if (type == AudioManager.AudioSourceType.MUSIC) {
            mediaPlayer = audioManager.playMusic(clipName, loop);
            if (mediaPlayer != null) {
                mediaPlayer.setVolume(volume);
                mediaPlayer.setLooping(loop);
            }
        } else {
            streamId = audioManager.playSound(clipName, volume, pitch, loop ? 1 : 0, 0, pan);
        }
        
        isPlaying = true;
        time = 0f;
    }
    
    /**
     * Play one shot (fire and forget)
     */
    public void playOneShot() {
        if (audioClip == null) return;
        
        AudioManager audioManager = AudioManager.getInstance();
        if (audioManager == null) return;
        
        audioManager.playSound(clipName, volume, pitch, 0, 0, pan);
    }
    
    /**
     * Stop the audio
     */
    public void stop() {
        AudioManager audioManager = AudioManager.getInstance();
        if (audioManager == null) return;
        
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        
        if (streamId >= 0) {
            audioManager.stopSound(streamId);
            streamId = -1;
        }
        
        isPlaying = false;
    }
    
    /**
     * Pause the audio
     */
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }
    
    /**
     * Resume the audio
     */
    public void resume() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }
    
    /**
     * Update the audio source
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        if (!isPlaying) return;
        
        time += deltaTime;
        
        // Update 3D audio position
        if (is3D && getEntity() != null) {
            // Get entity position
            // Update 3D audio parameters
        }
    }
    
    /**
     * Set the audio clip
     */
    public void setAudioClip(AudioClip clip) {
        this.audioClip = clip;
        if (clip != null) {
            this.clipName = clip.getName();
            this.is3D = clip.is3D();
            this.minDistance = clip.getMinDistance();
            this.maxDistance = clip.getMaxDistance();
        }
    }
    
    // Getters and setters
    
    public String getClipName() {
        return clipName;
    }
    
    public void setClipName(String clipName) {
        this.clipName = clipName;
        this.audioClip = null; // Will be loaded when played
    }
    
    public AudioClip getAudioClip() {
        return audioClip;
    }
    
    public int getStreamId() {
        return streamId;
    }
    
    public MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }
    
    public AudioManager.AudioSourceType getType() {
        return type;
    }
    
    public boolean isPlayOnStart() {
        return playOnStart;
    }
    
    public void setPlayOnStart(boolean playOnStart) {
        this.playOnStart = playOnStart;
    }
    
    public boolean isLoop() {
        return loop;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    public float getVolume() {
        return volume;
    }
    
    public void setVolume(float volume) {
        this.volume = Math.max(0f, Math.min(1f, volume));
        
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(this.volume, this.volume);
        }
    }
    
    public float getPitch() {
        return pitch;
    }
    
    public void setPitch(float pitch) {
        this.pitch = Math.max(0f, pitch);
    }
    
    public float getPan() {
        return pan;
    }
    
    public void setPan(float pan) {
        this.pan = Math.max(-1f, Math.min(1f, pan));
    }
    
    public boolean isMute() {
        return mute;
    }
    
    public void setMute(boolean mute) {
        this.mute = mute;
    }
    
    public boolean is3D() {
        return is3D;
    }
    
    public void set3D(boolean is3D) {
        this.is3D = is3D;
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    
    public float getMinDistance() {
        return minDistance;
    }
    
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }
    
    public float getMaxDistance() {
        return maxDistance;
    }
    
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }
    
    public float getRolloffFactor() {
        return rolloffFactor;
    }
    
    public void setRolloffFactor(float rolloffFactor) {
        this.rolloffFactor = rolloffFactor;
    }
    
    public boolean isPlaying() {
        return isPlaying;
    }
    
    public float getTime() {
        return time;
    }
    
    public void setTime(float time) {
        this.time = time;
    }
    
    @Override
    public String toString() {
        return "AudioSource{" +
                "clipName='" + clipName + '\'' +
                ", type=" + type +
                ", isPlaying=" + isPlaying +
                ", volume=" + volume +
                ", pitch=" + pitch +
                ", loop=" + loop +
                ", is3D=" + is3D +
                '}';
    }
}
