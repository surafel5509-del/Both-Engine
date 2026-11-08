package com.bothengine.network;

import com.bothengine.core.ecs.Component;
import com.bothengine.core.ecs.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * NetworkIdentity
 * Identifies an entity on the network
 * Similar to Unity's NetworkIdentity
 */
public class NetworkIdentity {
    private int netId;
    private Entity entity;
    private String prefabName;
    private int ownerId;
    private boolean isServer;
    private boolean isClient;
    private boolean isLocalPlayer;
    
    private List<NetworkBehaviour> networkBehaviours;
    
    // State synchronization
    private boolean syncPosition;
    private boolean syncRotation;
    private boolean syncScale;
    private boolean syncVelocity;
    private boolean syncAngularVelocity;
    
    // Authority
    private boolean hasAuthority;
    private boolean serverHasAuthority;
    
    // Dirty flags
    private boolean dirtyPosition;
    private boolean dirtyRotation;
    private boolean dirtyScale;
    
    public NetworkIdentity() {
        this.netId = 0;
        this.entity = null;
        this.prefabName = "";
        this.ownerId = -1;
        this.isServer = false;
        this.isClient = false;
        this.isLocalPlayer = false;
        this.networkBehaviours = new ArrayList<>();
        this.syncPosition = true;
        this.syncRotation = true;
        this.syncScale = false;
        this.syncVelocity = false;
        this.syncAngularVelocity = false;
        this.hasAuthority = false;
        this.serverHasAuthority = true;
        this.dirtyPosition = false;
        this.dirtyRotation = false;
        this.dirtyScale = false;
    }
    
    /**
     * Create a network identity with a netId
     */
    public NetworkIdentity(int netId) {
        this();
        this.netId = netId;
    }
    
    /**
     * Initialize the network identity
     */
    public void initialize(Entity entity) {
        this.entity = entity;
        
        // Find all NetworkBehaviour components
        if (entity != null) {
            for (Component component : entity.getComponents().values()) {
                if (component instanceof NetworkBehaviour) {
                    NetworkBehaviour behaviour = (NetworkBehaviour) component;
                    behaviour.setNetworkIdentity(this);
                    networkBehaviours.add(behaviour);
                }
            }
        }
    }
    
    /**
     * Update the network identity
     */
    public void update(float deltaTime) {
        if (!hasAuthority) return;
        
        // Check for state changes
        checkDirtyFlags();
        
        // If dirty, send state update
        if (isDirty()) {
            sendStateUpdate();
            clearDirtyFlags();
        }
    }
    
    /**
     * Check for dirty flags
     */
    private void checkDirtyFlags() {
        if (entity == null) return;
        
        // Check position
        if (syncPosition) {
            // Compare with last known position
            // dirtyPosition = true;
        }
        
        // Check rotation
        if (syncRotation) {
            // Compare with last known rotation
            // dirtyRotation = true;
        }
        
        // Check scale
        if (syncScale) {
            // Compare with last known scale
            // dirtyScale = true;
        }
    }
    
    /**
     * Check if any flag is dirty
     */
    public boolean isDirty() {
        return dirtyPosition || dirtyRotation || dirtyScale;
    }
    
    /**
     * Clear all dirty flags
     */
    public void clearDirtyFlags() {
        dirtyPosition = false;
        dirtyRotation = false;
        dirtyScale = false;
    }
    
    /**
     * Send state update to server
     */
    public void sendStateUpdate() {
        if (NetworkManager.getInstance() == null) return;
        
        NetworkMessage message = new NetworkMessage(NetworkMessage.NetworkMessageType.STATE_UPDATE);
        message.writeInt(netId);
        
        // Write state data
        byte[] stateData = serializeState();
        message.writeByteArray(stateData);
        
        // Send to server
        NetworkManager.getInstance().sendToServer(message);
    }
    
    /**
     * Serialize the state
     */
    private byte[] serializeState() {
        // Serialize position, rotation, scale, etc.
        // This would be implemented based on what needs to be synchronized
        return new byte[0];
    }
    
    /**
     * Called when state update is received
     */
    public void onStateUpdate(byte[] stateData) {
        if (!hasAuthority) {
            // Deserialize and apply state
            deserializeState(stateData);
        }
    }
    
    /**
     * Deserialize the state
     */
    private void deserializeState(byte[] stateData) {
        // Deserialize and apply to entity
    }
    
    /**
     * Called when RPC is received
     */
    public void onRpc(String methodName, byte[] parameters) {
        for (NetworkBehaviour behaviour : networkBehaviours) {
            behaviour.onRpc(methodName, parameters);
        }
    }
    
    /**
     * Register a network behaviour
     */
    public void registerBehaviour(NetworkBehaviour behaviour) {
        if (behaviour != null && !networkBehaviours.contains(behaviour)) {
            behaviour.setNetworkIdentity(this);
            networkBehaviours.add(behaviour);
        }
    }
    
    /**
     * Unregister a network behaviour
     */
    public void unregisterBehaviour(NetworkBehaviour behaviour) {
        if (behaviour != null) {
            behaviour.setNetworkIdentity(null);
            networkBehaviours.remove(behaviour);
        }
    }
    
    /**
     * Get a network behaviour by type
     */
    @SuppressWarnings("unchecked")
    public <T extends NetworkBehaviour> T getBehaviour(Class<T> behaviourClass) {
        for (NetworkBehaviour behaviour : networkBehaviours) {
            if (behaviourClass.isInstance(behaviour)) {
                return (T) behaviour;
            }
        }
        return null;
    }
    
    /**
     * Get all network behaviours
     */
    public List<NetworkBehaviour> getBehaviours() {
        return new ArrayList<>(networkBehaviours);
    }
    
    // Getters and setters
    
    public int getNetId() {
        return netId;
    }
    
    public void setNetId(int netId) {
        this.netId = netId;
    }
    
    public Entity getEntity() {
        return entity;
    }
    
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
    
    public String getPrefabName() {
        return prefabName;
    }
    
    public void setPrefabName(String prefabName) {
        this.prefabName = prefabName;
    }
    
    public int getOwnerId() {
        return ownerId;
    }
    
    public void setOwnerId(int ownerId) {
        this.ownerId = ownerId;
    }
    
    public boolean isServer() {
        return isServer;
    }
    
    public void setServer(boolean server) {
        isServer = server;
    }
    
    public boolean isClient() {
        return isClient;
    }
    
    public void setClient(boolean client) {
        isClient = client;
    }
    
    public boolean isLocalPlayer() {
        return isLocalPlayer;
    }
    
    public void setLocalPlayer(boolean localPlayer) {
        isLocalPlayer = localPlayer;
    }
    
    public boolean isSyncPosition() {
        return syncPosition;
    }
    
    public void setSyncPosition(boolean syncPosition) {
        this.syncPosition = syncPosition;
    }
    
    public boolean isSyncRotation() {
        return syncRotation;
    }
    
    public void setSyncRotation(boolean syncRotation) {
        this.syncRotation = syncRotation;
    }
    
    public boolean isSyncScale() {
        return syncScale;
    }
    
    public void setSyncScale(boolean syncScale) {
        this.syncScale = syncScale;
    }
    
    public boolean isSyncVelocity() {
        return syncVelocity;
    }
    
    public void setSyncVelocity(boolean syncVelocity) {
        this.syncVelocity = syncVelocity;
    }
    
    public boolean isSyncAngularVelocity() {
        return syncAngularVelocity;
    }
    
    public void setSyncAngularVelocity(boolean syncAngularVelocity) {
        this.syncAngularVelocity = syncAngularVelocity;
    }
    
    public boolean hasAuthority() {
        return hasAuthority;
    }
    
    public void setAuthority(boolean authority) {
        hasAuthority = authority;
    }
    
    public boolean isServerHasAuthority() {
        return serverHasAuthority;
    }
    
    public void setServerHasAuthority(boolean serverHasAuthority) {
        this.serverHasAuthority = serverHasAuthority;
    }
    
    @Override
    public String toString() {
        return "NetworkIdentity{" +
                "netId=" + netId +
                ", prefabName='" + prefabName + '\'' +
                ", ownerId=" + ownerId +
                ", isServer=" + isServer +
                ", isClient=" + isClient +
                ", isLocalPlayer=" + isLocalPlayer +
                ", hasAuthority=" + hasAuthority +
                '}';
    }
}
