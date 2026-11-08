package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;

public abstract class UIComponent extends Component {
    protected String name;
    protected boolean visible;
    protected boolean interactable;
    protected int renderOrder;
    protected RectTransform rectTransform;
    /** Engine-level canvas that owns this component (may be null). */
    protected Canvas uiCanvas;
    protected Entity entity;

    public UIComponent() {
        this.name = getClass().getSimpleName();
        this.visible = true;
        this.interactable = true;
        this.renderOrder = 0;
        this.rectTransform = new RectTransform();
    }

    public UIComponent(String name) {
        this();
        this.name = name;
    }

    public abstract void render(Canvas canvas, Paint paint);

    public void update(float deltaTime) {
    }

    public void onPointerDown(float x, float y, int pointerId) {
    }

    public void onPointerMove(float x, float y, int pointerId) {
    }

    public void onPointerUp(float x, float y, int pointerId) {
    }

    public void onPointerCancel() {
    }

    public void onClick() {
    }

    public void onHoverEnter() {
    }

    public void onHoverExit() {
    }

    public Rect getBounds() {
        if (rectTransform != null) {
            return rectTransform.getRect();
        }
        return new Rect();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean isInteractable() {
        return interactable;
    }

    public void setInteractable(boolean interactable) {
        this.interactable = interactable;
    }

    public int getRenderOrder() {
        return renderOrder;
    }

    public void setRenderOrder(int renderOrder) {
        this.renderOrder = renderOrder;
        notifyChanged();
    }

    public RectTransform getRectTransform() {
        return rectTransform;
    }

    public void setRectTransform(RectTransform rectTransform) {
        this.rectTransform = rectTransform;
    }

    public Canvas getUiCanvas() {
        return uiCanvas;
    }

    public void setCanvas(Canvas canvas) {
        this.uiCanvas = canvas;
    }

    /**
     * Mark the owning canvas dirty so it re-sorts and redraws.
     * Safe to call when the component is not attached to a canvas.
     */
    protected void notifyChanged() {
        if (uiCanvas != null) {
            uiCanvas.markDirty();
        }
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public void setAnchorMin(float x, float y) {
        if (rectTransform != null) {
            rectTransform.setAnchorMin(x, y);
        }
    }

    public void setAnchorMax(float x, float y) {
        if (rectTransform != null) {
            rectTransform.setAnchorMax(x, y);
        }
    }

    public void setPivot(float x, float y) {
        if (rectTransform != null) {
            rectTransform.setPivot(x, y);
        }
    }

    public void setSize(float width, float height) {
        if (rectTransform != null) {
            rectTransform.setSize(width, height);
        }
    }

    public void setPosition(float x, float y) {
        if (rectTransform != null) {
            rectTransform.setAnchoredPosition(x, y);
        }
    }

    public void setColor(int color) {
    }

    public int getColor() {
        return 0;
    }
}
