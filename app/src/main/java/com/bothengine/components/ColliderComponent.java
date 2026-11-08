package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;
import com.bothengine.rendering.Bounds;
import com.bothengine.rendering.Vector3;

/**
 * ColliderComponent
 * Adds collision detection to an entity
 */
public class ColliderComponent extends Component {
    private ColliderType type;
    private Bounds bounds;
    private Vector3 center;
    private Vector3 size;
    private float radius;
    private boolean isTrigger;
    private boolean enabled;
    private Entity entity;
    private boolean isGround;
    
    public enum ColliderType {
        BOX,
        SPHERE,
        CAPSULE,
        MESH
    }
    
    public ColliderComponent() {
        this.type = ColliderType.BOX;
        this.bounds = new Bounds();
        this.center = new Vector3(0, 0, 0);
        this.size = new Vector3(1, 1, 1);
        this.radius = 0.5f;
        this.isTrigger = false;
        this.enabled = true;
        this.entity = null;
        this.isGround = false;
    }
    
    /**
     * Create a box collider
     */
    public static ColliderComponent createBox(Vector3 center, Vector3 size) {
        ColliderComponent collider = new ColliderComponent();
        collider.setType(ColliderType.BOX);
        collider.setCenter(center);
        collider.setSize(size);
        collider.updateBounds();
        return collider;
    }
    
    /**
     * Create a sphere collider
     */
    public static ColliderComponent createSphere(Vector3 center, float radius) {
        ColliderComponent collider = new ColliderComponent();
        collider.setType(ColliderType.SPHERE);
        collider.setCenter(center);
        collider.setRadius(radius);
        collider.updateBounds();
        return collider;
    }
    
    /**
     * Update the bounds based on collider type
     */
    private void updateBounds() {
        switch (type) {
            case BOX:
                bounds = new Bounds(
                    Vector3.subtract(center, Vector3.multiply(size, 0.5f)),
                    Vector3.add(center, Vector3.multiply(size, 0.5f))
                );
                break;
            case SPHERE:
                bounds = new Bounds(
                    Vector3.subtract(center, new Vector3(radius, radius, radius)),
                    Vector3.add(center, new Vector3(radius, radius, radius))
                );
                break;
            case CAPSULE:
            case MESH:
                bounds = new Bounds();
                break;
        }
    }
    
    // Getters and setters
    
    public ColliderType getType() {
        return type;
    }
    
    public void setType(ColliderType type) {
        this.type = type;
        updateBounds();
    }
    
    public Bounds getBounds() {
        return bounds;
    }
    
    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }
    
    public Vector3 getCenter() {
        return center;
    }
    
    public void setCenter(Vector3 center) {
        this.center = center;
        updateBounds();
    }
    
    public void setCenter(float x, float y, float z) {
        this.center = new Vector3(x, y, z);
        updateBounds();
    }
    
    public Vector3 getSize() {
        return size;
    }
    
    public void setSize(Vector3 size) {
        this.size = size;
        updateBounds();
    }
    
    public void setSize(float x, float y, float z) {
        this.size = new Vector3(x, y, z);
        updateBounds();
    }
    
    public float getRadius() {
        return radius;
    }
    
    public void setRadius(float radius) {
        this.radius = radius;
        updateBounds();
    }
    
    public boolean isTrigger() {
        return isTrigger;
    }
    
    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }
    
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    public boolean isGround() {
        return isGround;
    }
    
    public void setGround(boolean ground) {
        isGround = ground;
    }
    
    @Override
    public String toString() {
        return "ColliderComponent{" +
                "type=" + type +
                ", center=" + center +
                ", size=" + size +
                ", radius=" + radius +
                ", isTrigger=" + isTrigger +
                '}';
    }
}
