package com.bothengine.rendering;

/**
 * Light
 * Represents a light source in 3D space
 */
public class Light {
    public enum LightType {
        DIRECTIONAL,
        POINT,
        SPOT
    }
    
    private String name;
    private LightType type;
    private Vector3 position;
    private Vector3 direction;
    private Color color;
    private float intensity;
    private float range;
    private float innerAngle;
    private float outerAngle;
    private boolean enabled;
    private boolean castShadows;
    
    public Light() {
        this.name = "Light";
        this.type = LightType.DIRECTIONAL;
        this.position = new Vector3(0, 0, 0);
        this.direction = new Vector3(0, -1, 0);
        this.color = Color.WHITE;
        this.intensity = 1;
        this.range = 100;
        this.innerAngle = 30;
        this.outerAngle = 60;
        this.enabled = true;
        this.castShadows = false;
    }
    
    public Light(String name) {
        this();
        this.name = name;
    }
    
    public Light(LightType type) {
        this();
        this.type = type;
    }
    
    /**
     * Create a directional light
     */
    public static Light createDirectional(String name, Vector3 direction, Color color) {
        Light light = new Light(name);
        light.setType(LightType.DIRECTIONAL);
        light.setDirection(direction);
        light.setColor(color);
        return light;
    }
    
    /**
     * Create a point light
     */
    public static Light createPoint(String name, Vector3 position, Color color, float range) {
        Light light = new Light(name);
        light.setType(LightType.POINT);
        light.setPosition(position);
        light.setColor(color);
        light.setRange(range);
        return light;
    }
    
    /**
     * Create a spot light
     */
    public static Light createSpot(String name, Vector3 position, Vector3 direction, Color color, 
                                   float range, float innerAngle, float outerAngle) {
        Light light = new Light(name);
        light.setType(LightType.SPOT);
        light.setPosition(position);
        light.setDirection(direction);
        light.setColor(color);
        light.setRange(range);
        light.setInnerAngle(innerAngle);
        light.setOuterAngle(outerAngle);
        return light;
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public LightType getType() {
        return type;
    }
    
    public void setType(LightType type) {
        this.type = type;
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
    
    public Vector3 getDirection() {
        return direction;
    }
    
    public void setDirection(Vector3 direction) {
        this.direction = direction.normalized();
    }
    
    public void setDirection(float x, float y, float z) {
        this.direction = new Vector3(x, y, z).normalized();
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public float getIntensity() {
        return intensity;
    }
    
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    
    public float getRange() {
        return range;
    }
    
    public void setRange(float range) {
        this.range = range;
    }
    
    public float getInnerAngle() {
        return innerAngle;
    }
    
    public void setInnerAngle(float innerAngle) {
        this.innerAngle = innerAngle;
    }
    
    public float getOuterAngle() {
        return outerAngle;
    }
    
    public void setOuterAngle(float outerAngle) {
        this.outerAngle = outerAngle;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isCastShadows() {
        return castShadows;
    }
    
    public void setCastShadows(boolean castShadows) {
        this.castShadows = castShadows;
    }
    
    @Override
    public String toString() {
        return "Light{" +
                "name='" + name + '\'' +
                ", type=" + type +
                ", position=" + position +
                ", direction=" + direction +
                ", color=" + color +
                ", enabled=" + enabled +
                '}';
    }
}
