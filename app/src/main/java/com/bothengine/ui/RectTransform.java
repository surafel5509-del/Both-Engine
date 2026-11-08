package com.bothengine.ui;

import android.graphics.Rect;
import android.graphics.RectF;

public class RectTransform {
    public enum AnchorPreset {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_CENTER,
        MIDDLE_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT,
        STRETCH_ALL,
        STRETCH_HORIZONTAL,
        STRETCH_VERTICAL
    }

    public enum PivotPreset {
        TOP_LEFT,
        TOP_CENTER,
        TOP_RIGHT,
        MIDDLE_LEFT,
        MIDDLE_CENTER,
        MIDDLE_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_CENTER,
        BOTTOM_RIGHT
    }

    private Vector2 anchorMin;
    private Vector2 anchorMax;
    private Vector2 anchoredPosition;
    private Vector2 sizeDelta;
    private Vector2 pivot;
    private Vector2 offsetMin;
    private Vector2 offsetMax;
    
    private RectTransform parent;
    private boolean dirty;
    private Rect cachedRect;

    public RectTransform() {
        this.anchorMin = new Vector2(0, 0);
        this.anchorMax = new Vector2(0, 0);
        this.anchoredPosition = new Vector2(0, 0);
        this.sizeDelta = new Vector2(100, 100);
        this.pivot = new Vector2(0.5f, 0.5f);
        this.offsetMin = new Vector2(0, 0);
        this.offsetMax = new Vector2(0, 0);
        this.dirty = true;
        this.cachedRect = new Rect();
    }

    public Rect getRect() {
        if (dirty || cachedRect == null) {
            updateRect();
            dirty = false;
        }
        return cachedRect;
    }

    private void updateRect() {
        Rect parentRect = null;
        if (parent != null) {
            parentRect = parent.getRect();
        }
        
        float parentWidth = parentRect != null ? parentRect.width() : 0;
        float parentHeight = parentRect != null ? parentRect.height() : 0;
        
        float minX = anchorMin.x * parentWidth + offsetMin.x;
        float minY = anchorMin.y * parentHeight + offsetMin.y;
        float maxX = anchorMax.x * parentWidth + offsetMax.x;
        float maxY = anchorMax.y * parentHeight + offsetMax.y;
        
        float width = maxX - minX + sizeDelta.x;
        float height = maxY - minY + sizeDelta.y;
        
        float pivotX = width * pivot.x;
        float pivotY = height * pivot.y;
        
        float finalX = minX + anchoredPosition.x - pivotX;
        float finalY = minY + anchoredPosition.y - pivotY;
        
        cachedRect.set(
            (int) finalX,
            (int) finalY,
            (int) (finalX + width),
            (int) (finalY + height)
        );
    }

    public void setAnchorMin(float x, float y) {
        anchorMin.set(x, y);
        dirty = true;
    }

    public void setAnchorMax(float x, float y) {
        anchorMax.set(x, y);
        dirty = true;
    }

    public void setAnchoredPosition(float x, float y) {
        anchoredPosition.set(x, y);
        dirty = true;
    }

    public void setSize(float width, float height) {
        sizeDelta.set(width, height);
        dirty = true;
    }

    public void setPivot(float x, float y) {
        pivot.set(x, y);
        dirty = true;
    }

    public void setOffsetMin(float x, float y) {
        offsetMin.set(x, y);
        dirty = true;
    }

    public void setOffsetMax(float x, float y) {
        offsetMax.set(x, y);
        dirty = true;
    }

    public void setParent(RectTransform parent) {
        this.parent = parent;
        dirty = true;
    }

    public RectTransform getParent() {
        return parent;
    }

    public Vector2 getAnchorMin() {
        return anchorMin;
    }

    public Vector2 getAnchorMax() {
        return anchorMax;
    }

    public Vector2 getAnchoredPosition() {
        return anchoredPosition;
    }

    public Vector2 getSizeDelta() {
        return sizeDelta;
    }

    public Vector2 getPivot() {
        return pivot;
    }

    public Vector2 getOffsetMin() {
        return offsetMin;
    }

    public Vector2 getOffsetMax() {
        return offsetMax;
    }

    public void setAnchorPreset(AnchorPreset preset) {
        switch (preset) {
            case TOP_LEFT:
                setAnchorMin(0, 0);
                setAnchorMax(0, 0);
                break;
            case TOP_CENTER:
                setAnchorMin(0.5f, 0);
                setAnchorMax(0.5f, 0);
                break;
            case TOP_RIGHT:
                setAnchorMin(1, 0);
                setAnchorMax(1, 0);
                break;
            case MIDDLE_LEFT:
                setAnchorMin(0, 0.5f);
                setAnchorMax(0, 0.5f);
                break;
            case MIDDLE_CENTER:
                setAnchorMin(0.5f, 0.5f);
                setAnchorMax(0.5f, 0.5f);
                break;
            case MIDDLE_RIGHT:
                setAnchorMin(1, 0.5f);
                setAnchorMax(1, 0.5f);
                break;
            case BOTTOM_LEFT:
                setAnchorMin(0, 1);
                setAnchorMax(0, 1);
                break;
            case BOTTOM_CENTER:
                setAnchorMin(0.5f, 1);
                setAnchorMax(0.5f, 1);
                break;
            case BOTTOM_RIGHT:
                setAnchorMin(1, 1);
                setAnchorMax(1, 1);
                break;
            case STRETCH_ALL:
                setAnchorMin(0, 0);
                setAnchorMax(1, 1);
                break;
            case STRETCH_HORIZONTAL:
                setAnchorMin(0, 0.5f);
                setAnchorMax(1, 0.5f);
                break;
            case STRETCH_VERTICAL:
                setAnchorMin(0.5f, 0);
                setAnchorMax(0.5f, 1);
                break;
        }
    }

    public void setPivotPreset(PivotPreset preset) {
        switch (preset) {
            case TOP_LEFT:
                setPivot(0, 0);
                break;
            case TOP_CENTER:
                setPivot(0.5f, 0);
                break;
            case TOP_RIGHT:
                setPivot(1, 0);
                break;
            case MIDDLE_LEFT:
                setPivot(0, 0.5f);
                break;
            case MIDDLE_CENTER:
                setPivot(0.5f, 0.5f);
                break;
            case MIDDLE_RIGHT:
                setPivot(1, 0.5f);
                break;
            case BOTTOM_LEFT:
                setPivot(0, 1);
                break;
            case BOTTOM_CENTER:
                setPivot(0.5f, 1);
                break;
            case BOTTOM_RIGHT:
                setPivot(1, 1);
                break;
        }
    }

    public boolean contains(float x, float y) {
        Rect rect = getRect();
        return rect.contains((int) x, (int) y);
    }

    public static class Vector2 {
        public float x;
        public float y;

        public Vector2(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public void set(float x, float y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "Vector2{x=" + x + ", y=" + y + "}";
        }
    }
}
