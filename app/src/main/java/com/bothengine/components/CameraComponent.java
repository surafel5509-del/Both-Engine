package com.bothengine.components;

import com.bothengine.core.ecs.Component;
import com.bothengine.rendering.Camera;

/**
 * CameraComponent
 * Adds a camera to an entity
 */
public class CameraComponent extends Component {
    private Camera camera;
    private boolean isMainCamera;
    private int depth;
    private float fieldOfView;
    private float nearPlane;
    private float farPlane;
    
    public CameraComponent() {
        this.camera = new Camera(Camera.CameraType.PERSPECTIVE);
        this.isMainCamera = false;
        this.depth = 0;
        this.fieldOfView = 60;
        this.nearPlane = 0.1f;
        this.farPlane = 1000;
    }
    
    /**
     * Create a perspective camera
     */
    public static CameraComponent createPerspective(float fieldOfView, float nearPlane, float farPlane) {
        CameraComponent component = new CameraComponent();
        component.setFieldOfView(fieldOfView);
        component.setNearPlane(nearPlane);
        component.setFarPlane(farPlane);
        component.getCamera().setFieldOfView(fieldOfView);
        component.getCamera().setNearPlane(nearPlane);
        component.getCamera().setFarPlane(farPlane);
        return component;
    }
    
    /**
     * Create an orthographic camera
     */
    public static CameraComponent createOrthographic(float size, float nearPlane, float farPlane) {
        CameraComponent component = new CameraComponent();
        component.camera = new Camera(Camera.CameraType.ORTHOGRAPHIC);
        component.camera.setOrthographicSize(size);
        component.camera.setNearPlane(nearPlane);
        component.camera.setFarPlane(farPlane);
        component.setFieldOfView(2 * (float) Math.toDegrees(Math.atan(size)));
        component.setNearPlane(nearPlane);
        component.setFarPlane(farPlane);
        return component;
    }
    
    /**
     * Update camera position from entity's TransformComponent
     */
    public void updateFromTransform() {
        TransformComponent transform = getEntity().getComponent(TransformComponent.class);
        if (transform != null) {
            camera.setPosition(transform.getPosition());
            camera.setTarget(transform.getPosition().add(transform.getForward()));
        }
    }
    
    // Getters and setters
    
    public Camera getCamera() {
        return camera;
    }
    
    public void setCamera(Camera camera) {
        this.camera = camera;
    }
    
    public boolean isMainCamera() {
        return isMainCamera;
    }
    
    public void setMainCamera(boolean mainCamera) {
        isMainCamera = mainCamera;
    }
    
    public int getDepth() {
        return depth;
    }
    
    public void setDepth(int depth) {
        this.depth = depth;
    }
    
    public float getFieldOfView() {
        return fieldOfView;
    }
    
    public void setFieldOfView(float fieldOfView) {
        this.fieldOfView = fieldOfView;
        if (camera != null) {
            camera.setFieldOfView(fieldOfView);
        }
    }
    
    public float getNearPlane() {
        return nearPlane;
    }
    
    public void setNearPlane(float nearPlane) {
        this.nearPlane = nearPlane;
        if (camera != null) {
            camera.setNearPlane(nearPlane);
        }
    }
    
    public float getFarPlane() {
        return farPlane;
    }
    
    public void setFarPlane(float farPlane) {
        this.farPlane = farPlane;
        if (camera != null) {
            camera.setFarPlane(farPlane);
        }
    }
    
    @Override
    public String toString() {
        return "CameraComponent{" +
                "isMainCamera=" + isMainCamera +
                ", fieldOfView=" + fieldOfView +
                ", nearPlane=" + nearPlane +
                ", farPlane=" + farPlane +
                '}';
    }
}
