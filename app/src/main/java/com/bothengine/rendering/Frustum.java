package com.bothengine.rendering;

/**
 * Frustum
 * Represents a view frustum for culling objects outside the camera view
 */
public class Frustum {
    private static final int PLANE_NEAR = 0;
    private static final int PLANE_FAR = 1;
    private static final int PLANE_LEFT = 2;
    private static final int PLANE_RIGHT = 3;
    private static final int PLANE_TOP = 4;
    private static final int PLANE_BOTTOM = 5;
    
    private final Plane[] planes;
    
    public Frustum() {
        this.planes = new Plane[6];
        for (int i = 0; i < planes.length; i++) {
            planes[i] = new Plane(0, 0, 1, 0);
        }
    }

    public Frustum(Matrix4 viewProjectionMatrix) {
        this.planes = new Plane[6];
        extractPlanes(viewProjectionMatrix);
    }

    /**
     * Re-extract the six clipping planes from a view-projection matrix.
     */
    public void extractFromMatrix(Matrix4 viewProjectionMatrix) {
        extractPlanes(viewProjectionMatrix);
    }

    public boolean contains(Vector3 point) {
        return containsPoint(point);
    }

    public boolean intersects(Bounds bounds) {
        return containsBounds(bounds);
    }

    public boolean intersectsSphere(Vector3 center, float radius) {
        return containsSphere(center, radius);
    }
    
    /**
     * Extract frustum planes from view-projection matrix
     */
    private void extractPlanes(Matrix4 matrix) {
        // Left plane
        planes[PLANE_LEFT] = new Plane(
            matrix.data[3] + matrix.data[0],
            matrix.data[7] + matrix.data[4],
            matrix.data[11] + matrix.data[8],
            matrix.data[15] + matrix.data[12]
        );
        
        // Right plane
        planes[PLANE_RIGHT] = new Plane(
            matrix.data[3] - matrix.data[0],
            matrix.data[7] - matrix.data[4],
            matrix.data[11] - matrix.data[8],
            matrix.data[15] - matrix.data[12]
        );
        
        // Bottom plane
        planes[PLANE_BOTTOM] = new Plane(
            matrix.data[3] + matrix.data[1],
            matrix.data[7] + matrix.data[5],
            matrix.data[11] + matrix.data[9],
            matrix.data[15] + matrix.data[13]
        );
        
        // Top plane
        planes[PLANE_TOP] = new Plane(
            matrix.data[3] - matrix.data[1],
            matrix.data[7] - matrix.data[5],
            matrix.data[11] - matrix.data[9],
            matrix.data[15] - matrix.data[13]
        );
        
        // Near plane
        planes[PLANE_NEAR] = new Plane(
            matrix.data[3] + matrix.data[2],
            matrix.data[7] + matrix.data[6],
            matrix.data[11] + matrix.data[10],
            matrix.data[15] + matrix.data[14]
        );
        
        // Far plane
        planes[PLANE_FAR] = new Plane(
            matrix.data[3] - matrix.data[2],
            matrix.data[7] - matrix.data[6],
            matrix.data[11] - matrix.data[10],
            matrix.data[15] - matrix.data[14]
        );
        
        // Normalize all planes
        for (Plane plane : planes) {
            plane.normalize();
        }
    }
    
    /**
     * Check if a point is inside the frustum
     */
    public boolean containsPoint(Vector3 point) {
        for (Plane plane : planes) {
            if (plane.getDistance(point) < 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if a sphere is inside the frustum
     */
    public boolean containsSphere(Vector3 center, float radius) {
        for (Plane plane : planes) {
            if (plane.getDistance(center) < -radius) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if an AABB (Axis-Aligned Bounding Box) is inside the frustum
     */
    public boolean containsAABB(Vector3 min, Vector3 max) {
        for (Plane plane : planes) {
            Vector3 p = new Vector3(
                plane.normal.x > 0 ? max.x : min.x,
                plane.normal.y > 0 ? max.y : min.y,
                plane.normal.z > 0 ? max.z : min.z
            );
            
            if (plane.getDistance(p) < 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Check if a bounding box is inside the frustum
     */
    public boolean containsBounds(Bounds bounds) {
        return containsAABB(bounds.getMin(), bounds.getMax());
    }
    
    /**
     * Get a frustum plane
     */
    public Plane getPlane(int index) {
        return planes[index];
    }
    
    /**
     * Get all frustum planes
     */
    public Plane[] getPlanes() {
        return planes;
    }
    
    /**
     * Plane class for frustum planes
     */
    public static class Plane {
        public Vector3 normal;
        public float distance;
        
        public Plane(float a, float b, float c, float d) {
            this.normal = new Vector3(a, b, c);
            this.distance = d;
        }
        
        /**
         * Normalize the plane
         */
        public void normalize() {
            float length = normal.length();
            if (length > 0) {
                normal.divide(length);
                distance /= length;
            }
        }
        
        /**
         * Get the distance from a point to this plane
         */
        public float getDistance(Vector3 point) {
            return Vector3.dot(normal, point) + distance;
        }
        
        @Override
        public String toString() {
            return "Plane{normal=" + normal + ", distance=" + distance + "}";
        }
    }
}
