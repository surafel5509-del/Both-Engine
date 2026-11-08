package com.bothengine.rendering;

import java.util.ArrayList;
import java.util.List;

/**
 * Model
 * Represents a 3D model composed of multiple meshes
 */
public class Model {
    private String name;
    private List<Mesh> meshes;
    private List<Material> materials;
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;
    private boolean visible;
    private Bounds bounds;
    
    public Model() {
        this.name = "Unnamed";
        this.meshes = new ArrayList<>();
        this.materials = new ArrayList<>();
        this.position = new Vector3(0, 0, 0);
        this.rotation = new Vector3(0, 0, 0);
        this.scale = new Vector3(1, 1, 1);
        this.visible = true;
        this.bounds = new Bounds();
    }
    
    public Model(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Add a mesh to the model
     */
    public void addMesh(Mesh mesh) {
        meshes.add(mesh);
        calculateBounds();
    }
    
    /**
     * Remove a mesh from the model
     */
    public void removeMesh(Mesh mesh) {
        meshes.remove(mesh);
        calculateBounds();
    }
    
    /**
     * Add a material to the model
     */
    public void addMaterial(Material material) {
        materials.add(material);
    }
    
    /**
     * Remove a material from the model
     */
    public void removeMaterial(Material material) {
        materials.remove(material);
    }
    
    /**
     * Get material by index
     */
    public Material getMaterial() {
        return materials.isEmpty() ? null : materials.get(0);
    }

    public Material getMaterial(int index) {
        if (index >= 0 && index < materials.size()) {
            return materials.get(index);
        }
        return null;
    }
    
    /**
     * Calculate bounds from all meshes
     */
    private void calculateBounds() {
        bounds = new Bounds();
        
        for (Mesh mesh : meshes) {
            bounds.expand(mesh.getBounds());
        }
    }
    
    /**
     * Get the transformation matrix
     */
    public Matrix4 getTransformMatrix() {
        Matrix4 modelMatrix = new Matrix4();
        
        // Translate
        modelMatrix.translate(position);
        
        // Rotate
        if (rotation.x != 0) modelMatrix.rotateX((float) Math.toRadians(rotation.x));
        if (rotation.y != 0) modelMatrix.rotateY((float) Math.toRadians(rotation.y));
        if (rotation.z != 0) modelMatrix.rotateZ((float) Math.toRadians(rotation.z));
        
        // Scale
        modelMatrix.scale(scale);
        
        return modelMatrix;
    }
    
    /**
     * Update the model
     */
    public void update(float deltaTime) {
        // Update animations, etc.
    }
    
    /**
     * Render the model
     */
    public void render() {
        if (!visible) return;
        
        for (int i = 0; i < meshes.size(); i++) {
            Mesh mesh = meshes.get(i);
            Material material = i < materials.size() ? materials.get(i) : null;
            
            if (mesh != null) {
                if (material != null) {
                    material.bind(null); // Will use the current shader
                }
                mesh.render();
            }
        }
    }
    
    /**
     * Delete all GPU resources
     */
    public void delete() {
        for (Mesh mesh : meshes) {
            if (mesh != null) {
                mesh.delete();
            }
        }
        meshes.clear();
        materials.clear();
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public List<Mesh> getMeshes() {
        return new ArrayList<>(meshes);
    }
    
    public List<Material> getMaterials() {
        return new ArrayList<>(materials);
    }
    
    public Mesh getMesh(int index) {
        if (index >= 0 && index < meshes.size()) {
            return meshes.get(index);
        }
        return null;
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    
    public void setPosition(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
    }
    
    public Vector3 getRotation() {
        return rotation;
    }
    
    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        this.rotation = new Vector3(x, y, z);
    }
    
    public Vector3 getScale() {
        return scale;
    }
    
    public void setScale(Vector3 scale) {
        this.scale = scale;
    }
    
    public void setScale(float x, float y, float z) {
        this.scale = new Vector3(x, y, z);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
    }
    
    public Bounds getBounds() {
        return bounds;
    }
    
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }
    
    @Override
    public String toString() {
        return "Model{" +
                "name='" + name + '\'' +
                ", meshes=" + meshes.size() +
                ", materials=" + materials.size() +
                ", visible=" + visible +
                '}';
    }
}
