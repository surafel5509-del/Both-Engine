package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Color;
import com.bothengine.rendering.Light;
import com.bothengine.rendering.Vector3;

/**
 * LightComponent
 * Adds a light source to an entity
 */
public class LightComponent extends Component {
    private Light light;
    private boolean castShadows;

    public LightComponent() {
        this.light = new Light(Light.LightType.DIRECTIONAL);
        this.castShadows = false;
    }

    public LightComponent(Light light) {
        this.light = light != null ? light : new Light(Light.LightType.DIRECTIONAL);
        this.castShadows = this.light.isCastShadows();
    }

    /**
     * Create a directional light component
     */
    public static LightComponent createDirectional(Vector3 direction, Color color, float intensity) {
        LightComponent component = new LightComponent(Light.createDirectional("Light", direction, color));
        component.setIntensity(intensity);
        return component;
    }

    /**
     * Create a point light component
     */
    public static LightComponent createPoint(Vector3 position, Color color, float intensity, float range) {
        LightComponent component = new LightComponent(Light.createPoint("Light", position, color, range));
        component.setIntensity(intensity);
        return component;
    }

    /**
     * Create a spot light component
     */
    public static LightComponent createSpot(Vector3 position, Vector3 direction, Color color,
                                            float intensity, float innerAngle, float outerAngle) {
        LightComponent component = new LightComponent(
                Light.createSpot("Light", position, direction, color, 10f, innerAngle, outerAngle));
        component.setIntensity(intensity);
        return component;
    }

    public Light getLight() {
        return light;
    }

    public void setLight(Light light) {
        this.light = light;
    }

    public Light.LightType getType() {
        return light.getType();
    }

    public void setType(Light.LightType type) {
        light.setType(type);
    }

    public Vector3 getPosition() {
        return light.getPosition();
    }

    public void setPosition(Vector3 position) {
        light.setPosition(position);
    }

    public void setPosition(float x, float y, float z) {
        light.setPosition(x, y, z);
    }

    public Vector3 getDirection() {
        return light.getDirection();
    }

    public void setDirection(Vector3 direction) {
        light.setDirection(direction);
    }

    public void setDirection(float x, float y, float z) {
        light.setDirection(x, y, z);
    }

    public Color getColor() {
        return light.getColor();
    }

    public void setColor(Color color) {
        light.setColor(color);
    }

    public float getIntensity() {
        return light.getIntensity();
    }

    public void setIntensity(float intensity) {
        light.setIntensity(intensity);
    }

    public float getRange() {
        return light.getRange();
    }

    public void setRange(float range) {
        light.setRange(range);
    }

    public float getInnerAngle() {
        return light.getInnerAngle();
    }

    public void setInnerAngle(float innerAngle) {
        light.setInnerAngle(innerAngle);
    }

    public float getOuterAngle() {
        return light.getOuterAngle();
    }

    public void setOuterAngle(float outerAngle) {
        light.setOuterAngle(outerAngle);
    }

    public boolean isCastShadows() {
        return castShadows;
    }

    public void setCastShadows(boolean castShadows) {
        this.castShadows = castShadows;
        light.setCastShadows(castShadows);
    }

    @Override
    public String toString() {
        return "LightComponent{" +
                "type=" + getType() +
                ", intensity=" + getIntensity() +
                ", castShadows=" + castShadows +
                '}';
    }
}
