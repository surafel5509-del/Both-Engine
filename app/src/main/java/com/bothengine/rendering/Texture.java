package com.bothengine.rendering;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Texture
 * OpenGL texture for 2D and 3D rendering
 */
public class Texture {
    private static final String TAG = "Texture";
    
    private String name;
    private int textureId;
    private int width;
    private int height;
    private int glTarget;
    private TextureFilter minFilter;
    private TextureFilter magFilter;
    private TextureWrap wrapS;
    private TextureWrap wrapT;
    private boolean loaded;
    private boolean mipmapped;
    
    public enum TextureFilter {
        NEAREST(GLES20.GL_NEAREST),
        LINEAR(GLES20.GL_LINEAR),
        NEAREST_MIPMAP_NEAREST(GLES20.GL_NEAREST_MIPMAP_NEAREST),
        LINEAR_MIPMAP_NEAREST(GLES20.GL_LINEAR_MIPMAP_NEAREST),
        NEAREST_MIPMAP_LINEAR(GLES20.GL_NEAREST_MIPMAP_LINEAR),
        LINEAR_MIPMAP_LINEAR(GLES20.GL_LINEAR_MIPMAP_LINEAR);
        
        private final int glValue;
        
        TextureFilter(int glValue) {
            this.glValue = glValue;
        }
        
        public int getGlValue() {
            return glValue;
        }
    }
    
    public enum TextureWrap {
        REPEAT(GLES20.GL_REPEAT),
        MIRRORED_REPEAT(GLES20.GL_MIRRORED_REPEAT),
        CLAMP_TO_EDGE(GLES20.GL_CLAMP_TO_EDGE);
        
        private final int glValue;
        
        TextureWrap(int glValue) {
            this.glValue = glValue;
        }
        
        public int getGlValue() {
            return glValue;
        }
    }
    
    public Texture() {
        this.name = "Unnamed";
        this.textureId = 0;
        this.width = 0;
        this.height = 0;
        this.glTarget = GLES20.GL_TEXTURE_2D;
        this.minFilter = TextureFilter.LINEAR;
        this.magFilter = TextureFilter.LINEAR;
        this.wrapS = TextureWrap.REPEAT;
        this.wrapT = TextureWrap.REPEAT;
        this.loaded = false;
        this.mipmapped = false;
    }
    
    public Texture(String name) {
        this();
        this.name = name;
    }

    public Texture(String name, int width, int height) {
        this(name);
        this.width = width;
        this.height = height;
    }
    
    /**
     * Load texture from assets
     */
    public boolean loadFromAssets(Context context, String path) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
            
            if (bitmap == null) {
                Log.e(TAG, "Failed to decode bitmap: " + path);
                return false;
            }
            
            return loadFromBitmap(bitmap);
        } catch (IOException e) {
            Log.e(TAG, "Error loading texture from assets: " + path, e);
            return false;
        }
    }
    
    /**
     * Load texture from a bitmap
     */
    public boolean loadFromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            Log.e(TAG, "Bitmap is null");
            return false;
        }
        
        // Delete existing texture if any
        if (loaded) {
            delete();
        }
        
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        
        // Generate texture ID
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        
        // Bind texture
        GLES20.glBindTexture(glTarget, textureId);
        
        // Set default parameters
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT.getGlValue());
        
        // Load bitmap into texture
        GLUtils.texImage2D(glTarget, 0, bitmap, 0);
        
        // Generate mipmaps if enabled
        if (mipmapped) {
            GLES20.glGenerateMipmap(glTarget);
        }
        
        // Unbind texture
        GLES20.glBindTexture(glTarget, 0);
        
        loaded = true;
        Log.d(TAG, "Texture loaded: " + name + " (" + width + "x" + height + ")");
        
        return true;
    }
    
    /**
     * Load empty texture with specified dimensions
     */
    public boolean loadEmpty(int width, int height) {
        if (loaded) {
            delete();
        }
        
        this.width = width;
        this.height = height;
        
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        textureId = textures[0];
        
        GLES20.glBindTexture(glTarget, textureId);
        
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT.getGlValue());
        
        GLES20.glTexImage2D(glTarget, 0, GLES20.GL_RGBA, width, height, 0, 
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
        
        if (mipmapped) {
            GLES20.glGenerateMipmap(glTarget);
        }
        
        GLES20.glBindTexture(glTarget, 0);
        
        loaded = true;
        Log.d(TAG, "Empty texture created: " + name + " (" + width + "x" + height + ")");
        
        return true;
    }
    
    /**
     * Update texture with new bitmap
     */
    public boolean update(Bitmap bitmap) {
        if (!loaded) {
            return loadFromBitmap(bitmap);
        }
        
        if (bitmap == null) {
            return false;
        }
        
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();
        
        GLES20.glBindTexture(glTarget, textureId);
        GLUtils.texImage2D(glTarget, 0, bitmap, 0);
        
        if (mipmapped) {
            GLES20.glGenerateMipmap(glTarget);
        }
        
        GLES20.glBindTexture(glTarget, 0);
        
        return true;
    }
    
    /**
     * Update texture with raw data
     */
    public boolean update(int width, int height, ByteBuffer data) {
        if (!loaded) {
            loadEmpty(width, height);
        }
        
        this.width = width;
        this.height = height;
        
        GLES20.glBindTexture(glTarget, textureId);
        GLES20.glTexImage2D(glTarget, 0, GLES20.GL_RGBA, width, height, 0,
            GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, data);
        
        if (mipmapped) {
            GLES20.glGenerateMipmap(glTarget);
        }
        
        GLES20.glBindTexture(glTarget, 0);
        
        return true;
    }
    
    /**
     * Bind the texture
     */
    public void bind() {
        if (!loaded) return;
        GLES20.glBindTexture(glTarget, textureId);
    }
    
    /**
     * Bind the texture to a specific texture unit
     */
    public void bind(int textureUnit) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + textureUnit);
        bind();
    }
    
    /**
     * Unbind the texture
     */
    public void unbind() {
        GLES20.glBindTexture(glTarget, 0);
    }
    
    /**
     * Delete the texture
     */
    public void delete() {
        if (loaded) {
            int[] textures = new int[] { textureId };
            GLES20.glDeleteTextures(1, textures, 0);
            textureId = 0;
            loaded = false;
            Log.d(TAG, "Texture deleted: " + name);
        }
    }
    
    /**
     * Generate mipmaps
     */
    public void generateMipmaps() {
        if (!loaded) return;
        GLES20.glBindTexture(glTarget, textureId);
        GLES20.glGenerateMipmap(glTarget);
        GLES20.glBindTexture(glTarget, 0);
        mipmapped = true;
    }
    
    /**
     * Set texture parameters
     */
    public void setParameters() {
        if (!loaded) return;
        GLES20.glBindTexture(glTarget, textureId);
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_MIN_FILTER, minFilter.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_MAG_FILTER, magFilter.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_WRAP_S, wrapS.getGlValue());
        GLES20.glTexParameteri(glTarget, GLES20.GL_TEXTURE_WRAP_T, wrapT.getGlValue());
        GLES20.glBindTexture(glTarget, 0);
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getTextureId() {
        return textureId;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getGlTarget() {
        return glTarget;
    }
    
    public void setGlTarget(int glTarget) {
        this.glTarget = glTarget;
    }
    
    public TextureFilter getMinFilter() {
        return minFilter;
    }
    
    public void setMinFilter(TextureFilter minFilter) {
        this.minFilter = minFilter;
        if (loaded) setParameters();
    }
    
    public TextureFilter getMagFilter() {
        return magFilter;
    }
    
    public void setMagFilter(TextureFilter magFilter) {
        this.magFilter = magFilter;
        if (loaded) setParameters();
    }
    
    public TextureWrap getWrapS() {
        return wrapS;
    }
    
    public void setWrapS(TextureWrap wrapS) {
        this.wrapS = wrapS;
        if (loaded) setParameters();
    }
    
    public TextureWrap getWrapT() {
        return wrapT;
    }
    
    public void setWrapT(TextureWrap wrapT) {
        this.wrapT = wrapT;
        if (loaded) setParameters();
    }
    
    public boolean isLoaded() {
        return loaded;
    }
    
    public boolean isMipmapped() {
        return mipmapped;
    }
    
    public void setMipmapped(boolean mipmapped) {
        this.mipmapped = mipmapped;
    }
    
    @Override
    public String toString() {
        return "Texture{" +
                "name='" + name + '\'' +
                ", textureId=" + textureId +
                ", width=" + width +
                ", height=" + height +
                ", loaded=" + loaded +
                '}';
    }
}
