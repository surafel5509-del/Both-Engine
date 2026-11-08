package com.bothengine.audio;

/**
 * AudioClip
 * Represents an audio resource that can be played
 * Similar to Unity's AudioClip
 */
public class AudioClip {
    private String name;
    private String filePath;
    private int resourceId;
    private float length;
    private int sampleRate;
    private int channels;
    private int bitsPerSample;
    private boolean is3D;
    private float minDistance;
    private float maxDistance;
    private float volume;
    private float pitch;
    private boolean loop;
    
    public AudioClip(String name) {
        this.name = name;
        this.filePath = "";
        this.resourceId = 0;
        this.length = 0f;
        this.sampleRate = 44100;
        this.channels = 2;
        this.bitsPerSample = 16;
        this.is3D = false;
        this.minDistance = 1f;
        this.maxDistance = 50f;
        this.volume = 1.0f;
        this.pitch = 1.0f;
        this.loop = false;
    }
    
    /**
     * Create an audio clip with name and resource ID
     */
    public AudioClip(String name, int resourceId) {
        this(name);
        this.resourceId = resourceId;
    }
    
    /**
     * Create an audio clip with name and file path
     */
    public AudioClip(String name, String filePath) {
        this(name);
        this.filePath = filePath;
    }
    
    /**
     * Create a 3D audio clip
     */
    public static AudioClip create3D(String name, int resourceId) {
        AudioClip clip = new AudioClip(name, resourceId);
        clip.set3D(true);
        return clip;
    }
    
    /**
     * Create a 3D audio clip with spatial settings
     */
    public static AudioClip create3D(String name, int resourceId, float minDistance, float maxDistance) {
        AudioClip clip = create3D(name, resourceId);
        clip.setMinDistance(minDistance);
        clip.setMaxDistance(maxDistance);
        return clip;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    
    public int getResourceId() {
        return resourceId;
    }
    
    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
    
    public float getLength() {
        return length;
    }
    
    public void setLength(float length) {
        this.length = length;
    }
    
    public int getSampleRate() {
        return sampleRate;
    }
    
    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }
    
    public int getChannels() {
        return channels;
    }
    
    public void setChannels(int channels) {
        this.channels = channels;
    }
    
    public int getBitsPerSample() {
        return bitsPerSample;
    }
    
    public void setBitsPerSample(int bitsPerSample) {
        this.bitsPerSample = bitsPerSample;
    }
    
    public boolean is3D() {
        return is3D;
    }
    
    public void set3D(boolean is3D) {
        this.is3D = is3D;
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
    
    public boolean isLoop() {
        return loop;
    }
    
    public void setLoop(boolean loop) {
        this.loop = loop;
    }
    
    @Override
    public String toString() {
        return "AudioClip{" +
                "name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", resourceId=" + resourceId +
                ", length=" + length + "s" +
                ", is3D=" + is3D +
                ", minDistance=" + minDistance +
                ", maxDistance=" + maxDistance +
                '}';
    }
}
