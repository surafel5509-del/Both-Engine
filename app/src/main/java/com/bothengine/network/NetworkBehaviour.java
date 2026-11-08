package com.bothengine.network;

import com.bothengine.core.ecs.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * NetworkBehaviour
 * Base class for components that need network synchronization
 * Similar to Unity's NetworkBehaviour
 */
public class NetworkBehaviour extends Component {
    private NetworkIdentity networkIdentity;
    private boolean isServer;
    private boolean isClient;
    private boolean hasAuthority;
    
    // Sync variables
    private Map<String, Object> syncVars;
    private Map<String, Object> lastSyncVars;
    
    public NetworkBehaviour() {
        this.networkIdentity = null;
        this.isServer = false;
        this.isClient = false;
        this.hasAuthority = false;
        this.syncVars = new HashMap<>();
        this.lastSyncVars = new HashMap<>();
    }
    
    /**
     * Called when the component is added to an entity
     */
    @Override
    public void onAddedToEntity() {
        super.onAddedToEntity();
        
        // Find NetworkIdentity component
        // NetworkIdentity is a plain (non-ECS) object, so it is resolved through
        // the network manager that owns it rather than through Entity components.
        NetworkIdentity identity = NetworkManager.getInstance() != null
                ? NetworkManager.getInstance().getNetworkIdentity(getEntity())
                : null;
        if (identity != null) {
            setNetworkIdentity(identity);
        }
    }
    
    /**
     * Called when the component is removed from an entity
     */
    @Override
    public void onRemovedFromEntity() {
        super.onRemovedFromEntity();
        
        // Unregister from NetworkIdentity
        if (networkIdentity != null) {
            networkIdentity.unregisterBehaviour(this);
            networkIdentity = null;
        }
    }
    
    /**
     * Called when the network identity is set
     */
    public void setNetworkIdentity(NetworkIdentity networkIdentity) {
        if (this.networkIdentity != null) {
            this.networkIdentity.unregisterBehaviour(this);
        }
        
        this.networkIdentity = networkIdentity;
        
        if (networkIdentity != null) {
            isServer = networkIdentity.isServer();
            isClient = networkIdentity.isClient();
            hasAuthority = networkIdentity.hasAuthority();
        } else {
            isServer = false;
            isClient = false;
            hasAuthority = false;
        }
    }
    
    /**
     * Update the network behaviour
     */
    @Override
    public void onUpdate(float deltaTime) {
        super.onUpdate(deltaTime);
        
        // Check for sync var changes
        checkSyncVars();
    }
    
    /**
     * Check for sync variable changes
     */
    private void checkSyncVars() {
        if (!hasAuthority) return;
        
        boolean dirty = false;
        
        for (Map.Entry<String, Object> entry : syncVars.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            Object lastValue = lastSyncVars.get(key);
            
            if (lastValue == null || !lastValue.equals(value)) {
                lastSyncVars.put(key, value);
                dirty = true;
            }
        }
        
        if (dirty && networkIdentity != null) {
            networkIdentity.setDirty();
        }
    }
    
    /**
     * Called when state update is received
     */
    public void onStateUpdate(byte[] stateData) {
        // Deserialize and apply state
    }
    
    /**
     * Called when RPC is received
     */
    public void onRpc(String methodName, byte[] parameters) {
        // Find and invoke the RPC method
        try {
            java.lang.reflect.Method method = getClass().getMethod(methodName, byte[].class);
            method.invoke(this, parameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Register a sync variable
     */
    public void registerSyncVar(String name, Object value) {
        syncVars.put(name, value);
        lastSyncVars.put(name, value);
    }
    
    /**
     * Update a sync variable
     */
    public void setSyncVar(String name, Object value) {
        syncVars.put(name, value);
    }
    
    /**
     * Get a sync variable
     */
    public Object getSyncVar(String name) {
        return syncVars.get(name);
    }
    
    /**
     * Remove a sync variable
     */
    public void removeSyncVar(String name) {
        syncVars.remove(name);
        lastSyncVars.remove(name);
    }
    
    /**
     * Call an RPC on the server
     */
    public void rpc(String methodName, byte[] parameters) {
        if (networkIdentity == null) return;
        
        NetworkMessage message = new NetworkMessage(NetworkMessage.NetworkMessageType.RPC);
        message.writeInt(networkIdentity.getNetId());
        message.writeString(methodName);
        message.writeByteArray(parameters);
        
        NetworkManager.getInstance().sendToServer(message);
    }
    
    /**
     * Call an RPC on all clients
     */
    public void rpcAll(String methodName, byte[] parameters) {
        if (networkIdentity == null) return;
        
        NetworkMessage message = new NetworkMessage(NetworkMessage.NetworkMessageType.RPC);
        message.writeInt(networkIdentity.getNetId());
        message.writeString(methodName);
        message.writeByteArray(parameters);
        
        NetworkManager.getInstance().sendToAll(message);
    }
    
    // Getters and setters
    
    public NetworkIdentity getNetworkIdentity() {
        return networkIdentity;
    }
    
    public boolean isServer() {
        return isServer;
    }
    
    public boolean isClient() {
        return isClient;
    }
    
    public boolean hasAuthority() {
        return hasAuthority;
    }
    
    public void setAuthority(boolean authority) {
        hasAuthority = authority;
    }
    
    @Override
    public String toString() {
        return "NetworkBehaviour{" +
                "networkIdentity=" + (networkIdentity != null ? networkIdentity.getNetId() : "null") +
                ", isServer=" + isServer +
                ", isClient=" + isClient +
                ", hasAuthority=" + hasAuthority +
                '}';
    }
}
