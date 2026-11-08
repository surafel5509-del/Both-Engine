package com.bothengine.assets.advanced;

import com.bothengine.rendering.Texture;
import com.bothengine.rendering.Vector2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TextureAtlas
 * Combines multiple textures into a single atlas texture
 * Improves rendering performance by reducing texture switches
 */
public class TextureAtlas {
    private String name;
    private Texture atlasTexture;
    private int width;
    private int height;
    private int padding;
    
    private Map<String, AtlasRegion> regions;
    private List<Texture> sourceTextures;
    
    public TextureAtlas(String name) {
        this.name = name;
        this.atlasTexture = null;
        this.width = 0;
        this.height = 0;
        this.padding = 2;
        this.regions = new HashMap<>();
        this.sourceTextures = new ArrayList<>();
    }
    
    /**
     * Create a texture atlas with name and size
     */
    public static TextureAtlas create(String name, int width, int height) {
        TextureAtlas atlas = new TextureAtlas(name);
        atlas.setWidth(width);
        atlas.setHeight(height);
        return atlas;
    }
    
    /**
     * Add a texture to the atlas
     */
    public void addTexture(String name, Texture texture) {
        sourceTextures.add(texture);
        
        // Calculate region
        AtlasRegion region = calculateRegion(texture);
        regions.put(name, region);
    }
    
    /**
     * Calculate the region for a texture
     */
    private AtlasRegion calculateRegion(Texture texture) {
        // Simple packing: place textures in a grid
        // In a real implementation, this would use a more sophisticated packing algorithm
        
        int x = 0;
        int y = 0;
        int currentY = 0;
        
        for (Texture tex : sourceTextures) {
            if (tex == texture) break;
            
            x += tex.getWidth() + padding;
            
            if (x + tex.getWidth() > width) {
                x = 0;
                currentY += tex.getHeight() + padding;
            }
        }
        
        return new AtlasRegion(
            name,
            x,
            currentY,
            texture.getWidth(),
            texture.getHeight(),
            width,
            height
        );
    }
    
    /**
     * Build the atlas texture
     */
    public void build() {
        if (sourceTextures.isEmpty()) return;
        
        // Create atlas texture
        // In a real implementation, this would create a new texture and copy all source textures into it
        
        // For now, we'll just create a placeholder
        atlasTexture = new Texture(name, width, height);
    }
    
    /**
     * Get a region by name
     */
    public AtlasRegion getRegion(String name) {
        return regions.get(name);
    }
    
    /**
     * Check if a region exists
     */
    public boolean hasRegion(String name) {
        return regions.containsKey(name);
    }
    
    /**
     * Remove a region
     */
    public void removeRegion(String name) {
        regions.remove(name);
    }
    
    /**
     * Get all region names
     */
    public List<String> getRegionNames() {
        return new ArrayList<>(regions.keySet());
    }
    
    /**
     * Get all regions
     */
    public List<AtlasRegion> getRegions() {
        return new ArrayList<>(regions.values());
    }
    
    /**
     * Clear all regions
     */
    public void clear() {
        regions.clear();
        sourceTextures.clear();
        
        if (atlasTexture != null) {
            atlasTexture.delete();
            atlasTexture = null;
        }
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Texture getAtlasTexture() {
        return atlasTexture;
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
    
    public int getPadding() {
        return padding;
    }
    
    public void setPadding(int padding) {
        this.padding = padding;
    }
    
    public List<Texture> getSourceTextures() {
        return new ArrayList<>(sourceTextures);
    }
    
    @Override
    public String toString() {
        return "TextureAtlas{" +
                "name='" + name + '\'' +
                ", width=" + width +
                ", height=" + height +
                ", regions=" + regions.size() +
                ", sourceTextures=" + sourceTextures.size() +
                '}';
    }
    
    /**
     * AtlasRegion class
     * Represents a region in the texture atlas
     */
    public static class AtlasRegion {
        private String name;
        private int x;
        private int y;
        private int width;
        private int height;
        private int atlasWidth;
        private int atlasHeight;
        
        public AtlasRegion(String name, int x, int y, int width, int height, int atlasWidth, int atlasHeight) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.atlasWidth = atlasWidth;
            this.atlasHeight = atlasHeight;
        }
        
        /**
         * Get the normalized texture coordinates
         */
        public float[] getTexCoords() {
            float u = (float) x / atlasWidth;
            float v = (float) y / atlasHeight;
            float u2 = (float) (x + width) / atlasWidth;
            float v2 = (float) (y + height) / atlasHeight;
            
            return new float[] {
                u, v,
                u2, v,
                u2, v2,
                u, v2
            };
        }
        
        /**
         * Get the UV min
         */
        public Vector2 getUvMin() {
            return new Vector2((float) x / atlasWidth, (float) y / atlasHeight);
        }
        
        /**
         * Get the UV max
         */
        public Vector2 getUvMax() {
            return new Vector2(
                (float) (x + width) / atlasWidth,
                (float) (y + height) / atlasHeight
            );
        }
        
        /**
         * Get the UV center
         */
        public Vector2 getUvCenter() {
            return new Vector2(
                (float) (x + width / 2) / atlasWidth,
                (float) (y + height / 2) / atlasHeight
            );
        }
        
        // Getters and setters
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public int getX() {
            return x;
        }
        
        public void setX(int x) {
            this.x = x;
        }
        
        public int getY() {
            return y;
        }
        
        public void setY(int y) {
            this.y = y;
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
        
        public int getAtlasWidth() {
            return atlasWidth;
        }
        
        public void setAtlasWidth(int atlasWidth) {
            this.atlasWidth = atlasWidth;
        }
        
        public int getAtlasHeight() {
            return atlasHeight;
        }
        
        public void setAtlasHeight(int atlasHeight) {
            this.atlasHeight = atlasHeight;
        }
        
        @Override
        public String toString() {
            return "AtlasRegion{" +
                    "name='" + name + '\'' +
                    ", x=" + x +
                    ", y=" + y +
                    ", width=" + width +
                    ", height=" + height +
                    '}';
        }
    }
}
