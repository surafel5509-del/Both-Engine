package com.bothengine.assets;

import android.content.Context;
import android.util.Log;

import com.bothengine.rendering.Mesh;
import com.bothengine.rendering.Vector2;
import com.bothengine.rendering.Vector3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * MeshLoader
 * Loads 3D mesh data from various file formats
 */
public class MeshLoader {
    private static final String TAG = "MeshLoader";
    
    /**
     * Load a mesh from an OBJ file
     */
    public static boolean loadOBJ(Context context, String path, Mesh mesh) {
        try {
            InputStream inputStream = context.getAssets().open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            
            List<Vector3> positions = new ArrayList<>();
            List<Vector3> normals = new ArrayList<>();
            List<Vector2> texCoords = new ArrayList<>();
            List<Face> faces = new ArrayList<>();
            
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] tokens = line.split("\\s+");
                String type = tokens[0];
                
                switch (type) {
                    case "v": // Vertex position
                        if (tokens.length >= 4) {
                            float x = Float.parseFloat(tokens[1]);
                            float y = Float.parseFloat(tokens[2]);
                            float z = Float.parseFloat(tokens[3]);
                            positions.add(new Vector3(x, y, z));
                        }
                        break;
                        
                    case "vn": // Vertex normal
                        if (tokens.length >= 4) {
                            float x = Float.parseFloat(tokens[1]);
                            float y = Float.parseFloat(tokens[2]);
                            float z = Float.parseFloat(tokens[3]);
                            normals.add(new Vector3(x, y, z));
                        }
                        break;
                        
                    case "vt": // Texture coordinate
                        if (tokens.length >= 3) {
                            float u = Float.parseFloat(tokens[1]);
                            float v = Float.parseFloat(tokens[2]);
                            texCoords.add(new Vector2(u, 1 - v)); // Flip V coordinate
                        }
                        break;
                        
                    case "f": // Face
                        if (tokens.length >= 4) {
                            Face face = new Face();
                            for (int i = 1; i < tokens.length; i++) {
                                String vertex = tokens[i];
                                String[] vertexTokens = vertex.split("/");
                                
                                int positionIndex = Integer.parseInt(vertexTokens[0]);
                                face.positionIndices.add(positionIndex);
                                
                                if (vertexTokens.length > 1 && !vertexTokens[1].isEmpty()) {
                                    int texCoordIndex = Integer.parseInt(vertexTokens[1]);
                                    face.texCoordIndices.add(texCoordIndex);
                                }
                                
                                if (vertexTokens.length > 2 && !vertexTokens[2].isEmpty()) {
                                    int normalIndex = Integer.parseInt(vertexTokens[2]);
                                    face.normalIndices.add(normalIndex);
                                }
                            }
                            faces.add(face);
                        }
                        break;
                }
            }
            
            reader.close();
            inputStream.close();
            
            // Build mesh data
            if (!buildMeshData(mesh, positions, normals, texCoords, faces)) {
                return false;
            }
            
            Log.d(TAG, "OBJ loaded: " + path + " (" + positions.size() + " vertices, " + faces.size() + " faces)");
            return true;
            
        } catch (IOException e) {
            Log.e(TAG, "Error loading OBJ file: " + path, e);
            return false;
        }
    }
    
    /**
     * Build mesh data from loaded OBJ data
     */
    private static boolean buildMeshData(Mesh mesh, List<Vector3> positions, 
                                         List<Vector3> normals, List<Vector2> texCoords, 
                                         List<Face> faces) {
        
        if (positions.isEmpty()) {
            Log.e(TAG, "No vertices in mesh");
            return false;
        }
        
        // Calculate total vertices needed
        int vertexCount = 0;
        for (Face face : faces) {
            vertexCount += face.positionIndices.size();
        }
        
        // Build arrays
        float[] vertices = new float[vertexCount * 3];
        float[] normalsArray = normals.isEmpty() ? null : new float[vertexCount * 3];
        float[] texCoordsArray = texCoords.isEmpty() ? null : new float[vertexCount * 2];
        short[] indices = new short[vertexCount];
        
        int vertexIndex = 0;
        int indexIndex = 0;
        
        for (Face face : faces) {
            for (int i = 0; i < face.positionIndices.size(); i++) {
                // Position
                Vector3 position = positions.get(face.positionIndices.get(i) - 1);
                vertices[vertexIndex * 3] = position.x;
                vertices[vertexIndex * 3 + 1] = position.y;
                vertices[vertexIndex * 3 + 2] = position.z;
                
                // Normal
                if (normalsArray != null) {
                    Vector3 normal = normals.get(face.normalIndices.get(i) - 1);
                    normalsArray[vertexIndex * 3] = normal.x;
                    normalsArray[vertexIndex * 3 + 1] = normal.y;
                    normalsArray[vertexIndex * 3 + 2] = normal.z;
                }
                
                // Texture coordinate
                if (texCoordsArray != null) {
                    Vector2 texCoord = texCoords.get(face.texCoordIndices.get(i) - 1);
                    texCoordsArray[vertexIndex * 2] = texCoord.x;
                    texCoordsArray[vertexIndex * 2 + 1] = texCoord.y;
                }
                
                indices[indexIndex++] = (short) vertexIndex;
                vertexIndex++;
            }
        }
        
        mesh.setVertices(vertices);
        if (normalsArray != null) {
            mesh.setNormals(normalsArray);
        }
        if (texCoordsArray != null) {
            mesh.setTexCoords(texCoordsArray);
        }
        mesh.setIndices(indices);
        
        return true;
    }
    
    /**
     * Face class for OBJ loading
     */
    private static class Face {
        List<Integer> positionIndices = new ArrayList<>();
        List<Integer> texCoordIndices = new ArrayList<>();
        List<Integer> normalIndices = new ArrayList<>();
    }
}
