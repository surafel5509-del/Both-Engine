package com.bothengine.rendering;

/**
 * Vector3
 * 3D vector for positions, directions, etc.
 */
public class Vector3 {
    public float x, y, z;
    
    public Vector3() {
        this(0, 0, 0);
    }
    
    public Vector3(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Vector3(float[] values) {
        this(values[0], values[1], values[2]);
    }
    
    public Vector3(Vector3 other) {
        this(other.x, other.y, other.z);
    }
    
    /**
     * Create a zero vector
     */
    public static Vector3 zero() {
        return new Vector3(0, 0, 0);
    }
    
    /**
     * Create a one vector
     */
    public static Vector3 one() {
        return new Vector3(1, 1, 1);
    }
    
    /**
     * Create a unit X vector
     */
    public static Vector3 unitX() {
        return new Vector3(1, 0, 0);
    }
    
    /**
     * Create a unit Y vector
     */
    public static Vector3 unitY() {
        return new Vector3(0, 1, 0);
    }
    
    /**
     * Create a unit Z vector
     */
    public static Vector3 unitZ() {
        return new Vector3(0, 0, 1);
    }
    
    /**
     * Create a forward vector
     */
    public static Vector3 forward() {
        return new Vector3(0, 0, -1);
    }
    
    /**
     * Create a backward vector
     */
    public static Vector3 backward() {
        return new Vector3(0, 0, 1);
    }
    
    /**
     * Create a right vector
     */
    public static Vector3 right() {
        return new Vector3(1, 0, 0);
    }
    
    /**
     * Create a left vector
     */
    public static Vector3 left() {
        return new Vector3(-1, 0, 0);
    }
    
    /**
     * Create an up vector
     */
    public static Vector3 up() {
        return new Vector3(0, 1, 0);
    }
    
    /**
     * Create a down vector
     */
    public static Vector3 down() {
        return new Vector3(0, -1, 0);
    }
    
    /**
     * Add two vectors
     */
    public static Vector3 add(Vector3 a, Vector3 b) {
        return new Vector3(a.x + b.x, a.y + b.y, a.z + b.z);
    }
    
    /**
     * Add a vector to this vector
     */
    public Vector3 add(Vector3 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        return this;
    }
    
    /**
     * Subtract two vectors
     */
    public static Vector3 subtract(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }
    
    /**
     * Subtract a vector from this vector
     */
    public Vector3 subtract(Vector3 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        return this;
    }
    
    /**
     * Multiply vector by scalar
     */
    public static Vector3 multiply(Vector3 vector, float scalar) {
        return new Vector3(vector.x * scalar, vector.y * scalar, vector.z * scalar);
    }
    
    /**
     * Multiply this vector by scalar
     */
    public Vector3 multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        return this;
    }
    
    /**
     * Divide vector by scalar
     */
    public static Vector3 divide(Vector3 vector, float scalar) {
        float inv = 1.0f / scalar;
        return new Vector3(vector.x * inv, vector.y * inv, vector.z * inv);
    }
    
    /**
     * Divide this vector by scalar
     */
    public Vector3 divide(float scalar) {
        float inv = 1.0f / scalar;
        this.x *= inv;
        this.y *= inv;
        this.z *= inv;
        return this;
    }
    
    /**
     * Negate this vector
     */
    public Vector3 negate() {
        this.x = -x;
        this.y = -y;
        this.z = -z;
        return this;
    }
    
    /**
     * Get the length of this vector
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    
    /**
     * Get the squared length of this vector
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    /**
     * Normalize this vector
     */
    public Vector3 normalize() {
        float len = length();
        if (len > 0) {
            float invLen = 1.0f / len;
            this.x *= invLen;
            this.y *= invLen;
            this.z *= invLen;
        }
        return this;
    }
    
    /**
     * Get a normalized copy of this vector
     */
    public Vector3 normalized() {
        Vector3 result = new Vector3(this);
        result.normalize();
        return result;
    }
    
    /**
     * Dot product of two vectors
     */
    public static float dot(Vector3 a, Vector3 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    
    /**
     * Dot product with this vector
     */
    public float dot(Vector3 other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    /**
     * Cross product of two vectors
     */
    public static Vector3 cross(Vector3 a, Vector3 b) {
        return new Vector3(
            a.y * b.z - a.z * b.y,
            a.z * b.x - a.x * b.z,
            a.x * b.y - a.y * b.x
        );
    }
    
    /**
     * Cross product with this vector
     */
    public Vector3 cross(Vector3 other) {
        return new Vector3(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
    
    /**
     * Calculate distance between two vectors
     */
    public static float distance(Vector3 a, Vector3 b) {
        return Vector3.subtract(a, b).length();
    }
    
    /**
     * Calculate distance from this vector to another
     */
    public float distance(Vector3 other) {
        return Vector3.distance(this, other);
    }
    
    /**
     * Calculate squared distance between two vectors
     */
    public static float distanceSquared(Vector3 a, Vector3 b) {
        return Vector3.subtract(a, b).lengthSquared();
    }
    
    /**
     * Calculate squared distance from this vector to another
     */
    public float distanceSquared(Vector3 other) {
        return Vector3.distanceSquared(this, other);
    }
    
    /**
     * Linear interpolation between two vectors
     */
    public static Vector3 lerp(Vector3 a, Vector3 b, float t) {
        return new Vector3(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t
        );
    }
    
    /**
     * Linear interpolation from this vector to another
     */
    public Vector3 lerp(Vector3 other, float t) {
        return Vector3.lerp(this, other, t);
    }
    
    /**
     * Spherical interpolation between two vectors
     */
    public static Vector3 slerp(Vector3 a, Vector3 b, float t) {
        float dot = Vector3.dot(a, b);
        
        if (Math.abs(dot) > 0.9995f) {
            return Vector3.lerp(a, b, t);
        }
        
        float theta = (float) Math.acos(dot);
        float sinTheta = (float) Math.sin(theta);
        
        float ratio1 = (float) Math.sin((1 - t) * theta) / sinTheta;
        float ratio2 = (float) Math.sin(t * theta) / sinTheta;
        
        return new Vector3(
            a.x * ratio1 + b.x * ratio2,
            a.y * ratio1 + b.y * ratio2,
            a.z * ratio1 + b.z * ratio2
        );
    }
    
    /**
     * Check if this vector equals another
     */
    public boolean equals(Vector3 other) {
        if (other == null) return false;
        return Math.abs(x - other.x) < 1e-6f && 
               Math.abs(y - other.y) < 1e-6f && 
               Math.abs(z - other.z) < 1e-6f;
    }
    
    /**
     * Copy values from another vector
     */
    public void set(Vector3 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }
    
    /**
     * Copy values from array
     */
    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
    }
    
    /**
     * Copy values from individual components
     */
    public void set(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Get vector as array
     */
    public float[] toArray() {
        return new float[] { x, y, z };
    }
    
    /**
     * Get vector as float buffer
     */
    public java.nio.FloatBuffer toFloatBuffer() {
        return java.nio.FloatBuffer.wrap(toArray());
    }
    
    @Override
    public String toString() {
        return "Vector3(" + x + ", " + y + ", " + z + ")";
    }
    
    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector3 vector3 = (Vector3) obj;
        return equals(vector3);
    }
}
