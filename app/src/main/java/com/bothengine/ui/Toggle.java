package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class Toggle extends UIComponent {
    public interface OnValueChangedListener {
        void onValueChanged(Toggle toggle, boolean isOn);
    }

    private boolean isOn;
    private String textOn;
    private String textOff;
    private int textColor;
    private float textSize;
    private int backgroundColor;
    private int thumbColor;
    private int onColor;
    private int offColor;
    private float thumbSize;
    private float transitionDuration;
    private float transitionProgress;
    
    private OnValueChangedListener onValueChangedListener;
    
    private Paint textPaint;
    private Paint backgroundPaint;
    private Paint thumbPaint;
    private RectF backgroundRect;
    private RectF thumbRect;
    private float thumbPosition;

    public Toggle() {
        super();
        this.isOn = false;
        this.textOn = "ON";
        this.textOff = "OFF";
        this.textColor = Color.WHITE;
        this.textSize = 16f;
        this.backgroundColor = Color.GRAY;
        this.thumbColor = Color.WHITE;
        this.onColor = Color.GREEN;
        this.offColor = Color.RED;
        this.thumbSize = 30f;
        this.transitionDuration = 0.2f;
        this.transitionProgress = isOn ? 1f : 0f;
        
        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(textSize);
        this.textPaint.setColor(textColor);
        this.textPaint.setTextAlign(Paint.Align.LEFT);
        
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setAntiAlias(true);
        this.backgroundPaint.setStyle(Paint.Style.FILL);
        
        this.thumbPaint = new Paint();
        this.thumbPaint.setAntiAlias(true);
        this.thumbPaint.setStyle(Paint.Style.FILL);
        
        this.backgroundRect = new RectF();
        this.thumbRect = new RectF();
        
        setSize(150, 50);
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        float height = bounds.height();
        float trackHeight = height * 0.4f;
        float trackY = bounds.centerY() - trackHeight / 2;
        
        backgroundRect.set(bounds.left, trackY, bounds.right, trackY + trackHeight);
        
        int currentColor = isOn ? onColor : offColor;
        backgroundPaint.setColor(currentColor);
        canvas.drawRoundRect(backgroundRect, trackHeight / 2, trackHeight / 2, backgroundPaint);
        
        float thumbDiameter = Math.min(thumbSize, height * 0.8f);
        thumbPosition = bounds.left + (bounds.width() - thumbDiameter) * transitionProgress;
        
        thumbRect.set(
            thumbPosition,
            bounds.centerY() - thumbDiameter / 2,
            thumbPosition + thumbDiameter,
            bounds.centerY() + thumbDiameter / 2
        );
        
        thumbPaint.setColor(thumbColor);
        canvas.drawOval(thumbRect, thumbPaint);
        
        String text = isOn ? textOn : textOff;
        float textX = bounds.left + thumbDiameter + 10;
        float textY = bounds.centerY() - (textPaint.descent() + textPaint.ascent()) / 2;
        canvas.drawText(text, textX, textY, textPaint);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        if (transitionProgress != (isOn ? 1f : 0f)) {
            float target = isOn ? 1f : 0f;
            float delta = (target - transitionProgress) / transitionDuration;
            transitionProgress += delta * deltaTime;
            
            if (Math.abs(target - transitionProgress) < 0.01f) {
                transitionProgress = target;
            }
            
            notifyChanged();
        }
    }

    @Override
    public void onPointerDown(float x, float y, int pointerId) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        float thumbDiameter = Math.min(thumbSize, bounds.height() * 0.8f);
        float thumbPos = bounds.left + (bounds.width() - thumbDiameter) * transitionProgress;
        
        if (x >= thumbPos && x <= thumbPos + thumbDiameter && 
            y >= bounds.centerY() - thumbDiameter / 2 && y <= bounds.centerY() + thumbDiameter / 2) {
            isOn = !isOn;
            transitionProgress = isOn ? 0f : 1f;
            
            if (onValueChangedListener != null) {
                onValueChangedListener.onValueChanged(this, isOn);
            }
            
            notifyChanged();
        }
    }

    public boolean isOn() {
        return isOn;
    }

    public void setOn(boolean on) {
        if (isOn != on) {
            isOn = on;
            transitionProgress = isOn ? 0f : 1f;
            
            if (onValueChangedListener != null) {
                onValueChangedListener.onValueChanged(this, isOn);
            }
            
            notifyChanged();
        }
    }

    public void toggle() {
        setOn(!isOn);
    }

    public String getTextOn() {
        return textOn;
    }

    public void setTextOn(String textOn) {
        this.textOn = textOn;
        notifyChanged();
    }

    public String getTextOff() {
        return textOff;
    }

    public void setTextOff(String textOff) {
        this.textOff = textOff;
        notifyChanged();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        notifyChanged();
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        notifyChanged();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        notifyChanged();
    }

    public int getThumbColor() {
        return thumbColor;
    }

    public void setThumbColor(int thumbColor) {
        this.thumbColor = thumbColor;
        notifyChanged();
    }

    public int getOnColor() {
        return onColor;
    }

    public void setOnColor(int onColor) {
        this.onColor = onColor;
        notifyChanged();
    }

    public int getOffColor() {
        return offColor;
    }

    public void setOffColor(int offColor) {
        this.offColor = offColor;
        notifyChanged();
    }

    public float getThumbSize() {
        return thumbSize;
    }

    public void setThumbSize(float thumbSize) {
        this.thumbSize = thumbSize;
        notifyChanged();
    }

    public float getTransitionDuration() {
        return transitionDuration;
    }

    public void setTransitionDuration(float transitionDuration) {
        this.transitionDuration = transitionDuration;
    }

    public OnValueChangedListener getOnValueChangedListener() {
        return onValueChangedListener;
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }
}
