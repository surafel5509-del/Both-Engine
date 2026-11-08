package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

public class Button extends UIComponent {
    public interface OnClickListener {
        void onClick(Button button);
    }

    private String text;
    private int textColor;
    private float textSize;
    private int backgroundColor;
    private int pressedColor;
    private int disabledColor;
    private Drawable backgroundDrawable;
    private Drawable pressedDrawable;
    private Drawable disabledDrawable;
    
    private OnClickListener onClickListener;
    private boolean isPressed;
    private boolean isHovered;
    private boolean isDisabled;
    
    private Paint textPaint;
    private Paint backgroundPaint;

    public Button() {
        super();
        this.text = "Button";
        this.textColor = Color.WHITE;
        this.textSize = 16f;
        this.backgroundColor = Color.GRAY;
        this.pressedColor = Color.DKGRAY;
        this.disabledColor = Color.LTGRAY;
        this.isPressed = false;
        this.isHovered = false;
        this.isDisabled = false;
        
        this.textPaint = new Paint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(textSize);
        this.textPaint.setColor(textColor);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        
        this.backgroundPaint = new Paint();
        this.backgroundPaint.setAntiAlias(true);
        this.backgroundPaint.setStyle(Paint.Style.FILL);
        
        setSize(200, 50);
    }

    public Button(String text) {
        this();
        this.text = text;
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        int color = isDisabled ? disabledColor : (isPressed ? pressedColor : backgroundColor);
        backgroundPaint.setColor(color);
        canvas.drawRect(bounds, backgroundPaint);
        
        if (backgroundDrawable != null && !isPressed) {
            backgroundDrawable.setBounds(bounds);
            backgroundDrawable.draw(canvas);
        } else if (pressedDrawable != null && isPressed) {
            pressedDrawable.setBounds(bounds);
            pressedDrawable.draw(canvas);
        } else if (disabledDrawable != null && isDisabled) {
            disabledDrawable.setBounds(bounds);
            disabledDrawable.draw(canvas);
        }
        
        if (text != null && !text.isEmpty()) {
            float x = bounds.centerX();
            float y = bounds.centerY() - (textPaint.descent() + textPaint.ascent()) / 2;
            canvas.drawText(text, x, y, textPaint);
        }
    }

    @Override
    public void onPointerDown(float x, float y, int pointerId) {
        if (isDisabled) return;
        isPressed = true;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    @Override
    public void onPointerMove(float x, float y, int pointerId) {
        Rect bounds = getBounds();
        if (bounds != null) {
            boolean wasHovered = isHovered;
            isHovered = bounds.contains((int) x, (int) y);
            if (wasHovered != isHovered) {
                if (isHovered) {
                    onHoverEnter();
                } else {
                    onHoverExit();
                }
            }
        }
    }

    @Override
    public void onPointerUp(float x, float y, int pointerId) {
        if (isDisabled) return;
        if (isPressed) {
            Rect bounds = getBounds();
            if (bounds != null && bounds.contains((int) x, (int) y)) {
                onClick();
                if (onClickListener != null) {
                    onClickListener.onClick(this);
                }
            }
        }
        isPressed = false;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    @Override
    public void onPointerCancel() {
        isPressed = false;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public int getPressedColor() {
        return pressedColor;
    }

    public void setPressedColor(int pressedColor) {
        this.pressedColor = pressedColor;
    }

    public int getDisabledColor() {
        return disabledColor;
    }

    public void setDisabledColor(int disabledColor) {
        this.disabledColor = disabledColor;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public void setBackgroundDrawable(Drawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public Drawable getPressedDrawable() {
        return pressedDrawable;
    }

    public void setPressedDrawable(Drawable pressedDrawable) {
        this.pressedDrawable = pressedDrawable;
    }

    public Drawable getDisabledDrawable() {
        return disabledDrawable;
    }

    public void setDisabledDrawable(Drawable disabledDrawable) {
        this.disabledDrawable = disabledDrawable;
    }

    public OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public boolean isPressed() {
        return isPressed;
    }

    public boolean isHovered() {
        return isHovered;
    }

    public boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
        if (canvas != null) {
            canvas.markDirty();
        }
    }

    public void setEnabled(boolean enabled) {
        setDisabled(!enabled);
    }

    public boolean isEnabled() {
        return !isDisabled;
    }
}
