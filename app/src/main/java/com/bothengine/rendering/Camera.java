package com.bothengine.rendering;

import com.bothengine.components.Quaternion;

import android.util.Log;

/**
 * Camera
 * Represents a camera in 3D space for rendering
 */
public class Camera {
    private String name = "Camera";
    private Quaternion rotation = Quaternion.identity();
    private static final String TAG = "Camera";
    
    public enum CameraType {
        PERSPECTIVE,
        ORTHOGRAPHIC
    }
    
    private final CameraType type;
    private float fieldOfView;
    private float aspectRatio;
    private float nearPlane;
    private float farPlane;
    private float orthographicSize;
    
    private Vector3 position;
    private Vector3 target;
    private Vector3 up;
    private Vector3 forward;
    private Vector3 right;
    
    private Matrix4 projectionMatrix;
    private Matrix4 viewMatrix;
    private Matrix4 viewProjectionMatrix;
    
    private boolean dirty;
    
    public Camera(CameraType type) {
        this.type = type;
        this.fieldOfView = 60.0f; // degrees
        this.aspectRatio = 1.0f;
        this.nearPlane = 0.1f;
        this.farPlane = 1000.0f;
        this.orthographicSize = 10.0f;
        
        this.position = new Vector3(0, 0, 5);
        this.target = new Vector3(0, 0, 0);
        this.up = new Vector3(0, 1, 0);
        this.forward = new Vector3(0, 0, -1);
        this.right = new Vector3(1, 0, 0);
        
        this.projectionMatrix = new Matrix4();
        this.viewMatrix = new Matrix4();
        this.viewProjectionMatrix = new Matrix4();
        this.dirty = true;
        
        updateVectors();
        updateMatrices();
    }
    
    /**
     * Update camera vectors based on position and target
     */
    private void updateVectors() {
        forward = Vector3.subtract(target, position).normalize();
        right = Vector3.cross(forward, up).normalize();
        up = Vector3.cross(right, forward).normalize();
    }
    
    /**
     * Update projection and view matrices
     */
    private void updateMatrices() {
        if (type == CameraType.PERSPECTIVE) {
            projectionMatrix = Matrix4.perspective(fieldOfView, aspectRatio, nearPlane, farPlane);
        } else {
            projectionMatrix = Matrix4.orthographic(-orthographicSize * aspectRatio, 
                orthographicSize * aspectRatio, -orthographicSize, orthographicSize, 
                nearPlane, farPlane);
        }
        
        viewMatrix = Matrix4.lookAt(position, target, up);
        viewProjectionMatrix = Matrix4.multiply(projectionMatrix, viewMatrix);
        dirty = false;
    }
    
    /**
     * Set camera position
     */
    public void setPosition(Vector3 position) {
        this.position = position;
        dirty = true;
    }
    
    /**
     * Set camera position
     */
    public void setPosition(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
        dirty = true;
    }
    
    /**
     * Get camera position
     */
    public Vector3 getPosition() {
        return position;
    }
    
    /**
     * Set camera target
     */
    public void setTarget(Vector3 target) {
        this.target = target;
        dirty = true;
    }
    
    /**
     * Set camera target
     */
    public void setTarget(float x, float y, float z) {
        this.target = new Vector3(x, y, z);
        dirty = true;
    }
    
    /**
     * Get camera target
     */
    public Vector3 getTarget() {
        return target;
    }
    
    /**
     * Set camera up vector
     */
    public void setUp(Vector3 up) {
        this.up = up;
        dirty = true;
    }
    
    /**
     * Get camera up vector
     */
    public Vector3 getUp() {
        return up;
    }
    
    /**
     * Get camera forward vector
     */
    public Vector3 getForward() {
        return forward;
    }
    
    /**
     * Get camera right vector
     */
    public Vector3 getRight() {
        return right;
    }
    
    /**
     * Set field of view
     */
    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
        dirty = true;
    }
    
    /**
     * Get field of view
     */
    public float getFieldOfView() {
        return fieldOfView;
    }
    
    /**
     * Set aspect ratio
     */
    public void setAspectRatio(float aspectRatio) {
        this.aspectRatio = aspectRatio;
        dirty = true;
    }
    
    /**
     * Get aspect ratio
     */
    public float getAspectRatio() {
        return aspectRatio;
    }
    
    /**
     * Set near plane
     */
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
        dirty = true;
    }
    
    /**
     * Get near plane
     */
    public float getNearPlane() {
        return nearPlane;
    }
    
    /**
     * Set far plane
     */
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
        dirty = true;
    }
    
    /**
     * Get far plane
     */
    public float getFarPlane() {
        return farPlane;
    }
    
    /**
     * Set orthographic size (for orthographic cameras)
     */
    public void setOrthographicSize(float orthographicSize) {
        this.orthographicSize = orthographicSize;
        dirty = true;
    }
    
    /**
     * Get orthographic size
     */
    public float getOrthographicSize() {
        return orthographicSize;
    }
    
    /**
     * Get camera type
     */
    public CameraType getType() {
        return type;
    }
    
    /**
     * Get projection matrix
     */
    public Matrix4 getProjectionMatrix() {
        if (dirty) updateMatrices();
        return projectionMatrix;
    }
    
    /**
     * Get view matrix
     */
    public Matrix4 getViewMatrix() {
        if (dirty) updateMatrices();
        return viewMatrix;
    }
    
    /**
     * Get view-projection matrix
     */
    public Matrix4 getViewProjectionMatrix() {
        if (dirty) updateMatrices();
        return viewProjectionMatrix;
    }
    
    /**
     * Move camera forward
     */
    public void moveForward(float distance) {
        position = Vector3.add(position, Vector3.multiply(forward, distance));
        dirty = true;
    }
    
    /**
     * Move camera backward
     */
    public void moveBackward(float distance) {
        moveForward(-distance);
    }
    
    /**
     * Move camera right
     */
    public void moveRight(float distance) {
        position = Vector3.add(position, Vector3.multiply(right, distance));
        dirty = true;
    }
    
    /**
     * Move camera left
     */
    public void moveLeft(float distance) {
        moveRight(-distance);
    }
    
    /**
     * Move camera up
     */
    public void moveUp(float distance) {
        position = Vector3.add(position, Vector3.multiply(up, distance));
        dirty = true;
    }
    
    /**
     * Move camera down
     */
    public void moveDown(float distance) {
        moveUp(-distance);
    }
    
    /**
     * Rotate camera around X axis
     */
    public void rotateX(float angle) {
        Matrix4 rotation = Matrix4.createRotationX(angle);
        forward = Matrix4.transform(rotation, forward);
        up = Matrix4.transform(rotation, up);
        forward.normalize();
        up.normalize();
        right = Vector3.cross(forward, up).normalize();
        target = Vector3.add(position, forward);
        dirty = true;
    }
    
    /**
     * Rotate camera around Y axis
     */
    public void rotateY(float angle) {
        Matrix4 rotation = Matrix4.createRotationY(angle);
        forward = Matrix4.transform(rotation, forward);
        right = Matrix4.transform(rotation, right);
        forward.normalize();
        right.normalize();
        up = Vector3.cross(right, forward).normalize();
        target = Vector3.add(position, forward);
        dirty = true;
    }
    
    /**
     * Rotate camera around Z axis
     */
    public void rotateZ(float angle) {
        Matrix4 rotation = Matrix4.createRotationZ(angle);
        up = Matrix4.transform(rotation, up);
        right = Matrix4.transform(rotation, right);
        up.normalize();
        right.normalize();
        forward = Vector3.cross(right, up).normalize();
        target = Vector3.add(position, forward);
        dirty = true;
    }
    
    /**
     * Look at a point
     */
    public void lookAt(Vector3 point) {
        this.target = point;
        updateVectors();
        dirty = true;
    }
    
    /**
     * Update camera (call this every frame if needed)
     */
    public void update() {
        if (dirty) {
            updateVectors();
            updateMatrices();
        }
    }
    
    /**
     * Get camera frustum for culling
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Quaternion getRotation() {
        return rotation;
    }

    public void setRotation(Quaternion rotation) {
        if (rotation != null) {
            this.rotation = rotation;
            // Keep the look direction in sync with the new orientation.
            Vector3 forward = rotation.getForward();
            if (forward != null) {
                setTarget(getPosition().add(forward));
            }
        }
    }

    /**
     * Current orientation expressed as XYZ euler angles (radians).
     */
    public Vector3 getEulerAngles() {
        return rotation != null ? rotation.getEulerAngles() : new Vector3();
    }

    public Frustum getFrustum() {
        if (dirty) updateMatrices();
        return new Frustum(viewProjectionMatrix);
    }
}
