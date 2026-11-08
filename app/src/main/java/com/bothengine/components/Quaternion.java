package com.bothengine.components;

import com.bothengine.rendering.Matrix4;
import com.bothengine.rendering.Vector3;

/**
 * Quaternion
 * Represents a quaternion for 3D rotations
 */
public class Quaternion {
    public float x, y, z, w;
    
    public Quaternion() {
        this(0, 0, 0, 1);
    }
    
    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Quaternion(Vector3 axis, float angle) {
        float halfAngle = angle * 0.5f;
        float sin = (float) Math.sin(halfAngle);
        this.x = axis.x * sin;
        this.y = axis.y * sin;
        this.z = axis.z * sin;
        this.w = (float) Math.cos(halfAngle);
    }
    
    /**
     * Create an identity quaternion
     */
    public static Quaternion identity() {
        return new Quaternion(0, 0, 0, 1);
    }
    
    /**
     * Create a quaternion from Euler angles (in radians)
     */
    public static Quaternion fromEuler(float pitch, float yaw, float roll) {
        float cy = (float) Math.cos(yaw * 0.5f);
        float sy = (float) Math.sin(yaw * 0.5f);
        float cp = (float) Math.cos(pitch * 0.5f);
        float sp = (float) Math.sin(pitch * 0.5f);
        float cr = (float) Math.cos(roll * 0.5f);
        float sr = (float) Math.sin(roll * 0.5f);
        
        return new Quaternion(
            sr * cp * cy - cr * sp * sy,
            cr * sp * cy + sr * cp * sy,
            cr * cp * sy - sr * sp * cy,
            cr * cp * cy + sr * sp * sy
        );
    }
    
    /**
     * Create a quaternion from Euler angles (in degrees)
     */
    public static Quaternion fromEulerDegrees(float pitch, float yaw, float roll) {
        return fromEuler(
            (float) Math.toRadians(pitch),
            (float) Math.toRadians(yaw),
            (float) Math.toRadians(roll)
        );
    }
    
    /**
     * Create a quaternion that rotates from one vector to another
     */
    public static Quaternion lookAt(Vector3 direction) {
        Vector3 forward = new Vector3(0, 0, -1);
        return lookAt(direction, new Vector3(0, 1, 0));
    }
    
    /**
     * Create a quaternion that rotates from one vector to another with up vector
     */
    public static Quaternion lookAt(Vector3 direction, Vector3 up) {
        Vector3 forward = new Vector3(0, 0, -1);
        Vector3 right = Vector3.cross(up, direction).normalize();
        up = Vector3.cross(direction, right).normalize();
        
        Matrix4 matrix = new Matrix4();
        matrix.data[0] = right.x;
        matrix.data[1] = right.y;
        matrix.data[2] = right.z;
        matrix.data[4] = up.x;
        matrix.data[5] = up.y;
        matrix.data[6] = up.z;
        matrix.data[8] = direction.x;
        matrix.data[9] = direction.y;
        matrix.data[10] = direction.z;
        
        return fromMatrix(matrix);
    }
    
    /**
     * Create a quaternion from a rotation matrix
     */
    public static Quaternion fromMatrix(Matrix4 matrix) {
        float trace = matrix.data[0] + matrix.data[5] + matrix.data[10];
        
        if (trace > 0) {
            float s = (float) Math.sqrt(trace + 1) * 2;
            return new Quaternion(
                (matrix.data[6] - matrix.data[9]) / s,
                (matrix.data[8] - matrix.data[2]) / s,
                (matrix.data[1] - matrix.data[4]) / s,
                s / 4
            );
        } else if (matrix.data[0] > matrix.data[5] && matrix.data[0] > matrix.data[10]) {
            float s = (float) Math.sqrt(matrix.data[0] - matrix.data[5] - matrix.data[10] + 1) * 2;
            return new Quaternion(
                s / 4,
                (matrix.data[1] + matrix.data[4]) / s,
                (matrix.data[8] + matrix.data[2]) / s,
                (matrix.data[6] - matrix.data[9]) / s
            );
        } else if (matrix.data[5] > matrix.data[10]) {
            float s = (float) Math.sqrt(matrix.data[5] - matrix.data[0] - matrix.data[10] + 1) * 2;
            return new Quaternion(
                (matrix.data[1] + matrix.data[4]) / s,
                s / 4,
                (matrix.data[6] + matrix.data[9]) / s,
                (matrix.data[8] - matrix.data[2]) / s
            );
        } else {
            float s = (float) Math.sqrt(matrix.data[10] - matrix.data[0] - matrix.data[5] + 1) * 2;
            return new Quaternion(
                (matrix.data[8] + matrix.data[2]) / s,
                (matrix.data[6] + matrix.data[9]) / s,
                s / 4,
                (matrix.data[1] - matrix.data[4]) / s
            );
        }
    }
    
    /**
     * Create a rotation quaternion
     */
    public static Quaternion rotate(Vector3 axis, float angle) {
        return new Quaternion(axis, angle);
    }
    
    /**
     * Normalize the quaternion
     */
    public Quaternion normalize() {
        float length = (float) Math.sqrt(x * x + y * y + z * z + w * w);
        if (length > 0) {
            float invLength = 1.0f / length;
            x *= invLength;
            y *= invLength;
            z *= invLength;
            w *= invLength;
        }
        return this;
    }
    
    /**
     * Get the normalized quaternion
     */
    public Quaternion normalized() {
        Quaternion result = new Quaternion(x, y, z, w);
        result.normalize();
        return result;
    }
    
    /**
     * Multiply two quaternions
     */
    public static Quaternion multiply(Quaternion a, Quaternion b) {
        return new Quaternion(
            a.w * b.x + a.x * b.w + a.y * b.z - a.z * b.y,
            a.w * b.y - a.x * b.z + a.y * b.w + a.z * b.x,
            a.w * b.z + a.x * b.y - a.y * b.x + a.z * b.w,
            a.w * b.w - a.x * b.x - a.y * b.y - a.z * b.z
        );
    }
    
    /**
     * Multiply this quaternion by another
     */
    public Quaternion multiply(Quaternion other) {
        return multiply(this, other);
    }
    
    /**
     * Inverse the quaternion
     */
    public Quaternion inverse() {
        return new Quaternion(-x, -y, -z, w).normalize();
    }
    
    /**
     * Get the conjugate of the quaternion
     */
    public Quaternion conjugate() {
        return new Quaternion(-x, -y, -z, w);
    }
    
    /**
     * Get the dot product of two quaternions
     */
    public static float dot(Quaternion a, Quaternion b) {
        return a.x * b.x + a.y * b.y + a.z * b.z + a.w * b.w;
    }
    
    /**
     * Get the dot product with another quaternion
     */
    public float dot(Quaternion other) {
        return dot(this, other);
    }
    
    /**
     * Spherical linear interpolation between two quaternions
     */
    public static Quaternion slerp(Quaternion a, Quaternion b, float t) {
        float dot = dot(a, b);
        
        if (Math.abs(dot) > 0.9995f) {
            // Linear interpolation
            return new Quaternion(
                a.x + (b.x - a.x) * t,
                a.y + (b.y - a.y) * t,
                a.z + (b.z - a.z) * t,
                a.w + (b.w - a.w) * t
            ).normalize();
        }
        
        float theta = (float) Math.acos(dot);
        float sinTheta = (float) Math.sin(theta);
        
        float ratio1 = (float) Math.sin((1 - t) * theta) / sinTheta;
        float ratio2 = (float) Math.sin(t * theta) / sinTheta;
        
        return new Quaternion(
            a.x * ratio1 + b.x * ratio2,
            a.y * ratio1 + b.y * ratio2,
            a.z * ratio1 + b.z * ratio2,
            a.w * ratio1 + b.w * ratio2
        );
    }
    
    /**
     * Linear interpolation between two quaternions
     */
    public static Quaternion lerp(Quaternion a, Quaternion b, float t) {
        return new Quaternion(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t,
            a.w + (b.w - a.w) * t
        ).normalize();
    }
    
    /**
     * Rotate a vector by this quaternion
     */
    public Vector3 rotate(Vector3 vector) {
        Quaternion q = new Quaternion(vector.x, vector.y, vector.z, 0);
        Quaternion result = multiply(this, multiply(q, this.conjugate()));
        return new Vector3(result.x, result.y, result.z);
    }
    
    /**
     * Get the forward vector
     */
    public Vector3 getForward() {
        return rotate(new Vector3(0, 0, -1));
    }
    
    /**
     * Get the up vector
     */
    public Vector3 getUp() {
        return rotate(new Vector3(0, 1, 0));
    }
    
    /**
     * Get the right vector
     */
    public Vector3 getRight() {
        return rotate(new Vector3(1, 0, 0));
    }
    
    /**
     * Get Euler angles (in radians)
     */
    public Vector3 getEulerAngles() {
        float pitch = (float) Math.atan2(2 * (w * x + y * z), 1 - 2 * (x * x + y * y));
        float yaw = (float) Math.asin(2 * (w * y - z * x));
        float roll = (float) Math.atan2(2 * (w * z + x * y), 1 - 2 * (y * y + z * z));
        
        return new Vector3(pitch, yaw, roll);
    }
    
    /**
     * Get Euler angles (in degrees)
     */
    public Vector3 getEulerAnglesDegrees() {
        Vector3 radians = getEulerAngles();
        return new Vector3(
            (float) Math.toDegrees(radians.x),
            (float) Math.toDegrees(radians.y),
            (float) Math.toDegrees(radians.z)
        );
    }
    
    /**
     * Get the length of the quaternion
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z + w * w);
    }
    
    /**
     * Get the squared length of the quaternion
     */
    public float lengthSquared() {
        return x * x + y * y + z * z + w * w;
    }
    
    /**
     * Check if the quaternion is normalized
     */
    public boolean isNormalized() {
        return Math.abs(lengthSquared() - 1) < 1e-6f;
    }
    
    /**
     * Check if this quaternion equals another
     */
    public boolean equals(Quaternion other) {
        if (other == null) return false;
        return Math.abs(x - other.x) < 1e-6f && 
               Math.abs(y - other.y) < 1e-6f && 
               Math.abs(z - other.z) < 1e-6f && 
               Math.abs(w - other.w) < 1e-6f;
    }
    
    /**
     * Copy from another quaternion
     */
    public void set(Quaternion other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }
    
    /**
     * Set quaternion components
     */
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     * Get quaternion as array
     */
    public float[] toArray() {
        return new float[] { x, y, z, w };
    }
    
    @Override
    public String toString() {
        return "Quaternion(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
}
