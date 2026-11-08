package com.bothengine.rendering;

import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.bothengine.rendering.shaders.Basic2DShader;
import com.bothengine.rendering.shaders.Basic3DShader;
import com.bothengine.rendering.shaders.Shader;

import javax.microedition.khronos.opengles.GL10;

/**
 * Material
 * Defines the appearance of rendered objects
 */
public class Material {
    private String name;
    private Color diffuseColor;
    private Color ambientColor;
    private Color specularColor;
    private Color emissionColor;
    private float shininess;
    private float opacity;
    private boolean useLighting;
    private boolean useTexture;
    private boolean doubleSided;
    private boolean wireframe;
    
    private int textureId;
    private Texture texture;
    
    private Shader shader;
    
    public Material() {
        this.name = "Default";
        this.diffuseColor = Color.WHITE;
        this.ambientColor = Color.WHITE;
        this.specularColor = Color.BLACK;
        this.emissionColor = Color.BLACK;
        this.shininess = 0;
        this.opacity = 1;
        this.useLighting = true;
        this.useTexture = false;
        this.doubleSided = false;
        this.wireframe = false;
        this.textureId = 0;
        this.texture = null;
        this.shader = null;
    }
    
    public Material(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Create a material with a color
     */
    public static Material createWithColor(String name, Color color) {
        Material material = new Material(name);
        material.setDiffuseColor(color);
        return material;
    }
    
    /**
     * Create a material with a texture
     */
    public static Material createWithTexture(String name, Texture texture) {
        Material material = new Material(name);
        material.setTexture(texture);
        material.setUseTexture(true);
        return material;
    }
    
    /**
     * Bind the material for rendering
     */
    public void bind(Shader shader) {
        if (shader == null) return;
        
        this.shader = shader;
        shader.use();
        
        // Set common uniforms
        if (shader instanceof Basic2DShader) {
            bind2DUniforms((Basic2DShader) shader);
        } else if (shader instanceof Basic3DShader) {
            bind3DUniforms((Basic3DShader) shader);
        }
    }
    
    /**
     * Bind uniforms for 2D rendering
     */
    private void bind2DUniforms(Basic2DShader shader) {
        shader.setUniform("uUseTexture", useTexture ? 1 : 0);
        
        if (useTexture && texture != null && texture.isLoaded()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureId());
            shader.setUniform("uTexture", 0);
        }
    }
    
    /**
     * Bind uniforms for 3D rendering
     */
    private void bind3DUniforms(Basic3DShader shader) {
        shader.setUniform("uUseTexture", useTexture ? 1 : 0);
        shader.setUniform("uUseLighting", useLighting ? 1 : 0);
        
        if (useLighting) {
            shader.setUniform("uLightDirection", 0, 1, 0); // Default light direction
            shader.setUniform("uLightColor", 1, 1, 1); // White light
            shader.setUniform("uAmbientColor", ambientColor.r, ambientColor.g, ambientColor.b);
        }
        
        if (useTexture && texture != null && texture.isLoaded()) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture.getTextureId());
            shader.setUniform("uTexture", 0);
        }
    }
    
    /**
     * Unbind the material
     */
    public void unbind() {
        if (useTexture && texture != null && texture.isLoaded()) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        }
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Color getDiffuseColor() {
        return diffuseColor;
    }
    
    public void setDiffuseColor(Color diffuseColor) {
        this.diffuseColor = diffuseColor;
    }
    
    public Color getAmbientColor() {
        return ambientColor;
    }
    
    public void setAmbientColor(Color ambientColor) {
        this.ambientColor = ambientColor;
    }
    
    public Color getSpecularColor() {
        return specularColor;
    }
    
    public void setSpecularColor(Color specularColor) {
        this.specularColor = specularColor;
    }
    
    public Color getEmissionColor() {
        return emissionColor;
    }
    
    public void setEmissionColor(Color emissionColor) {
        this.emissionColor = emissionColor;
    }
    
    public float getShininess() {
        return shininess;
    }
    
    public void setShininess(float shininess) {
        this.shininess = shininess;
    }
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }
    
    public boolean isUseLighting() {
        return useLighting;
    }
    
    public void setUseLighting(boolean useLighting) {
        this.useLighting = useLighting;
    }
    
    public boolean isUseTexture() {
        return useTexture;
    }
    
    public void setUseTexture(boolean useTexture) {
        this.useTexture = useTexture;
    }
    
    public boolean isDoubleSided() {
        return doubleSided;
    }
    
    public void setDoubleSided(boolean doubleSided) {
        this.doubleSided = doubleSided;
    }
    
    public boolean isWireframe() {
        return wireframe;
    }
    
    public void setWireframe(boolean wireframe) {
        this.wireframe = wireframe;
    }
    
    public Texture getTexture() {
        return texture;
    }
    
    public void setTexture(Texture texture) {
        this.texture = texture;
        this.textureId = texture != null ? texture.getTextureId() : 0;
    }
    
    public int getTextureId() {
        return textureId;
    }
    
    public Shader getShader() {
        return shader;
    }
    
    public void setShader(Shader shader) {
        this.shader = shader;
    }
    
    @Override
    public String toString() {
        return "Material{" +
                "name='" + name + '\'' +
                ", diffuseColor=" + diffuseColor +
                ", useTexture=" + useTexture +
                ", useLighting=" + useLighting +
                '}';
    }
}
