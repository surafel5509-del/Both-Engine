package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.List;

public class Panel extends UIComponent {
    private int backgroundColor;
    private Drawable backgroundDrawable;
    private int borderColor;
    private float borderWidth;
    private float cornerRadius;
    
    private List<UIComponent> children;
    private Paint backgroundPaint;
    private Paint borderPaint;

    public Panel() {
        super();
        this.backgroundColor = Color.TRANSPARENT;
        this.borderColor = Color.BLACK;
        this.borderWidth = 0;
        this.cornerRadius = 0;
        this.children = new ArrayList<>();
        
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setAntiAlias(true);
        this.backgroundPaint.setStyle(Paint.Style.FILL);
        
        this.borderPaint = new Paint();
        this.borderPaint.setAntiAlias(true);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeCap(Paint.Cap.ROUND);
        this.borderPaint.setStrokeJoin(Paint.Join.ROUND);
        
        setSize(200, 200);
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        drawBackground(canvas, bounds);
        drawBorder(canvas, bounds);
        
        for (UIComponent child : children) {
            if (child.isVisible()) {
                child.render(canvas, paint);
            }
        }
    }

    protected void drawBackground(Canvas canvas, Rect bounds) {
        if (backgroundDrawable != null) {
            backgroundDrawable.setBounds(bounds);
            backgroundDrawable.draw(canvas);
        } else if (backgroundColor != Color.TRANSPARENT) {
            backgroundPaint.setColor(backgroundColor);
            if (cornerRadius > 0) {
                canvas.drawRoundRect(
                    bounds.left, bounds.top, bounds.right, bounds.bottom,
                    cornerRadius, cornerRadius, backgroundPaint
                );
            } else {
                canvas.drawRect(bounds, backgroundPaint);
            }
        }
    }

    protected void drawBorder(Canvas canvas, Rect bounds) {
        if (borderWidth <= 0) return;
        
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderWidth);
        
        float offset = borderWidth / 2;
        RectF borderRect = new RectF(
            bounds.left + offset,
            bounds.top + offset,
            bounds.right - offset,
            bounds.bottom - offset
        );
        
        if (cornerRadius > 0) {
            canvas.drawRoundRect(borderRect, cornerRadius, cornerRadius, borderPaint);
        } else {
            canvas.drawRect(borderRect, borderPaint);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        for (UIComponent child : children) {
            child.update(deltaTime);
        }
    }

    public void addChild(UIComponent child) {
        children.add(child);
        child.getRectTransform().setParent(getRectTransform());
        notifyChanged();
    }

    public void removeChild(UIComponent child) {
        children.remove(child);
        child.getRectTransform().setParent(null);
        notifyChanged();
    }

    public void clearChildren() {
        for (UIComponent child : children) {
            child.getRectTransform().setParent(null);
        }
        children.clear();
        notifyChanged();
    }

    public List<UIComponent> getChildren() {
        return new ArrayList<>(children);
    }

    public int getChildCount() {
        return children.size();
    }

    public UIComponent getChild(int index) {
        return children.get(index);
    }

    public int indexOfChild(UIComponent child) {
        return children.indexOf(child);
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        notifyChanged();
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public void setBackgroundDrawable(Drawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        notifyChanged();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        notifyChanged();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        notifyChanged();
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        notifyChanged();
    }

    @Override
    public void onPointerDown(float x, float y, int pointerId) {
        super.onPointerDown(x, y, pointerId);
        for (UIComponent child : children) {
            Rect childBounds = child.getBounds();
            if (child.isVisible() && child.isInteractable() && childBounds != null && childBounds.contains((int) x, (int) y)) {
                child.onPointerDown(x, y, pointerId);
                break;
            }
        }
    }

    @Override
    public void onPointerMove(float x, float y, int pointerId) {
        super.onPointerMove(x, y, pointerId);
        for (UIComponent child : children) {
            Rect childBounds = child.getBounds();
            if (child.isVisible() && child.isInteractable() && childBounds != null && childBounds.contains((int) x, (int) y)) {
                child.onPointerMove(x, y, pointerId);
                break;
            }
        }
    }

    @Override
    public void onPointerUp(float x, float y, int pointerId) {
        super.onPointerUp(x, y, pointerId);
        for (UIComponent child : children) {
            Rect childBounds = child.getBounds();
            if (child.isVisible() && child.isInteractable() && childBounds != null && childBounds.contains((int) x, (int) y)) {
                child.onPointerUp(x, y, pointerId);
                break;
            }
        }
    }

    @Override
    public void onPointerCancel() {
        super.onPointerCancel();
        for (UIComponent child : children) {
            child.onPointerCancel();
        }
    }
}
