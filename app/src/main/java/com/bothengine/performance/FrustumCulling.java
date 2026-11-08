package com.bothengine.performance;

import com.bothengine.rendering.Bounds;
import com.bothengine.rendering.Camera;
import com.bothengine.rendering.Frustum;
import com.bothengine.rendering.Matrix4;
import com.bothengine.rendering.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * FrustumCulling
 * Culls objects that are outside the camera's view frustum
 * Improves rendering performance by not rendering objects that aren't visible
 */
public class FrustumCulling {
    private Frustum frustum;
    private Camera camera;
    private Matrix4 viewProjectionMatrix;
    
    private boolean enabled;
    
    public FrustumCulling() {
        this.frustum = null;
        this.camera = null;
        this.viewProjectionMatrix = Matrix4.createIdentity();
        this.enabled = true;
    }
    
    /**
     * Create a frustum culling with a camera
     */
    public static FrustumCulling create(Camera camera) {
        FrustumCulling culling = new FrustumCulling();
        culling.setCamera(camera);
        return culling;
    }
    
    /**
     * Update the frustum culling
     */
    public void update() {
        if (camera == null) return;
        
        // Update view-projection matrix
        viewProjectionMatrix = Matrix4.multiply(camera.getProjectionMatrix(), camera.getViewMatrix());
        
        // Extract frustum planes from view-projection matrix
        if (frustum == null) {
            frustum = new Frustum();
        }
        
        frustum.extractFromMatrix(viewProjectionMatrix);
    }
    
    /**
     * Check if a bounds is visible
     */
    public boolean isVisible(Bounds bounds) {
        if (!enabled || frustum == null) return true;
        
        return frustum.intersects(bounds);
    }
    
    /**
     * Check if a point is visible
     */
    public boolean isVisible(Vector3 point) {
        if (!enabled || frustum == null) return true;
        
        return frustum.contains(point);
    }
    
    /**
     * Check if a sphere is visible
     */
    public boolean isVisible(Vector3 center, float radius) {
        if (!enabled || frustum == null) return true;
        
        return frustum.intersectsSphere(center, radius);
    }
    
    /**
     * Filter a list of objects to only include visible ones
     */
    public <T> List<T> filterVisible(List<T> objects, BoundsGetter<T> boundsGetter) {
        if (!enabled || frustum == null) return objects;
        
        List<T> visible = new ArrayList<>();
        
        for (T obj : objects) {
            Bounds bounds = boundsGetter.getBounds(obj);
            if (isVisible(bounds)) {
                visible.add(obj);
            }
        }
        
        return visible;
    }
    
    /**
     * Get the visibility ratio
     */
    public float getVisibilityRatio(List<?> objects, BoundsGetter<?> boundsGetter) {
        if (!enabled || frustum == null || objects.isEmpty()) return 1f;
        
        int visibleCount = 0;
        
        for (Object obj : objects) {
            @SuppressWarnings("unchecked")
            Bounds bounds = ((BoundsGetter<Object>) boundsGetter).getBounds(obj);
            if (isVisible(bounds)) {
                visibleCount++;
            }
        }
        
        return (float) visibleCount / objects.size();
    }
    
    // Getters and setters
    
    public Frustum getFrustum() {
        return frustum;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
        update();
    }
    
    public Matrix4 getViewProjectionMatrix() {
        return viewProjectionMatrix;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "FrustumCulling{" +
                "camera=" + (camera != null ? camera.getName() : "null") +
                ", enabled=" + enabled +
                '}';
    }
    
    /**
     * BoundsGetter interface
     * Gets bounds from an object
     */
    @FunctionalInterface
    public interface BoundsGetter<T> {
        Bounds getBounds(T obj);
    }
}
