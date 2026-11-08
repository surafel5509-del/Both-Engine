package com.bothengine.rendering;

/**
 * Vector4
 * 4D vector for colors, homogeneous coordinates, etc.
 */
public class Vector4 {
    public float x, y, z, w;
    
    public Vector4() {
        this(0, 0, 0, 0);
    }
    
    public Vector4(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    public Vector4(float[] values) {
        this(values[0], values[1], values[2], values[3]);
    }
    
    public Vector4(Vector4 other) {
        this(other.x, other.y, other.z, other.w);
    }
    
    public Vector4(Vector3 xyz, float w) {
        this(xyz.x, xyz.y, xyz.z, w);
    }
    
    /**
     * Create a zero vector
     */
    public static Vector4 zero() {
        return new Vector4(0, 0, 0, 0);
    }
    
    /**
     * Create a one vector
     */
    public static Vector4 one() {
        return new Vector4(1, 1, 1, 1);
    }
    
    /**
     * Create a vector from a color (RGBA)
     */
    public static Vector4 fromColor(int color) {
        return new Vector4(
            ((color >> 16) & 0xFF) / 255.0f,
            ((color >> 8) & 0xFF) / 255.0f,
            (color & 0xFF) / 255.0f,
            ((color >> 24) & 0xFF) / 255.0f
        );
    }
    
    /**
     * Add two vectors
     */
    public static Vector4 add(Vector4 a, Vector4 b) {
        return new Vector4(a.x + b.x, a.y + b.y, a.z + b.z, a.w + b.w);
    }
    
    /**
     * Add a vector to this vector
     */
    public Vector4 add(Vector4 other) {
        this.x += other.x;
        this.y += other.y;
        this.z += other.z;
        this.w += other.w;
        return this;
    }
    
    /**
     * Subtract two vectors
     */
    public static Vector4 subtract(Vector4 a, Vector4 b) {
        return new Vector4(a.x - b.x, a.y - b.y, a.z - b.z, a.w - b.w);
    }
    
    /**
     * Subtract a vector from this vector
     */
    public Vector4 subtract(Vector4 other) {
        this.x -= other.x;
        this.y -= other.y;
        this.z -= other.z;
        this.w -= other.w;
        return this;
    }
    
    /**
     * Multiply vector by scalar
     */
    public static Vector4 multiply(Vector4 vector, float scalar) {
        return new Vector4(vector.x * scalar, vector.y * scalar, vector.z * scalar, vector.w * scalar);
    }
    
    /**
     * Multiply this vector by scalar
     */
    public Vector4 multiply(float scalar) {
        this.x *= scalar;
        this.y *= scalar;
        this.z *= scalar;
        this.w *= scalar;
        return this;
    }
    
    /**
     * Divide vector by scalar
     */
    public static Vector4 divide(Vector4 vector, float scalar) {
        float inv = 1.0f / scalar;
        return new Vector4(vector.x * inv, vector.y * inv, vector.z * inv, vector.w * inv);
    }
    
    /**
     * Divide this vector by scalar
     */
    public Vector4 divide(float scalar) {
        float inv = 1.0f / scalar;
        this.x *= inv;
        this.y *= inv;
        this.z *= inv;
        this.w *= inv;
        return this;
    }
    
    /**
     * Negate this vector
     */
    public Vector4 negate() {
        this.x = -x;
        this.y = -y;
        this.z = -z;
        this.w = -w;
        return this;
    }
    
    /**
     * Get the length of this vector (ignoring w)
     */
    public float length() {
        return (float) Math.sqrt(x * x + y * y + z * z);
    }
    
    /**
     * Get the squared length of this vector (ignoring w)
     */
    public float lengthSquared() {
        return x * x + y * y + z * z;
    }
    
    /**
     * Normalize this vector (ignoring w)
     */
    public Vector4 normalize() {
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
    public Vector4 normalized() {
        Vector4 result = new Vector4(this);
        result.normalize();
        return result;
    }
    
    /**
     * Dot product of two vectors (ignoring w)
     */
    public static float dot(Vector4 a, Vector4 b) {
        return a.x * b.x + a.y * b.y + a.z * b.z;
    }
    
    /**
     * Dot product with this vector (ignoring w)
     */
    public float dot(Vector4 other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    /**
     * Linear interpolation between two vectors
     */
    public static Vector4 lerp(Vector4 a, Vector4 b, float t) {
        return new Vector4(
            a.x + (b.x - a.x) * t,
            a.y + (b.y - a.y) * t,
            a.z + (b.z - a.z) * t,
            a.w + (b.w - a.w) * t
        );
    }
    
    /**
     * Linear interpolation from this vector to another
     */
    public Vector4 lerp(Vector4 other, float t) {
        return Vector4.lerp(this, other, t);
    }
    
    /**
     * Check if this vector equals another
     */
    public boolean equals(Vector4 other) {
        if (other == null) return false;
        return Math.abs(x - other.x) < 1e-6f && 
               Math.abs(y - other.y) < 1e-6f && 
               Math.abs(z - other.z) < 1e-6f && 
               Math.abs(w - other.w) < 1e-6f;
    }
    
    /**
     * Copy values from another vector
     */
    public void set(Vector4 other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }
    
    /**
     * Copy values from array
     */
    public void set(float[] values) {
        this.x = values[0];
        this.y = values[1];
        this.z = values[2];
        this.w = values[3];
    }
    
    /**
     * Copy values from individual components
     */
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     * Get vector as array
     */
    public float[] toArray() {
        return new float[] { x, y, z, w };
    }
    
    /**
     * Get vector as float buffer
     */
    public java.nio.FloatBuffer toFloatBuffer() {
        return java.nio.FloatBuffer.wrap(toArray());
    }
    
    /**
     * Get the XYZ components as a Vector3
     */
    public Vector3 toVector3() {
        return new Vector3(x, y, z);
    }
    
    @Override
    public String toString() {
        return "Vector4(" + x + ", " + y + ", " + z + ", " + w + ")";
    }
    
    @Override
    public int hashCode() {
        int result = Float.floatToIntBits(x);
        result = 31 * result + Float.floatToIntBits(y);
        result = 31 * result + Float.floatToIntBits(z);
        result = 31 * result + Float.floatToIntBits(w);
        return result;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Vector4 vector4 = (Vector4) obj;
        return equals(vector4);
    }
}
