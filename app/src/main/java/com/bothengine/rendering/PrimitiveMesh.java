package com.bothengine.rendering;

/**
 * PrimitiveMesh
 * Factory for creating primitive 3D meshes
 */
public class PrimitiveMesh {
    
    /**
     * Create a primitive mesh
     */
    public static Mesh createPrimitive(Renderer3D.PrimitiveType type) {
        switch (type) {
            case CUBE:
                return createCube();
            case SPHERE:
                return createSphere(16, 16);
            case CYLINDER:
                return createCylinder(16);
            case PLANE:
                return createPlane();
            case QUAD:
                return createQuad();
            default:
                return null;
        }
    }
    
    /**
     * Create a cube mesh
     */
    public static Mesh createCube() {
        return createCube(1, 1, 1);
    }
    
    /**
     * Create a cube mesh with specified size
     */
    public static Mesh createCube(float width, float height, float depth) {
        Mesh mesh = new Mesh("Cube");
        
        float hw = width / 2;
        float hh = height / 2;
        float hd = depth / 2;
        
        // Vertices (x, y, z)
        float[] vertices = {
            // Front face
            -hw, -hh, hd,
             hw, -hh, hd,
             hw,  hh, hd,
            -hw,  hh, hd,
            
            // Back face
            -hw, -hh, -hd,
             hw, -hh, -hd,
             hw,  hh, -hd,
            -hw,  hh, -hd,
            
            // Top face
            -hw, hh, -hd,
             hw, hh, -hd,
             hw, hh,  hd,
            -hw, hh,  hd,
            
            // Bottom face
            -hw, -hh, -hd,
             hw, -hh, -hd,
             hw, -hh,  hd,
            -hw, -hh,  hd,
            
            // Right face
             hw, -hh, -hd,
             hw,  hh, -hd,
             hw,  hh,  hd,
             hw, -hh,  hd,
            
            // Left face
            -hw, -hh, -hd,
            -hw,  hh, -hd,
            -hw,  hh,  hd,
            -hw, -hh,  hd
        };
        
        // Normals
        float[] normals = {
            // Front face
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            
            // Back face
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            0, 0, -1,
            
            // Top face
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            0, 1, 0,
            
            // Bottom face
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            0, -1, 0,
            
            // Right face
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            1, 0, 0,
            
            // Left face
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0,
            -1, 0, 0
        };
        
        // Texture coordinates
        float[] texCoords = {
            // Front face
            0, 0,
            1, 0,
            1, 1,
            0, 1,
            
            // Back face
            1, 0,
            0, 0,
            0, 1,
            1, 1,
            
            // Top face
            0, 1,
            1, 1,
            1, 0,
            0, 0,
            
            // Bottom face
            1, 1,
            0, 1,
            0, 0,
            1, 0,
            
            // Right face
            0, 0,
            0, 1,
            1, 1,
            1, 0,
            
            // Left face
            1, 0,
            1, 1,
            0, 1,
            0, 0
        };
        
        // Indices
        short[] indices = {
            // Front face
            0, 1, 2, 0, 2, 3,
            // Back face
            4, 6, 5, 4, 7, 6,
            // Top face
            8, 10, 9, 8, 11, 10,
            // Bottom face
            12, 14, 13, 12, 15, 14,
            // Right face
            16, 18, 17, 16, 19, 18,
            // Left face
            20, 22, 21, 20, 23, 22
        };
        
        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setIndices(indices);
        mesh.uploadToGPU();
        
        return mesh;
    }
    
    /**
     * Create a sphere mesh
     */
    public static Mesh createSphere() {
        return createSphere(16, 16);
    }
    
    /**
     * Create a sphere mesh with specified segments
     */
    public static Mesh createSphere(int latitudeSegments, int longitudeSegments) {
        Mesh mesh = new Mesh("Sphere");
        
        int vertexCount = (latitudeSegments + 1) * (longitudeSegments + 1);
        int indexCount = latitudeSegments * longitudeSegments * 6;
        
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        short[] indices = new short[indexCount];
        
        int vIndex = 0;
        int tIndex = 0;
        
        // Generate vertices
        for (int lat = 0; lat <= latitudeSegments; lat++) {
            float theta = (float) (lat * Math.PI / latitudeSegments);
            float sinTheta = (float) Math.sin(theta);
            float cosTheta = (float) Math.cos(theta);
            
            for (int lon = 0; lon <= longitudeSegments; lon++) {
                float phi = (float) (lon * 2 * Math.PI / longitudeSegments);
                float sinPhi = (float) Math.sin(phi);
                float cosPhi = (float) Math.cos(phi);
                
                // Position
                vertices[vIndex] = cosPhi * sinTheta;
                vertices[vIndex + 1] = cosTheta;
                vertices[vIndex + 2] = sinPhi * sinTheta;
                
                // Normal (same as position for unit sphere)
                normals[vIndex] = vertices[vIndex];
                normals[vIndex + 1] = vertices[vIndex + 1];
                normals[vIndex + 2] = vertices[vIndex + 2];
                
                // Texture coordinates
                texCoords[tIndex] = (float) lon / longitudeSegments;
                texCoords[tIndex + 1] = (float) lat / latitudeSegments;
                
                vIndex += 3;
                tIndex += 2;
            }
        }
        
        // Generate indices
        int iIndex = 0;
        for (int lat = 0; lat < latitudeSegments; lat++) {
            for (int lon = 0; lon < longitudeSegments; lon++) {
                int first = lat * (longitudeSegments + 1) + lon;
                int second = first + longitudeSegments + 1;
                
                indices[iIndex++] = (short) first;
                indices[iIndex++] = (short) (first + 1);
                indices[iIndex++] = (short) (second + 1);
                
                indices[iIndex++] = (short) first;
                indices[iIndex++] = (short) (second + 1);
                indices[iIndex++] = (short) second;
            }
        }
        
        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setIndices(indices);
        mesh.uploadToGPU();
        
        return mesh;
    }
    
    /**
     * Create a cylinder mesh
     */
    public static Mesh createCylinder() {
        return createCylinder(16);
    }
    
    /**
     * Create a cylinder mesh with specified segments
     */
    public static Mesh createCylinder(int segments) {
        Mesh mesh = new Mesh("Cylinder");
        
        int vertexCount = (segments + 1) * 2 + 2;
        int indexCount = segments * 6 + segments * 3 * 2;
        
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        short[] indices = new short[indexCount];
        
        // Top and bottom vertices
        vertices[0] = 0; vertices[1] = 1; vertices[2] = 0; // Top center
        vertices[3] = 0; vertices[4] = -1; vertices[5] = 0; // Bottom center
        
        normals[0] = 0; normals[1] = 1; normals[2] = 0; // Top normal
        normals[3] = 0; normals[4] = -1; normals[5] = 0; // Bottom normal
        
        texCoords[0] = 0.5f; texCoords[1] = 1; // Top center UV
        texCoords[2] = 0.5f; texCoords[3] = 0; // Bottom center UV
        
        int vIndex = 2;
        int nIndex = 2;
        int tIndex = 2;
        
        // Side vertices
        for (int i = 0; i <= segments; i++) {
            float angle = (float) (i * 2 * Math.PI / segments);
            float x = (float) Math.cos(angle);
            float z = (float) Math.sin(angle);
            
            // Top vertex
            vertices[vIndex * 3] = x;
            vertices[vIndex * 3 + 1] = 1;
            vertices[vIndex * 3 + 2] = z;
            
            normals[nIndex * 3] = x;
            normals[nIndex * 3 + 1] = 0;
            normals[nIndex * 3 + 2] = z;
            
            texCoords[tIndex * 2] = (float) i / segments;
            texCoords[tIndex * 2 + 1] = 1;
            
            vIndex++;
            nIndex++;
            tIndex++;
            
            // Bottom vertex
            vertices[vIndex * 3] = x;
            vertices[vIndex * 3 + 1] = -1;
            vertices[vIndex * 3 + 2] = z;
            
            normals[nIndex * 3] = x;
            normals[nIndex * 3 + 1] = 0;
            normals[nIndex * 3 + 2] = z;
            
            texCoords[tIndex * 2] = (float) i / segments;
            texCoords[tIndex * 2 + 1] = 0;
            
            vIndex++;
            nIndex++;
            tIndex++;
        }
        
        // Generate indices for sides
        int iIndex = 0;
        for (int i = 0; i < segments; i++) {
            int topLeft = 2 + i * 2;
            int topRight = 2 + (i + 1) * 2;
            int bottomLeft = topLeft + 1;
            int bottomRight = topRight + 1;
            
            // Side triangles
            indices[iIndex++] = (short) topLeft;
            indices[iIndex++] = (short) bottomLeft;
            indices[iIndex++] = (short) bottomRight;
            
            indices[iIndex++] = (short) topLeft;
            indices[iIndex++] = (short) bottomRight;
            indices[iIndex++] = (short) topRight;
            
            // Top triangles
            int topCenter = 0;
            indices[iIndex++] = (short) topCenter;
            indices[iIndex++] = (short) topRight;
            indices[iIndex++] = (short) topLeft;
            
            // Bottom triangles
            int bottomCenter = 1;
            indices[iIndex++] = (short) bottomCenter;
            indices[iIndex++] = (short) bottomLeft;
            indices[iIndex++] = (short) bottomRight;
        }
        
        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setIndices(indices);
        mesh.uploadToGPU();
        
        return mesh;
    }
    
    /**
     * Create a plane mesh
     */
    public static Mesh createPlane() {
        return createPlane(1, 1, 1, 1);
    }
    
    /**
     * Create a plane mesh with specified size
     */
    public static Mesh createPlane(float width, float height, int widthSegments, int heightSegments) {
        Mesh mesh = new Mesh("Plane");
        
        int vertexCount = (widthSegments + 1) * (heightSegments + 1);
        int indexCount = widthSegments * heightSegments * 6;
        
        float[] vertices = new float[vertexCount * 3];
        float[] normals = new float[vertexCount * 3];
        float[] texCoords = new float[vertexCount * 2];
        short[] indices = new short[indexCount];
        
        float halfWidth = width / 2;
        float halfHeight = height / 2;
        
        int vIndex = 0;
        int nIndex = 0;
        int tIndex = 0;
        
        for (int y = 0; y <= heightSegments; y++) {
            float v = (float) y / heightSegments;
            float yPos = (v - 0.5f) * height;
            
            for (int x = 0; x <= widthSegments; x++) {
                float u = (float) x / widthSegments;
                float xPos = (u - 0.5f) * width;
                
                vertices[vIndex * 3] = xPos;
                vertices[vIndex * 3 + 1] = 0;
                vertices[vIndex * 3 + 2] = yPos;
                
                normals[nIndex * 3] = 0;
                normals[nIndex * 3 + 1] = 1;
                normals[nIndex * 3 + 2] = 0;
                
                texCoords[tIndex * 2] = u;
                texCoords[tIndex * 2 + 1] = 1 - v;
                
                vIndex++;
                nIndex++;
                tIndex++;
            }
        }
        
        int iIndex = 0;
        for (int y = 0; y < heightSegments; y++) {
            for (int x = 0; x < widthSegments; x++) {
                int first = y * (widthSegments + 1) + x;
                int second = first + widthSegments + 1;
                
                indices[iIndex++] = (short) first;
                indices[iIndex++] = (short) (first + 1);
                indices[iIndex++] = (short) (second + 1);
                
                indices[iIndex++] = (short) first;
                indices[iIndex++] = (short) (second + 1);
                indices[iIndex++] = (short) second;
            }
        }
        
        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setIndices(indices);
        mesh.uploadToGPU();
        
        return mesh;
    }
    
    /**
     * Create a simple quad mesh
     */
    public static Mesh createQuad() {
        Mesh mesh = new Mesh("Quad");
        
        // Vertices
        float[] vertices = {
            -0.5f, -0.5f, 0,
             0.5f, -0.5f, 0,
             0.5f,  0.5f, 0,
            -0.5f,  0.5f, 0
        };
        
        // Normals
        float[] normals = {
            0, 0, 1,
            0, 0, 1,
            0, 0, 1,
            0, 0, 1
        };
        
        // Texture coordinates
        float[] texCoords = {
            0, 0,
            1, 0,
            1, 1,
            0, 1
        };
        
        // Indices
        short[] indices = {0, 1, 2, 0, 2, 3};
        
        mesh.setVertices(vertices);
        mesh.setNormals(normals);
        mesh.setTexCoords(texCoords);
        mesh.setIndices(indices);
        mesh.uploadToGPU();
        
        return mesh;
    }
}
