package com.bothengine.rendering;

/**
 * Color
 * RGBA color representation
 */
public class Color {
    public float r, g, b, a;
    
    public Color() {
        this(1, 1, 1, 1); // White
    }
    
    public Color(float r, float g, float b, float a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }
    
    public Color(float r, float g, float b) {
        this(r, g, b, 1);
    }
    
    public Color(int color) {
        this.r = ((color >> 16) & 0xFF) / 255.0f;
        this.g = ((color >> 8) & 0xFF) / 255.0f;
        this.b = (color & 0xFF) / 255.0f;
        this.a = ((color >> 24) & 0xFF) / 255.0f;
    }
    
    public Color(Vector4 vector) {
        this(vector.x, vector.y, vector.z, vector.w);
    }
    
    public Color(Vector3 rgb, float a) {
        this(rgb.x, rgb.y, rgb.z, a);
    }
    
    /**
     * Clamp a value between 0 and 1
     */
    private static float clamp(float value) {
        return Math.max(0, Math.min(1, value));
    }
    
    // Preset colors
    public static final Color WHITE = new Color(1, 1, 1, 1);
    public static final Color BLACK = new Color(0, 0, 0, 1);
    public static final Color RED = new Color(1, 0, 0, 1);
    public static final Color GREEN = new Color(0, 1, 0, 1);
    public static final Color BLUE = new Color(0, 0, 1, 1);
    public static final Color YELLOW = new Color(1, 1, 0, 1);
    public static final Color CYAN = new Color(0, 1, 1, 1);
    public static final Color MAGENTA = new Color(1, 0, 1, 1);
    public static final Color GRAY = new Color(0.5f, 0.5f, 0.5f, 1);
    public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    public static final Color ORANGE = new Color(1, 0.5f, 0, 1);
    public static final Color PURPLE = new Color(0.5f, 0, 0.5f, 1);
    public static final Color PINK = new Color(1, 0.5f, 0.8f, 1);
    public static final Color BROWN = new Color(0.6f, 0.4f, 0.2f, 1);
    
    /**
     * Create a color from HSB/HSV values
     */
    public static Color fromHSB(float hue, float saturation, float brightness, float alpha) {
        hue = hue % 1.0f;
        if (hue < 0) hue += 1.0f;
        saturation = clamp(saturation);
        brightness = clamp(brightness);
        alpha = clamp(alpha);
        
        float h = hue * 6.0f;
        int i = (int) h;
        float f = h - i;
        float p = brightness * (1 - saturation);
        float q = brightness * (1 - f * saturation);
        float t = brightness * (1 - (1 - f) * saturation);
        
        switch (i) {
            case 0: return new Color(brightness, t, p, alpha);
            case 1: return new Color(q, brightness, p, alpha);
            case 2: return new Color(p, brightness, t, alpha);
            case 3: return new Color(p, q, brightness, alpha);
            case 4: return new Color(t, p, brightness, alpha);
            default: return new Color(brightness, p, q, alpha);
        }
    }
    
    /**
     * Create a color from HSL values
     */
    public static Color fromHSL(float hue, float saturation, float lightness, float alpha) {
        hue = hue % 1.0f;
        if (hue < 0) hue += 1.0f;
        saturation = clamp(saturation);
        lightness = clamp(lightness);
        alpha = clamp(alpha);
        
        float q = lightness < 0.5f ? lightness * (1 + saturation) : lightness + saturation - lightness * saturation;
        float p = 2 * lightness - q;
        
        float h = hue * 6.0f;
        int i = (int) h;
        float f = h - i;
        
        float[] t = new float[3];
        t[0] = p + (q - p) * (1 - f);
        t[1] = p + (q - p) * f;
        t[2] = p + (q - p) * 0.5f;
        
        switch (i) {
            case 0: return new Color(q, t[2], p, alpha);
            case 1: return new Color(t[1], q, p, alpha);
            case 2: return new Color(p, q, t[0], alpha);
            case 3: return new Color(p, t[1], q, alpha);
            case 4: return new Color(t[0], p, q, alpha);
            default: return new Color(q, p, t[1], alpha);
        }
    }
    
    /**
     * Get the color as an integer (ARGB)
     */
    public int toInt() {
        return ((int) (a * 255) << 24) | 
               ((int) (r * 255) << 16) | 
               ((int) (g * 255) << 8) | 
               (int) (b * 255);
    }
    
    /**
     * Get the color as an integer (RGBA)
     */
    public int toRGBA() {
        return ((int) (r * 255) << 24) | 
               ((int) (g * 255) << 16) | 
               ((int) (b * 255) << 8) | 
               (int) (a * 255);
    }
    
    /**
     * Get the color as a Vector4
     */
    public Vector4 toVector4() {
        return new Vector4(r, g, b, a);
    }
    
    /**
     * Get the color as a float array (RGBA)
     */
    public float[] toArray() {
        return new float[] { r, g, b, a };
    }
    
    /**
     * Get the grayscale value
     */
    public float getGrayscale() {
        return 0.299f * r + 0.587f * g + 0.114f * b;
    }
    
    /**
     * Get the brightness
     */
    public float getBrightness() {
        return (r + g + b) / 3.0f;
    }
    
    /**
     * Get the hue (0-1)
     */
    public float getHue() {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        
        if (delta == 0) return 0;
        
        float hue;
        if (max == r) {
            hue = (g - b) / delta;
        } else if (max == g) {
            hue = 2 + (b - r) / delta;
        } else {
            hue = 4 + (r - g) / delta;
        }
        
        hue /= 6;
        if (hue < 0) hue += 1;
        return hue;
    }
    
    /**
     * Get the saturation (0-1)
     */
    public float getSaturation() {
        float max = Math.max(r, Math.max(g, b));
        float min = Math.min(r, Math.min(g, b));
        float delta = max - min;
        
        if (delta == 0) return 0;
        return delta / max;
    }
    
    /**
     * Invert the color
     */
    public Color invert() {
        return new Color(1 - r, 1 - g, 1 - b, a);
    }
    
    /**
     * Create a lighter version of this color
     */
    public Color lighter(float factor) {
        return new Color(
            r + (1 - r) * factor,
            g + (1 - g) * factor,
            b + (1 - b) * factor,
            a
        );
    }
    
    /**
     * Create a darker version of this color
     */
    public Color darker(float factor) {
        return new Color(
            r * (1 - factor),
            g * (1 - factor),
            b * (1 - factor),
            a
        );
    }
    
    /**
     * Create a transparent version of this color
     */
    public Color withAlpha(float alpha) {
        return new Color(r, g, b, alpha);
    }
    
    /**
     * Set the alpha value
     */
    public void setAlpha(float alpha) {
        this.a = clamp(alpha);
    }
    
    /**
     * Set the RGB values
     */
    public void setRGB(float r, float g, float b) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
    }
    
    /**
     * Set the RGBA values
     */
    public void setRGBA(float r, float g, float b, float a) {
        this.r = clamp(r);
        this.g = clamp(g);
        this.b = clamp(b);
        this.a = clamp(a);
    }
    
    /**
     * Copy from another color
     */
    public void set(Color other) {
        this.r = other.r;
        this.g = other.g;
        this.b = other.b;
        this.a = other.a;
    }
    
    /**
     * Linear interpolation between two colors
     */
    public static Color lerp(Color a, Color b, float t) {
        return new Color(
            a.r + (b.r - a.r) * t,
            a.g + (b.g - a.g) * t,
            a.b + (b.b - a.b) * t,
            a.a + (b.a - a.a) * t
        );
    }
    
    /**
     * Check if this color equals another
     */
    public boolean equals(Color other) {
        if (other == null) return false;
        return Math.abs(r - other.r) < 1e-6f && 
               Math.abs(g - other.g) < 1e-6f && 
               Math.abs(b - other.b) < 1e-6f && 
               Math.abs(a - other.a) < 1e-6f;
    }
    
    @Override
    public String toString() {
        return String.format("Color(r=%.2f, g=%.2f, b=%.2f, a=%.2f)", r, g, b, a);
    }
    
    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(r);
        result = 31 * result + Float.floatToIntBits(g);
        result = 31 * result + Float.floatToIntBits(b);
        result = 31 * result + Float.floatToIntBits(a);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Color color = (Color) obj;
        return equals(color);
    }
}
