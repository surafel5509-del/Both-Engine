package com.bothengine.core;

import android.content.Context;

/**
 * Engine Configuration
 * Holds all configuration settings for the game engine
 */
public class EngineConfig {
    private final Context context;
    private final int targetFPS;
    private final boolean enablePhysics;
    private final boolean enableAudio;
    private final boolean enableLogging;
    private final boolean enableDebugRendering;
    private final int maxEntities;
    private final int textureAtlasSize;
    private final float physicsGravity;
    
    private EngineConfig(Builder builder) {
        this.context = builder.context;
        this.targetFPS = builder.targetFPS;
        this.enablePhysics = builder.enablePhysics;
        this.enableAudio = builder.enableAudio;
        this.enableLogging = builder.enableLogging;
        this.enableDebugRendering = builder.enableDebugRendering;
        this.maxEntities = builder.maxEntities;
        this.textureAtlasSize = builder.textureAtlasSize;
        this.physicsGravity = builder.physicsGravity;
    }
    
    public Context getContext() {
        return context;
    }
    
    public int getTargetFPS() {
        return targetFPS;
    }
    
    public float getFrameTime() {
        return 1f / targetFPS;
    }
    
    public boolean isPhysicsEnabled() {
        return enablePhysics;
    }
    
    public boolean isAudioEnabled() {
        return enableAudio;
    }
    
    public boolean isLoggingEnabled() {
        return enableLogging;
    }
    
    public boolean isDebugRenderingEnabled() {
        return enableDebugRendering;
    }
    
    public int getMaxEntities() {
        return maxEntities;
    }
    
    public int getTextureAtlasSize() {
        return textureAtlasSize;
    }
    
    public float getPhysicsGravity() {
        return physicsGravity;
    }
    
    public static class Builder {
        private Context context;
        private int targetFPS = 60;
        private boolean enablePhysics = true;
        private boolean enableAudio = true;
        private boolean enableLogging = true;
        private boolean enableDebugRendering = false;
        private int maxEntities = 10000;
        private int textureAtlasSize = 2048;
        private float physicsGravity = -9.8f;
        
        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }
        
        public Builder setTargetFPS(int targetFPS) {
            this.targetFPS = targetFPS;
            return this;
        }
        
        public Builder setEnablePhysics(boolean enablePhysics) {
            this.enablePhysics = enablePhysics;
            return this;
        }
        
        public Builder setEnableAudio(boolean enableAudio) {
            this.enableAudio = enableAudio;
            return this;
        }
        
        public Builder setEnableLogging(boolean enableLogging) {
            this.enableLogging = enableLogging;
            return this;
        }
        
        public Builder setEnableDebugRendering(boolean enableDebugRendering) {
            this.enableDebugRendering = enableDebugRendering;
            return this;
        }
        
        public Builder setMaxEntities(int maxEntities) {
            this.maxEntities = maxEntities;
            return this;
        }
        
        public Builder setTextureAtlasSize(int textureAtlasSize) {
            this.textureAtlasSize = textureAtlasSize;
            return this;
        }
        
        public Builder setPhysicsGravity(float physicsGravity) {
            this.physicsGravity = physicsGravity;
            return this;
        }
        
        public EngineConfig build() {
            if (context == null) {
                throw new IllegalStateException("Context must be set");
            }
            return new EngineConfig(this);
        }
    }
}
