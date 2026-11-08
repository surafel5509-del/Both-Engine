package com.bothengine.rendering;

/**
 * Matrix4
 * 4x4 matrix for 3D transformations
 */
public class Matrix4 {
    public static final int SIZE = 16;
    
    public float[] data;
    
    public Matrix4() {
        data = new float[SIZE];
        setIdentity();
    }
    
    public Matrix4(float[] data) {
        this.data = new float[SIZE];
        System.arraycopy(data, 0, this.data, 0, SIZE);
    }
    
    /**
     * Create an identity matrix
     */
    public static Matrix4 identity() {
        return new Matrix4();
    }
    
    /**
     * Set this matrix to identity
     */
    public Matrix4 setIdentity() {
        for (int i = 0; i < SIZE; i++) {
            data[i] = 0;
        }
        data[0] = data[5] = data[10] = data[15] = 1;
        return this;
    }
    
    /**
     * Create a translation matrix
     */
    public static Matrix4 translation(Vector3 vector) {
        return new Matrix4().translate(vector);
    }
    
    /**
     * Translate this matrix
     */
    public Matrix4 translate(Vector3 vector) {
        float x = vector.x;
        float y = vector.y;
        float z = vector.z;
        
        data[12] += data[0] * x + data[4] * y + data[8] * z;
        data[13] += data[1] * x + data[5] * y + data[9] * z;
        data[14] += data[2] * x + data[6] * y + data[10] * z;
        data[15] += data[3] * x + data[7] * y + data[11] * z;
        return this;
    }
    
    /**
     * Create a rotation matrix around X axis
     */
    public static Matrix4 rotateXStatic(float angle) {
        Matrix4 matrix = new Matrix4();
        matrix.rotateX(angle);
        return matrix;
    }
    
    /**
     * Rotate this matrix around X axis
     */
    public Matrix4 rotateX(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        
        float[] temp = new float[SIZE];
        System.arraycopy(data, 0, temp, 0, SIZE);
        
        data[4] = temp[4] * cos + temp[8] * sin;
        data[5] = temp[5] * cos + temp[9] * sin;
        data[6] = temp[6] * cos + temp[10] * sin;
        data[7] = temp[7] * cos + temp[11] * sin;
        
        data[8] = temp[4] * -sin + temp[8] * cos;
        data[9] = temp[5] * -sin + temp[9] * cos;
        data[10] = temp[6] * -sin + temp[10] * cos;
        data[11] = temp[7] * -sin + temp[11] * cos;
        return this;
    }
    
    /**
     * Create a rotation matrix around Y axis
     */
    public static Matrix4 rotateYStatic(float angle) {
        Matrix4 matrix = new Matrix4();
        matrix.rotateY(angle);
        return matrix;
    }
    
    /**
     * Rotate this matrix around Y axis
     */
    public Matrix4 rotateY(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        
        float[] temp = new float[SIZE];
        System.arraycopy(data, 0, temp, 0, SIZE);
        
        data[0] = temp[0] * cos + temp[8] * -sin;
        data[1] = temp[1] * cos + temp[9] * -sin;
        data[2] = temp[2] * cos + temp[10] * -sin;
        data[3] = temp[3] * cos + temp[11] * -sin;
        
        data[8] = temp[0] * sin + temp[8] * cos;
        data[9] = temp[1] * sin + temp[9] * cos;
        data[10] = temp[2] * sin + temp[10] * cos;
        data[11] = temp[3] * sin + temp[11] * cos;
        return this;
    }
    
    /**
     * Create a rotation matrix around Z axis
     */
    public static Matrix4 rotateZStatic(float angle) {
        Matrix4 matrix = new Matrix4();
        matrix.rotateZ(angle);
        return matrix;
    }
    
    /**
     * Rotate this matrix around Z axis
     */
    public Matrix4 rotateZ(float angle) {
        float cos = (float) Math.cos(angle);
        float sin = (float) Math.sin(angle);
        
        float[] temp = new float[SIZE];
        System.arraycopy(data, 0, temp, 0, SIZE);
        
        data[0] = temp[0] * cos + temp[4] * sin;
        data[1] = temp[1] * cos + temp[5] * sin;
        data[2] = temp[2] * cos + temp[6] * sin;
        data[3] = temp[3] * cos + temp[7] * sin;
        
        data[4] = temp[0] * -sin + temp[4] * cos;
        data[5] = temp[1] * -sin + temp[5] * cos;
        data[6] = temp[2] * -sin + temp[6] * cos;
        data[7] = temp[3] * -sin + temp[7] * cos;
        return this;
    }
    
    /**
     * Rotate this matrix by a quaternion
     */
    public Matrix4 rotate(com.bothengine.components.Quaternion q) {
        float x = q.x, y = q.y, z = q.z, w = q.w;
        float xx = x * x, yy = y * y, zz = z * z;
        float xy = x * y, xz = x * z, yz = y * z;
        float wx = w * x, wy = w * y, wz = w * z;

        float[] r = new float[SIZE];
        r[0]  = 1 - 2 * (yy + zz);
        r[1]  = 2 * (xy - wz);
        r[2]  = 2 * (xz + wy);
        r[4]  = 2 * (xy + wz);
        r[5]  = 1 - 2 * (xx + zz);
        r[6]  = 2 * (yz - wx);
        r[8]  = 2 * (xz - wy);
        r[9]  = 2 * (yz + wx);
        r[10] = 1 - 2 * (xx + yy);
        r[15] = 1;

        Matrix4 rotation = new Matrix4(r);
        multiply(rotation);
        return this;
    }

    /**
     * Create a scale matrix
     */
    public static Matrix4 scaling(Vector3 vector) {
        return new Matrix4().scale(vector);
    }
    
    /**
     * Scale this matrix
     */
    public Matrix4 scale(Vector3 vector) {
        float x = vector.x;
        float y = vector.y;
        float z = vector.z;
        
        data[0] *= x;
        data[1] *= x;
        data[2] *= x;
        data[3] *= x;
        
        data[4] *= y;
        data[5] *= y;
        data[6] *= y;
        data[7] *= y;
        
        data[8] *= z;
        data[9] *= z;
        data[10] *= z;
        data[11] *= z;
        return this;
    }
    
    /**
     * Create a perspective projection matrix
     */
    public static Matrix4 perspective(float fov, float aspect, float near, float far) {
        Matrix4 matrix = new Matrix4();
        
        float f = 1.0f / (float) Math.tan(Math.toRadians(fov) / 2);
        float range = 1.0f / (near - far);
        
        matrix.data[0] = f / aspect;
        matrix.data[5] = f;
        matrix.data[10] = (far + near) * range;
        matrix.data[11] = -1;
        matrix.data[14] = (2 * far * near) * range;
        matrix.data[15] = 0;
        
        return matrix;
    }
    
    /**
     * Create an orthographic projection matrix
     */
    public static Matrix4 orthographic(float left, float right, float bottom, float top, float near, float far) {
        Matrix4 matrix = new Matrix4();
        
        float width = right - left;
        float height = top - bottom;
        float depth = far - near;
        
        matrix.data[0] = 2 / width;
        matrix.data[5] = 2 / height;
        matrix.data[10] = -2 / depth;
        
        matrix.data[12] = -(right + left) / width;
        matrix.data[13] = -(top + bottom) / height;
        matrix.data[14] = -(far + near) / depth;
        matrix.data[15] = 1;
        
        return matrix;
    }
    
    /**
     * Create a look-at matrix
     */
    public static Matrix4 lookAt(Vector3 eye, Vector3 center, Vector3 up) {
        Vector3 f = Vector3.subtract(center, eye).normalize();
        Vector3 s = Vector3.cross(f, up).normalize();
        Vector3 u = Vector3.cross(s, f);
        
        Matrix4 matrix = new Matrix4();
        
        matrix.data[0] = s.x;
        matrix.data[1] = u.x;
        matrix.data[2] = -f.x;
        matrix.data[3] = 0;
        
        matrix.data[4] = s.y;
        matrix.data[5] = u.y;
        matrix.data[6] = -f.y;
        matrix.data[7] = 0;
        
        matrix.data[8] = s.z;
        matrix.data[9] = u.z;
        matrix.data[10] = -f.z;
        matrix.data[11] = 0;
        
        matrix.data[12] = -Vector3.dot(s, eye);
        matrix.data[13] = -Vector3.dot(u, eye);
        matrix.data[14] = Vector3.dot(f, eye);
        matrix.data[15] = 1;
        
        return matrix;
    }
    
    /**
     * Multiply two matrices
     */
    public static Matrix4 multiply(Matrix4 a, Matrix4 b) {
        Matrix4 result = new Matrix4();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[i * 4 + j] = 0;
                for (int k = 0; k < 4; k++) {
                    result.data[i * 4 + j] += a.data[i * 4 + k] * b.data[k * 4 + j];
                }
            }
        }
        
        return result;
    }
    
    /**
     * Multiply this matrix by another
     */
    public Matrix4 multiply(Matrix4 other) {
        float[] temp = new float[SIZE];
        System.arraycopy(data, 0, temp, 0, SIZE);
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                data[i * 4 + j] = 0;
                for (int k = 0; k < 4; k++) {
                    data[i * 4 + j] += temp[i * 4 + k] * other.data[k * 4 + j];
                }
            }
        }
        return this;
    }
    
    /**
     * Transform a vector by this matrix
     */
    public static Vector3 transform(Matrix4 matrix, Vector3 vector) {
        float x = vector.x;
        float y = vector.y;
        float z = vector.z;
        
        float w = matrix.data[3] * x + matrix.data[7] * y + matrix.data[11] * z + matrix.data[15];
        
        if (w != 0 && w != 1) {
            w = 1 / w;
        }
        
        return new Vector3(
            (matrix.data[0] * x + matrix.data[4] * y + matrix.data[8] * z + matrix.data[12]) * w,
            (matrix.data[1] * x + matrix.data[5] * y + matrix.data[9] * z + matrix.data[13]) * w,
            (matrix.data[2] * x + matrix.data[6] * y + matrix.data[10] * z + matrix.data[14]) * w
        );
    }
    
    /**
     * Transform a vector by this matrix (no perspective division)
     */
    public static Vector4 transform(Matrix4 matrix, Vector4 vector) {
        return new Vector4(
            matrix.data[0] * vector.x + matrix.data[4] * vector.y + matrix.data[8] * vector.z + matrix.data[12] * vector.w,
            matrix.data[1] * vector.x + matrix.data[5] * vector.y + matrix.data[9] * vector.z + matrix.data[13] * vector.w,
            matrix.data[2] * vector.x + matrix.data[6] * vector.y + matrix.data[10] * vector.z + matrix.data[14] * vector.w,
            matrix.data[3] * vector.x + matrix.data[7] * vector.y + matrix.data[11] * vector.z + matrix.data[15] * vector.w
        );
    }
    
    /**
     * Transpose this matrix
     */
    public Matrix4 transpose() {
        Matrix4 result = new Matrix4();
        
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                result.data[i * 4 + j] = data[j * 4 + i];
            }
        }
        
        return result;
    }
    
    /**
     * Invert this matrix
     */
    public Matrix4 invert() {
        float a00 = data[0], a01 = data[1], a02 = data[2], a03 = data[3];
        float a10 = data[4], a11 = data[5], a12 = data[6], a13 = data[7];
        float a20 = data[8], a21 = data[9], a22 = data[10], a23 = data[11];
        float a30 = data[12], a31 = data[13], a32 = data[14], a33 = data[15];
        
        float b00 = a00 * a11 - a01 * a10;
        float b01 = a00 * a12 - a02 * a10;
        float b02 = a00 * a13 - a03 * a10;
        float b03 = a01 * a12 - a02 * a11;
        float b04 = a01 * a13 - a03 * a11;
        float b05 = a02 * a13 - a03 * a12;
        float b06 = a20 * a31 - a21 * a30;
        float b07 = a20 * a32 - a22 * a30;
        float b08 = a20 * a33 - a23 * a30;
        float b09 = a21 * a32 - a22 * a31;
        float b10 = a21 * a33 - a23 * a31;
        float b11 = a22 * a33 - a23 * a32;
        
        float det = b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06;
        
        if (Math.abs(det) < 1e-10f) {
            return null; // Matrix is not invertible
        }
        
        float invDet = 1.0f / det;
        
        Matrix4 result = new Matrix4();
        
        result.data[0] = (a11 * b11 - a12 * b10 + a13 * b09) * invDet;
        result.data[1] = (a02 * b10 - a01 * b11 - a03 * b09) * invDet;
        result.data[2] = (a31 * b05 - a32 * b04 + a33 * b03) * invDet;
        result.data[3] = (a22 * b04 - a21 * b05 - a23 * b03) * invDet;
        
        result.data[4] = (a12 * b08 - a10 * b11 - a13 * b07) * invDet;
        result.data[5] = (a00 * b11 - a02 * b08 + a03 * b07) * invDet;
        result.data[6] = (a32 * b02 - a30 * b05 - a33 * b01) * invDet;
        result.data[7] = (a20 * b05 - a22 * b02 + a23 * b01) * invDet;
        
        result.data[8] = (a10 * b10 - a11 * b08 + a13 * b06) * invDet;
        result.data[9] = (a01 * b08 - a00 * b10 - a03 * b06) * invDet;
        result.data[10] = (a30 * b04 - a31 * b02 + a33 * b00) * invDet;
        result.data[11] = (a21 * b02 - a20 * b04 - a23 * b00) * invDet;
        
        result.data[12] = (a11 * b07 - a10 * b09 - a12 * b06) * invDet;
        result.data[13] = (a00 * b09 - a01 * b07 + a02 * b06) * invDet;
        result.data[14] = (a31 * b01 - a30 * b03 - a32 * b00) * invDet;
        result.data[15] = (a20 * b03 - a21 * b01 + a22 * b00) * invDet;
        
        return result;
    }
    
    /**
     * Copy this matrix
     */
    public Matrix4 copy() {
        return new Matrix4(data);
    }
    
    /**
     * Check if this matrix equals another
     */
    public boolean equals(Matrix4 other) {
        if (other == null) return false;
        for (int i = 0; i < SIZE; i++) {
            if (Math.abs(data[i] - other.data[i]) > 1e-6f) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Matrix4[\n");
        for (int i = 0; i < 4; i++) {
            sb.append("  ");
            for (int j = 0; j < 4; j++) {
                sb.append(String.format("%.4f", data[i * 4 + j]));
                if (j < 3) sb.append(", ");
            }
            sb.append("\n");
        }
        sb.append("]");
        return sb.toString();
    }
}
