package com.bothengine.network;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;

/**
 * NetworkComponent
 * ECS component for network synchronization
 * Manages NetworkIdentity for an entity
 */
public class NetworkComponent extends Component {
    private NetworkIdentity networkIdentity;
    private boolean spawnOnStart;
    private boolean destroyOnStop;
    
    public NetworkComponent() {
        this.networkIdentity = null;
        this.spawnOnStart = true;
        this.destroyOnStop = true;
    }
    
    /**
     * Create a network component
     */
    public static NetworkComponent create() {
        return new NetworkComponent();
    }
    
    /**
     * Create a network component with spawn settings
     */
    public static NetworkComponent create(boolean spawnOnStart, boolean destroyOnStop) {
        NetworkComponent component = new NetworkComponent();
        component.setSpawnOnStart(spawnOnStart);
        component.setDestroyOnStop(destroyOnStop);
        return component;
    }
    
    /**
     * Called when component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        
        // Create network identity if it doesn't exist
        if (networkIdentity == null) {
            networkIdentity = new NetworkIdentity();
        }
        
        // Initialize network identity
        networkIdentity.initialize(getEntity());
        
        // Register with network manager
        NetworkManager manager = NetworkManager.getInstance();
        if (manager != null) {
            manager.registerIdentity(networkIdentity);
        }
    }
    
    /**
     * Called when component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        
        // Unregister from network manager
        NetworkManager manager = NetworkManager.getInstance();
        if (manager != null && networkIdentity != null) {
            manager.unregisterIdentity(networkIdentity);
        }
        
        // Shutdown network identity
        if (networkIdentity != null) {
            networkIdentity.setEntity(null);
        }
    }
    
    /**
     * Update the network component
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Update network identity
        if (networkIdentity != null) {
            networkIdentity.update(deltaTime);
        }
    }
    
    /**
     * Spawn the entity on the network
     */
    public void spawn() {
        if (networkIdentity == null) return;
        
        NetworkManager manager = NetworkManager.getInstance();
        if (manager != null && manager.isServer()) {
            manager.getServer().spawnIdentity(networkIdentity);
        }
    }
    
    /**
     * Destroy the entity on the network
     */
    public void destroy() {
        if (networkIdentity == null) return;
        
        NetworkManager manager = NetworkManager.getInstance();
        if (manager != null && manager.isServer()) {
            manager.getServer().destroyIdentity(networkIdentity.getNetId());
        }
    }
    
    // Getters and setters
    
    public NetworkIdentity getNetworkIdentity() {
        return networkIdentity;
    }
    
    public void setNetworkIdentity(NetworkIdentity networkIdentity) {
        this.networkIdentity = networkIdentity;
    }
    
    public boolean isSpawnOnStart() {
        return spawnOnStart;
    }
    
    public void setSpawnOnStart(boolean spawnOnStart) {
        this.spawnOnStart = spawnOnStart;
    }
    
    public boolean isDestroyOnStop() {
        return destroyOnStop;
    }
    
    public void setDestroyOnStop(boolean destroyOnStop) {
        this.destroyOnStop = destroyOnStop;
    }
    
    @Override
    public String toString() {
        return "NetworkComponent{" +
                "networkIdentity=" + (networkIdentity != null ? networkIdentity.getNetId() : "null") +
                ", spawnOnStart=" + spawnOnStart +
                ", destroyOnStop=" + destroyOnStop +
                '}';
    }
}
