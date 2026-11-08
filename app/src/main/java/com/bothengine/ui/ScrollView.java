package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class ScrollView extends Panel {
    public enum ScrollDirection {
        VERTICAL,
        HORIZONTAL,
        BOTH
    }

    public enum ScrollbarVisibility {
        ALWAYS,
        AUTO,
        NEVER
    }

    private ScrollDirection scrollDirection;
    private ScrollbarVisibility scrollbarVisibility;
    private float scrollX;
    private float scrollY;
    private float maxScrollX;
    private float maxScrollY;
    private float contentWidth;
    private float contentHeight;
    private boolean isDragging;
    private float dragStartX;
    private float dragStartY;
    private float dragStartScrollX;
    private float dragStartScrollY;
    
    private int scrollbarColor;
    private float scrollbarWidth;
    private int scrollbarBackgroundColor;
    private Drawable scrollbarDrawable;
    private Drawable scrollbarBackgroundDrawable;
    
    private Paint scrollbarPaint;
    private Paint scrollbarBackgroundPaint;
    private Rect scrollbarRect;
    private Rect scrollbarBackgroundRect;
    
    private float scrollSensitivity;
    private boolean scrollable;

    public ScrollView() {
        super();
        this.scrollDirection = ScrollDirection.VERTICAL;
        this.scrollbarVisibility = ScrollbarVisibility.AUTO;
        this.scrollX = 0;
        this.scrollY = 0;
        this.maxScrollX = 0;
        this.maxScrollY = 0;
        this.contentWidth = 0;
        this.contentHeight = 0;
        this.isDragging = false;
        this.scrollbarColor = Color.GRAY;
        this.scrollbarWidth = 10;
        this.scrollbarBackgroundColor = Color.LTGRAY;
        this.scrollSensitivity = 1.0f;
        this.scrollable = true;
        
        this.scrollbarPaint = new Paint();
        this.scrollbarPaint.setAntiAlias(true);
        this.scrollbarPaint.setStyle(Paint.Style.FILL);
        this.scrollbarPaint.setColor(scrollbarColor);
        
        this.scrollbarBackgroundPaint = new Paint();
        this.scrollbarBackgroundPaint.setAntiAlias(true);
        this.scrollbarBackgroundPaint.setStyle(Paint.Style.FILL);
        this.scrollbarBackgroundPaint.setColor(scrollbarBackgroundColor);
        
        this.scrollbarRect = new Rect();
        this.scrollbarBackgroundRect = new Rect();
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        drawBackground(canvas, bounds);
        drawBorder(canvas, bounds);
        
        canvas.save();
        canvas.clipRect(bounds);
        canvas.translate(-scrollX, -scrollY);
        
        for (UIComponent child : getChildren()) {
            if (child.isVisible()) {
                child.render(canvas, paint);
            }
        }
        
        canvas.restore();
        
        drawScrollbars(canvas, bounds);
    }

    private void drawScrollbars(Canvas canvas, Rect bounds) {
        if (scrollbarVisibility == ScrollbarVisibility.NEVER) return;
        
        boolean showHorizontal = scrollDirection == ScrollDirection.HORIZONTAL || scrollDirection == ScrollDirection.BOTH;
        boolean showVertical = scrollDirection == ScrollDirection.VERTICAL || scrollDirection == ScrollDirection.BOTH;
        
        if (showVertical && (scrollbarVisibility == ScrollbarVisibility.ALWAYS || maxScrollY > 0)) {
            drawVerticalScrollbar(canvas, bounds);
        }
        
        if (showHorizontal && (scrollbarVisibility == ScrollbarVisibility.ALWAYS || maxScrollX > 0)) {
            drawHorizontalScrollbar(canvas, bounds);
        }
    }

    private void drawVerticalScrollbar(Canvas canvas, Rect bounds) {
        float scrollbarHeight = bounds.height() * (bounds.height() / (contentHeight + bounds.height()));
        scrollbarHeight = Math.max(20, scrollbarHeight);
        
        float scrollbarY = bounds.top + (bounds.height() - scrollbarHeight) * (scrollY / maxScrollY);
        
        scrollbarBackgroundRect.set(
            bounds.right - (int) scrollbarWidth,
            bounds.top,
            bounds.right,
            bounds.bottom
        );
        
        scrollbarRect.set(
            bounds.right - (int) scrollbarWidth,
            (int) scrollbarY,
            bounds.right,
            (int) (scrollbarY + scrollbarHeight)
        );
        
        if (scrollbarBackgroundDrawable != null) {
            scrollbarBackgroundDrawable.setBounds(scrollbarBackgroundRect);
            scrollbarBackgroundDrawable.draw(canvas);
        } else {
            canvas.drawRect(scrollbarBackgroundRect, scrollbarBackgroundPaint);
        }
        
        if (scrollbarDrawable != null) {
            scrollbarDrawable.setBounds(scrollbarRect);
            scrollbarDrawable.draw(canvas);
        } else {
            canvas.drawRect(scrollbarRect, scrollbarPaint);
        }
    }

    private void drawHorizontalScrollbar(Canvas canvas, Rect bounds) {
        float scrollbarWidth = bounds.width() * (bounds.width() / (contentWidth + bounds.width()));
        scrollbarWidth = Math.max(20, scrollbarWidth);
        
        float scrollbarX = bounds.left + (bounds.width() - scrollbarWidth) * (scrollX / maxScrollX);
        
        scrollbarBackgroundRect.set(
            bounds.left,
            bounds.bottom - (int) this.scrollbarWidth,
            bounds.right,
            bounds.bottom
        );
        
        scrollbarRect.set(
            (int) scrollbarX,
            bounds.bottom - (int) this.scrollbarWidth,
            (int) (scrollbarX + scrollbarWidth),
            bounds.bottom
        );
        
        if (scrollbarBackgroundDrawable != null) {
            scrollbarBackgroundDrawable.setBounds(scrollbarBackgroundRect);
            scrollbarBackgroundDrawable.draw(canvas);
        } else {
            canvas.drawRect(scrollbarBackgroundRect, scrollbarBackgroundPaint);
        }
        
        if (scrollbarDrawable != null) {
            scrollbarDrawable.setBounds(scrollbarRect);
            scrollbarDrawable.draw(canvas);
        } else {
            canvas.drawRect(scrollbarRect, scrollbarPaint);
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateScrollBounds();
    }

    private void updateScrollBounds() {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        contentWidth = 0;
        contentHeight = 0;
        
        for (UIComponent child : getChildren()) {
            Rect childBounds = child.getBounds();
            if (childBounds != null) {
                contentWidth = Math.max(contentWidth, childBounds.right);
                contentHeight = Math.max(contentHeight, childBounds.bottom);
            }
        }
        
        maxScrollX = Math.max(0, contentWidth - bounds.width());
        maxScrollY = Math.max(0, contentHeight - bounds.height());
        
        scrollX = Math.max(0, Math.min(scrollX, maxScrollX));
        scrollY = Math.max(0, Math.min(scrollY, maxScrollY));
    }

    @Override
    public void addChild(UIComponent child) {
        super.addChild(child);
        updateScrollBounds();
    }

    @Override
    public void removeChild(UIComponent child) {
        super.removeChild(child);
        updateScrollBounds();
    }

    @Override
    public void onPointerDown(float x, float y, int pointerId) {
        if (!scrollable) return;
        
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        dragStartX = x;
        dragStartY = y;
        dragStartScrollX = scrollX;
        dragStartScrollY = scrollY;
        isDragging = true;
    }

    @Override
    public void onPointerMove(float x, float y, int pointerId) {
        if (!isDragging || !scrollable) return;
        
        float deltaX = (dragStartX - x) * scrollSensitivity;
        float deltaY = (dragStartY - y) * scrollSensitivity;
        
        scrollX = Math.max(0, Math.min(maxScrollX, dragStartScrollX + deltaX));
        scrollY = Math.max(0, Math.min(maxScrollY, dragStartScrollY + deltaY));
        
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    @Override
    public void onPointerUp(float x, float y, int pointerId) {
        isDragging = false;
    }

    @Override
    public void onPointerCancel() {
        isDragging = false;
    }

    public void scrollTo(float x, float y) {
        scrollX = Math.max(0, Math.min(maxScrollX, x));
        scrollY = Math.max(0, Math.min(maxScrollY, y));
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public void scrollBy(float dx, float dy) {
        scrollTo(scrollX + dx, scrollY + dy);
    }

    public void scrollToTop() {
        scrollTo(0, 0);
    }

    public void scrollToBottom() {
        scrollTo(maxScrollX, maxScrollY);
    }

    public float getScrollX() {
        return scrollX;
    }

    public void setScrollX(float scrollX) {
        this.scrollX = Math.max(0, Math.min(maxScrollX, scrollX));
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getScrollY() {
        return scrollY;
    }

    public void setScrollY(float scrollY) {
        this.scrollY = Math.max(0, Math.min(maxScrollY, scrollY));
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getMaxScrollX() {
        return maxScrollX;
    }

    public float getMaxScrollY() {
        return maxScrollY;
    }

    public ScrollDirection getScrollDirection() {
        return scrollDirection;
    }

    public void setScrollDirection(ScrollDirection scrollDirection) {
        this.scrollDirection = scrollDirection;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public ScrollbarVisibility getScrollbarVisibility() {
        return scrollbarVisibility;
    }

    public void setScrollbarVisibility(ScrollbarVisibility scrollbarVisibility) {
        this.scrollbarVisibility = scrollbarVisibility;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getScrollbarColor() {
        return scrollbarColor;
    }

    public void setScrollbarColor(int scrollbarColor) {
        this.scrollbarColor = scrollbarColor;
        scrollbarPaint.setColor(scrollbarColor);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getScrollbarWidth() {
        return scrollbarWidth;
    }

    public void setScrollbarWidth(float scrollbarWidth) {
        this.scrollbarWidth = scrollbarWidth;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getScrollbarBackgroundColor() {
        return scrollbarBackgroundColor;
    }

    public void setScrollbarBackgroundColor(int scrollbarBackgroundColor) {
        this.scrollbarBackgroundColor = scrollbarBackgroundColor;
        scrollbarBackgroundPaint.setColor(scrollbarBackgroundColor);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public Drawable getScrollbarDrawable() {
        return scrollbarDrawable;
    }

    public void setScrollbarDrawable(Drawable scrollbarDrawable) {
        this.scrollbarDrawable = scrollbarDrawable;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public Drawable getScrollbarBackgroundDrawable() {
        return scrollbarBackgroundDrawable;
    }

    public void setScrollbarBackgroundDrawable(Drawable scrollbarBackgroundDrawable) {
        this.scrollbarBackgroundDrawable = scrollbarBackgroundDrawable;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getScrollSensitivity() {
        return scrollSensitivity;
    }

    public void setScrollSensitivity(float scrollSensitivity) {
        this.scrollSensitivity = scrollSensitivity;
    }

    public boolean isScrollable() {
        return scrollable;
    }

    public void setScrollable(boolean scrollable) {
        this.scrollable = scrollable;
    }

    public float getContentWidth() {
        return contentWidth;
    }

    public float getContentHeight() {
        return contentHeight;
    }
}
