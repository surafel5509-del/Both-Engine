package com.bothengine.rendering;

import android.opengl.GLES20;
import android.util.Log;

import com.bothengine.core.EngineConfig;
import com.bothengine.rendering.shaders.Basic3DShader;
import com.bothengine.rendering.shaders.ShaderManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Renderer3D
 * Handles all 3D rendering (meshes, models, etc.)
 */
public class Renderer3D {
    private static final String TAG = "Renderer3D";
    
    private final EngineConfig config;
    private final ShaderManager shaderManager;
    private Basic3DShader shader;
    
    private List<MeshRenderer> renderers;
    private List<Model> models;
    private List<Light> lights;
    
    private Camera camera;
    private Matrix4 projectionMatrix;
    private Matrix4 viewMatrix;
    
    private int objectCount;
    private int drawCalls;
    private int triangleCount;
    
    // Lighting
    private Vector3 ambientLight;
    private boolean useShadows;
    
    public Renderer3D(EngineConfig config, ShaderManager shaderManager) {
        this.config = config;
        this.shaderManager = shaderManager;
        this.shader = shaderManager.getBasic3DShader();
        this.renderers = new ArrayList<>();
        this.models = new ArrayList<>();
        this.lights = new ArrayList<>();
        this.camera = null;
        this.projectionMatrix = new Matrix4();
        this.viewMatrix = new Matrix4();
        this.objectCount = 0;
        this.drawCalls = 0;
        this.triangleCount = 0;
        this.ambientLight = new Vector3(0.1f, 0.1f, 0.1f);
        this.useShadows = false;
        
        Log.i(TAG, "Renderer3D initialized");
    }
    
    /**
     * Initialize the renderer
     */
    public void initialize(Camera camera) {
        this.camera = camera;
        if (camera != null) {
            updateProjection();
        }
    }
    
    /**
     * Update projection matrix from camera
     */
    public void updateProjection() {
        if (camera != null) {
            projectionMatrix = camera.getProjectionMatrix();
            viewMatrix = camera.getViewMatrix();
        }
    }
    
    /**
     * Add a mesh renderer
     */
    public void addRenderer(MeshRenderer renderer) {
        renderers.add(renderer);
    }
    
    /**
     * Remove a mesh renderer
     */
    public void removeRenderer(MeshRenderer renderer) {
        renderers.remove(renderer);
    }
    
    /**
     * Add a model
     */
    public void addModel(Model model) {
        models.add(model);
    }
    
    /**
     * Remove a model
     */
    public void removeModel(Model model) {
        models.remove(model);
    }
    
    /**
     * Add a light
     */
    public void addLight(Light light) {
        lights.add(light);
    }
    
    /**
     * Remove a light
     */
    public void removeLight(Light light) {
        lights.remove(light);
    }
    
    /**
     * Clear all renderers
     */
    public void clearRenderers() {
        renderers.clear();
    }
    
    /**
     * Clear all models
     */
    public void clearModels() {
        models.clear();
    }
    
    /**
     * Clear all lights
     */
    public void clearLights() {
        lights.clear();
    }
    
    /**
     * Render all 3D objects
     */
    public void render(float deltaTime) {
        if (!shader.isCompiled()) {
            Log.w(TAG, "Shader not compiled, skipping render");
            return;
        }
        
        // Reset counters
        objectCount = 0;
        drawCalls = 0;
        triangleCount = 0;
        
        if (camera == null) {
            Log.w(TAG, "No camera set, skipping render");
            return;
        }
        
        updateProjection();
        
        // Use shader
        shader.use();
        
        // Set view-projection matrix
        Matrix4 viewProjection = Matrix4.multiply(projectionMatrix, viewMatrix);
        shader.setUniform("uViewProjectionMatrix", viewProjection.data);
        
        // Set lighting
        if (lights.isEmpty()) {
            // Default directional light
            shader.setUniform("uLightDirection", 0, 1, 0);
            shader.setUniform("uLightColor", 1, 1, 1);
        } else {
            // Use first light (for now)
            Light light = lights.get(0);
            shader.setUniform("uLightDirection", light.getDirection().x, light.getDirection().y, light.getDirection().z);
            shader.setUniform("uLightColor", light.getColor().r, light.getColor().g, light.getColor().b);
        }
        
        shader.setUniform("uAmbientColor", ambientLight.x, ambientLight.y, ambientLight.z);
        
        // Render models
        for (Model model : models) {
            if (model.isVisible()) {
                renderModel(model);
            }
        }
        
        // Render mesh renderers
        for (MeshRenderer renderer : renderers) {
            if (renderer.isVisible()) {
                renderMeshRenderer(renderer);
            }
        }
        
        Log.d(TAG, "Rendered " + objectCount + " objects, " + triangleCount + " triangles in " + drawCalls + " draw calls");
    }
    
    /**
     * Render a model
     */
    private void renderModel(Model model) {
        Matrix4 modelMatrix = model.getTransformMatrix();
        
        // Set model matrix
        shader.setUniform("uModelMatrix", modelMatrix.data);
        
        // Calculate normal matrix
        Matrix4 normalMatrix = modelMatrix.invert().transpose();
        shader.setUniformMatrix("uNormalMatrix", normalMatrix.data);
        
        // Bind material
        Material material = model.getMaterial();
        if (material != null) {
            material.bind(shader);
        }
        
        // Render all meshes in the model
        for (Mesh mesh : model.getMeshes()) {
            if (mesh != null) {
                mesh.render();
                int meshTriangles = mesh.isIndexed() ? mesh.getIndexCount() / 3 : mesh.getVertexCount() / 3;
                triangleCount += meshTriangles;
            }
        }
        
        objectCount++;
        drawCalls++;
    }
    
    /**
     * Render a mesh renderer
     */
    private void renderMeshRenderer(MeshRenderer renderer) {
        Matrix4 modelMatrix = renderer.getTransformMatrix();
        
        // Set model matrix
        shader.setUniform("uModelMatrix", modelMatrix.data);
        
        // Calculate normal matrix
        Matrix4 normalMatrix = modelMatrix.invert().transpose();
        shader.setUniformMatrix("uNormalMatrix", normalMatrix.data);
        
        // Bind material
        Material material = renderer.getMaterial();
        if (material != null) {
            material.bind(shader);
        }
        
        // Render mesh
        Mesh mesh = renderer.getMesh();
        if (mesh != null) {
            mesh.render();
            int meshTriangles = mesh.isIndexed() ? mesh.getIndexCount() / 3 : mesh.getVertexCount() / 3;
            triangleCount += meshTriangles;
        }
        
        objectCount++;
        drawCalls++;
    }
    
    /**
     * Render a single mesh
     */
    public void renderMesh(Mesh mesh, Matrix4 modelMatrix, Material material) {
        if (!shader.isCompiled() || mesh == null) return;
        
        shader.use();
        
        Matrix4 viewProjection = Matrix4.multiply(projectionMatrix, viewMatrix);
        shader.setUniform("uViewProjectionMatrix", viewProjection.data);
        shader.setUniform("uModelMatrix", modelMatrix.data);
        
        Matrix4 normalMatrix = modelMatrix.invert().transpose();
        shader.setUniformMatrix("uNormalMatrix", normalMatrix.data);
        
        shader.setUniform("uLightDirection", 0, 1, 0);
        shader.setUniform("uLightColor", 1, 1, 1);
        shader.setUniform("uAmbientColor", ambientLight.x, ambientLight.y, ambientLight.z);
        
        if (material != null) {
            material.bind(shader);
        }
        
        mesh.render();
        
        int meshTriangles = mesh.isIndexed() ? mesh.getIndexCount() / 3 : mesh.getVertexCount() / 3;
        triangleCount += meshTriangles;
        objectCount++;
        drawCalls++;
    }
    
    /**
     * Draw a primitive shape
     */
    public void drawPrimitive(PrimitiveType type, Material material) {
        Mesh mesh = PrimitiveMesh.createPrimitive(type);
        if (mesh != null) {
            Matrix4 identity = new Matrix4();
            renderMesh(mesh, identity, material);
        }
    }
    
    /**
     * Shutdown the renderer
     */
    public void shutdown() {
        renderers.clear();
        models.clear();
        lights.clear();
        Log.i(TAG, "Renderer3D shutdown");
    }
    
    // Getters and setters
    
    public Basic3DShader getShader() {
        return shader;
    }
    
    public void setShader(Basic3DShader shader) {
        this.shader = shader;
    }
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
        updateProjection();
    }
    
    public Matrix4 getProjectionMatrix() {
        return projectionMatrix;
    }
    
    public void setProjectionMatrix(Matrix4 projectionMatrix) {
        this.projectionMatrix = projectionMatrix;
    }
    
    public Matrix4 getViewMatrix() {
        return viewMatrix;
    }
    
    public void setViewMatrix(Matrix4 viewMatrix) {
        this.viewMatrix = viewMatrix;
    }
    
    public Vector3 getAmbientLight() {
        return ambientLight;
    }
    
    public void setAmbientLight(Vector3 ambientLight) {
        this.ambientLight = ambientLight;
    }
    
    public boolean isUseShadows() {
        return useShadows;
    }
    
    public void setUseShadows(boolean useShadows) {
        this.useShadows = useShadows;
    }
    
    public int getObjectCount() {
        return objectCount;
    }
    
    public int getDrawCalls() {
        return drawCalls;
    }
    
    public int getTriangleCount() {
        return triangleCount;
    }
    
    public List<Light> getLights() {
        return new ArrayList<>(lights);
    }
    
    /**
     * Primitive type enum
     */
    public enum PrimitiveType {
        CUBE,
        SPHERE,
        CYLINDER,
        PLANE,
        QUAD
    }
}
