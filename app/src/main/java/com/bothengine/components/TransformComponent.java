package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Matrix4;
import com.bothengine.rendering.Vector3;
import com.bothengine.components.Quaternion;

/**
 * TransformComponent
 * Stores the position, rotation, and scale of an entity
 */
public class TransformComponent extends Component {
    private Vector3 position;
    private Quaternion rotation;
    private Vector3 scale;
    
    // Cached transformation matrix
    private Matrix4 worldMatrix;
    private boolean dirty;
    
    // Parent and children
    private TransformComponent parent;
    
    public TransformComponent() {
        this.position = new Vector3(0, 0, 0);
        this.rotation = new Quaternion();
        this.scale = new Vector3(1, 1, 1);
        this.worldMatrix = new Matrix4();
        this.dirty = true;
        this.parent = null;
    }
    
    /**
     * Create a transform component with position
     */
    public static TransformComponent create(Vector3 position) {
        TransformComponent component = new TransformComponent();
        component.setPosition(position);
        return component;
    }
    
    /**
     * Create a transform component with position, rotation, and scale
     */
    public static TransformComponent create(Vector3 position, Quaternion rotation, Vector3 scale) {
        TransformComponent component = new TransformComponent();
        component.setPosition(position);
        component.setRotation(rotation);
        component.setScale(scale);
        return component;
    }
    
    /**
     * Update the transformation matrix
     */
    private void updateMatrix() {
        if (!dirty) return;
        
        worldMatrix = Matrix4.identity()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
        
        // If has parent, multiply by parent matrix
        if (parent != null) {
            worldMatrix = Matrix4.multiply(parent.getWorldMatrix(), worldMatrix);
        }
        
        dirty = false;
    }
    
    /**
     * Get the world transformation matrix
     */
    public Matrix4 getWorldMatrix() {
        updateMatrix();
        return worldMatrix;
    }
    
    /**
     * Get the local transformation matrix
     */
    public Matrix4 getLocalMatrix() {
        return new Matrix4()
                .translate(position)
                .rotate(rotation)
                .scale(scale);
    }
    
    /**
     * Mark the transform as dirty (needs matrix update)
     */
    public void setDirty() {
        dirty = true;
        
        // Mark children as dirty too
        if (getEntity() != null) {
            for (Component component : getEntity().getComponents().values()) {
                if (component instanceof TransformComponent) {
                    TransformComponent child = (TransformComponent) component;
                    if (child != this) {
                        child.setDirty();
                    }
                }
            }
        }
    }
    
    // Getters and setters
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void setPosition(Vector3 position) {
        this.position = position;
        setDirty();
    }
    
    public void setPosition(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
        setDirty();
    }
    
    public Quaternion getRotation() {
        return rotation;
    }
    
    public void setRotation(Quaternion rotation) {
        this.rotation = rotation;
        setDirty();
    }
    
    public void setRotation(float x, float y, float z, float w) {
        this.rotation = new Quaternion(x, y, z, w);
        setDirty();
    }
    
    public Vector3 getScale() {
        return scale;
    }
    
    public void setScale(Vector3 scale) {
        this.scale = scale;
        setDirty();
    }
    
    public void setScale(float x, float y, float z) {
        this.scale = new Vector3(x, y, z);
        setDirty();
    }
    
    public void setScale(float scale) {
        this.scale = new Vector3(scale, scale, scale);
        setDirty();
    }
    
    public TransformComponent getParent() {
        return parent;
    }
    
    public void setParent(TransformComponent parent) {
        this.parent = parent;
        setDirty();
    }
    
    // Translation methods
    
    public void translate(Vector3 translation) {
        position = position.add(translation);
        setDirty();
    }
    
    public void translate(float x, float y, float z) {
        position = position.add(new Vector3(x, y, z));
        setDirty();
    }
    
    // Rotation methods
    
    public void rotate(Quaternion rotation) {
        this.rotation = this.rotation.multiply(rotation);
        setDirty();
    }
    
    public void rotate(float angle, Vector3 axis) {
        Quaternion q = Quaternion.rotate(axis, angle);
        rotate(q);
    }
    
    public void rotate(float angle, float x, float y, float z) {
        rotate(angle, new Vector3(x, y, z));
    }
    
    // Scale methods
    
    public void scale(Vector3 scale) {
        this.scale = Vector3.multiply(this.scale, scale);
        setDirty();
    }
    
    public void scale(float x, float y, float z) {
        scale(new Vector3(x, y, z));
    }
    
    public void scale(float scale) {
        scale(new Vector3(scale, scale, scale));
    }
    
    // Look at methods
    
    public void lookAt(Vector3 target) {
        Vector3 direction = target.subtract(position).normalize();
        rotation = Quaternion.lookAt(direction);
        setDirty();
    }
    
    public void lookAt(Vector3 target, Vector3 up) {
        Vector3 direction = target.subtract(position).normalize();
        rotation = Quaternion.lookAt(direction, up);
        setDirty();
    }
    
    // Forward, up, right methods
    
    public Vector3 getForward() {
        return rotation.getForward();
    }
    
    public Vector3 getUp() {
        return rotation.getUp();
    }
    
    public Vector3 getRight() {
        return rotation.getRight();
    }
    
    @Override
    public String toString() {
        return "TransformComponent{" +
                "position=" + position +
                ", rotation=" + rotation +
                ", scale=" + scale +
                '}';
    }
}
