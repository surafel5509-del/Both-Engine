package com.bothengine.rendering;

/**
 * Bounds
 * Axis-Aligned Bounding Box for collision detection and culling
 */
public class Bounds {
    private Vector3 min;
    private Vector3 max;
    private Vector3 center;
    private Vector3 size;
    
    public Bounds() {
        this.min = new Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        this.max = new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        this.center = new Vector3();
        this.size = new Vector3();
    }
    
    public Bounds(Vector3 min, Vector3 max) {
        this.min = new Vector3(min);
        this.max = new Vector3(max);
        updateCenterAndSize();
    }
    
    public static Bounds fromCenterAndSize(Vector3 center, Vector3 size) {
        Bounds bounds = new Bounds();
        bounds.center = new Vector3(center);
        bounds.size = new Vector3(size);
        bounds.min = Vector3.subtract(center, Vector3.multiply(size, 0.5f));
        bounds.max = Vector3.add(center, Vector3.multiply(size, 0.5f));
        return bounds;
    }
    
    /**
     * Update center and size based on min and max
     */
    private void updateCenterAndSize() {
        this.center = Vector3.add(min, max).multiply(0.5f);
        this.size = Vector3.subtract(max, min);
    }
    
    /**
     * Update min and max based on center and size
     */
    private void updateMinAndMax() {
        this.min = Vector3.subtract(center, Vector3.multiply(size, 0.5f));
        this.max = Vector3.add(center, Vector3.multiply(size, 0.5f));
    }
    
    public Vector3 getMin() {
        return min;
    }
    
    public void setMin(Vector3 min) {
        this.min = min;
        updateCenterAndSize();
    }
    
    public Vector3 getMax() {
        return max;
    }
    
    public void setMax(Vector3 max) {
        this.max = max;
        updateCenterAndSize();
    }
    
    public Vector3 getCenter() {
        return center;
    }
    
    public void setCenter(Vector3 center) {
        this.center = center;
        updateMinAndMax();
    }
    
    public Vector3 getSize() {
        return size;
    }
    
    public void setSize(Vector3 size) {
        this.size = size;
        updateMinAndMax();
    }
    
    /**
     * Get the radius of the bounding sphere that contains this AABB
     */
    public float getBoundingSphereRadius() {
        return size.length() * 0.5f;
    }
    
    /**
     * Check if this bounds contains a point
     */
    public boolean contains(Vector3 point) {
        return point.x >= min.x && point.x <= max.x &&
               point.y >= min.y && point.y <= max.y &&
               point.z >= min.z && point.z <= max.z;
    }
    
    /**
     * Check if this bounds intersects with another bounds
     */
    public boolean intersects(Bounds other) {
        return !(max.x < other.min.x || min.x > other.max.x ||
                 max.y < other.min.y || min.y > other.max.y ||
                 max.z < other.min.z || min.z > other.max.z);
    }
    
    /**
     * Check if this bounds intersects with a sphere
     */
    public boolean intersectsSphere(Vector3 center, float radius) {
        float closestX = Math.max(min.x, Math.min(center.x, max.x));
        float closestY = Math.max(min.y, Math.min(center.y, max.y));
        float closestZ = Math.max(min.z, Math.min(center.z, max.z));
        
        float distanceSquared = Vector3.distanceSquared(center, new Vector3(closestX, closestY, closestZ));
        return distanceSquared <= radius * radius;
    }
    
    /**
     * Expand this bounds to include a point
     */
    /**
     * Slab test: does the ray from {@code origin} along {@code direction} hit these bounds
     * within {@code maxDistance}?
     */
    public boolean intersectsRay(Vector3 origin, Vector3 direction, float maxDistance) {
        float tmin = 0f;
        float tmax = maxDistance > 0 ? maxDistance : Float.POSITIVE_INFINITY;
        float[] o = {origin.x, origin.y, origin.z};
        float[] d = {direction.x, direction.y, direction.z};
        float[] lo = {min.x, min.y, min.z};
        float[] hi = {max.x, max.y, max.z};

        for (int i = 0; i < 3; i++) {
            if (Math.abs(d[i]) < 1e-6f) {
                if (o[i] < lo[i] || o[i] > hi[i]) return false;
                continue;
            }
            float inv = 1f / d[i];
            float t1 = (lo[i] - o[i]) * inv;
            float t2 = (hi[i] - o[i]) * inv;
            tmin = Math.max(tmin, Math.min(t1, t2));
            tmax = Math.min(tmax, Math.max(t1, t2));
            if (tmin > tmax) return false;
        }
        return true;
    }

    public void expand(Vector3 point) {
        min.x = Math.min(min.x, point.x);
        min.y = Math.min(min.y, point.y);
        min.z = Math.min(min.z, point.z);
        
        max.x = Math.max(max.x, point.x);
        max.y = Math.max(max.y, point.y);
        max.z = Math.max(max.z, point.z);
        
        updateCenterAndSize();
    }
    
    /**
     * Expand this bounds to include another bounds
     */
    public void expand(Bounds other) {
        min.x = Math.min(min.x, other.min.x);
        min.y = Math.min(min.y, other.min.y);
        min.z = Math.min(min.z, other.min.z);
        
        max.x = Math.max(max.x, other.max.x);
        max.y = Math.max(max.y, other.max.y);
        max.z = Math.max(max.z, other.max.z);
        
        updateCenterAndSize();
    }
    
    /**
     * Transform this bounds by a matrix
     */
    public Bounds transform(Matrix4 matrix) {
        Vector3[] corners = getCorners();
        Vector3 newMin = new Vector3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
        Vector3 newMax = new Vector3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
        
        for (Vector3 corner : corners) {
            Vector3 transformed = Matrix4.transform(matrix, corner);
            newMin.x = Math.min(newMin.x, transformed.x);
            newMin.y = Math.min(newMin.y, transformed.y);
            newMin.z = Math.min(newMin.z, transformed.z);
            
            newMax.x = Math.max(newMax.x, transformed.x);
            newMax.y = Math.max(newMax.y, transformed.y);
            newMax.z = Math.max(newMax.z, transformed.z);
        }
        
        return new Bounds(newMin, newMax);
    }
    
    /**
     * Get all 8 corners of this bounds
     */
    public Vector3[] getCorners() {
        Vector3[] corners = new Vector3[8];
        corners[0] = new Vector3(min.x, min.y, min.z);
        corners[1] = new Vector3(max.x, min.y, min.z);
        corners[2] = new Vector3(min.x, max.y, min.z);
        corners[3] = new Vector3(max.x, max.y, min.z);
        corners[4] = new Vector3(min.x, min.y, max.z);
        corners[5] = new Vector3(max.x, min.y, max.z);
        corners[6] = new Vector3(min.x, max.y, max.z);
        corners[7] = new Vector3(max.x, max.y, max.z);
        return corners;
    }
    
    /**
     * Clone this bounds
     */
    public Bounds clone() {
        return new Bounds(min, max);
    }
    
    /**
     * Check if this bounds is valid
     */
    public boolean isValid() {
        return min.x <= max.x && min.y <= max.y && min.z <= max.z;
    }
    
    @Override
    public String toString() {
        return "Bounds{min=" + min + ", max=" + max + ", center=" + center + ", size=" + size + "}";
    }
}
