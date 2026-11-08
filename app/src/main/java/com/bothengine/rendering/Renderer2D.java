package com.bothengine.rendering;

import android.opengl.GLES20;
import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.rendering.shaders.Basic2DShader;
import com.bothengine.rendering.shaders.ShaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Renderer2D
 * Handles all 2D rendering (sprites, UI, particles, etc.)
 */
public class Renderer2D {
    private static final String TAG = "Renderer2D";
    
    private final EngineConfig config;
    private final ShaderManager shaderManager;
    private Basic2DShader shader;
    
    private List<Sprite> sprites;
    private List<Sprite> uiElements;
    private List<ParticleSystem> particleSystems;
    
    private Matrix4 projectionMatrix;
    private Matrix4 viewMatrix;
    
    private int spriteCount;
    private int drawCalls;
    
    public Renderer2D(EngineConfig config, ShaderManager shaderManager) {
        this.config = config;
        this.shaderManager = shaderManager;
        this.shader = shaderManager.getBasic2DShader();
        this.sprites = new ArrayList<>();
        this.uiElements = new ArrayList<>();
        this.particleSystems = new ArrayList<>();
        this.projectionMatrix = new Matrix4();
        this.viewMatrix = new Matrix4();
        this.spriteCount = 0;
        this.drawCalls = 0;
        
        Log.i(TAG, "Renderer2D initialized");
    }
    
    /**
     * Initialize the renderer
     */
    public void initialize() {
        // Create orthographic projection for 2D
        updateProjection();
    }
    
    /**
     * Update projection matrix
     */
    public void updateProjection() {
        float width = config.getContext().getResources().getDisplayMetrics().widthPixels;
        float height = config.getContext().getResources().getDisplayMetrics().heightPixels;
        
        // Orthographic projection for 2D
        projectionMatrix = Matrix4.orthographic(0, width, height, 0, -1000, 1000);
    }
    
    /**
     * Add a sprite to be rendered
     */
    public void addSprite(Sprite sprite) {
        sprites.add(sprite);
    }
    
    /**
     * Remove a sprite
     */
    public void removeSprite(Sprite sprite) {
        sprites.remove(sprite);
    }
    
    /**
     * Add a UI element
     */
    public void addUIElement(Sprite uiElement) {
        uiElements.add(uiElement);
    }
    
    /**
     * Remove a UI element
     */
    public void removeUIElement(Sprite uiElement) {
        uiElements.remove(uiElement);
    }
    
    /**
     * Add a particle system
     */
    public void addParticleSystem(ParticleSystem particleSystem) {
        particleSystems.add(particleSystem);
    }
    
    /**
     * Remove a particle system
     */
    public void removeParticleSystem(ParticleSystem particleSystem) {
        particleSystems.remove(particleSystem);
    }
    
    /**
     * Clear all sprites
     */
    public void clearSprites() {
        sprites.clear();
    }
    
    /**
     * Clear all UI elements
     */
    public void clearUIElements() {
        uiElements.clear();
    }
    
    /**
     * Clear all particle systems
     */
    public void clearParticleSystems() {
        particleSystems.clear();
    }
    
    /**
     * Render all 2D objects
     */
    public void render(float deltaTime) {
        if (!shader.isCompiled()) {
            Log.w(TAG, "Shader not compiled, skipping render");
            return;
        }
        
        // Reset counters
        spriteCount = 0;
        drawCalls = 0;
        
        // Use shader
        shader.use();
        
        // Set projection matrix
        shader.setUniformMatrix("uViewProjectionMatrix", projectionMatrix.data);
        
        // Render sprites
        renderSprites(sprites);
        
        // Render UI elements
        renderSprites(uiElements);
        
        // Render particle systems
        for (ParticleSystem particleSystem : particleSystems) {
            particleSystem.update(deltaTime);
            particleSystem.render(shader);
            drawCalls++;
        }
        
        Log.d(TAG, "Rendered " + spriteCount + " sprites in " + drawCalls + " draw calls");
    }
    
    /**
     * Render a list of sprites
     */
    private void renderSprites(List<Sprite> spriteList) {
        if (spriteList.isEmpty()) return;
        
        // Sort sprites by z-index
        spriteList.sort((a, b) -> Integer.compare(a.getZIndex(), b.getZIndex()));
        
        // Batch sprites with the same texture and material
        Sprite currentSprite = null;
        Material currentMaterial = null;
        Texture currentTexture = null;
        
        for (Sprite sprite : spriteList) {
            if (sprite.isVisible()) {
                // Check if we need to change material/texture
                if (currentMaterial != sprite.getMaterial() || 
                    currentTexture != sprite.getTexture()) {
                    
                    // Flush current batch
                    if (currentSprite != null) {
                        drawCalls++;
                    }
                    
                    // Bind new material
                    currentMaterial = sprite.getMaterial();
                    currentTexture = sprite.getTexture();
                    
                    if (currentMaterial != null) {
                        currentMaterial.bind(shader);
                    }
                    
                    currentSprite = sprite;
                }
                
                // Set model matrix
                Matrix4 modelMatrix = sprite.getTransformMatrix();
                shader.setUniformMatrix("uModelMatrix", modelMatrix.data);
                
                // Set color
                Color color = sprite.getColor();
                shader.setUniform("aColor", color.r, color.g, color.b, color.a);
                
                // Render sprite
                sprite.render();
                spriteCount++;
            }
        }
        
        // Flush last batch
        if (currentSprite != null) {
            drawCalls++;
        }
    }
    
    /**
     * Render a single sprite
     */
    public void renderSprite(Sprite sprite) {
        if (!shader.isCompiled() || !sprite.isVisible()) return;
        
        shader.use();
        shader.setUniformMatrix("uViewProjectionMatrix", projectionMatrix.data);
        
        Matrix4 modelMatrix = sprite.getTransformMatrix();
        shader.setUniformMatrix("uModelMatrix", modelMatrix.data);
        
        if (sprite.getMaterial() != null) {
            sprite.getMaterial().bind(shader);
        }
        
        Color color = sprite.getColor();
        shader.setUniform("aColor", color.r, color.g, color.b, color.a);
        
        sprite.render();
        spriteCount++;
        drawCalls++;
    }
    
    /**
     * Draw a textured quad
     */
    public void drawTexturedQuad(Texture texture, float x, float y, float width, float height) {
        Sprite sprite = new Sprite("TempQuad");
        sprite.setPosition(x, y);
        sprite.setSize(width, height);
        sprite.setTexture(texture);
        sprite.setMaterial(Material.createWithTexture("TempMaterial", texture));
        
        renderSprite(sprite);
    }
    
    /**
     * Draw a colored quad
     */
    public void drawColoredQuad(Color color, float x, float y, float width, float height) {
        Sprite sprite = new Sprite("TempQuad");
        sprite.setPosition(x, y);
        sprite.setSize(width, height);
        sprite.setColor(color);
        sprite.setMaterial(Material.createWithColor("TempMaterial", color));
        
        renderSprite(sprite);
    }
    
    /**
     * Shutdown the renderer
     */
    public void shutdown() {
        sprites.clear();
        uiElements.clear();
        particleSystems.clear();
        Log.i(TAG, "Renderer2D shutdown");
    }
    
    // Getters and setters
    
    public Basic2DShader getShader() {
        return shader;
    }
    
    public void setShader(Basic2DShader shader) {
        this.shader = shader;
    }
    
    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }
    
    public void setProjectionMatrix(Matrix4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }
    
    public Matrix4 getViewMatrix() {
        return viewMatrix;
    }
    
    public void setViewMatrix(Matrix4 viewMatrix) {
        this.viewMatrix = viewMatrix;
    }
    
    public int getSpriteCount() {
        return spriteCount;
    }
    
    public int getDrawCalls() {
        return drawCalls;
    }
}
