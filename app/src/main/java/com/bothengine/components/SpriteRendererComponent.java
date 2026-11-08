package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Color;
import com.bothengine.rendering.Sprite;
import com.bothengine.rendering.Texture;

/**
 * SpriteRendererComponent
 * Renders a 2D sprite for an entity
 */
public class SpriteRendererComponent extends Component {
    private Sprite sprite;
    private boolean visible;
    private int renderQueue;
    private float[] uvCoords = {0f, 0f, 1f, 1f};
    
    public SpriteRendererComponent() {
        this.sprite = new Sprite();
        this.visible = true;
        this.renderQueue = 0;
    }
    
    public SpriteRendererComponent(Sprite sprite) {
        this();
        this.sprite = sprite;
    }
    
    public SpriteRendererComponent(Texture texture) {
        this();
        this.sprite.setTexture(texture);
    }
    
    /**
     * Update the sprite position from the entity's TransformComponent
     */
    public void updateFromTransform() {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        if (transform != null) {
            sprite.setPosition(transform.getPosition().x, transform.getPosition().y);
            sprite.setRotation(transform.getRotation().getEulerAngles().z);
            sprite.setScale(transform.getScale().x, transform.getScale().y);
        }
    }
    
    // Getters and setters
    
    public Sprite getSprite() {
        return sprite;
    }
    
    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }
    
    public Texture getTexture() {
        return sprite.getTexture();
    }
    
    public void setTexture(Texture texture) {
        sprite.setTexture(texture);
    }

    /**
     * UV rectangle used when drawing the sprite (left, top, right, bottom).
     */
    public float[] getUvCoords() {
        return uvCoords.clone();
    }

    public void setUvCoords(float[] uvCoords) {
        if (uvCoords != null && uvCoords.length == 4) {
            this.uvCoords = uvCoords.clone();
        }
    }
    
    public Color getColor() {
        return sprite.getColor();
    }
    
    public void setColor(Color color) {
        sprite.setColor(color);
    }
    
    public boolean isVisible() {
        return visible;
    }
    
    public void setVisible(boolean visible) {
        this.visible = visible;
        sprite.setVisible(visible);
    }
    
    public int getRenderQueue() {
        return renderQueue;
    }
    
    public void setRenderQueue(int renderQueue) {
        this.renderQueue = renderQueue;
    }
    
    @Override
    public String toString() {
        return "SpriteRendererComponent{" +
                "sprite=" + (sprite != null ? sprite.getName() : "null") +
                ", visible=" + visible +
                '}';
    }
}
