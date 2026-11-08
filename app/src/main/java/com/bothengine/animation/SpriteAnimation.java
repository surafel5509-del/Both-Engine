package com.bothengine.animation;

import com.bothengine.rendering.Texture;
import com.bothengine.rendering.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * SpriteAnimation
 * Manages 2D sprite animation with frames and animation clips
 * Supports sprite sheets and individual textures
 */
public class SpriteAnimation {
    private String name;
    private Texture spriteSheet;
    private List<SpriteFrame> frames;
    private List<SpriteAnimationClip> clips;
    private SpriteAnimationClip currentClip;
    private int currentFrameIndex;
    private float currentFrameTime;
    private float speed;
    private boolean playing;
    private boolean loop;
    private boolean pingPong;
    private int direction;
    
    // Sprite sheet settings
    private int columns;
    private int rows;
    private int frameWidth;
    private int frameHeight;
    private Vector2 pivot;
    
    public SpriteAnimation(String name) {
        this.name = name;
        this.spriteSheet = null;
        this.frames = new ArrayList<>();
        this.clips = new ArrayList<>();
        this.currentClip = null;
        this.currentFrameIndex = 0;
        this.currentFrameTime = 0f;
        this.speed = 1.0f;
        this.playing = false;
        this.loop = true;
        this.pingPong = false;
        this.direction = 1;
        this.columns = 1;
        this.rows = 1;
        this.frameWidth = 0;
        this.frameHeight = 0;
        this.pivot = new Vector2(0.5f, 0.5f);
    }
    
    /**
     * Create a sprite animation with a sprite sheet
     */
    public static SpriteAnimation create(String name, Texture spriteSheet, int frameWidth, int frameHeight) {
        SpriteAnimation animation = new SpriteAnimation(name);
        animation.setSpriteSheet(spriteSheet);
        animation.setFrameSize(frameWidth, frameHeight);
        animation.generateFramesFromSpriteSheet();
        return animation;
    }
    
    /**
     * Create a sprite animation with individual frames
     */
    public static SpriteAnimation create(String name, List<SpriteFrame> frames) {
        SpriteAnimation animation = new SpriteAnimation(name);
        animation.setFrames(frames);
        return animation;
    }
    
    /**
     * Set the sprite sheet
     */
    public void setSpriteSheet(Texture spriteSheet) {
        this.spriteSheet = spriteSheet;
        if (spriteSheet != null) {
            this.frameWidth = spriteSheet.getWidth() / columns;
            this.frameHeight = spriteSheet.getHeight() / rows;
        }
    }
    
    /**
     * Set the frame size
     */
    public void setFrameSize(int frameWidth, int frameHeight) {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        
        if (spriteSheet != null) {
            this.columns = spriteSheet.getWidth() / frameWidth;
            this.rows = spriteSheet.getHeight() / frameHeight;
        }
    }
    
    /**
     * Set the sprite sheet layout
     */
    public void setSpriteSheetLayout(int columns, int rows) {
        this.columns = columns;
        this.rows = rows;
        
        if (spriteSheet != null) {
            this.frameWidth = spriteSheet.getWidth() / columns;
            this.frameHeight = spriteSheet.getHeight() / rows;
        }
    }
    
    /**
     * Generate frames from the sprite sheet
     */
    public void generateFramesFromSpriteSheet() {
        frames.clear();
        
        if (spriteSheet == null || frameWidth <= 0 || frameHeight <= 0) {
            return;
        }
        
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < columns; col++) {
                float u = (float) col / columns;
                float v = (float) row / rows;
                float u2 = (float) (col + 1) / columns;
                float v2 = (float) (row + 1) / rows;
                
                SpriteFrame frame = new SpriteFrame(
                    "frame_" + row + "_" + col,
                    spriteSheet,
                    new Vector2(u, v),
                    new Vector2(u2, v2),
                    frameWidth,
                    frameHeight
                );
                frames.add(frame);
            }
        }
    }
    
    /**
     * Set the frames
     */
    public void setFrames(List<SpriteFrame> frames) {
        this.frames = new ArrayList<>(frames);
    }
    
    /**
     * Add a frame
     */
    public void addFrame(SpriteFrame frame) {
        frames.add(frame);
    }
    
    /**
     * Remove a frame
     */
    public void removeFrame(SpriteFrame frame) {
        frames.remove(frame);
    }
    
    /**
     * Create an animation clip
     */
    public SpriteAnimationClip createClip(String name, int startFrame, int endFrame, float frameRate) {
        SpriteAnimationClip clip = new SpriteAnimationClip(name, startFrame, endFrame, frameRate);
        clips.add(clip);
        return clip;
    }
    
    /**
     * Create an animation clip with specific frames
     */
    public SpriteAnimationClip createClip(String name, List<Integer> frameIndices, float frameRate) {
        SpriteAnimationClip clip = new SpriteAnimationClip(name, frameIndices, frameRate);
        clips.add(clip);
        return clip;
    }
    
    /**
     * Play an animation clip by name
     */
    public void play(String clipName) {
        for (SpriteAnimationClip clip : clips) {
            if (clip.getName().equals(clipName)) {
                play(clip);
                return;
            }
        }
    }
    
    /**
     * Play an animation clip
     */
    public void play(SpriteAnimationClip clip) {
        if (clip == null) return;
        
        currentClip = clip;
        currentFrameIndex = clip.getStartFrame();
        currentFrameTime = 0f;
        playing = true;
        direction = 1;
    }
    
    /**
     * Stop the animation
     */
    public void stop() {
        playing = false;
    }
    
    /**
     * Pause the animation
     */
    public void pause() {
        playing = false;
    }
    
    /**
     * Resume the animation
     */
    public void resume() {
        playing = true;
    }
    
    /**
     * Update the animation
     */
    public void update(float deltaTime) {
        if (!playing || currentClip == null) return;
        
        deltaTime *= speed;
        currentFrameTime += deltaTime;
        
        float frameDuration = 1.0f / currentClip.getFrameRate();
        
        while (currentFrameTime >= frameDuration) {
            currentFrameTime -= frameDuration;
            currentFrameIndex += direction;
            
            // Handle animation wrap mode
            if (currentFrameIndex >= currentClip.getEndFrame()) {
                if (loop) {
                    if (pingPong) {
                        direction = -1;
                        currentFrameIndex = currentClip.getEndFrame() - 1;
                    } else {
                        currentFrameIndex = currentClip.getStartFrame();
                    }
                } else {
                    currentFrameIndex = currentClip.getEndFrame() - 1;
                    playing = false;
                }
            } else if (currentFrameIndex < currentClip.getStartFrame()) {
                if (loop) {
                    if (pingPong) {
                        direction = 1;
                        currentFrameIndex = currentClip.getStartFrame() + 1;
                    } else {
                        currentFrameIndex = currentClip.getEndFrame() - 1;
                    }
                } else {
                    currentFrameIndex = currentClip.getStartFrame();
                    playing = false;
                }
            }
        }
    }
    
    /**
     * Get the current frame
     */
    public SpriteFrame getCurrentFrame() {
        if (currentFrameIndex >= 0 && currentFrameIndex < frames.size()) {
            return frames.get(currentFrameIndex);
        }
        return null;
    }
    
    /**
     * Get the current frame texture coordinates
     */
    public float[] getCurrentFrameTexCoords() {
        SpriteFrame frame = getCurrentFrame();
        if (frame != null) {
            return frame.getTexCoords();
        }
        return new float[] { 0, 0, 1, 1 };
    }
    
    /**
     * Get the current frame index
     */
    public int getCurrentFrameIndex() {
        return currentFrameIndex;
    }
    
    /**
     * Set the current frame index
     */
    public void setCurrentFrameIndex(int index) {
        if (index >= 0 && index < frames.size()) {
            currentFrameIndex = index;
        }
    }
    
    /**
     * Get the frame at a specific index
     */
    public SpriteFrame getFrame(int index) {
        if (index >= 0 && index < frames.size()) {
            return frames.get(index);
        }
        return null;
    }
    
    /**
     * Get the animation clip by name
     */
    public SpriteAnimationClip getClip(String name) {
        for (SpriteAnimationClip clip : clips) {
            if (clip.getName().equals(name)) {
                return clip;
            }
        }
        return null;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Texture getSpriteSheet() {
        return spriteSheet;
    }
    
    public List<SpriteFrame> getFrames() {
        return new ArrayList<>(frames);
    }
    
    public List<SpriteAnimationClip> getClips() {
        return new ArrayList<>(clips);
    }
    
    public SpriteAnimationClip getCurrentClip() {
        return currentClip;
    }
    
    public void setCurrentClip(SpriteAnimationClip currentClip) {
        this.currentClip = currentClip;
    }
    
    public float getSpeed() {
        return speed;
    }
    
    public void setSpeed(float speed) {
        this.speed = speed;
    }
    
    public boolean isPlaying() {
        return playing;
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
    
    public int getColumns() {
        return columns;
    }
    
    public int getRows() {
        return rows;
    }
    
    public int getFrameWidth() {
        return frameWidth;
    }
    
    public int getFrameHeight() {
        return frameHeight;
    }
    
    public Vector2 getPivot() {
        return pivot;
    }
    
    public void setPivot(Vector2 pivot) {
        this.pivot = pivot;
    }
    
    public int getFrameCount() {
        return frames.size();
    }
    
    @Override
    public String toString() {
        return "SpriteAnimation{" +
                "name='" + name + '\'' +
                ", frames=" + frames.size() +
                ", clips=" + clips.size() +
                ", playing=" + playing +
                ", currentFrame=" + currentFrameIndex +
                '}';
    }
    
    /**
     * SpriteFrame class for individual animation frames
     */
    public static class SpriteFrame {
        private String name;
        private Texture texture;
        private Vector2 uvMin;
        private Vector2 uvMax;
        private int width;
        private int height;
        private Vector2 offset;
        
        public SpriteFrame(String name, Texture texture, Vector2 uvMin, Vector2 uvMax, int width, int height) {
            this.name = name;
            this.texture = texture;
            this.uvMin = uvMin;
            this.uvMax = uvMax;
            this.width = width;
            this.height = height;
            this.offset = new Vector2();
        }
        
        /**
         * Create a sprite frame with a full texture
         */
        public static SpriteFrame create(String name, Texture texture) {
            return new SpriteFrame(name, texture, new Vector2(0, 0), new Vector2(1, 1), 
                texture != null ? texture.getWidth() : 0, 
                texture != null ? texture.getHeight() : 0);
        }
        
        /**
         * Get the texture coordinates as a float array
         */
        public float[] getTexCoords() {
            return new float[] {
                uvMin.x, uvMin.y,
                uvMax.x, uvMin.y,
                uvMax.x, uvMax.y,
                uvMin.x, uvMax.y
            };
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public Texture getTexture() {
            return texture;
        }
        
        public void setTexture(Texture texture) {
            this.texture = texture;
        }
        
        public Vector2 getUvMin() {
            return uvMin;
        }
        
        public void setUvMin(Vector2 uvMin) {
            this.uvMin = uvMin;
        }
        
        public Vector2 getUvMax() {
            return uvMax;
        }
        
        public void setUvMax(Vector2 uvMax) {
            this.uvMax = uvMax;
        }
        
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public Vector2 getOffset() {
            return offset;
        }
        
        public void setOffset(Vector2 offset) {
            this.offset = offset;
        }
        
        @Override
        public String toString() {
            return "SpriteFrame{" +
                    "name='" + name + '\'' +
                    ", texture=" + (texture != null ? texture.getName() : "null") +
                    ", uvMin=" + uvMin +
                    ", uvMax=" + uvMax +
                    '}';
        }
    }
    
    /**
     * SpriteAnimationClip class for defining animation sequences
     */
    public static class SpriteAnimationClip {
        private String name;
        private List<Integer> frameIndices;
        private int startFrame;
        private int endFrame;
        private float frameRate;
        private boolean loop;
        private boolean pingPong;
        
        public SpriteAnimationClip(String name, int startFrame, int endFrame, float frameRate) {
            this.name = name;
            this.frameIndices = new ArrayList<>();
            for (int i = startFrame; i <= endFrame; i++) {
                frameIndices.add(i);
            }
            this.startFrame = startFrame;
            this.endFrame = endFrame;
            this.frameRate = frameRate;
            this.loop = true;
            this.pingPong = false;
        }
        
        public SpriteAnimationClip(String name, List<Integer> frameIndices, float frameRate) {
            this.name = name;
            this.frameIndices = new ArrayList<>(frameIndices);
            this.startFrame = frameIndices.isEmpty() ? 0 : frameIndices.get(0);
            this.endFrame = frameIndices.isEmpty() ? 0 : frameIndices.get(frameIndices.size() - 1);
            this.frameRate = frameRate;
            this.loop = true;
            this.pingPong = false;
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public List<Integer> getFrameIndices() {
            return new ArrayList<>(frameIndices);
        }
        
        public int getStartFrame() {
            return startFrame;
        }
        
        public void setStartFrame(int startFrame) {
            this.startFrame = startFrame;
        }
        
        public int getEndFrame() {
            return endFrame;
        }
        
        public void setEndFrame(int endFrame) {
            this.endFrame = endFrame;
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
        
        public int getFrameCount() {
            return frameIndices.size();
        }
        
        @Override
        public String toString() {
            return "SpriteAnimationClip{" +
                    "name='" + name + '\'' +
                    ", startFrame=" + startFrame +
                    ", endFrame=" + endFrame +
                    ", frameRate=" + frameRate +
                    '}';
        }
    }
}
