package com.bothengine.rendering;

/**
 * MeshRenderer
 * Component that renders a mesh
 */
public class MeshRenderer {
    private String name;
    private Mesh mesh;
    private Material material;
    private Vector3 position;
    private Vector3 rotation;
    private Vector3 scale;
    private boolean visible;
    private int layer;
    private boolean castShadows;
    private boolean receiveShadows;
    
    public MeshRenderer() {
        this.name = "MeshRenderer";
        this.mesh = null;
        this.material = null;
        this.position = new Vector3(0, 0, 0);
        this.rotation = new Vector3(0, 0, 0);
        this.scale = new Vector3(1, 1, 1);
        this.visible = true;
        this.layer = 0;
        this.castShadows = true;
        this.receiveShadows = true;
    }
    
    public MeshRenderer(String name) {
        this();
        this.name = name;
    }
    
    public MeshRenderer(Mesh mesh, Material material) {
        this();
        this.mesh = mesh;
        this.material = material;
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
     * Render the mesh
     */
    public void render() {
        if (!visible || mesh == null) return;
        
        // Bind material
        if (material != null) {
            material.bind(null); // Will use the current shader
        }
        
        // Render mesh
        mesh.render();
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }
    
    public Material getMaterial() {
        return material;
    }
    
    public void setMaterial(Material material) {
        this.material = material;
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
    
    public int getLayer() {
        return layer;
    }
    
    public void setLayer(int layer) {
        this.layer = layer;
    }
    
    public boolean isCastShadows() {
        return castShadows;
    }
    
    public void setCastShadows(boolean castShadows) {
        this.castShadows = castShadows;
    }
    
    public boolean isReceiveShadows() {
        return receiveShadows;
    }
    
    public void setReceiveShadows(boolean receiveShadows) {
        this.receiveShadows = receiveShadows;
    }
    
    @Override
    public String toString() {
        return "MeshRenderer{" +
                "name='" + name + '\'' +
                ", mesh=" + (mesh != null ? mesh.getName() : "null") +
                ", material=" + (material != null ? material.getName() : "null") +
                ", visible=" + visible +
                '}';
    }
}
