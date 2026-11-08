package com.bothengine.network;

/**
 * NetworkConnection
 * Represents a connection between a client and server
 */
public class NetworkConnection {
    private int connectionId;
    private String address;
    private String hostName;
    private int port;
    private boolean isConnected;
    private long connectTime;
    private long lastActivityTime;
    
    public NetworkConnection(int connectionId, String address) {
        this.connectionId = connectionId;
        this.address = address;
        this.hostName = address;
        this.port = 0;
        this.isConnected = true;
        this.connectTime = System.currentTimeMillis();
        this.lastActivityTime = connectTime;
    }
    
    /**
     * Create a network connection with all details
     */
    public NetworkConnection(int connectionId, String address, String hostName, int port) {
        this.connectionId = connectionId;
        this.address = address;
        this.hostName = hostName;
        this.port = port;
        this.isConnected = true;
        this.connectTime = System.currentTimeMillis();
        this.lastActivityTime = connectTime;
    }
    
    /**
     * Update the last activity time
     */
    public void updateActivity() {
        lastActivityTime = System.currentTimeMillis();
    }
    
    /**
     * Get the time since last activity
     */
    public long getTimeSinceLastActivity() {
        return System.currentTimeMillis() - lastActivityTime;
    }
    
    /**
     * Get the connection duration
     */
    public long getConnectionDuration() {
        return System.currentTimeMillis() - connectTime;
    }
    
    // Getters and setters
    
    public int getConnectionId() {
        return connectionId;
    }
    
    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    public void setHostName(String hostName) {
        this.hostName = hostName;
    }
    
    public int getPort() {
        return port;
    }
    
    public void setPort(int port) {
        this.port = port;
    }
    
    public boolean isConnected() {
        return isConnected;
    }
    
    public void setConnected(boolean connected) {
        isConnected = connected;
    }
    
    public long getConnectTime() {
        return connectTime;
    }
    
    public void setConnectTime(long connectTime) {
        this.connectTime = connectTime;
    }
    
    public long getLastActivityTime() {
        return lastActivityTime;
    }
    
    public void setLastActivityTime(long lastActivityTime) {
        this.lastActivityTime = lastActivityTime;
    }
    
    @Override
    public String toString() {
        return "NetworkConnection{" +
                "connectionId=" + connectionId +
                ", address='" + address + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", isConnected=" + isConnected +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetworkConnection that = (NetworkConnection) o;
        return connectionId == that.connectionId;
    }
    
    @Override
    public int hashCode() {
        return connectionId;
    }
}
