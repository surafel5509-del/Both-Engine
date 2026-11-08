package com.bothengine.rendering;

import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Mesh
 * Represents a 3D mesh with vertices, normals, UVs, etc.
 */
public class Mesh {
    private String name;
    private VertexData vertexData;
    private int vertexCount;
    private int indexCount;
    private int vbo;
    private int ibo;
    private boolean indexed;
    private Bounds bounds;
    
    public Mesh() {
        this.name = "Unnamed";
        this.vertexData = new VertexData();
        this.vertexCount = 0;
        this.indexCount = 0;
        this.vbo = 0;
        this.ibo = 0;
        this.indexed = false;
        this.bounds = new Bounds();
    }
    
    public Mesh(String name) {
        this();
        this.name = name;
    }
    
    /**
     * Create a mesh with vertices
     */
    public static Mesh createWithVertices(String name, float[] vertices) {
        Mesh mesh = new Mesh(name);
        mesh.setVertices(vertices);
        return mesh;
    }
    
    /**
     * Create a mesh with vertices and indices
     */
    public static Mesh createWithIndices(String name, float[] vertices, short[] indices) {
        Mesh mesh = new Mesh(name);
        mesh.setVertices(vertices);
        mesh.setIndices(indices);
        mesh.indexed = true;
        return mesh;
    }
    
    /**
     * Set vertices
     */
    public void setVertices(float[] vertices) {
        vertexData.vertices = vertices;
        vertexCount = vertices.length / vertexData.getVertexSize();
        calculateBounds();
    }
    
    /**
     * Set indices
     */
    public void setIndices(short[] indices) {
        vertexData.indices = indices;
        indexCount = indices.length;
        indexed = true;
    }
    
    /**
     * Set normals
     */
    public void setNormals(float[] normals) {
        vertexData.normals = normals;
    }
    
    /**
     * Set texture coordinates
     */
    public void setTexCoords(float[] texCoords) {
        vertexData.texCoords = texCoords;
    }
    
    /**
     * Set colors
     */
    public void setColors(float[] colors) {
        vertexData.colors = colors;
    }
    
    /**
     * Set tangents
     */
    public void setTangents(float[] tangents) {
        vertexData.tangents = tangents;
    }
    
    /**
     * Calculate bounds from vertices
     */
    private void calculateBounds() {
        if (vertexData.vertices == null || vertexData.vertices.length == 0) {
            bounds = new Bounds();
            return;
        }
        
        float minX = Float.POSITIVE_INFINITY;
        float minY = Float.POSITIVE_INFINITY;
        float minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY;
        float maxY = Float.NEGATIVE_INFINITY;
        float maxZ = Float.NEGATIVE_INFINITY;
        
        int vertexSize = vertexData.getVertexSize();
        
        for (int i = 0; i < vertexData.vertices.length; i += vertexSize) {
            float x = vertexData.vertices[i];
            float y = vertexData.vertices[i + 1];
            float z = vertexData.vertices[i + 2];
            
            minX = Math.min(minX, x);
            minY = Math.min(minY, y);
            minZ = Math.min(minZ, z);
            maxX = Math.max(maxX, x);
            maxY = Math.max(maxY, y);
            maxZ = Math.max(maxZ, z);
        }
        
        bounds = new Bounds(
            new Vector3(minX, minY, minZ),
            new Vector3(maxX, maxY, maxZ)
        );
    }
    
    /**
     * Upload mesh data to GPU
     */
    public void uploadToGPU() {
        if (vbo != 0) {
            GLES20.glDeleteBuffers(1, new int[] { vbo }, 0);
            vbo = 0;
        }
        
        if (ibo != 0) {
            GLES20.glDeleteBuffers(1, new int[] { ibo }, 0);
            ibo = 0;
        }
        
        // Create and upload VBO
        float[] interleavedData = vertexData.getInterleavedData();
        
        int[] vboArray = new int[1];
        GLES20.glGenBuffers(1, vboArray, 0);
        vbo = vboArray[0];
        
        FloatBuffer vertexBuffer = GLES20Helper.createFloatBuffer(interleavedData);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
        GLES20.glBufferData(GLES20.GL_ARRAY_BUFFER, interleavedData.length * 4, vertexBuffer, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
        
        // Create and upload IBO if indexed
        if (indexed && vertexData.indices != null && vertexData.indices.length > 0) {
            int[] iboArray = new int[1];
            GLES20.glGenBuffers(1, iboArray, 0);
            ibo = iboArray[0];
            
            ShortBuffer indexBuffer = GLES20Helper.createShortBuffer(vertexData.indices);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo);
            GLES20.glBufferData(GLES20.GL_ELEMENT_ARRAY_BUFFER, vertexData.indices.length * 2, indexBuffer, GLES20.GL_STATIC_DRAW);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        }
    }
    
    /**
     * Render the mesh
     */
    public void render() {
        if (vbo == 0) {
            uploadToGPU();
        }
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vbo);
        
        // Enable vertex attributes
        int offset = 0;
        int stride = vertexData.getStride();
        
        // Position attribute
        if (vertexData.hasVertices()) {
            GLES20.glVertexAttribPointer(0, 3, GLES20.GL_FLOAT, false, stride, offset);
            GLES20.glEnableVertexAttribArray(0);
            offset += 12; // 3 floats * 4 bytes
        }
        
        // Normal attribute
        if (vertexData.hasNormals()) {
            GLES20.glVertexAttribPointer(1, 3, GLES20.GL_FLOAT, false, stride, offset);
            GLES20.glEnableVertexAttribArray(1);
            offset += 12;
        }
        
        // Texture coordinate attribute
        if (vertexData.hasTexCoords()) {
            GLES20.glVertexAttribPointer(2, 2, GLES20.GL_FLOAT, false, stride, offset);
            GLES20.glEnableVertexAttribArray(2);
            offset += 8; // 2 floats * 4 bytes
        }
        
        // Color attribute
        if (vertexData.hasColors()) {
            GLES20.glVertexAttribPointer(3, 4, GLES20.GL_FLOAT, false, stride, offset);
            GLES20.glEnableVertexAttribArray(3);
            offset += 16; // 4 floats * 4 bytes
        }
        
        // Draw
        if (indexed && ibo != 0) {
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, ibo);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, indexCount, GLES20.GL_UNSIGNED_SHORT, 0);
            GLES20.glBindBuffer(GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
        } else {
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);
        }
        
        // Disable vertex attributes
        if (vertexData.hasVertices()) {
            GLES20.glDisableVertexAttribArray(0);
        }
        if (vertexData.hasNormals()) {
            GLES20.glDisableVertexAttribArray(1);
        }
        if (vertexData.hasTexCoords()) {
            GLES20.glDisableVertexAttribArray(2);
        }
        if (vertexData.hasColors()) {
            GLES20.glDisableVertexAttribArray(3);
        }
        
        GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
    }
    
    /**
     * Delete GPU resources
     */
    public void delete() {
        if (vbo != 0) {
            GLES20.glDeleteBuffers(1, new int[] { vbo }, 0);
            vbo = 0;
        }
        
        if (ibo != 0) {
            GLES20.glDeleteBuffers(1, new int[] { ibo }, 0);
            ibo = 0;
        }
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public VertexData getVertexData() {
        return vertexData;
    }
    
    public int getVertexCount() {
        return vertexCount;
    }
    
    public int getIndexCount() {
        return indexCount;
    }
    
    public int getVbo() {
        return vbo;
    }
    
    public int getIbo() {
        return ibo;
    }
    
    public boolean isIndexed() {
        return indexed;
    }
    
    public Bounds getBounds() {
        return bounds;
    }
    
    @Override
    public String toString() {
        return "Mesh{" +
                "name='" + name + '\'' +
                ", vertexCount=" + vertexCount +
                ", indexCount=" + indexCount +
                ", indexed=" + indexed +
                '}';
    }
    
    /**
     * VertexData class to hold all vertex attributes
     */
    public static class VertexData {
        public float[] vertices;
        public float[] normals;
        public float[] texCoords;
        public float[] colors;
        public float[] tangents;
        public short[] indices;
        
        public VertexData() {
        }
        
        /**
         * Get the size of each vertex in floats
         */
        public int getVertexSize() {
            int size = 0;
            if (hasVertices()) size += 3;
            if (hasNormals()) size += 3;
            if (hasTexCoords()) size += 2;
            if (hasColors()) size += 4;
            if (hasTangents()) size += 4;
            return size;
        }
        
        /**
         * Get the stride in bytes
         */
        public int getStride() {
            return getVertexSize() * 4; // 4 bytes per float
        }
        
        /**
         * Check if vertices are present
         */
        public boolean hasVertices() {
            return vertices != null && vertices.length > 0;
        }
        
        /**
         * Check if normals are present
         */
        public boolean hasNormals() {
            return normals != null && normals.length > 0;
        }
        
        /**
         * Check if texture coordinates are present
         */
        public boolean hasTexCoords() {
            return texCoords != null && texCoords.length > 0;
        }
        
        /**
         * Check if colors are present
         */
        public boolean hasColors() {
            return colors != null && colors.length > 0;
        }
        
        /**
         * Check if tangents are present
         */
        public boolean hasTangents() {
            return tangents != null && tangents.length > 0;
        }
        
        /**
         * Get interleaved vertex data
         */
        public float[] getInterleavedData() {
            List<Float> interleaved = new ArrayList<>();
            int vertexCount = vertices != null ? vertices.length / 3 : 0;
            
            for (int i = 0; i < vertexCount; i++) {
                // Position
                if (hasVertices()) {
                    interleaved.add(vertices[i * 3]);
                    interleaved.add(vertices[i * 3 + 1]);
                    interleaved.add(vertices[i * 3 + 2]);
                }
                
                // Normal
                if (hasNormals()) {
                    interleaved.add(normals[i * 3]);
                    interleaved.add(normals[i * 3 + 1]);
                    interleaved.add(normals[i * 3 + 2]);
                }
                
                // Texture coordinate
                if (hasTexCoords()) {
                    interleaved.add(texCoords[i * 2]);
                    interleaved.add(texCoords[i * 2 + 1]);
                }
                
                // Color
                if (hasColors()) {
                    interleaved.add(colors[i * 4]);
                    interleaved.add(colors[i * 4 + 1]);
                    interleaved.add(colors[i * 4 + 2]);
                    interleaved.add(colors[i * 4 + 3]);
                }
                
                // Tangent
                if (hasTangents()) {
                    interleaved.add(tangents[i * 4]);
                    interleaved.add(tangents[i * 4 + 1]);
                    interleaved.add(tangents[i * 4 + 2]);
                    interleaved.add(tangents[i * 4 + 3]);
                }
            }
            
            float[] result = new float[interleaved.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = interleaved.get(i);
            }
            
            return result;
        }
    }
}
