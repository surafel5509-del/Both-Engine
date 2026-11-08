package com.bothengine.network;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * NetworkClient
 * Client connection to a network server
 * Handles connecting, disconnecting, and message sending/receiving
 */
public class NetworkClient {
    private static final String TAG = "NetworkClient";
    
    private NetworkManager manager;
    private NetworkConfig config;
    
    private Socket socket;
    private DataOutputStream outputStream;
    private DataInputStream inputStream;
    
    private String serverAddress;
    private int serverPort;
    
    private int connectionId;
    private ConnectionState state;
    
    private Thread receiveThread;
    private boolean running;
    
    private List<NetworkMessage> messageQueue;
    
    private long lastPingTime;
    private long lastPongTime;
    private int ping;
    
    public NetworkClient(NetworkManager manager, NetworkConfig config) {
        this.manager = manager;
        this.config = config;
        this.socket = null;
        this.outputStream = null;
        this.inputStream = null;
        this.serverAddress = config.getServerAddress();
        this.serverPort = config.getServerPort();
        this.connectionId = -1;
        this.state = ConnectionState.DISCONNECTED;
        this.receiveThread = null;
        this.running = false;
        this.messageQueue = new ArrayList<>();
        this.lastPingTime = 0;
        this.lastPongTime = 0;
        this.ping = 0;
    }
    
    /**
     * Connect to the server
     */
    public void connect(String address, int port) {
        if (state != ConnectionState.DISCONNECTED) {
            Log.w(TAG, "Already connected or connecting");
            return;
        }
        
        serverAddress = address;
        serverPort = port;
        
        try {
            // Set state to connecting
            state = ConnectionState.CONNECTING;
            
            // Create socket
            socket = new Socket();
            socket.setSoTimeout(config.getConnectionTimeout());
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            
            // Connect to server
            socket.connect(new InetSocketAddress(address, port), config.getConnectionTimeout());
            
            // Get streams
            outputStream = new DataOutputStream(socket.getOutputStream());
            inputStream = new DataInputStream(socket.getInputStream());
            
            // Start receive thread
            running = true;
            receiveThread = new Thread(this::receiveLoop, "NetworkClient-Receive");
            receiveThread.start();
            
            // Set state to connected
            state = ConnectionState.CONNECTED;
            connectionId = -1; // Will be set by server
            
            Log.i(TAG, "Connected to server at " + address + ":" + port);
            
            // Fire connection event
            if (manager != null) {
                manager.fireEvent(new NetworkEvent(NetworkEventType.CONNECTED, -1));
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Connection failed: " + e.getMessage());
            disconnect();
            
            // Fire connection failed event
            if (manager != null) {
                manager.fireEvent(new NetworkEvent(NetworkEventType.CONNECT_FAILED, -1));
            }
        }
    }
    
    /**
     * Disconnect from the server
     */
    public void disconnect() {
        if (state == ConnectionState.DISCONNECTED) {
            return;
        }
        
        running = false;
        
        try {
            if (outputStream != null) {
                // Send disconnect message
                NetworkMessage disconnectMessage = new NetworkMessage(NetworkMessageType.DISCONNECT);
                sendMessage(disconnectMessage);
            }
            
            if (receiveThread != null) {
                receiveThread.interrupt();
                receiveThread = null;
            }
            
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
            
            if (socket != null) {
                socket.close();
                socket = null;
            }
            
            state = ConnectionState.DISCONNECTED;
            connectionId = -1;
            messageQueue.clear();
            
            Log.i(TAG, "Disconnected from server");
            
            // Fire disconnection event
            if (manager != null) {
                manager.fireEvent(new NetworkEvent(NetworkEventType.DISCONNECTED, -1));
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error while disconnecting: " + e.getMessage());
        }
    }
    
    /**
     * Receive loop
     */
    private void receiveLoop() {
        while (running && state == ConnectionState.CONNECTED) {
            try {
                // Read message type
                int messageType = inputStream.readInt();
                
                // Read message
                NetworkMessage message = NetworkMessage.read(inputStream);
                message.setType(messageType);
                
                // Handle message
                handleMessage(message);
                
            } catch (IOException e) {
                if (running) {
                    Log.e(TAG, "Error receiving message: " + e.getMessage());
                    disconnect();
                }
            }
        }
    }
    
    /**
     * Handle incoming message
     */
    private void handleMessage(NetworkMessage message) {
        if (message == null) return;
        
        switch (message.getType()) {
            case NetworkMessageType.CONNECT:
                // Server assigned our connection ID
                connectionId = message.readInt();
                Log.i(TAG, "Server assigned connection ID: " + connectionId);
                break;
                
            case NetworkMessageType.DISCONNECT:
                disconnect();
                break;
                
            case NetworkMessageType.PING:
                // Send pong back
                sendPong();
                break;
                
            case NetworkMessageType.PONG:
                // Calculate ping
                long currentTime = System.currentTimeMillis();
                ping = (int) (currentTime - lastPingTime);
                lastPongTime = currentTime;
                break;
                
            case NetworkMessageType.SPAWN:
                // Handle spawn message
                handleSpawnMessage(message);
                break;
                
            case NetworkMessageType.DESTROY:
                // Handle destroy message
                handleDestroyMessage(message);
                break;
                
            case NetworkMessageType.STATE_UPDATE:
                // Handle state update message
                handleStateUpdateMessage(message);
                break;
                
            case NetworkMessageType.RPC:
                // Handle RPC message
                handleRpcMessage(message);
                break;
                
            default:
                // Deliver to message handlers
                if (manager != null) {
                    manager.handleMessage(connectionId, message);
                }
                break;
        }
    }
    
    /**
     * Handle spawn message
     */
    private void handleSpawnMessage(NetworkMessage message) {
        int netId = message.readInt();
        String prefabName = message.readString();
        int ownerId = message.readInt();
        
        Log.d(TAG, "Spawn message: netId=" + netId + ", prefab=" + prefabName + ", owner=" + ownerId);
        
        // Notify manager
        if (manager != null) {
            // Create network identity
            NetworkIdentity identity = new NetworkIdentity(netId);
            identity.setPrefabName(prefabName);
            identity.setOwnerId(ownerId);
            
            manager.registerIdentity(identity);
        }
    }
    
    /**
     * Handle destroy message
     */
    private void handleDestroyMessage(NetworkMessage message) {
        int netId = message.readInt();
        
        Log.d(TAG, "Destroy message: netId=" + netId);
        
        // Notify manager
        if (manager != null) {
            NetworkIdentity identity = manager.getIdentity(netId);
            if (identity != null) {
                manager.unregisterIdentity(identity);
            }
        }
    }
    
    /**
     * Handle state update message
     */
    private void handleStateUpdateMessage(NetworkMessage message) {
        int netId = message.readInt();
        byte[] stateData = message.readByteArray();
        
        // Find the network identity
        if (manager != null) {
            NetworkIdentity identity = manager.getIdentity(netId);
            if (identity != null) {
                identity.onStateUpdate(stateData);
            }
        }
    }
    
    /**
     * Handle RPC message
     */
    private void handleRpcMessage(NetworkMessage message) {
        int netId = message.readInt();
        String methodName = message.readString();
        byte[] parameters = message.readByteArray();
        
        // Find the network identity
        if (manager != null) {
            NetworkIdentity identity = manager.getIdentity(netId);
            if (identity != null) {
                identity.onRpc(methodName, parameters);
            }
        }
    }
    
    /**
     * Send a message to the server
     */
    public void sendToServer(NetworkMessage message) {
        if (state != ConnectionState.CONNECTED || outputStream == null) {
            Log.w(TAG, "Cannot send message, not connected");
            return;
        }
        
        sendMessage(message);
    }
    
    /**
     * Send a raw message
     */
    private void sendMessage(NetworkMessage message) {
        if (message == null || outputStream == null) {
            return;
        }
        
        try {
            // Write message type
            outputStream.writeInt(message.getType());
            
            // Write message
            message.write(outputStream);
            
            outputStream.flush();
            
        } catch (IOException e) {
            Log.e(TAG, "Error sending message: " + e.getMessage());
            disconnect();
        }
    }
    
    /**
     * Send a ping to the server
     */
    public void sendPing() {
        if (state != ConnectionState.CONNECTED) return;
        
        NetworkMessage pingMessage = new NetworkMessage(NetworkMessageType.PING);
        sendMessage(pingMessage);
        lastPingTime = System.currentTimeMillis();
    }
    
    /**
     * Send a pong to the server
     */
    private void sendPong() {
        NetworkMessage pongMessage = new NetworkMessage(NetworkMessageType.PONG);
        sendMessage(pongMessage);
    }
    
    // Getters and setters
    
    public NetworkManager getManager() {
        return manager;
    }
    
    public void setManager(NetworkManager manager) {
        this.manager = manager;
    }
    
    public NetworkConfig getConfig() {
        return config;
    }
    
    public void setConfig(NetworkConfig config) {
        this.config = config;
    }
    
    public String getServerAddress() {
        return serverAddress;
    }
    
    public int getServerPort() {
        return serverPort;
    }
    
    public int getConnectionId() {
        return connectionId;
    }
    
    public ConnectionState getState() {
        return state;
    }
    
    public int getPing() {
        return ping;
    }
    
    public boolean isConnected() {
        return state == ConnectionState.CONNECTED;
    }
    
    @Override
    public String toString() {
        return "NetworkClient{" +
                "serverAddress='" + serverAddress + '\'' +
                ", serverPort=" + serverPort +
                ", connectionId=" + connectionId +
                ", state=" + state +
                ", ping=" + ping + "ms" +
                '}';
    }
    
    /**
     * Connection state enum
     */
    public enum ConnectionState {
        DISCONNECTED,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }
}
