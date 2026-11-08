package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Vector3;

/**
 * RigidbodyComponent
 * Adds physics properties to an entity
 */
public class RigidbodyComponent extends Component {
    private Vector3 velocity;
    private Vector3 angularVelocity;
    private Vector3 acceleration;
    private float mass;
    private float drag;
    private float angularDrag;
    private boolean useGravity;
    private boolean isKinematic;
    private boolean isTrigger;
    private boolean isOnGround;
    private boolean freezeRotation;
    private CollisionDetectionMode collisionDetectionMode;
    
    public enum CollisionDetectionMode {
        DISCRETE,
        CONTINUOUS,
        CONTINUOUS_DYNAMIC
    }
    
    public RigidbodyComponent() {
        this.velocity = new Vector3(0, 0, 0);
        this.angularVelocity = new Vector3(0, 0, 0);
        this.acceleration = new Vector3(0, 0, 0);
        this.mass = 1;
        this.drag = 0;
        this.angularDrag = 0.05f;
        this.useGravity = true;
        this.isKinematic = false;
        this.isTrigger = false;
        this.isOnGround = false;
        this.freezeRotation = false;
        this.collisionDetectionMode = CollisionDetectionMode.DISCRETE;
    }
    
    /**
     * Add force to the rigidbody
     */
    public void addForce(Vector3 force) {
        if (isKinematic) return;
        acceleration = acceleration.add(force.multiply(1 / mass));
    }
    
    /**
     * Add force at a specific position
     */
    public void addForceAtPosition(Vector3 force, Vector3 position) {
        if (isKinematic) return;
        addForce(force);
        // Would also add torque in a real physics engine
    }
    
    /**
     * Add torque to the rigidbody
     */
    public void addTorque(Vector3 torque) {
        if (isKinematic || freezeRotation) return;
        // Would add to angular acceleration in a real physics engine
    }
    
    // Getters and setters
    
    public Vector3 getVelocity() {
        return velocity;
    }
    
    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
    
    public void setVelocity(float x, float y, float z) {
        this.velocity = new Vector3(x, y, z);
    }
    
    public Vector3 getAngularVelocity() {
        return angularVelocity;
    }
    
    public void setAngularVelocity(Vector3 angularVelocity) {
        this.angularVelocity = angularVelocity;
    }
    
    public Vector3 getAcceleration() {
        return acceleration;
    }
    
    public void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;
    }
    
    public float getMass() {
        return mass;
    }
    
    public void setMass(float mass) {
        this.mass = mass;
    }
    
    public float getDrag() {
        return drag;
    }
    
    public void setDrag(float drag) {
        this.drag = drag;
    }
    
    public float getAngularDrag() {
        return angularDrag;
    }
    
    public void setAngularDrag(float angularDrag) {
        this.angularDrag = angularDrag;
    }
    
    public boolean isUseGravity() {
        return useGravity;
    }
    
    public void setUseGravity(boolean useGravity) {
        this.useGravity = useGravity;
    }
    
    public boolean isKinematic() {
        return isKinematic;
    }
    
    public void setKinematic(boolean kinematic) {
        isKinematic = kinematic;
    }
    
    public boolean isTrigger() {
        return isTrigger;
    }
    
    public void setTrigger(boolean trigger) {
        isTrigger = trigger;
    }
    
    public boolean isOnGround() {
        return isOnGround;
    }
    
    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }
    
    public boolean isFreezeRotation() {
        return freezeRotation;
    }
    
    public void setFreezeRotation(boolean freezeRotation) {
        this.freezeRotation = freezeRotation;
    }
    
    public CollisionDetectionMode getCollisionDetectionMode() {
        return collisionDetectionMode;
    }
    
    public void setCollisionDetectionMode(CollisionDetectionMode collisionDetectionMode) {
        this.collisionDetectionMode = collisionDetectionMode;
    }
    
    @Override
    public String toString() {
        return "RigidbodyComponent{" +
                "velocity=" + velocity +
                ", mass=" + mass +
                ", useGravity=" + useGravity +
                ", isKinematic=" + isKinematic +
                '}';
    }
}
