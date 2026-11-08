package com.bothengine.editor;

import com.bothengine.core.ecs.Entity;
import com.bothengine.core.ecs.EntityManager;

import java.util.ArrayList;
import java.util.List;

/**
 * HierarchyWindow
 * Displays the scene hierarchy (tree view of entities)
 * Similar to Unity's Hierarchy window
 */
public class HierarchyWindow extends EditorWindow {
    private EntityManager entityManager;
    private Entity selectedEntity;
    private List<Entity> selectedEntities;
    private boolean allowMultiSelect;
    private boolean showInactive;
    
    public HierarchyWindow() {
        super("Hierarchy");
        this.entityManager = null;
        this.selectedEntity = null;
        this.selectedEntities = new ArrayList<>();
        this.allowMultiSelect = true;
        this.showInactive = true;
    }
    
    /**
     * Initialize the hierarchy window
     */
    @Override
    public void onInitialize() {
        super.onInitialize();
        
        if (getEditorManager() != null) {
            this.entityManager = getEditorManager().getEntityManager();
        }
    }
    
    /**
     * Update the hierarchy window
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Update entity list
        if (entityManager != null) {
            entityManager.getAllEntities();
        }
    }
    
    /**
     * Render the hierarchy window
     */
    @Override
    public void onRender() {
        super.onRender();
        
        // Render entity tree
        if (entityManager != null) {
            renderEntityTree();
        }
    }
    
    /**
     * Render the entity tree
     */
    private void renderEntityTree() {
        // Get root entities (entities without parents)
        List<Entity> rootEntities = new ArrayList<>();
        
        for (Entity entity : entityManager.getAllEntities()) {
            if (entity.getParent() == null) {
                rootEntities.add(entity);
            }
        }
        
        // Render each root entity and its children
        for (Entity entity : rootEntities) {
            renderEntityNode(entity, 0);
        }
    }
    
    /**
     * Render an entity node in the tree
     */
    private void renderEntityNode(Entity entity, int depth) {
        if (!showInactive && !entity.isActive()) {
            return;
        }
        
        // Indent based on depth
        String indent = createIndent(depth);
        
        // Check if this entity is selected
        boolean isSelected = selectedEntity != null && selectedEntity.equals(entity);
        boolean isMultiSelected = selectedEntities.contains(entity);
        
        // Render entity name with prefix
        String prefix = getEntityPrefix(entity);
        String displayName = prefix + entity.getName();
        
        // Render the entity
        // This would be implemented with actual UI rendering
        
        // Render children
        for (Entity child : getEntityChildren(entity)) {
            renderEntityNode(child, depth + 1);
        }
    }
    
    /**
     * Create indentation string
     */
    private String createIndent(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("  ");
        }
        return sb.toString();
    }
    
    /**
     * Get the prefix for an entity based on its active/visible state
     */
    private String getEntityPrefix(Entity entity) {
        StringBuilder sb = new StringBuilder();
        
        if (!entity.isActive()) {
            sb.append("[D] ");
        }
        
        if (!entity.isVisible()) {
            sb.append("[H] ");
        }
        
        return sb.toString();
    }
    
    /**
     * Get the children of an entity
     */
    private List<Entity> getEntityChildren(Entity entity) {
        List<Entity> children = new ArrayList<>();
        
        for (Entity other : entityManager.getAllEntities()) {
            if (other.getParent() != null && other.getParent().equals(entity)) {
                children.add(other);
            }
        }
        
        return children;
    }
    
    /**
     * Select an entity
     */
    public void selectEntity(Entity entity) {
        if (entity == null) {
            clearSelection();
            return;
        }
        
        if (allowMultiSelect && selectedEntity != null && 
            (getEditorManager().getInput().isShiftDown() || getEditorManager().getInput().isControlDown())) {
            // Multi-select
            if (selectedEntities.contains(entity)) {
                selectedEntities.remove(entity);
            } else {
                selectedEntities.add(entity);
            }
        } else {
            // Single select
            selectedEntity = entity;
            selectedEntities.clear();
            selectedEntities.add(entity);
        }
        
        // Notify editor manager
        if (getEditorManager() != null) {
            getEditorManager().selectEntity(entity);
        }
    }
    
    /**
     * Clear selection
     */
    public void clearSelection() {
        selectedEntity = null;
        selectedEntities.clear();
        
        if (getEditorManager() != null) {
            getEditorManager().clearSelection();
        }
    }
    
    /**
     * Called when selection changes externally
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
     * Called when selection changes externally (multiple entities)
     */
    @Override
    public void onSelectionChanged(List<Entity> entities) {
        selectedEntities = new ArrayList<>(entities);
        selectedEntity = selectedEntities.isEmpty() ? null : selectedEntities.get(0);
    }
    
    /**
     * Create a new entity
     */
    public Entity createEntity(String name) {
        if (getEditorManager() != null) {
            return getEditorManager().createEntity(name);
        }
        return null;
    }
    
    /**
     * Delete selected entities
     */
    public void deleteSelected() {
        if (entityManager == null) return;
        
        for (Entity entity : selectedEntities) {
            entityManager.destroyEntity(entity);
        }
        
        clearSelection();
    }
    
    /**
     * Rename an entity
     */
    public void renameEntity(Entity entity, String newName) {
        if (entity != null) {
            entity.setName(newName);
        }
    }
    
    /**
     * Set an entity as parent of another
     */
    public void setParent(Entity child, Entity parent) {
        if (child != null) {
            child.setParent(parent);
        }
    }
    
    // Getters and setters
    
    public EntityManager getEntityManager() {
        return entityManager;
    }
    
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }
    
    public Entity getSelectedEntity() {
        return selectedEntity;
    }
    
    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
    }
    
    public List<Entity> getSelectedEntities() {
        return new ArrayList<>(selectedEntities);
    }
    
    public boolean isAllowMultiSelect() {
        return allowMultiSelect;
    }
    
    public void setAllowMultiSelect(boolean allowMultiSelect) {
        this.allowMultiSelect = allowMultiSelect;
    }
    
    public boolean isShowInactive() {
        return showInactive;
    }
    
    public void setShowInactive(boolean showInactive) {
        this.showInactive = showInactive;
    }
    
    @Override
    public String toString() {
        return "HierarchyWindow{" +
                "title='" + getTitle() + '\'' +
                ", selectedEntity=" + (selectedEntity != null ? selectedEntity.getName() : "null") +
                ", selectedEntities=" + selectedEntities.size() +
                '}';
    }
}
