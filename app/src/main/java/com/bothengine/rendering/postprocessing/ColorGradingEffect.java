package com.bothengine.rendering.postprocessing;

/**
 * ColorGradingEffect
 * Applies color grading/LUT (Look-Up Table) to the scene
 * Used for creating specific color moods and styles
 */
public class ColorGradingEffect extends PostProcessingEffect {
    
    private float temperature;
    private float tint;
    private float exposure;
    private float contrast;
    private float saturation;
    private float brightness;
    private float gamma;
    private float vibrance;
    private float shadows;
    private float highlights;
    private float shadowTint;
    private float highlightTint;
    
    // LUT texture (optional)
    private int lutTextureId;
    private float lutIntensity;
    
    public ColorGradingEffect() {
        super("ColorGrading");
        this.temperature = 0.0f;  // Neutral
        this.tint = 0.0f;         // Neutral
        this.exposure = 0.0f;     // No change
        this.contrast = 1.0f;     // Normal
        this.saturation = 1.0f;   // Normal
        this.brightness = 0.0f;   // No change
        this.gamma = 1.0f;        // Normal
        this.vibrance = 0.0f;     // No change
        this.shadows = 0.0f;      // No change
        this.highlights = 0.0f;   // No change
        this.shadowTint = 0.5f;   // Neutral
        this.highlightTint = 0.5f; // Neutral
        this.lutTextureId = 0;
        this.lutIntensity = 1.0f;
    }
    
    @Override
    public void initialize() {
        // Create color grading shader
        // This would create a shader that applies all color adjustments
    }
    
    @Override
    protected void onApply() {
        // Apply color grading:
        // 1. Apply temperature and tint (color balance)
        // 2. Apply exposure
        // 3. Apply contrast
        // 4. Apply saturation
        // 5. Apply brightness
        // 6. Apply gamma correction
        // 7. Apply vibrance
        // 8. Apply shadows and highlights adjustment
        // 9. Apply LUT if available
        
        // This would use a shader with all these parameters
    }
    
    /**
     * Set temperature (color balance)
     * @param temperature Range: -100 to 100 (blue to orange)
     */
    public void setTemperature(float temperature) {
        this.temperature = Math.max(-100, Math.min(100, temperature));
    }
    
    /**
     * Set tint (color balance)
     * @param tint Range: -100 to 100 (green to magenta)
     */
    public void setTint(float tint) {
        this.tint = Math.max(-100, Math.min(100, tint));
    }
    
    /**
     * Set exposure
     * @param exposure Range: -10 to 10 (darker to brighter)
     */
    public void setExposure(float exposure) {
        this.exposure = Math.max(-10, Math.min(10, exposure));
    }
    
    /**
     * Set contrast
     * @param contrast Range: 0 to 2 (low to high contrast)
     */
    public void setContrast(float contrast) {
        this.contrast = Math.max(0, Math.min(2, contrast));
    }
    
    /**
     * Set saturation
     * @param saturation Range: 0 to 2 (desaturated to oversaturated)
     */
    public void setSaturation(float saturation) {
        this.saturation = Math.max(0, Math.min(2, saturation));
    }
    
    /**
     * Set brightness
     * @param brightness Range: -1 to 1 (darker to brighter)
     */
    public void setBrightness(float brightness) {
        this.brightness = Math.max(-1, Math.min(1, brightness));
    }
    
    /**
     * Set gamma
     * @param gamma Range: 0.1 to 3 (darker to brighter)
     */
    public void setGamma(float gamma) {
        this.gamma = Math.max(0.1f, Math.min(3, gamma));
    }
    
    /**
     * Set vibrance (saturation for muted colors)
     * @param vibrance Range: -1 to 1
     */
    public void setVibrance(float vibrance) {
        this.vibrance = Math.max(-1, Math.min(1, vibrance));
    }
    
    /**
     * Set shadows adjustment
     * @param shadows Range: -1 to 1 (darker to brighter)
     */
    public void setShadows(float shadows) {
        this.shadows = Math.max(-1, Math.min(1, shadows));
    }
    
    /**
     * Set highlights adjustment
     * @param highlights Range: -1 to 1 (darker to brighter)
     */
    public void setHighlights(float highlights) {
        this.highlights = Math.max(-1, Math.min(1, highlights));
    }
    
    /**
     * Set shadow tint
     * @param shadowTint Range: 0 to 1 (blue to orange)
     */
    public void setShadowTint(float shadowTint) {
        this.shadowTint = Math.max(0, Math.min(1, shadowTint));
    }
    
    /**
     * Set highlight tint
     * @param highlightTint Range: 0 to 1 (blue to orange)
     */
    public void setHighlightTint(float highlightTint) {
        this.highlightTint = Math.max(0, Math.min(1, highlightTint));
    }
    
    /**
     * Set LUT texture
     */
    public void setLUTTexture(int lutTextureId) {
        this.lutTextureId = lutTextureId;
    }
    
    /**
     * Set LUT intensity
     */
    public void setLUTIntensity(float intensity) {
        this.lutIntensity = Math.max(0, Math.min(1, intensity));
    }
    
    // Getters
    
    public float getTemperature() {
        return temperature;
    }
    
    public float getTint() {
        return tint;
    }
    
    public float getExposure() {
        return exposure;
    }
    
    public float getContrast() {
        return contrast;
    }
    
    public float getSaturation() {
        return saturation;
    }
    
    public float getBrightness() {
        return brightness;
    }
    
    public float getGamma() {
        return gamma;
    }
    
    public float getVibrance() {
        return vibrance;
    }
    
    public float getShadows() {
        return shadows;
    }
    
    public float getHighlights() {
        return highlights;
    }
    
    public float getShadowTint() {
        return shadowTint;
    }
    
    public float getHighlightTint() {
        return highlightTint;
    }
    
    public int getLUTTextureId() {
        return lutTextureId;
    }
    
    public float getLUTIntensity() {
        return lutIntensity;
    }
    
    @Override
    public String toString() {
        return "ColorGradingEffect{" +
                "temperature=" + temperature +
                ", tint=" + tint +
                ", exposure=" + exposure +
                ", contrast=" + contrast +
                ", saturation=" + saturation +
                ", brightness=" + brightness +
                ", gamma=" + gamma +
                '}';
    }
}
