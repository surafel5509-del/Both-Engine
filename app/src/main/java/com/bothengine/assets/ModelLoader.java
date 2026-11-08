package com.bothengine.assets;

import android.content.Context;
import android.util.Log;

import com.bothengine.rendering.Mesh;
import com.bothengine.rendering.Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * ModelLoader
 * Loads 3D models from various file formats
 */
public class ModelLoader {
    private static final String TAG = "ModelLoader";
    
    /**
     * Load a model from a file
     * Currently supports OBJ files
     */
    public static boolean loadModel(Context context, String path, Model model) {
        // Check file extension
        if (path.toLowerCase().endsWith(".obj")) {
            return loadOBJModel(context, path, model);
        }
        
        Log.e(TAG, "Unsupported model format: " + path);
        return false;
    }
    
    /**
     * Load a model from an OBJ file
     */
    private static boolean loadOBJModel(Context context, String path, Model model) {
        try {
            // For now, just load a single mesh
            // In the future, this will support multi-mesh models
            Mesh mesh = new Mesh(model.getName());
            boolean success = MeshLoader.loadOBJ(context, path, mesh);
            
            if (success) {
                model.addMesh(mesh);
                Log.d(TAG, "Model loaded: " + model.getName());
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading model: " + path, e);
            return false;
        }
    }
}
