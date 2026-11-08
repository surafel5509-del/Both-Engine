package com.bothengine.camera;

import com.bothengine.rendering.Camera;
import com.bothengine.rendering.Vector3;
import com.bothengine.components.Quaternion;
import com.bothengine.rendering.Matrix4;

/**
 * CameraController
 * Controls camera movement and behavior
 * Supports various camera modes: first-person, third-person, orbit, etc.
 */
public class CameraController {
    private Camera camera;
    private CameraMode mode;
    
    // Common settings
    private float moveSpeed;
    private float rotationSpeed;
    private float zoomSpeed;
    private boolean invertY;
    
    // First-person settings
    private Vector3 position;
    private Vector3 eulerAngles;
    
    // Third-person settings
    private Vector3 target;
    private float distance;
    private float minDistance;
    private float maxDistance;
    private float rotationX;
    private float rotationY;
    private float minRotationX;
    private float maxRotationX;
    
    // Orbit settings
    private Vector3 orbitTarget;
    private float orbitDistance;
    
    // Follow settings
    private Vector3 followTarget;
    private Vector3 followOffset;
    private float followSmoothness;
    
    // Shake settings
    private boolean shaking;
    private float shakeIntensity;
    private float shakeDuration;
    private float shakeTimer;
    
    public CameraController(Camera camera) {
        this.camera = camera;
        this.mode = CameraMode.FIRST_PERSON;
        
        // Common settings
        this.moveSpeed = 5f;
        this.rotationSpeed = 100f;
        this.zoomSpeed = 1f;
        this.invertY = false;
        
        // First-person settings
        this.position = new Vector3();
        this.eulerAngles = new Vector3();
        
        // Third-person settings
        this.target = new Vector3();
        this.distance = 5f;
        this.minDistance = 1f;
        this.maxDistance = 20f;
        this.rotationX = 0f;
        this.rotationY = 0f;
        this.minRotationX = -89f;
        this.maxRotationX = 89f;
        
        // Orbit settings
        this.orbitTarget = new Vector3();
        this.orbitDistance = 10f;
        
        // Follow settings
        this.followTarget = new Vector3();
        this.followOffset = new Vector3(0, 2, -5);
        this.followSmoothness = 5f;
        
        // Shake settings
        this.shaking = false;
        this.shakeIntensity = 0f;
        this.shakeDuration = 0f;
        this.shakeTimer = 0f;
    }
    
    /**
     * Create a camera controller with a camera
     */
    public static CameraController create(Camera camera) {
        return new CameraController(camera);
    }
    
    /**
     * Update the camera controller
     */
    public void update(float deltaTime) {
        switch (mode) {
            case FIRST_PERSON:
                updateFirstPerson(deltaTime);
                break;
            case THIRD_PERSON:
                updateThirdPerson(deltaTime);
                break;
            case ORBIT:
                updateOrbit(deltaTime);
                break;
            case FOLLOW:
                updateFollow(deltaTime);
                break;
        }
        
        // Apply shake
        if (shaking) {
            applyShake(deltaTime);
        }
    }
    
    /**
     * Update first-person camera
     */
    private void updateFirstPerson(float deltaTime) {
        // Calculate rotation from euler angles
        Quaternion rotation = Quaternion.euler(eulerAngles.x, eulerAngles.y, eulerAngles.z);
        
        // Calculate forward, right, and up vectors
        Vector3 forward = rotation.getForward();
        Vector3 right = rotation.getRight();
        
        // Update camera position and rotation
        camera.setPosition(position);
        camera.setRotation(rotation);
    }
    
    /**
     * Update third-person camera
     */
    private void updateThirdPerson(float deltaTime) {
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
     * Update orbit camera
     */
    private void updateOrbit(float deltaTime) {
        // Similar to third-person but with different target
        float radX = (float) Math.toRadians(rotationX);
        float radY = (float) Math.toRadians(rotationY);
        
        float x = orbitDistance * (float) Math.sin(radX) * (float) Math.cos(radY);
        float y = orbitDistance * (float) Math.sin(radX) * (float) Math.sin(radY);
        float z = orbitDistance * (float) Math.cos(radX);
        
        Vector3 cameraPosition = new Vector3(orbitTarget.x + x, orbitTarget.y + y, orbitTarget.z + z);
        
        camera.setPosition(cameraPosition);
        camera.lookAt(orbitTarget);
    }
    
    /**
     * Update follow camera
     */
    private void updateFollow(float deltaTime) {
        // Smoothly follow the target
        Vector3 targetPosition = followTarget.add(followOffset);
        Vector3 currentPosition = camera.getPosition();
        
        // Smooth interpolation
        Vector3 newPosition = Vector3.lerp(currentPosition, targetPosition, followSmoothness * deltaTime);
        
        camera.setPosition(newPosition);
        camera.lookAt(followTarget);
    }
    
    /**
     * Apply camera shake
     */
    private void applyShake(float deltaTime) {
        if (!shaking) return;
        
        shakeTimer += deltaTime;
        
        if (shakeTimer >= shakeDuration) {
            shaking = false;
            shakeTimer = 0f;
            return;
        }
        
        // Calculate shake offset
        float shakeX = (float) (Math.random() * 2 - 1) * shakeIntensity;
        float shakeY = (float) (Math.random() * 2 - 1) * shakeIntensity;
        
        // Apply shake to camera
        Vector3 position = camera.getPosition();
        camera.setPosition(position.add(new Vector3(shakeX, shakeY, 0)));
    }
    
    /**
     * Set the camera mode
     */
    public void setMode(CameraMode mode) {
        this.mode = mode;
        
        // Initialize mode-specific settings
        switch (mode) {
            case FIRST_PERSON:
                if (camera != null) {
                    position = camera.getPosition();
                    eulerAngles = camera.getEulerAngles();
                }
                break;
            case THIRD_PERSON:
                if (camera != null) {
                    target = camera.getPosition().add(camera.getForward().multiply(distance));
                }
                break;
            case ORBIT:
                if (camera != null) {
                    orbitTarget = camera.getPosition().add(camera.getForward().multiply(orbitDistance));
                }
                break;
            case FOLLOW:
                if (camera != null) {
                    followTarget = camera.getPosition().subtract(followOffset);
                }
                break;
        }
    }
    
    /**
     * Move the camera
     */
    public void move(Vector3 direction, float speed) {
        switch (mode) {
            case FIRST_PERSON:
                Vector3 move = direction.multiply(speed * moveSpeed);
                position = position.add(move);
                break;
            case THIRD_PERSON:
                // Move target
                Vector3 targetMove = direction.multiply(speed * moveSpeed);
                target = target.add(targetMove);
                break;
            case ORBIT:
                // Move orbit target
                Vector3 orbitMove = direction.multiply(speed * moveSpeed);
                orbitTarget = orbitTarget.add(orbitMove);
                break;
        }
    }
    
    /**
     * Rotate the camera
     */
    public void rotate(float deltaX, float deltaY) {
        switch (mode) {
            case FIRST_PERSON:
                eulerAngles.y += deltaX * rotationSpeed;
                eulerAngles.x -= deltaY * rotationSpeed * (invertY ? -1 : 1);
                
                // Clamp rotation
                eulerAngles.x = Math.max(-89f, Math.min(89f, eulerAngles.x));
                break;
            case THIRD_PERSON:
            case ORBIT:
                rotationY += deltaX * rotationSpeed;
                rotationX -= deltaY * rotationSpeed * (invertY ? -1 : 1);
                
                // Clamp rotation
                rotationX = Math.max(minRotationX, Math.min(maxRotationX, rotationX));
                break;
        }
    }
    
    /**
     * Zoom the camera
     */
    public void zoom(float delta) {
        switch (mode) {
            case THIRD_PERSON:
                distance -= delta * zoomSpeed;
                distance = Math.max(minDistance, Math.min(maxDistance, distance));
                break;
            case ORBIT:
                orbitDistance -= delta * zoomSpeed;
                orbitDistance = Math.max(1f, orbitDistance);
                break;
        }
    }
    
    /**
     * Shake the camera
     */
    public void shake(float intensity, float duration) {
        shaking = true;
        shakeIntensity = intensity;
        shakeDuration = duration;
        shakeTimer = 0f;
    }
    
    /**
     * Stop shaking the camera
     */
    public void stopShake() {
        shaking = false;
        shakeTimer = 0f;
    }
    
    /**
     * Focus on a target
     */
    public void focusOn(Vector3 target) {
        switch (mode) {
            case FIRST_PERSON:
                position = new Vector3(target);
                break;
            case THIRD_PERSON:
            case ORBIT:
                this.target = new Vector3(target);
                this.orbitTarget = new Vector3(target);
                break;
            case FOLLOW:
                followTarget = new Vector3(target);
                break;
        }
    }
    
    /**
     * Reset the camera to default position
     */
    public void reset() {
        position = new Vector3();
        eulerAngles = new Vector3();
        target = new Vector3();
        distance = 5f;
        rotationX = 0f;
        rotationY = 0f;
        orbitTarget = new Vector3();
        orbitDistance = 10f;
        followTarget = new Vector3();
        followOffset = new Vector3(0, 2, -5);
        
        if (camera != null) {
            camera.setPosition(new Vector3());
            camera.setRotation(Quaternion.identity());
        }
    }
    
    // Getters and setters
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public CameraMode getMode() {
        return mode;
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
    
    public float getZoomSpeed() {
        return zoomSpeed;
    }
    
    public void setZoomSpeed(float zoomSpeed) {
        this.zoomSpeed = zoomSpeed;
    }
    
    public boolean isInvertY() {
        return invertY;
    }
    
    public void setInvertY(boolean invertY) {
        this.invertY = invertY;
    }
    
    public Vector3 getPosition() {
        return position;
    }
    
    public void setPosition(Vector3 position) {
        this.position = position;
    }
    
    public Vector3 getEulerAngles() {
        return eulerAngles;
    }
    
    public void setEulerAngles(Vector3 eulerAngles) {
        this.eulerAngles = eulerAngles;
    }
    
    public Vector3 getTarget() {
        return target;
    }
    
    public void setTarget(Vector3 target) {
        this.target = target;
    }
    
    public float getDistance() {
        return distance;
    }
    
    public void setDistance(float distance) {
        this.distance = distance;
    }
    
    public float getMinDistance() {
        return minDistance;
    }
    
    public void setMinDistance(float minDistance) {
        this.minDistance = minDistance;
    }
    
    public float getMaxDistance() {
        return maxDistance;
    }
    
    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }
    
    public Vector3 getOrbitTarget() {
        return orbitTarget;
    }
    
    public void setOrbitTarget(Vector3 orbitTarget) {
        this.orbitTarget = orbitTarget;
    }
    
    public float getOrbitDistance() {
        return orbitDistance;
    }
    
    public void setOrbitDistance(float orbitDistance) {
        this.orbitDistance = orbitDistance;
    }
    
    public Vector3 getFollowTarget() {
        return followTarget;
    }
    
    public void setFollowTarget(Vector3 followTarget) {
        this.followTarget = followTarget;
    }
    
    public Vector3 getFollowOffset() {
        return followOffset;
    }
    
    public void setFollowOffset(Vector3 followOffset) {
        this.followOffset = followOffset;
    }
    
    public float getFollowSmoothness() {
        return followSmoothness;
    }
    
    public void setFollowSmoothness(float followSmoothness) {
        this.followSmoothness = followSmoothness;
    }
    
    public boolean isShaking() {
        return shaking;
    }
    
    @Override
    public String toString() {
        return "CameraController{" +
                "mode=" + mode +
                ", camera=" + (camera != null ? camera.getName() : "null") +
                ", position=" + position +
                '}';
    }
    
    /**
     * Camera mode enum
     */
    public enum CameraMode {
        FIRST_PERSON,
        THIRD_PERSON,
        ORBIT,
        FOLLOW,
        FREE
    }
}
