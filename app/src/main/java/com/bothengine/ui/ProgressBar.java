package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

public class ProgressBar extends UIComponent {
    public enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private float minValue;
    private float maxValue;
    private float value;
    private Direction direction;
    private int backgroundColor;
    private int progressColor;
    private int borderColor;
    private float borderWidth;
    private float cornerRadius;
    private boolean showText;
    private String textFormat;
    private int textColor;
    private float textSize;
    
    private Paint backgroundPaint;
    private Paint progressPaint;
    private Paint borderPaint;
    private Paint textPaint;
    private RectF backgroundRect;
    private RectF progressRect;
    private RectF borderRect;

    public ProgressBar() {
        super();
        this.minValue = 0f;
        this.maxValue = 100f;
        this.value = 0f;
        this.direction = Direction.HORIZONTAL;
        this.backgroundColor = Color.LTGRAY;
        this.progressColor = Color.BLUE;
        this.borderColor = Color.BLACK;
        this.borderWidth = 1f;
        this.cornerRadius = 5f;
        this.showText = false;
        this.textFormat = "%d%%";
        this.textColor = Color.WHITE;
        this.textSize = 14f;
        
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setAntiAlias(true);
        this.backgroundPaint.setStyle(Paint.Style.FILL);
        this.backgroundPaint.setColor(backgroundColor);
        
        this.progressPaint = new Paint();
        this.progressPaint.setAntiAlias(true);
        this.progressPaint.setStyle(Paint.Style.FILL);
        this.progressPaint.setColor(progressColor);
        
        this.borderPaint = new Paint();
        this.borderPaint.setAntiAlias(true);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(borderWidth);
        this.borderPaint.setColor(borderColor);
        
        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(textSize);
        this.textPaint.setColor(textColor);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        
        this.backgroundRect = new RectF();
        this.progressRect = new RectF();
        this.borderRect = new RectF();
        
        setSize(200, 30);
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        float offset = borderWidth / 2;
        backgroundRect.set(
            bounds.left + offset,
            bounds.top + offset,
            bounds.right - offset,
            bounds.bottom - offset
        );
        
        float normalizedValue = (value - minValue) / (maxValue - minValue);
        
        if (direction == Direction.HORIZONTAL) {
            float progressWidth = backgroundRect.width() * normalizedValue;
            progressRect.set(
                backgroundRect.left,
                backgroundRect.top,
                backgroundRect.left + progressWidth,
                backgroundRect.bottom
            );
        } else {
            float progressHeight = backgroundRect.height() * normalizedValue;
            progressRect.set(
                backgroundRect.left,
                backgroundRect.bottom - progressHeight,
                backgroundRect.right,
                backgroundRect.bottom
            );
        }
        
        canvas.drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint);
        canvas.drawRoundRect(progressRect, cornerRadius, cornerRadius, progressPaint);
        
        if (borderWidth > 0) {
            borderRect.set(
                bounds.left + offset,
                bounds.top + offset,
                bounds.right - offset,
                bounds.bottom - offset
            );
            canvas.drawRoundRect(borderRect, cornerRadius, cornerRadius, borderPaint);
        }
        
        if (showText) {
            String text = String.format(textFormat, (int) (normalizedValue * 100));
            float x = bounds.centerX();
            float y = bounds.centerY() - (textPaint.descent() + textPaint.ascent()) / 2;
            canvas.drawText(text, x, y, textPaint);
        }
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = Math.max(minValue, Math.min(maxValue, value));
        notifyChanged();
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
        notifyChanged();
    }

    public float getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
        setValue(value);
        notifyChanged();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
        notifyChanged();
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        backgroundPaint.setColor(backgroundColor);
        notifyChanged();
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
        progressPaint.setColor(progressColor);
        notifyChanged();
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        borderPaint.setColor(borderColor);
        notifyChanged();
    }

    public float getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(float borderWidth) {
        this.borderWidth = borderWidth;
        borderPaint.setStrokeWidth(borderWidth);
        notifyChanged();
    }

    public float getCornerRadius() {
        return cornerRadius;
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        notifyChanged();
    }

    public boolean isShowText() {
        return showText;
    }

    public void setShowText(boolean showText) {
        this.showText = showText;
        notifyChanged();
    }

    public String getTextFormat() {
        return textFormat;
    }

    public void setTextFormat(String textFormat) {
        this.textFormat = textFormat;
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
}
