package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Slider extends UIComponent {
    public interface OnValueChangedListener {
        void onValueChanged(Slider slider, float value);
    }

    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private float minValue;
    private float maxValue;
    private float value;
    private Direction direction;
    private int trackColor;
    private int progressColor;
    private int thumbColor;
    private float thumbSize;
    private boolean isDragging;
    
    private OnValueChangedListener onValueChangedListener;
    
    private Paint trackPaint;
    private Paint progressPaint;
    private Paint thumbPaint;
    private RectF trackRect;
    private RectF progressRect;
    private RectF thumbRect;

    public Slider() {
        super();
        this.minValue = 0f;
        this.maxValue = 100f;
        this.value = 50f;
        this.direction = Direction.HORIZONTAL;
        this.trackColor = Color.LTGRAY;
        this.progressColor = Color.BLUE;
        this.thumbColor = Color.WHITE;
        this.thumbSize = 20f;
        this.isDragging = false;
        
        this.trackPaint = new Paint();
        this.trackPaint.setAntiAlias(true);
        this.trackPaint.setStyle(Paint.Style.FILL);
        this.trackPaint.setColor(trackColor);
        
        this.progressPaint = new Paint();
        this.progressPaint.setAntiAlias(true);
        this.progressPaint.setStyle(Paint.Style.FILL);
        this.progressPaint.setColor(progressColor);
        
        this.thumbPaint = new Paint();
        this.thumbPaint.setAntiAlias(true);
        this.thumbPaint.setStyle(Paint.Style.FILL);
        this.thumbPaint.setColor(thumbColor);
        
        this.trackRect = new RectF();
        this.progressRect = new RectF();
        this.thumbRect = new RectF();
        
        setSize(200, 30);
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        float trackHeight = direction == Direction.HORIZONTAL ? bounds.height() * 0.2f : bounds.width() * 0.2f;
        
        if (direction == Direction.HORIZONTAL) {
            float trackY = bounds.centerY() - trackHeight / 2;
            trackRect.set(bounds.left, trackY, bounds.right, trackY + trackHeight);
            
            float progressWidth = (value - minValue) / (maxValue - minValue) * bounds.width();
            progressRect.set(bounds.left, trackY, bounds.left + progressWidth, trackY + trackHeight);
            
            float thumbX = bounds.left + progressWidth - thumbSize / 2;
            float thumbY = bounds.centerY() - thumbSize / 2;
            thumbRect.set(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize);
            
            canvas.drawRoundRect(trackRect, trackHeight / 2, trackHeight / 2, trackPaint);
            canvas.drawRoundRect(progressRect, trackHeight / 2, trackHeight / 2, progressPaint);
            canvas.drawOval(thumbRect, thumbPaint);
        } else {
            float trackX = bounds.centerX() - trackHeight / 2;
            trackRect.set(trackX, bounds.top, trackX + trackHeight, bounds.bottom);
            
            float progressHeight = (value - minValue) / (maxValue - minValue) * bounds.height();
            progressRect.set(trackX, bounds.bottom - progressHeight, trackX + trackHeight, bounds.bottom);
            
            float thumbX = bounds.centerX() - thumbSize / 2;
            float thumbY = bounds.bottom - progressHeight - thumbSize / 2;
            thumbRect.set(thumbX, thumbY, thumbX + thumbSize, thumbY + thumbSize);
            
            canvas.drawRoundRect(trackRect, trackHeight / 2, trackHeight / 2, trackPaint);
            canvas.drawRoundRect(progressRect, trackHeight / 2, trackHeight / 2, progressPaint);
            canvas.drawOval(thumbRect, thumbPaint);
        }
    }

    @Override
    public void onPointerDown(float x, float y, int pointerId) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        if (thumbRect.contains(x, y)) {
            isDragging = true;
        } else {
            updateValueFromPosition(x, y);
        }
    }

    @Override
    public void onPointerMove(float x, float y, int pointerId) {
        if (isDragging) {
            updateValueFromPosition(x, y);
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

    private void updateValueFromPosition(float x, float y) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        float normalizedValue;
        if (direction == Direction.HORIZONTAL) {
            normalizedValue = (x - bounds.left) / (float) bounds.width();
        } else {
            normalizedValue = 1f - (y - bounds.top) / (float) bounds.height();
        }
        
        normalizedValue = Math.max(0f, Math.min(1f, normalizedValue));
        float newValue = minValue + normalizedValue * (maxValue - minValue);
        
        if (Math.abs(newValue - value) > 0.001f) {
            value = newValue;
            if (onValueChangedListener != null) {
                onValueChangedListener.onValueChanged(this, value);
            }
            if (canvas != null) {
                canvas.markDirty();
            }
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getNormalizedValue() {
        return (value - minValue) / (maxValue - minValue);
    }

    public void setNormalizedValue(float normalizedValue) {
        setValue(minValue + normalizedValue * (maxValue - minValue));
    }

    public float getMinValue() {
        return minValue;
    }

    public void setMinValue(float minValue) {
        this.minValue = minValue;
        setValue(value);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        setValue(value);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getTrackColor() {
        return trackColor;
    }

    public void setTrackColor(int trackColor) {
        this.trackColor = trackColor;
        trackPaint.setColor(trackColor);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getThumbColor() {
        return thumbColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
        thumbPaint.setColor(thumbColor);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(float thumbSize) {
        this.thumbSize = thumbSize;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }
}
