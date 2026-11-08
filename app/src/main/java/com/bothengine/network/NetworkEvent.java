package com.bothengine.network;

/**
 * NetworkEvent
 * Represents a network event
 */
public class NetworkEvent {
    private NetworkEventType type;
    private int connectionId;
    private String message;
    private Object data;
    
    public NetworkEvent(NetworkEventType type) {
        this(type, -1);
    }
    
    public NetworkEvent(NetworkEventType type, int connectionId) {
        this(type, connectionId, null);
    }
    
    public NetworkEvent(NetworkEventType type, int connectionId, String message) {
        this(type, connectionId, message, null);
    }
    
    public NetworkEvent(NetworkEventType type, int connectionId, String message, Object data) {
        this.type = type;
        this.connectionId = connectionId;
        this.message = message;
        this.data = data;
    }
    
    // Getters and setters
    
    public NetworkEventType getType() {
        return type;
    }
    
    public void setType(NetworkEventType type) {
        this.type = type;
    }
    
    public int getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public Object getData() {
        return data;
    }
    
    public void setData(Object data) {
        this.data = data;
    }
    
    @Override
    public String toString() {
        return "NetworkEvent{" +
                "type=" + type +
                ", connectionId=" + connectionId +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
    
    /**
     * Network event type enum
     */
    public enum NetworkEventType {
        // Connection events
        CONNECTED,
        CONNECT_FAILED,
        DISCONNECTED,
        
        // Server events
        SERVER_STARTED,
        SERVER_STOPPED,
        
        // Client events
        CLIENT_CONNECTED,
        CLIENT_DISCONNECTED,
        
        // Network events
        NETWORK_ERROR,
        NETWORK_LATENCY_WARNING,
        
        // Message events
        MESSAGE_RECEIVED,
        MESSAGE_SENT,
        
        // Custom events
        CUSTOM
    }
}
