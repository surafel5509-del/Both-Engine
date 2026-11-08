package com.bothengine.physics;

/**
 * PhysicsMaterial
 * Material properties for physics collisions
 */
public class PhysicsMaterial {
    private String name;
    private float restitution;
    private float staticFriction;
    private float dynamicFriction;
    private float frictionCombine;
    private float bounceCombine;
    
    public PhysicsMaterial() {
        this.name = "Default";
        this.restitution = 0.5f;
        this.staticFriction = 0.5f;
        this.dynamicFriction = 0.5f;
        this.frictionCombine = 0f; // Average
        this.bounceCombine = 0f; // Average
    }
    
    /**
     * Create a physics material with name
     */
    public PhysicsMaterial(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Create a physics material with all properties
     */
    public PhysicsMaterial(String name, float restitution, float staticFriction, float dynamicFriction) {
        this(name);
        this.restitution = restitution;
        this.staticFriction = staticFriction;
        this.dynamicFriction = dynamicFriction;
    }
    
    /**
     * Create a bouncy material
     */
    public static PhysicsMaterial createBouncy() {
        return new PhysicsMaterial("Bouncy", 0.8f, 0.2f, 0.2f);
    }
    
    /**
     * Create a sticky material
     */
    public static PhysicsMaterial createSticky() {
        return new PhysicsMaterial("Sticky", 0.1f, 0.8f, 0.8f);
    }
    
    /**
     * Create a slippery material
     */
    public static PhysicsMaterial createSlippery() {
        return new PhysicsMaterial("Slippery", 0.2f, 0.1f, 0.1f);
    }
    
    /**
     * Create a default material
     */
    public static PhysicsMaterial createDefault() {
        return new PhysicsMaterial("Default");
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public float getRestitution() {
        return restitution;
    }
    
    public void setRestitution(float restitution) {
        this.restitution = Math.max(0f, Math.min(1f, restitution));
    }
    
    public float getStaticFriction() {
        return staticFriction;
    }
    
    public void setStaticFriction(float staticFriction) {
        this.staticFriction = Math.max(0f, staticFriction);
    }
    
    public float getDynamicFriction() {
        return dynamicFriction;
    }
    
    public void setDynamicFriction(float dynamicFriction) {
        this.dynamicFriction = Math.max(0f, dynamicFriction);
    }
    
    public float getFrictionCombine() {
        return frictionCombine;
    }
    
    public void setFrictionCombine(float frictionCombine) {
        this.frictionCombine = frictionCombine;
    }
    
    public float getBounceCombine() {
        return bounceCombine;
    }
    
    public void setBounceCombine(float bounceCombine) {
        this.bounceCombine = bounceCombine;
    }
    
    @Override
    public String toString() {
        return "PhysicsMaterial{" +
                "name='" + name + '\'' +
                ", restitution=" + restitution +
                ", staticFriction=" + staticFriction +
                ", dynamicFriction=" + dynamicFriction +
                '}';
    }
    
    /**
     * Friction combine mode
     */
    public enum FrictionCombineMode {
        AVERAGE,
        MINIMUM,
        MAXIMUM,
        MULTIPLY
    }
    
    /**
     * Bounce combine mode
     */
    public enum BounceCombineMode {
        AVERAGE,
        MINIMUM,
        MAXIMUM,
        MULTIPLY
    }
}
