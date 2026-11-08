package com.bothengine.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

public class Image extends UIComponent {
    public enum ScaleType {
        CENTER,
        CENTER_CROP,
        CENTER_INSIDE,
        FIT_CENTER,
        FIT_END,
        FIT_START,
        FIT_XY,
        MATRIX
    }

    public enum ColorMode {
        NONE,
        TINT,
        MULTIPLY,
        SCREEN,
        OVERLAY
    }

    private Bitmap bitmap;
    private Drawable drawable;
    private ScaleType scaleType;
    private ColorMode colorMode;
    private int color;
    private float alpha;
    private Matrix matrix;
    private Shader shader;
    private ColorFilter colorFilter;
    
    private Paint paint;
    private Rect sourceRect;
    private Rect destRect;
    private boolean preserveAspectRatio;

    public Image() {
        super();
        this.scaleType = ScaleType.FIT_CENTER;
        this.colorMode = ColorMode.NONE;
        this.color = Color.WHITE;
        this.alpha = 1.0f;
        this.preserveAspectRatio = true;
        
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.paint.setFilterBitmap(true);
        this.paint.setDither(true);
        this.paint.setAlpha((int) (255 * alpha));
        
        setSize(100, 100);
    }

    public Image(Bitmap bitmap) {
        this();
        this.bitmap = bitmap;
        updateRects();
    }

    public Image(Drawable drawable) {
        this();
        this.drawable = drawable;
        updateRects();
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        if (bitmap != null) {
            drawBitmap(canvas);
        } else if (drawable != null) {
            drawDrawable(canvas);
        }
    }

    private void drawBitmap(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds == null || bitmap == null || bitmap.isRecycled()) return;
        
        updatePaint();
        updateRects();
        
        if (matrix != null) {
            canvas.drawBitmap(bitmap, matrix, this.paint);
        } else if (destRect != null) {
            android.graphics.Rect src = new android.graphics.Rect(
                    Math.round(sourceRect.left), Math.round(sourceRect.top),
                    Math.round(sourceRect.right), Math.round(sourceRect.bottom));
            android.graphics.Rect dst = new android.graphics.Rect(
                    Math.round(destRect.left), Math.round(destRect.top),
                    Math.round(destRect.right), Math.round(destRect.bottom));
            canvas.drawBitmap(bitmap, src, dst, this.paint);
        } else {
            canvas.drawBitmap(bitmap, bounds.left, bounds.top, this.paint);
        }
    }

    private void drawDrawable(Canvas canvas) {
        Rect bounds = getBounds();
        if (bounds == null || drawable == null) return;
        
        drawable.setBounds(bounds);
        if (colorFilter != null) {
            drawable.setColorFilter(colorFilter);
        }
        drawable.setAlpha((int) (255 * alpha));
        drawable.draw(canvas);
    }

    private void updatePaint() {
        this.paint.setAlpha((int) (255 * alpha));
        
        switch (colorMode) {
            case TINT:
                colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN);
                break;
            case MULTIPLY:
                colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
                break;
            case SCREEN:
                colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.SCREEN);
                break;
            case OVERLAY:
                colorFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.OVERLAY);
                break;
            case NONE:
            default:
                colorFilter = null;
                break;
        }
        
        if (colorFilter != null) {
            this.paint.setColorFilter(colorFilter);
        } else {
            this.paint.setColorFilter(null);
        }
        
        if (shader != null) {
            this.paint.setShader(shader);
        } else {
            this.paint.setShader(null);
        }
    }

    private void updateRects() {
        Rect bounds = getBounds();
        if (bounds == null) return;
        
        if (bitmap != null) {
            float bitmapWidth = bitmap.getWidth();
            float bitmapHeight = bitmap.getHeight();
            float viewWidth = bounds.width();
            float viewHeight = bounds.height();
            
            sourceRect = new Rect(0, 0, bitmapWidth, bitmapHeight);
            destRect = new Rect(bounds);
            
            if (preserveAspectRatio) {
                float bitmapAspect = bitmapWidth / bitmapHeight;
                float viewAspect = viewWidth / viewHeight;
                
                switch (scaleType) {
                    case CENTER:
                        destRect.set(
                            bounds.centerX() - bitmapWidth / 2,
                            bounds.centerY() - bitmapHeight / 2,
                            bounds.centerX() + bitmapWidth / 2,
                            bounds.centerY() + bitmapHeight / 2
                        );
                        break;
                    case CENTER_CROP:
                        if (bitmapAspect > viewAspect) {
                            float scale = viewHeight / bitmapHeight;
                            float width = bitmapWidth * scale;
                            destRect.set(
                                bounds.centerX() - width / 2,
                                bounds.top,
                                bounds.centerX() + width / 2,
                                bounds.bottom
                            );
                        } else {
                            float scale = viewWidth / bitmapWidth;
                            float height = bitmapHeight * scale;
                            destRect.set(
                                bounds.left,
                                bounds.centerY() - height / 2,
                                bounds.right,
                                bounds.centerY() + height / 2
                            );
                        }
                        break;
                    case CENTER_INSIDE:
                        if (bitmapAspect > viewAspect) {
                            float scale = viewWidth / bitmapWidth;
                            float height = bitmapHeight * scale;
                            destRect.set(
                                bounds.left,
                                bounds.centerY() - height / 2,
                                bounds.right,
                                bounds.centerY() + height / 2
                            );
                        } else {
                            float scale = viewHeight / bitmapHeight;
                            float width = bitmapWidth * scale;
                            destRect.set(
                                bounds.centerX() - width / 2,
                                bounds.top,
                                bounds.centerX() + width / 2,
                                bounds.bottom
                            );
                        }
                        break;
                    case FIT_CENTER:
                    case FIT_END:
                    case FIT_START:
                        float scale = Math.min(viewWidth / bitmapWidth, viewHeight / bitmapHeight);
                        float width = bitmapWidth * scale;
                        float height = bitmapHeight * scale;
                        destRect.set(
                            bounds.centerX() - width / 2,
                            bounds.centerY() - height / 2,
                            bounds.centerX() + width / 2,
                            bounds.centerY() + height / 2
                        );
                        break;
                    case FIT_XY:
                        destRect.set(bounds);
                        break;
                    case MATRIX:
                        break;
                }
            } else {
                destRect.set(bounds);
            }
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateRects();
        notifyChanged();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.drawable = null;
        updateRects();
        notifyChanged();
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
        this.bitmap = null;
        notifyChanged();
    }

    public ScaleType getScaleType() {
        return scaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.scaleType = scaleType;
        updateRects();
        notifyChanged();
    }

    public ColorMode getColorMode() {
        return colorMode;
    }

    public void setColorMode(ColorMode colorMode) {
        this.colorMode = colorMode;
        updatePaint();
        notifyChanged();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        updatePaint();
        notifyChanged();
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = Math.max(0, Math.min(1, alpha));
        updatePaint();
        notifyChanged();
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
        notifyChanged();
    }

    public Shader getShader() {
        return shader;
    }

    public void setShader(Shader shader) {
        this.shader = shader;
        updatePaint();
        notifyChanged();
    }

    public ColorFilter getColorFilter() {
        return colorFilter;
    }

    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
        updatePaint();
        notifyChanged();
    }

    public boolean isPreserveAspectRatio() {
        return preserveAspectRatio;
    }

    public void setPreserveAspectRatio(boolean preserveAspectRatio) {
        this.preserveAspectRatio = preserveAspectRatio;
        updateRects();
        notifyChanged();
    }
}
