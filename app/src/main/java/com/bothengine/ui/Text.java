package com.bothengine.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class Text extends UIComponent {
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    public enum TextOverflow {
        WRAP,
        ELLIPSIS,
        CLIP,
        FADE
    }

    private String text;
    private int color;
    private float fontSize;
    private Typeface typeface;
    private TextAlignment alignment;
    private TextOverflow overflow;
    private int maxLines;
    private float lineSpacing;
    private boolean bold;
    private boolean italic;
    private boolean underline;
    private boolean strikethrough;
    
    private TextPaint textPaint;
    private StaticLayout staticLayout;
    private float textWidth;
    private float textHeight;

    public Text() {
        super();
        this.text = "Text";
        this.color = Color.WHITE;
        this.fontSize = 16f;
        this.typeface = Typeface.DEFAULT;
        this.alignment = TextAlignment.LEFT;
        this.overflow = TextOverflow.WRAP;
        this.maxLines = 0;
        this.lineSpacing = 1.0f;
        this.bold = false;
        this.italic = false;
        this.underline = false;
        this.strikethrough = false;
        
        this.textPaint = new TextPaint();
        this.textPaint.setAntiAlias(true);
        this.textPaint.setTextSize(fontSize);
        this.textPaint.setColor(color);
        this.textPaint.setTypeface(typeface);
        
        setSize(200, 50);
    }

    public Text(String text) {
        this();
        this.text = text;
        updateTextLayout();
    }

    @Override
    public void render(Canvas canvas, Paint paint) {
        Rect bounds = getBounds();
        if (bounds == null || text == null || text.isEmpty()) return;
        
        updateTextPaint();
        
        float x = bounds.left;
        float y = bounds.top + textHeight / 2 + fontSize / 2;
        
        switch (alignment) {
            case LEFT:
                x = bounds.left;
                break;
            case CENTER:
                x = bounds.centerX();
                break;
            case RIGHT:
                x = bounds.right;
                break;
        }
        
        if (staticLayout != null) {
            float layoutX = bounds.left;
            switch (alignment) {
                case CENTER:
                    layoutX = bounds.left + (bounds.width() - textWidth) / 2;
                    break;
                case RIGHT:
                    layoutX = bounds.right - textWidth;
                    break;
            }
            canvas.save();
            canvas.translate(layoutX, bounds.top);
            staticLayout.draw(canvas);
            canvas.restore();
        } else {
            canvas.drawText(text, x, y, textPaint);
        }
        
        if (underline) {
            float underlineY = y + textPaint.descent() + 2;
            canvas.drawLine(bounds.left, underlineY, bounds.right, underlineY, textPaint);
        }
        
        if (strikethrough) {
            float strikeY = y - textPaint.ascent() / 2;
            canvas.drawLine(bounds.left, strikeY, bounds.right, strikeY, textPaint);
        }
    }

    private void updateTextPaint() {
        int style = Typeface.NORMAL;
        if (bold) style |= Typeface.BOLD;
        if (italic) style |= Typeface.ITALIC;
        
        textPaint.setTypeface(Typeface.create(typeface, style));
        textPaint.setTextSize(fontSize);
        textPaint.setColor(color);
        textPaint.setUnderlineText(underline);
        textPaint.setStrikeThruText(strikethrough);
    }

    private void updateTextLayout() {
        if (text == null || text.isEmpty()) return;
        
        updateTextPaint();
        
        Rect bounds = getBounds();
        if (bounds != null) {
            int width = bounds.width();
            if (width <= 0) width = 1000;
            
            Layout.Alignment layoutAlignment;
            switch (alignment) {
                case LEFT:
                    layoutAlignment = Layout.Alignment.ALIGN_NORMAL;
                    break;
                case CENTER:
                    layoutAlignment = Layout.Alignment.ALIGN_CENTER;
                    break;
                case RIGHT:
                    layoutAlignment = Layout.Alignment.ALIGN_OPPOSITE;
                    break;
                default:
                    layoutAlignment = Layout.Alignment.ALIGN_NORMAL;
            }
            
            if (maxLines > 0) {
                staticLayout = new StaticLayout(
                    text, textPaint, width, layoutAlignment, lineSpacing, 0, true
                );
            } else {
                staticLayout = null;
            }
            
            textWidth = textPaint.measureText(text);
            textHeight = textPaint.getFontMetrics().descent - textPaint.getFontMetrics().ascent;
        }
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        updateTextLayout();
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
        updateTextLayout();
        notifyChanged();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
        notifyChanged();
    }

    public float getFontSize() {
        return fontSize;
    }

    public void setFontSize(float fontSize) {
        this.fontSize = fontSize;
        updateTextLayout();
        notifyChanged();
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
        updateTextLayout();
        notifyChanged();
    }

    public TextAlignment getAlignment() {
        return alignment;
    }

    public void setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        updateTextLayout();
        notifyChanged();
    }

    public TextOverflow getOverflow() {
        return overflow;
    }

    public void setOverflow(TextOverflow overflow) {
        this.overflow = overflow;
        updateTextLayout();
        notifyChanged();
    }

    public int getMaxLines() {
        return maxLines;
    }

    public void setMaxLines(int maxLines) {
        this.maxLines = maxLines;
        updateTextLayout();
        notifyChanged();
    }

    public float getLineSpacing() {
        return lineSpacing;
    }

    public void setLineSpacing(float lineSpacing) {
        this.lineSpacing = lineSpacing;
        updateTextLayout();
        notifyChanged();
    }

    public boolean isBold() {
        return bold;
    }

    public void setBold(boolean bold) {
        this.bold = bold;
        updateTextLayout();
        notifyChanged();
    }

    public boolean isItalic() {
        return italic;
    }

    public void setItalic(boolean italic) {
        this.italic = italic;
        updateTextLayout();
        notifyChanged();
    }

    public boolean isUnderline() {
        return underline;
    }

    public void setUnderline(boolean underline) {
        this.underline = underline;
        notifyChanged();
    }

    public boolean isStrikethrough() {
        return strikethrough;
    }

    public void setStrikethrough(boolean strikethrough) {
        this.strikethrough = strikethrough;
        notifyChanged();
    }

    public float getTextWidth() {
        return textWidth;
    }

    public float getTextHeight() {
        return textHeight;
    }
}
