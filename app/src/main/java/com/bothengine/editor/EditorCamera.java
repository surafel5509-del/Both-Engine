package com.bothengine.editor;

import com.bothengine.rendering.Camera;
import com.bothengine.rendering.Vector3;
import com.bothengine.rendering.Quaternion;
import com.bothengine.rendering.Matrix4;

/**
 * EditorCamera
 * Camera controller for the editor view
 * Supports orbit, first-person, and orthographic modes
 */
public class EditorCamera {
    private Camera camera;
    private CameraMode mode;
    
    // Orbit camera settings
    private Vector3 target;
    private float distance;
    private float minDistance;
    private float maxDistance;
    private float rotationX;
    private float rotationY;
    private float minRotationX;
    private float maxRotationX;
    
    // First-person camera settings
    private Vector3 position;
    private Vector3 eulerAngles;
    private float moveSpeed;
    private float rotationSpeed;
    
    // Orthographic camera settings
    private float orthographicSize;
    private float minOrthographicSize;
    private float maxOrthographicSize;
    
    // Common settings
    private float zoomSpeed;
    private float panSpeed;
    private float scrollSensitivity;
    
    public EditorCamera() {
        this.camera = new Camera();
        this.mode = CameraMode.PERSPECTIVE;
        
        // Orbit settings
        this.target = new Vector3();
        this.distance = 10f;
        this.minDistance = 1f;
        this.maxDistance = 100f;
        this.rotationX = 45f;
        this.rotationY = 45f;
        this.minRotationX = -89f;
        this.maxRotationX = 89f;
        
        // First-person settings
        this.position = new Vector3(0, 0, 10);
        this.eulerAngles = new Vector3(0, 0, 0);
        this.moveSpeed = 5f;
        this.rotationSpeed = 100f;
        
        // Orthographic settings
        this.orthographicSize = 10f;
        this.minOrthographicSize = 1f;
        this.maxOrthographicSize = 100f;
        
        // Common settings
        this.zoomSpeed = 1f;
        this.panSpeed = 1f;
        this.scrollSensitivity = 0.1f;
        
        // Initialize camera
        updateCamera();
    }
    
    /**
     * Initialize the editor camera
     */
    public void initialize() {
        // Setup camera projection
        camera.setFieldOfView(60f);
        camera.setNearClipPlane(0.1f);
        camera.setFarClipPlane(1000f);
        
        // Set initial position
        updateCamera();
    }
    
    /**
     * Update the editor camera
     */
    public void update(float deltaTime) {
        // Handle input and update camera
        handleInput(deltaTime);
        updateCamera();
    }
    
    /**
     * Handle input for camera movement
     */
    private void handleInput(float deltaTime) {
        // This would be implemented based on input system
        // For now, we'll handle common camera controls
        
        switch (mode) {
            case ORBIT:
                handleOrbitInput(deltaTime);
                break;
            case FIRST_PERSON:
                handleFirstPersonInput(deltaTime);
                break;
            case ORTHOGRAPHIC:
                handleOrthographicInput(deltaTime);
                break;
        }
    }
    
    /**
     * Handle orbit camera input
     */
    private void handleOrbitInput(float deltaTime) {
        // Handle rotation
        // Handle zoom
        // Handle pan
    }
    
    /**
     * Handle first-person camera input
     */
    private void handleFirstPersonInput(float deltaTime) {
        // Handle movement
        // Handle rotation
    }
    
    /**
     * Handle orthographic camera input
     */
    private void handleOrthographicInput(float deltaTime) {
        // Handle zoom
        // Handle pan
    }
    
    /**
     * Update the camera based on current settings
     */
    private void updateCamera() {
        switch (mode) {
            case ORBIT:
                updateOrbitCamera();
                break;
            case FIRST_PERSON:
                updateFirstPersonCamera();
                break;
            case ORTHOGRAPHIC:
                updateOrthographicCamera();
                break;
        }
    }
    
    /**
     * Update orbit camera
     */
    private void updateOrbitCamera() {
        // Calculate camera position based on target, distance, and rotation
        float radX = (float) Math.toRadians(rotationX);
        float radY = (float) Math.toRadians(rotationY);
        
        float x = distance * (float) Math.sin(radX) * (float) Math.cos(radY);
        float y = distance * (float) Math.sin(radX) * (float) Math.sin(radY);
        float z = distance * (float) Math.cos(radX);
        
        Vector3 cameraPosition = new Vector3(target.x + x, target.y + y, target.z + z);
        
        // Look at target
        camera.setPosition(cameraPosition);
        camera.lookAt(target);
    }
    
    /**
     * Update first-person camera
     */
    private void updateFirstPersonCamera() {
        // Calculate camera rotation from euler angles
        Quaternion rotation = Quaternion.euler(eulerAngles.x, eulerAngles.y, eulerAngles.z);
        
        // Calculate forward vector
        Vector3 forward = rotation.getForward();
        
        // Set camera position and rotation
        camera.setPosition(position);
        camera.setRotation(rotation);
    }
    
    /**
     * Update orthographic camera
     */
    private void updateOrthographicCamera() {
        camera.setOrthographic(true);
        camera.setOrthographicSize(orthographicSize);
        camera.setPosition(position);
        camera.setRotation(Quaternion.identity());
    }
    
    /**
     * Set the camera mode
     */
    public void setMode(CameraMode mode) {
        this.mode = mode;
        updateCamera();
    }
    
    /**
     * Orbit around the target
     */
    public void orbit(float deltaX, float deltaY) {
        if (mode != CameraMode.ORBIT) return;
        
        rotationY += deltaX;
        rotationX -= deltaY;
        
        // Clamp rotation
        rotationX = Math.max(minRotationX, Math.min(maxRotationX, rotationX));
        
        updateCamera();
    }
    
    /**
     * Zoom the camera
     */
    public void zoom(float delta) {
        switch (mode) {
            case ORBIT:
                distance -= delta * zoomSpeed;
                distance = Math.max(minDistance, Math.min(maxDistance, distance));
                break;
            case ORTHOGRAPHIC:
                orthographicSize -= delta * zoomSpeed;
                orthographicSize = Math.max(minOrthographicSize, Math.min(maxOrthographicSize, orthographicSize));
                break;
        }
        
        updateCamera();
    }
    
    /**
     * Pan the camera
     */
    public void pan(float deltaX, float deltaY) {
        switch (mode) {
            case ORBIT:
                Vector3 right = camera.getRight().multiply(-deltaX * panSpeed);
                Vector3 up = camera.getUp().multiply(deltaY * panSpeed);
                target = target.add(right).add(up);
                break;
            case FIRST_PERSON:
                Vector3 moveRight = camera.getRight().multiply(deltaX * panSpeed);
                Vector3 moveUp = camera.getUp().multiply(deltaY * panSpeed);
                position = position.add(moveRight).add(moveUp);
                break;
            case ORTHOGRAPHIC:
                Vector3 orthoRight = new Vector3(1, 0, 0).multiply(-deltaX * panSpeed);
                Vector3 orthoUp = new Vector3(0, 1, 0).multiply(deltaY * panSpeed);
                position = position.add(orthoRight).add(orthoUp);
                break;
        }
        
        updateCamera();
    }
    
    /**
     * Move the camera in first-person mode
     */
    public void move(Vector3 direction, float speed) {
        if (mode != CameraMode.FIRST_PERSON) return;
        
        Vector3 move = direction.multiply(speed * moveSpeed);
        position = position.add(move);
        
        updateCamera();
    }
    
    /**
     * Rotate the camera in first-person mode
     */
    public void rotate(float deltaX, float deltaY) {
        if (mode != CameraMode.FIRST_PERSON) return;
        
        eulerAngles.y += deltaX * rotationSpeed;
        eulerAngles.x -= deltaY * rotationSpeed;
        
        // Clamp rotation
        eulerAngles.x = Math.max(-89f, Math.min(89f, eulerAngles.x));
        
        updateCamera();
    }
    
    /**
     * Reset the camera to default position
     */
    public void reset() {
        target = new Vector3();
        distance = 10f;
        rotationX = 45f;
        rotationY = 45f;
        position = new Vector3(0, 0, 10);
        eulerAngles = new Vector3(0, 0, 0);
        orthographicSize = 10f;
        
        updateCamera();
    }
    
    /**
     * Focus on an entity or position
     */
    public void focusOn(Vector3 position) {
        target = new Vector3(position);
        
        if (mode == CameraMode.ORBIT) {
            // Calculate distance based on scene bounds
            distance = 10f;
        }
        
        updateCamera();
    }
    
    /**
     * Shutdown the editor camera
     */
    public void shutdown() {
        if (camera != null) {
            camera.delete();
            camera = null;
        }
    }
    
    // Getters and setters
    
    public Camera getCamera() {
        return camera;
    }
    
    public CameraMode getMode() {
        return mode;
    }
    
    public Vector3 getTarget() {
        return target;
    }
    
    public void setTarget(Vector3 target) {
        this.target = target;
        updateCamera();
    }
    
    public float getDistance() {
        return distance;
    }
    
    public void setDistance(float distance) {
        this.distance = distance;
        updateCamera();
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void setPosition(Vector3 position) {
        this.position = position;
        updateCamera();
    }
    
    public Vector3 getEulerAngles() {
        return eulerAngles;
    }
    
    public void setEulerAngles(Vector3 eulerAngles) {
        this.eulerAngles = eulerAngles;
        updateCamera();
    }
    
    public float getMoveSpeed() {
        return moveSpeed;
    }
    
    public void setMoveSpeed(float moveSpeed) {
        this.moveSpeed = moveSpeed;
    }
    
    public float getRotationSpeed() {
        return rotationSpeed;
    }
    
    public void setRotationSpeed(float rotationSpeed) {
        this.rotationSpeed = rotationSpeed;
    }
    
    public float getOrthographicSize() {
        return orthographicSize;
    }
    
    public void setOrthographicSize(float orthographicSize) {
        this.orthographicSize = orthographicSize;
        updateCamera();
    }
    
    public Matrix4 getViewMatrix() {
        return camera.getViewMatrix();
    }
    
    public Matrix4 getProjectionMatrix() {
        return camera.getProjectionMatrix();
    }
    
    public Matrix4 getViewProjectionMatrix() {
        return Matrix4.multiply(camera.getProjectionMatrix(), camera.getViewMatrix());
    }
    
    @Override
    public String toString() {
        return "EditorCamera{" +
                "mode=" + mode +
                ", position=" + position +
                ", target=" + target +
                ", distance=" + distance +
                '}';
    }
    
    // Camera mode enum
    public enum CameraMode {
        PERSPECTIVE,
        ORBIT,
        FIRST_PERSON,
        ORTHOGRAPHIC
    }
}
