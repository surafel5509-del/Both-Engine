package com.bothengine.network;

/**
 * NetworkConfig
 * Configuration for network connections
 */
public class NetworkConfig {
    // Connection settings
    private String serverAddress;
    private int serverPort;
    private int maxConnections;
    private int connectionTimeout;
    private int keepAliveInterval;
    
    // Transport settings
    private TransportProtocol protocol;
    private boolean useCompression;
    private boolean useEncryption;
    private boolean useReliableMessages;
    private boolean useOrderedMessages;
    
    // Quality of Service settings
    private int reliableChannel;
    private int unreliableChannel;
    private int stateUpdateChannel;
    private int rpcChannel;
    
    // Performance settings
    private int maxMessageSize;
    private int sendRate;
    private int receiveBufferSize;
    private int sendBufferSize;
    
    // Discovery settings
    private boolean enableDiscovery;
    private int discoveryPort;
    private String discoveryBroadcastAddress;
    
    // NAT punchthrough settings
    private boolean enableNatPunchthrough;
    private int natPunchthroughPort;
    
    public NetworkConfig() {
        // Default values
        this.serverAddress = "localhost";
        this.serverPort = 7777;
        this.maxConnections = 32;
        this.connectionTimeout = 10000; // 10 seconds
        this.keepAliveInterval = 1000; // 1 second
        
        this.protocol = TransportProtocol.UDP;
        this.useCompression = false;
        this.useEncryption = false;
        this.useReliableMessages = true;
        this.useOrderedMessages = true;
        
        this.reliableChannel = 0;
        this.unreliableChannel = 1;
        this.stateUpdateChannel = 2;
        this.rpcChannel = 3;
        
        this.maxMessageSize = 1400; // MTU size
        this.sendRate = 30; // 30 messages per second
        this.receiveBufferSize = 65536;
        this.sendBufferSize = 65536;
        
        this.enableDiscovery = false;
        this.discoveryPort = 7778;
        this.discoveryBroadcastAddress = "255.255.255.255";
        
        this.enableNatPunchthrough = false;
        this.natPunchthroughPort = 7779;
    }
    
    // Getters and setters
    
    public String getServerAddress() {
        return serverAddress;
    }
    
    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }
    
    public int getServerPort() {
        return serverPort;
    }
    
    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
    
    public int getMaxConnections() {
        return maxConnections;
    }
    
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }
    
    public int getConnectionTimeout() {
        return connectionTimeout;
    }
    
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }
    
    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }
    
    public void setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
    }
    
    public TransportProtocol getProtocol() {
        return protocol;
    }
    
    public void setProtocol(TransportProtocol protocol) {
        this.protocol = protocol;
    }
    
    public boolean isUseCompression() {
        return useCompression;
    }
    
    public void setUseCompression(boolean useCompression) {
        this.useCompression = useCompression;
    }
    
    public boolean isUseEncryption() {
        return useEncryption;
    }
    
    public void setUseEncryption(boolean useEncryption) {
        this.useEncryption = useEncryption;
    }
    
    public boolean isUseReliableMessages() {
        return useReliableMessages;
    }
    
    public void setUseReliableMessages(boolean useReliableMessages) {
        this.useReliableMessages = useReliableMessages;
    }
    
    public boolean isUseOrderedMessages() {
        return useOrderedMessages;
    }
    
    public void setUseOrderedMessages(boolean useOrderedMessages) {
        this.useOrderedMessages = useOrderedMessages;
    }
    
    public int getReliableChannel() {
        return reliableChannel;
    }
    
    public void setReliableChannel(int reliableChannel) {
        this.reliableChannel = reliableChannel;
    }
    
    public int getUnreliableChannel() {
        return unreliableChannel;
    }
    
    public void setUnreliableChannel(int unreliableChannel) {
        this.unreliableChannel = unreliableChannel;
    }
    
    public int getStateUpdateChannel() {
        return stateUpdateChannel;
    }
    
    public void setStateUpdateChannel(int stateUpdateChannel) {
        this.stateUpdateChannel = stateUpdateChannel;
    }
    
    public int getRpcChannel() {
        return rpcChannel;
    }
    
    public void setRpcChannel(int rpcChannel) {
        this.rpcChannel = rpcChannel;
    }
    
    public int getMaxMessageSize() {
        return maxMessageSize;
    }
    
    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }
    
    public int getSendRate() {
        return sendRate;
    }
    
    public void setSendRate(int sendRate) {
        this.sendRate = sendRate;
    }
    
    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }
    
    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }
    
    public int getSendBufferSize() {
        return sendBufferSize;
    }
    
    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }
    
    public boolean isEnableDiscovery() {
        return enableDiscovery;
    }
    
    public void setEnableDiscovery(boolean enableDiscovery) {
        this.enableDiscovery = enableDiscovery;
    }
    
    public int getDiscoveryPort() {
        return discoveryPort;
    }
    
    public void setDiscoveryPort(int discoveryPort) {
        this.discoveryPort = discoveryPort;
    }
    
    public String getDiscoveryBroadcastAddress() {
        return discoveryBroadcastAddress;
    }
    
    public void setDiscoveryBroadcastAddress(String discoveryBroadcastAddress) {
        this.discoveryBroadcastAddress = discoveryBroadcastAddress;
    }
    
    public boolean isEnableNatPunchthrough() {
        return enableNatPunchthrough;
    }
    
    public void setEnableNatPunchthrough(boolean enableNatPunchthrough) {
        this.enableNatPunchthrough = enableNatPunchthrough;
    }
    
    public int getNatPunchthroughPort() {
        return natPunchthroughPort;
    }
    
    public void setNatPunchthroughPort(int natPunchthroughPort) {
        this.natPunchthroughPort = natPunchthroughPort;
    }
    
    @Override
    public String toString() {
        return "NetworkConfig{" +
                "serverAddress='" + serverAddress + '\'' +
                ", serverPort=" + serverPort +
                ", maxConnections=" + maxConnections +
                ", protocol=" + protocol +
                ", useCompression=" + useCompression +
                ", useEncryption=" + useEncryption +
                ", maxMessageSize=" + maxMessageSize +
                ", sendRate=" + sendRate +
                '}';
    }
    
    /**
     * Transport protocol enum
     */
    public enum TransportProtocol {
        TCP,
        UDP,
        WEBSOCKET,
        RELIABLE_UDP
    }
}
