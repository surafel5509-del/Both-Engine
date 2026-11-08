package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Material;
import com.bothengine.rendering.Mesh;
import com.bothengine.rendering.MeshRenderer;

/**
 * MeshRendererComponent
 * Renders a mesh for an entity
 */
public class MeshRendererComponent extends Component {
    private MeshRenderer meshRenderer;
    private boolean visible;
    private int renderQueue;
    
    public MeshRendererComponent() {
        this.meshRenderer = new MeshRenderer();
        this.visible = true;
        this.renderQueue = 0;
    }
    
    public MeshRendererComponent(Mesh mesh, Material material) {
        this();
        this.meshRenderer.setMesh(mesh);
        this.meshRenderer.setMaterial(material);
    }
    
    /**
     * Get the transformation matrix from the entity's TransformComponent
     */
    public com.bothengine.rendering.Matrix4 getTransformMatrix() {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        if (transform != null) {
            return transform.getWorldMatrix();
        }
        return new com.bothengine.rendering.Matrix4();
    }
    
    // Getters and setters
    
    public MeshRenderer getMeshRenderer() {
        return meshRenderer;
    }
    
    public void setMeshRenderer(MeshRenderer meshRenderer) {
        this.meshRenderer = meshRenderer;
    }
    
    public Mesh getMesh() {
        return meshRenderer.getMesh();
    }
    
    public void setMesh(Mesh mesh) {
        meshRenderer.setMesh(mesh);
    }
    
    public Material getMaterial() {
        return meshRenderer.getMaterial();
    }
    
    public void setMaterial(Material material) {
        meshRenderer.setMaterial(material);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        meshRenderer.setVisible(visible);
    }
    
    public int getRenderQueue() {
        return renderQueue;
    }
    
    public void setRenderQueue(int renderQueue) {
        this.renderQueue = renderQueue;
    }
    
    @Override
    public String toString() {
        return "MeshRendererComponent{" +
                "mesh=" + (meshRenderer.getMesh() != null ? meshRenderer.getMesh().getName() : "null") +
                ", material=" + (meshRenderer.getMaterial() != null ? meshRenderer.getMaterial().getName() : "null") +
                ", visible=" + visible +
                '}';
    }
}
