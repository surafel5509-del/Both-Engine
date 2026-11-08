package com.bothengine.scripting;

import com.bothengine.core.ecs.Component;

/**
 * ScriptComponent
 * ECS component for attaching scripts to entities
 * Similar to Unity's MonoBehaviour
 */
public class ScriptComponent extends Component {
    private String scriptName;
    private Script script;
    private boolean enabled;
    
    public ScriptComponent() {
        this.scriptName = "";
        this.script = null;
        this.enabled = true;
    }
    
    /**
     * Create a script component with a script name
     */
    public static ScriptComponent create(String scriptName) {
        ScriptComponent component = new ScriptComponent();
        component.setScriptName(scriptName);
        return component;
    }
    
    /**
     * Create a script component with a script
     */
    public static ScriptComponent create(Script script) {
        ScriptComponent component = new ScriptComponent();
        component.setScript(script);
        return component;
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        
        // Load script if not already loaded
        if (script == null && scriptName != null && !scriptName.isEmpty()) {
            ScriptManager scriptManager = ScriptManager.getInstance();
            if (scriptManager != null) {
                script = scriptManager.loadScript(scriptName);
            }
        }
        
        // Initialize script
        if (script != null) {
            script.setEntity(getEntity());
            script.onInitialize();
        }
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        
        // Shutdown script
        if (script != null) {
            script.onShutdown();
            script.setEntity(null);
        }
    }
    
    /**
     * Called when the entity is enabled
     */
    @Override
    public void onEnable() {
        super.onEnable();
        
        if (script != null) {
            script.onEnable();
        }
    }
    
    /**
     * Called when the entity is disabled
     */
    @Override
    public void onDisable() {
        super.onDisable();
        
        if (script != null) {
            script.onDisable();
        }
    }
    
    /**
     * Update the script
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        if (script != null && enabled) {
            script.onUpdate(deltaTime);
        }
    }
    
    /**
     * Fixed update the script
     */
    @Override
    public void onFixedUpdate(float fixedDeltaTime) {
        super.onFixedUpdate(fixedDeltaTime);
        
        if (script != null && enabled) {
            script.onFixedUpdate(fixedDeltaTime);
        }
    }
    
    /**
     * Called when a collision occurs
     */
    public void onCollisionEnter(Object other) {
        if (script != null) {
            script.onCollisionEnter(other);
        }
    }
    
    /**
     * Called when a collision ends
     */
    public void onCollisionExit(Object other) {
        if (script != null) {
            script.onCollisionExit(other);
        }
    }
    
    /**
     * Called when a trigger collision occurs
     */
    public void onTriggerEnter(Object other) {
        if (script != null) {
            script.onTriggerEnter(other);
        }
    }
    
    /**
     * Called when a trigger collision ends
     */
    public void onTriggerExit(Object other) {
        if (script != null) {
            script.onTriggerExit(other);
        }
    }
    
    // Getters and setters
    
    public String getScriptName() {
        return scriptName;
    }
    
    public void setScriptName(String scriptName) {
        this.scriptName = scriptName;
        
        // Reload script if needed
        if (script == null || !script.getName().equals(scriptName)) {
            ScriptManager scriptManager = ScriptManager.getInstance();
            if (scriptManager != null) {
                script = scriptManager.loadScript(scriptName);
                if (script != null) {
                    script.setEntity(getEntity());
                    script.onInitialize();
                }
            }
        }
    }
    
    public Script getScript() {
        return script;
    }
    
    public void setScript(Script script) {
        // Shutdown old script
        if (this.script != null) {
            this.script.onShutdown();
            this.script.setEntity(null);
        }
        
        this.script = script;
        this.scriptName = script != null ? script.getName() : "";
        
        // Initialize new script
        if (this.script != null) {
            this.script.setEntity(getEntity());
            this.script.onInitialize();
        }
    }
    
    public boolean isEnabled() {
        return enabled && super.isEnabled();
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    @Override
    public String toString() {
        return "ScriptComponent{" +
                "scriptName='" + scriptName + '\'' +
                ", script=" + (script != null ? script.getName() : "null") +
                ", enabled=" + enabled +
                '}';
    }
}
