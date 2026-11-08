package com.bothengine.rendering;

/**
 * Vector2
 * 2D vector for positions, sizes, etc.
 */
public class Vector2 {
    public float x, y;
    
    public Vector2() {
        this(0, 0);
    }
    
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2(float[] values) {
        this(values[0], values[1]);
    }
    
    public Vector2(Vector2 other) {
        this(other.x, other.y);
    }
    
    /**
     * Create a zero vector
     */
    public static Vector2 zero() {
        return new Vector2(0, 0);
    }
    
    /**
     * Create a one vector
     */
    public static Vector2 one() {
        return new Vector2(1, 1);
    }
    
    /**
     * Create a unit X vector
     */
    public static Vector2 unitX() {
        return new Vector2(1, 0);
    }
    
    /**
     * Create a unit Y vector
     */
    public static Vector2 unitY() {
        return new Vector2(0, 1);
    }
    
    /**
     * Add two vectors
     */
    public static Vector2 add(Vector2 a, Vector2 b) {
        return new Vector2(a.x + b.x, a.y + b.y);
    }
    
    /**
     * Add a vector to this vector
     */
    public Vector2 add(Vector2 other) {
        this.x += other.x;
        this.y += other.y;
        return this;
    }
    
    /**
     * Subtract two vectors
     */
    public static Vector2 subtract(Vector2 a, Vector2 b) {
        return new Vector2(a.x - b.x, a.y - b.y);
    }
    
    /**
     * Subtract a vector from this vector
     */
    public Vector2 subtract(Vector2 other) {
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }
    
    /**
     * Multiply vector by scalar
     */
    public static Vector2 multiply(Vector2 vector, float scalar) {
        return new Vector2(vector.x * scalar, vector.y * scalar);
    }
    
    /**
     * Multiply this vector by scalar
     */
    public Vector2 multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        return this;
    }
    
    /**
     * Divide vector by scalar
     */
    public static Vector2 divide(Vector2 vector, float scalar) {
        float inv = 1.0f / scalar;
        return new Vector2(vector.x * inv, vector.y * inv);
    }
    
    /**
     * Divide this vector by scalar
     */
    public Vector2 divide(float scalar) {
        float inv = 1.0f / scalar;
        this.x *= inv;
        this.y *= inv;
        return this;
    }
    
    /**
     * Negate this vector
     */
    public Vector2 negate() {
        this.x = -x;
        this.y = -y;
        return this;
    }
    
    /**
     * Get the length of this vector
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y);
    }
    
    /**
     * Get the squared length of this vector
     */
    public float lengthSquared() {
        return x * x + y * y;
    }
    
    /**
     * Normalize this vector
     */
    public Vector2 normalize() {
        float len = length();
        if (len > 0) {
            float invLen = 1.0f / len;
            this.x *= invLen;
            this.y *= invLen;
        }
        return this;
    }
    
    /**
     * Get a normalized copy of this vector
     */
    public Vector2 normalized() {
        Vector2 result = new Vector2(this);
        result.normalize();
        return result;
    }
    
    /**
     * Dot product of two vectors
     */
    public static float dot(Vector2 a, Vector2 b) {
        return a.x * b.x + a.y * b.y;
    }
    
    /**
     * Dot product with this vector
     */
    public float dot(Vector2 other) {
        return x * other.x + y * other.y;
    }
    
    /**
     * Calculate distance between two vectors
     */
    public static float distance(Vector2 a, Vector2 b) {
        return Vector2.subtract(a, b).length();
    }
    
    /**
     * Calculate distance from this vector to another
     */
    public float distance(Vector2 other) {
        return Vector2.distance(this, other);
    }
    
    /**
     * Calculate squared distance between two vectors
     */
    public static float distanceSquared(Vector2 a, Vector2 b) {
        return Vector2.subtract(a, b).lengthSquared();
    }
    
    /**
     * Calculate squared distance from this vector to another
     */
    public float distanceSquared(Vector2 other) {
        return Vector2.distanceSquared(this, other);
    }
    
    /**
     * Linear interpolation between two vectors
     */
    public static Vector2 lerp(Vector2 a, Vector2 b, float t) {
        return new Vector2(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t
        );
    }
    
    /**
     * Linear interpolation from this vector to another
     */
    public Vector2 lerp(Vector2 other, float t) {
        return Vector2.lerp(this, other, t);
    }
    
    /**
     * Check if this vector equals another
     */
    public boolean equals(Vector2 other) {
        if (other == null) return false;
        return Math.abs(x - other.x) < 1e-6f && 
               Math.abs(y - other.y) < 1e-6f;
    }
    
    /**
     * Copy values from another vector
     */
    public void set(Vector2 other) {
        this.x = other.x;
        this.y = other.y;
    }
    
    /**
     * Copy values from array
     */
    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
    }
    
    /**
     * Copy values from individual components
     */
    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Get vector as array
     */
    public float[] toArray() {
        return new float[] { x, y };
    }
    
    /**
     * Get vector as float buffer
     */
    public java.nio.FloatBuffer toFloatBuffer() {
        return java.nio.FloatBuffer.wrap(toArray());
    }
    
    @Override
    public String toString() {
        return "Vector2(" + x + ", " + y + ")";
    }
    
    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector2 vector2 = (Vector2) obj;
        return equals(vector2);
    }
}
