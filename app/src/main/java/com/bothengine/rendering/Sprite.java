package com.bothengine.rendering;

import android.opengl.GLES20;

/**
 * Sprite
 * 2D image that can be rendered
 */
public class Sprite {
    private String name;
    private Vector2 position;
    private Vector2 size;
    private Vector2 scale;
    private float rotation;
    private Vector2 origin;
    private Vector2 anchor;
    private Color color;
    private Texture texture;
    private Material material;
    private Mesh mesh;
    private boolean visible;
    private int zIndex;
    
    // Animation
    private boolean animated;
    private int frameCount;
    private int currentFrame;
    private float frameTime;
    private float animationTime;
    private boolean animationLoop;
    private boolean animationPlaying;
    
    public Sprite() {
        this.name = "Sprite";
        this.position = new Vector2(0, 0);
        this.size = new Vector2(100, 100);
        this.scale = new Vector2(1, 1);
        this.rotation = 0;
        this.origin = new Vector2(0, 0);
        this.anchor = new Vector2(0.5f, 0.5f);
        this.color = Color.WHITE;
        this.texture = null;
        this.material = null;
        this.mesh = null;
        this.visible = true;
        this.zIndex = 0;
        this.animated = false;
        this.frameCount = 1;
        this.currentFrame = 0;
        this.frameTime = 0.1f;
        this.animationTime = 0;
        this.animationLoop = true;
        this.animationPlaying = false;
        
        createMesh();
    }
    
    public Sprite(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Create the mesh for this sprite
     */
    private void createMesh() {
        // Create a simple quad mesh
        float width = size.x;
        float height = size.y;
        
        // Vertices (x, y, z)
        float[] vertices = {
            0, 0, 0,
            width, 0, 0,
            width, height, 0,
            0, height, 0
        };
        
        // Texture coordinates
        float[] texCoords = {
            0, 1,  // Bottom-left
            1, 1,  // Bottom-right
            1, 0,  // Top-right
            0, 0   // Top-left
        };
        
        // Indices
        short[] indices = {0, 1, 2, 0, 2, 3};
        
        mesh = Mesh.createWithIndices(name + "_Mesh", vertices, indices);
        mesh.getVertexData().texCoords = texCoords;
        mesh.uploadToGPU();
    }
    
    /**
     * Update the sprite
     */
    public void update(float deltaTime) {
        if (animated && animationPlaying) {
            animationTime += deltaTime;
            
            if (animationTime >= frameTime) {
                animationTime = 0;
                currentFrame++;
                
                if (currentFrame >= frameCount) {
                    if (animationLoop) {
                        currentFrame = 0;
                    } else {
                        currentFrame = frameCount - 1;
                        animationPlaying = false;
                    }
                }
            }
        }
    }
    
    /**
     * Render the sprite
     */
    public void render() {
        if (!visible || mesh == null) return;
        
        // Set texture if available
        if (texture != null && texture.isLoaded()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureId());
        }
        
        // Render mesh
        mesh.render();
    }
    
    /**
     * Get the transformation matrix
     */
    public Matrix4 getTransformMatrix() {
        Matrix4 modelMatrix = new Matrix4();
        
        // Translate to position
        modelMatrix.translate(new Vector3(position.x, position.y, 0));
        
        // Translate by origin
        modelMatrix.translate(new Vector3(-origin.x * size.x, -origin.y * size.y, 0));
        
        // Rotate
        if (rotation != 0) {
            modelMatrix.rotateZ(rotation);
        }
        
        // Scale
        modelMatrix.scale(new Vector3(scale.x, scale.y, 1));
        
        // Translate by anchor
        modelMatrix.translate(new Vector3(anchor.x * size.x, anchor.y * size.y, 0));
        
        return modelMatrix;
    }
    
    /**
     * Set the texture and update UV coordinates
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
        if (texture != null) {
            // Update mesh UV coordinates if needed
            // For now, we use the default UVs
        }
    }
    
    /**
     * Set sprite from a texture atlas
     */
    public void setFromAtlas(Texture atlas, int frame, int frameWidth, int frameHeight, int atlasWidth) {
        this.texture = atlas;
        
        // Calculate UV coordinates for this frame
        float u = (frame % atlasWidth) * frameWidth / (float) atlasWidth;
        float v = (frame / atlasWidth) * frameHeight / (float) atlas.getHeight();
        float u2 = (frame % atlasWidth + 1) * frameWidth / (float) atlasWidth;
        float v2 = (frame / atlasWidth + 1) * frameHeight / (float) atlas.getHeight();
        
        float[] texCoords = {
            u, v2,  // Bottom-left
            u2, v2, // Bottom-right
            u2, v,  // Top-right
            u, v    // Top-left
        };
        
        mesh.getVertexData().texCoords = texCoords;
        mesh.uploadToGPU();
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Vector2 getPosition() {
        return position;
    }
    
    public void setPosition(Vector2 position) {
        this.position = position;
    }
    
    public void setPosition(float x, float y) {
        this.position = new Vector2(x, y);
    }
    
    public Vector2 getSize() {
        return size;
    }
    
    public void setSize(Vector2 size) {
        this.size = size;
        createMesh(); // Recreate mesh with new size
    }
    
    public void setSize(float width, float height) {
        this.size = new Vector2(width, height);
        createMesh();
    }
    
    public Vector2 getScale() {
        return scale;
    }
    
    public void setScale(Vector2 scale) {
        this.scale = scale;
    }
    
    public void setScale(float x, float y) {
        this.scale = new Vector2(x, y);
    }
    
    public float getRotation() {
        return rotation;
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    
    public Vector2 getOrigin() {
        return origin;
    }
    
    public void setOrigin(Vector2 origin) {
        this.origin = origin;
    }
    
    public void setOrigin(float x, float y) {
        this.origin = new Vector2(x, y);
    }
    
    public Vector2 getAnchor() {
        return anchor;
    }
    
    public void setAnchor(Vector2 anchor) {
        this.anchor = anchor;
    }
    
    public void setAnchor(float x, float y) {
        this.anchor = new Vector2(x, y);
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public int getZIndex() {
        return zIndex;
    }
    
    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
    
    public boolean isAnimated() {
        return animated;
    }
    
    public void setAnimated(boolean animated) {
        this.animated = animated;
    }
    
    public int getFrameCount() {
        return frameCount;
    }
    
    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
    }
    
    public int getCurrentFrame() {
        return currentFrame;
    }
    
    public void setCurrentFrame(int currentFrame) {
        this.currentFrame = currentFrame;
    }
    
    public float getFrameTime() {
        return frameTime;
    }
    
    public void setFrameTime(float frameTime) {
        this.frameTime = frameTime;
    }
    
    public boolean isAnimationLoop() {
        return animationLoop;
    }
    
    public void setAnimationLoop(boolean animationLoop) {
        this.animationLoop = animationLoop;
    }
    
    public boolean isAnimationPlaying() {
        return animationPlaying;
    }
    
    public void setAnimationPlaying(boolean animationPlaying) {
        this.animationPlaying = animationPlaying;
    }
    
    public void playAnimation() {
        animationPlaying = true;
        animationTime = 0;
        currentFrame = 0;
    }
    
    public void stopAnimation() {
        animationPlaying = false;
    }
    
    public void pauseAnimation() {
        animationPlaying = false;
    }
    
    public void resumeAnimation() {
        animationPlaying = true;
    }
    
    @Override
    public String toString() {
        return "Sprite{" +
                "name='" + name + '\'' +
                ", position=" + position +
                ", size=" + size +
                ", visible=" + visible +
                '}';
    }
}
