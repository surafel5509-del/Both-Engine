package com.bothengine.network;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * NetworkManager
 * Main networking controller for the engine
 * Manages clients, servers, and network messages
 * Similar to Unity's NetworkManager
 */
public class NetworkManager {
    private static final String TAG = "NetworkManager";
    
    private static NetworkManager instance;
    
    private NetworkConfig config;
    private NetworkClient client;
    private NetworkServer server;
    
    private Map<Integer, NetworkConnection> connections;
    private Map<Integer, NetworkIdentity> networkIdentities;
    
    private List<NetworkMessageHandler> messageHandlers;
    private List<NetworkEventListener> eventListeners;
    
    private boolean initialized;
    private boolean isClient;
    private boolean isServer;
    private boolean isHost;
    
    public NetworkManager() {
        this.config = new NetworkConfig();
        this.client = null;
        this.server = null;
        this.connections = new HashMap<>();
        this.networkIdentities = new HashMap<>();
        this.messageHandlers = new ArrayList<>();
        this.eventListeners = new ArrayList<>();
        this.initialized = false;
        this.isClient = false;
        this.isServer = false;
        this.isHost = false;
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized NetworkManager getInstance() {
        if (instance == null) {
            instance = new NetworkManager();
        }
        return instance;
    }
    
    /**
     * Initialize the network manager
     */
    public void initialize(NetworkConfig config) {
        this.config = config;
        this.initialized = true;
        
        Log.i(TAG, "NetworkManager initialized");
    }
    
    /**
     * Initialize with default config
     */
    public void initialize() {
        initialize(new NetworkConfig());
    }
    
    /**
     * Start as a client
     */
    public void startClient(String address, int port) {
        if (!initialized) {
            initialize();
        }
        
        // Shutdown existing client/server
        shutdown();
        
        // Create and start client
        client = new NetworkClient(this, config);
        client.connect(address, port);
        
        isClient = true;
        isServer = false;
        isHost = false;
        
        Log.i(TAG, "Started client, connecting to " + address + ":" + port);
    }
    
    /**
     * Start as a server
     */
    public void startServer(int port) {
        if (!initialized) {
            initialize();
        }
        
        // Shutdown existing client/server
        shutdown();
        
        // Create and start server
        server = new NetworkServer(this, config);
        server.start(port);
        
        isClient = false;
        isServer = true;
        isHost = false;
        
        Log.i(TAG, "Started server on port " + port);
    }
    
    /**
     * Start as a host (client + server)
     */
    public void startHost(int port) {
        if (!initialized) {
            initialize();
        }
        
        // Shutdown existing client/server
        shutdown();
        
        // Create and start server
        server = new NetworkServer(this, config);
        server.start(port);
        
        // Create and start client
        client = new NetworkClient(this, config);
        client.connect("localhost", port);
        
        isClient = true;
        isServer = true;
        isHost = true;
        
        Log.i(TAG, "Started host on port " + port);
    }
    
    /**
     * Stop client
     */
    public void stopClient() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
        
        isClient = false;
        isHost = false;
        
        Log.i(TAG, "Stopped client");
    }
    
    /**
     * Stop server
     */
    public void stopServer() {
        if (server != null) {
            server.stop();
            server = null;
        }
        
        isServer = false;
        isHost = false;
        
        Log.i(TAG, "Stopped server");
    }
    
    /**
     * Shutdown the network manager
     */
    public void shutdown() {
        stopClient();
        stopServer();
        
        connections.clear();
        networkIdentities.clear();
        messageHandlers.clear();
        eventListeners.clear();
        
        initialized = false;
        
        Log.i(TAG, "NetworkManager shutdown");
    }
    
    /**
     * Register a network identity
     */
    public void registerIdentity(NetworkIdentity identity) {
        if (identity == null) return;
        
        networkIdentities.put(identity.getNetId(), identity);
        
        // If server, notify all clients
        if (isServer && server != null) {
            server.spawnIdentity(identity);
        }
        
        Log.d(TAG, "Registered identity: " + identity.getNetId());
    }
    
    /**
     * Unregister a network identity
     */
    public void unregisterIdentity(NetworkIdentity identity) {
        if (identity == null) return;
        
        networkIdentities.remove(identity.getNetId());
        
        // If server, notify all clients
        if (isServer && server != null) {
            server.destroyIdentity(identity.getNetId());
        }
        
        Log.d(TAG, "Unregistered identity: " + identity.getNetId());
    }
    
    /**
     * Get a network identity by netId
     */
    public NetworkIdentity getIdentity(int netId) {
        return networkIdentities.get(netId);
    }
    
    /**
     * Send a message to all clients
     */
    public void sendToAll(NetworkMessage message) {
        if (isServer && server != null) {
            server.sendToAll(message);
        }
    }
    
    /**
     * Send a message to a specific connection
     */
    public void sendTo(int connectionId, NetworkMessage message) {
        if (isServer && server != null) {
            server.sendTo(connectionId, message);
        } else if (isClient && client != null) {
            client.sendToServer(message);
        }
    }
    
    /**
     * Send a message to the server
     */
    public void sendToServer(NetworkMessage message) {
        if (isClient && client != null) {
            client.sendToServer(message);
        }
    }
    
    /**
     * Register a message handler
     */
    public void registerMessageHandler(int messageType, NetworkMessageHandler handler) {
        // In a real implementation, we'd store handlers by message type
        if (!messageHandlers.contains(handler)) {
            messageHandlers.add(handler);
        }
    }
    
    /**
     * Unregister a message handler
     */
    public void unregisterMessageHandler(NetworkMessageHandler handler) {
        messageHandlers.remove(handler);
    }
    
    /**
     * Register an event listener
     */
    public void registerEventListener(NetworkEventListener listener) {
        if (!eventListeners.contains(listener)) {
            eventListeners.add(listener);
        }
    }
    
    /**
     * Unregister an event listener
     */
    public void unregisterEventListener(NetworkEventListener listener) {
        eventListeners.remove(listener);
    }
    
    /**
     * Handle incoming message
     */
    public void handleMessage(int connectionId, NetworkMessage message) {
        // Deliver to message handlers
        for (NetworkMessageHandler handler : messageHandlers) {
            handler.handleMessage(connectionId, message);
        }
    }
    
    /**
     * Fire network event
     */
    public void fireEvent(NetworkEvent event) {
        for (NetworkEventListener listener : eventListeners) {
            listener.onNetworkEvent(event);
        }
    }
    
    /**
     * Add a connection
     */
    public void addConnection(NetworkConnection connection) {
        connections.put(connection.getConnectionId(), connection);
        
        // Fire connection event
        fireEvent(new NetworkEvent(NetworkEvent.NetworkEventType.CLIENT_CONNECTED, connection.getConnectionId()));
    }
    
    /**
     * Remove a connection
     */
    public void removeConnection(int connectionId) {
        NetworkConnection connection = connections.remove(connectionId);
        
        if (connection != null) {
            // Fire disconnection event
            fireEvent(new NetworkEvent(NetworkEvent.NetworkEventType.CLIENT_DISCONNECTED, connectionId));
        }
    }
    
    /**
     * Get a connection by ID
     */
    public NetworkConnection getConnection(int connectionId) {
        return connections.get(connectionId);
    }
    
    /**
     * Get all connections
     */
    public List<NetworkConnection> getAllConnections() {
        return new ArrayList<>(connections.values());
    }
    
    // Getters and setters
    
    public NetworkConfig getConfig() {
        return config;
    }
    
    public void setConfig(NetworkConfig config) {
        this.config = config;
    }
    
    public NetworkClient getClient() {
        return client;
    }
    
    public NetworkServer getServer() {
        return server;
    }
    
    public Map<Integer, NetworkIdentity> getNetworkIdentities() {
        return new HashMap<>(networkIdentities);
    }
    
    public boolean isInitialized() {
        return initialized;
    }
    
    public boolean isClient() {
        return isClient;
    }
    
    public boolean isServer() {
        return isServer;
    }
    
    public boolean isHost() {
        return isHost;
    }
    
    public boolean isNetworkActive() {
        return isClient || isServer;
    }
    
    @Override
    public String toString() {
        return "NetworkManager{" +
                "isClient=" + isClient +
                ", isServer=" + isServer +
                ", isHost=" + isHost +
                ", connections=" + connections.size() +
                ", networkIdentities=" + networkIdentities.size() +
                '}';
    }
}
