package com.bothengine.editor;

import com.bothengine.components.CameraComponent;
import com.bothengine.components.ColliderComponent;
import com.bothengine.core.ecs.Component;
import com.bothengine.components.LightComponent;
import com.bothengine.components.MeshRendererComponent;
import com.bothengine.components.RigidbodyComponent;
import com.bothengine.components.SpriteRendererComponent;
import com.bothengine.components.TransformComponent;
import com.bothengine.components.UpdateComponent;
import com.bothengine.core.ecs.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * InspectorWindow
 * Displays and edits component properties of selected entities
 * Similar to Unity's Inspector window
 */
public class InspectorWindow extends EditorWindow {
    private Entity selectedEntity;
    private List<Entity> selectedEntities;
    private boolean showAllComponents;
    
    public InspectorWindow() {
        super("Inspector");
        this.selectedEntity = null;
        this.selectedEntities = new ArrayList<>();
        this.showAllComponents = true;
    }
    
    /**
     * Initialize the inspector window
     */
    @Override
    public void onInitialize() {
        super.onInitialize();
    }
    
    /**
     * Update the inspector window
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
    }
    
    /**
     * Render the inspector window
     */
    @Override
    public void onRender() {
        super.onRender();
        
        if (selectedEntity == null) {
            renderEmptyMessage();
            return;
        }
        
        renderEntityHeader();
        renderComponents();
    }
    
    /**
     * Render empty message when no entity is selected
     */
    private void renderEmptyMessage() {
        // Render "No entity selected" message
    }
    
    /**
     * Render entity header
     */
    private void renderEntityHeader() {
        // Render entity name
        // Render entity active/visible toggles
        // Render entity layer
        // Render entity tag
    }
    
    /**
     * Render all components of the selected entity
     */
    private void renderComponents() {
        if (selectedEntity == null) return;
        
        Map<Class<? extends Component>, Component> components = selectedEntity.getComponents();
        
        // Render each component
        for (Map.Entry<Class<? extends Component>, Component> entry : components.entrySet()) {
            Component component = entry.getValue();
            renderComponent(component);
        }
        
        // Render add component button
        renderAddComponentButton();
    }
    
    /**
     * Render a single component
     */
    private void renderComponent(Component component) {
        if (component == null) return;
        
        // Render component header
        renderComponentHeader(component);
        
        // Render component properties based on type
        if (component instanceof TransformComponent) {
            renderTransformComponent((TransformComponent) component);
        } else if (component instanceof MeshRendererComponent) {
            renderMeshRendererComponent((MeshRendererComponent) component);
        } else if (component instanceof SpriteRendererComponent) {
            renderSpriteRendererComponent((SpriteRendererComponent) component);
        } else if (component instanceof CameraComponent) {
            renderCameraComponent((CameraComponent) component);
        } else if (component instanceof LightComponent) {
            renderLightComponent((LightComponent) component);
        } else if (component instanceof RigidbodyComponent) {
            renderRigidbodyComponent((RigidbodyComponent) component);
        } else if (component instanceof ColliderComponent) {
            renderColliderComponent((ColliderComponent) component);
        } else if (component instanceof UpdateComponent) {
            renderUpdateComponent((UpdateComponent) component);
        } else {
            // Generic component rendering
            renderGenericComponent(component);
        }
    }
    
    /**
     * Render component header
     */
    private void renderComponentHeader(Component component) {
        // Render component name
        // Render component enabled toggle
        // Render component remove button
    }
    
    /**
     * Render TransformComponent
     */
    private void renderTransformComponent(TransformComponent component) {
        // Render position field
        // Render rotation field
        // Render scale field
    }
    
    /**
     * Render MeshRendererComponent
     */
    private void renderMeshRendererComponent(MeshRendererComponent component) {
        // Render mesh field
        // Render material field
        // Render cast shadows toggle
        // Render receive shadows toggle
    }
    
    /**
     * Render SpriteRendererComponent
     */
    private void renderSpriteRendererComponent(SpriteRendererComponent component) {
        // Render sprite field
        // Render color field
        // Render draw mode
        // Render size
    }
    
    /**
     * Render CameraComponent
     */
    private void renderCameraComponent(CameraComponent component) {
        // Render field of view
        // Render near clip plane
        // Render far clip plane
        // Render clear flags
        // Render background color
    }
    
    /**
     * Render LightComponent
     */
    private void renderLightComponent(LightComponent component) {
        // Render light type
        // Render color
        // Render intensity
        // Render range
        // Render shadow settings
    }
    
    /**
     * Render RigidbodyComponent
     */
    private void renderRigidbodyComponent(RigidbodyComponent component) {
        // Render mass
        // Render drag
        // Render angular drag
        // Render use gravity toggle
        // Render is kinematic toggle
    }
    
    /**
     * Render ColliderComponent
     */
    private void renderColliderComponent(ColliderComponent component) {
        // Render collider type
        // Render size
        // Render center
        // Render material
        // Render is trigger toggle
    }
    
    /**
     * Render UpdateComponent
     */
    private void renderUpdateComponent(UpdateComponent component) {
        // Render script field
    }
    
    /**
     * Render generic component
     */
    private void renderGenericComponent(Component component) {
        // Render all fields using reflection or custom property drawers
    }
    
    /**
     * Render add component button
     */
    private void renderAddComponentButton() {
        // Render button to add new components
    }
    
    /**
     * Called when selection changes
     */
    @Override
    public void onSelectionChanged(Entity entity) {
        selectedEntity = entity;
        selectedEntities.clear();
        if (entity != null) {
            selectedEntities.add(entity);
        }
    }
    
    /**
     * Called when selection changes (multiple entities)
     */
    @Override
    public void onSelectionChanged(List<Entity> entities) {
        selectedEntities = new ArrayList<>(entities);
        selectedEntity = selectedEntities.isEmpty() ? null : selectedEntities.get(0);
    }
    
    /**
     * Add a component to the selected entity
     */
    public void addComponent(Class<? extends Component> componentClass) {
        if (selectedEntity == null) return;
        
        try {
            Component component = componentClass.newInstance();
            selectedEntity.addComponent(component);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Remove a component from the selected entity
     */
    public void removeComponent(Class<? extends Component> componentClass) {
        if (selectedEntity == null) return;
        
        selectedEntity.removeComponent(componentClass);
    }
    
    // Getters and setters
    
    public Entity getSelectedEntity() {
        return selectedEntity;
    }
    
    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
    
    public List<Entity> getSelectedEntities() {
        return new ArrayList<>(selectedEntities);
    }
    
    public boolean isShowAllComponents() {
        return showAllComponents;
    }
    
    public void setShowAllComponents(boolean showAllComponents) {
        this.showAllComponents = showAllComponents;
    }
    
    @Override
    public String toString() {
        return "InspectorWindow{" +
                "title='" + getTitle() + '\'' +
                ", selectedEntity=" + (selectedEntity != null ? selectedEntity.getName() : "null") +
                '}';
    }
}
