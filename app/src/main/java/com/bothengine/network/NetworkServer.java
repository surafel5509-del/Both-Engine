package com.bothengine.network;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * NetworkServer
 * Server for hosting network connections
 * Handles accepting connections and managing clients
 */
public class NetworkServer {
    private static final String TAG = "NetworkServer";
    
    private NetworkManager manager;
    private NetworkConfig config;
    
    private ServerSocket serverSocket;
    private int port;
    
    private List<ClientConnection> clients;
    private int nextConnectionId;
    private int nextNetId;
    
    private ExecutorService threadPool;
    private boolean running;
    
    private Thread acceptThread;
    
    public NetworkServer(NetworkManager manager, NetworkConfig config) {
        this.manager = manager;
        this.config = config;
        this.serverSocket = null;
        this.port = config.getServerPort();
        this.clients = new ArrayList<>();
        this.nextConnectionId = 1;
        this.nextNetId = 1;
        this.threadPool = Executors.newCachedThreadPool();
        this.running = false;
        this.acceptThread = null;
    }
    
    /**
     * Start the server
     */
    public void start(int port) {
        if (running) {
            Log.w(TAG, "Server already running");
            return;
        }
        
        this.port = port;
        
        try {
            // Create server socket
            serverSocket = new ServerSocket();
            serverSocket.setReuseAddress(true);
            serverSocket.bind(new InetSocketAddress(port), config.getMaxConnections());
            
            // Start accept thread
            running = true;
            acceptThread = new Thread(this::acceptLoop, "NetworkServer-Accept");
            acceptThread.start();
            
            Log.i(TAG, "Server started on port " + port);
            
            // Fire server started event
            if (manager != null) {
                manager.fireEvent(new NetworkEvent(NetworkEventType.SERVER_STARTED, -1));
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Failed to start server: " + e.getMessage());
            running = false;
        }
    }
    
    /**
     * Stop the server
     */
    public void stop() {
        if (!running) {
            return;
        }
        
        running = false;
        
        try {
            if (acceptThread != null) {
                acceptThread.interrupt();
                acceptThread = null;
            }
            
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
            
            // Disconnect all clients
            for (ClientConnection client : clients) {
                client.disconnect();
            }
            clients.clear();
            
            threadPool.shutdownNow();
            
            Log.i(TAG, "Server stopped");
            
            // Fire server stopped event
            if (manager != null) {
                manager.fireEvent(new NetworkEvent(NetworkEventType.SERVER_STOPPED, -1));
            }
            
        } catch (IOException e) {
            Log.e(TAG, "Error while stopping server: " + e.getMessage());
        }
    }
    
    /**
     * Accept loop
     */
    private void acceptLoop() {
        while (running) {
            try {
                // Accept new connection
                Socket socket = serverSocket.accept();
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                
                // Create client connection
                int connectionId = nextConnectionId++;
                ClientConnection client = new ClientConnection(connectionId, socket, this);
                
                // Add to clients list
                clients.add(client);
                
                // Register with manager
                if (manager != null) {
                    NetworkConnection networkConnection = new NetworkConnection(connectionId, socket.getInetAddress().getHostAddress());
                    manager.addConnection(networkConnection);
                }
                
                // Start client thread
                threadPool.execute(client::handleConnection);
                
                Log.i(TAG, "Client connected: " + socket.getInetAddress().getHostAddress() + ", connectionId=" + connectionId);
                
            } catch (IOException e) {
                if (running) {
                    Log.e(TAG, "Error accepting connection: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Spawn a network identity
     */
    public void spawnIdentity(NetworkIdentity identity) {
        if (identity == null) return;
        
        // Assign netId if not set
        if (identity.getNetId() <= 0) {
            identity.setNetId(nextNetId++);
        }
        
        // Create spawn message
        NetworkMessage spawnMessage = new NetworkMessage(NetworkMessageType.SPAWN);
        spawnMessage.writeInt(identity.getNetId());
        spawnMessage.writeString(identity.getPrefabName());
        spawnMessage.writeInt(identity.getOwnerId());
        
        // Send to all clients
        sendToAll(spawnMessage);
        
        Log.d(TAG, "Spawned identity: netId=" + identity.getNetId() + ", prefab=" + identity.getPrefabName());
    }
    
    /**
     * Destroy a network identity
     */
    public void destroyIdentity(int netId) {
        // Create destroy message
        NetworkMessage destroyMessage = new NetworkMessage(NetworkMessageType.DESTROY);
        destroyMessage.writeInt(netId);
        
        // Send to all clients
        sendToAll(destroyMessage);
        
        Log.d(TAG, "Destroyed identity: netId=" + netId);
    }
    
    /**
     * Send a message to all clients
     */
    public void sendToAll(NetworkMessage message) {
        if (message == null) return;
        
        for (ClientConnection client : clients) {
            client.sendMessage(message);
        }
    }
    
    /**
     * Send a message to a specific client
     */
    public void sendTo(int connectionId, NetworkMessage message) {
        if (message == null) return;
        
        for (ClientConnection client : clients) {
            if (client.getConnectionId() == connectionId) {
                client.sendMessage(message);
                break;
            }
        }
    }
    
    /**
     * Remove a client
     */
    public void removeClient(ClientConnection client) {
        clients.remove(client);
        
        // Unregister from manager
        if (manager != null) {
            manager.removeConnection(client.getConnectionId());
        }
        
        Log.i(TAG, "Client disconnected: connectionId=" + client.getConnectionId());
    }
    
    /**
     * Get a client by connection ID
     */
    public ClientConnection getClient(int connectionId) {
        for (ClientConnection client : clients) {
            if (client.getConnectionId() == connectionId) {
                return client;
            }
        }
        return null;
    }
    
    /**
     * Get all clients
     */
    public List<ClientConnection> getAllClients() {
        return new ArrayList<>(clients);
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
    
    public int getPort() {
        return port;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    public int getClientCount() {
        return clients.size();
    }
    
    @Override
    public String toString() {
        return "NetworkServer{" +
                "port=" + port +
                ", running=" + running +
                ", clients=" + clients.size() +
                '}';
    }
    
    /**
     * ClientConnection inner class
     * Handles communication with a single client
     */
    private class ClientConnection {
        private int connectionId;
        private Socket socket;
        private DataInputStream inputStream;
        private DataOutputStream outputStream;
        private NetworkServer server;
        
        private boolean connected;
        
        public ClientConnection(int connectionId, Socket socket, NetworkServer server) {
            this.connectionId = connectionId;
            this.socket = socket;
            this.server = server;
            this.connected = true;
            
            try {
                this.inputStream = new DataInputStream(socket.getInputStream());
                this.outputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                Log.e(TAG, "Error creating client connection streams: " + e.getMessage());
                this.connected = false;
            }
        }
        
        /**
         * Handle connection
         */
        public void handleConnection() {
            try {
                // Send connection ID to client
                NetworkMessage connectMessage = new NetworkMessage(NetworkMessageType.CONNECT);
                connectMessage.writeInt(connectionId);
                sendMessage(connectMessage);
                
                // Main receive loop
                while (connected) {
                    // Read message type
                    int messageType = inputStream.readInt();
                    
                    // Read message
                    NetworkMessage message = NetworkMessage.read(inputStream);
                    message.setType(messageType);
                    
                    // Handle message
                    handleMessage(message);
                }
                
            } catch (IOException e) {
                Log.e(TAG, "Error handling client connection: " + e.getMessage());
                disconnect();
            } finally {
                disconnect();
            }
        }
        
        /**
         * Handle incoming message
         */
        private void handleMessage(NetworkMessage message) {
            if (message == null) return;
            
            switch (message.getType()) {
                case NetworkMessageType.DISCONNECT:
                    disconnect();
                    break;
                    
                case NetworkMessageType.PING:
                    // Send pong back
                    NetworkMessage pongMessage = new NetworkMessage(NetworkMessageType.PONG);
                    sendMessage(pongMessage);
                    break;
                    
                case NetworkMessageType.STATE_UPDATE:
                    // Forward to all other clients
                    int netId = message.readInt();
                    if (server != null) {
                        server.sendToAll(message);
                    }
                    break;
                    
                case NetworkMessageType.RPC:
                    // Forward to all other clients
                    if (server != null) {
                        server.sendToAll(message);
                    }
                    break;
                    
                default:
                    // Deliver to manager
                    if (server != null && server.manager != null) {
                        server.manager.handleMessage(connectionId, message);
                    }
                    break;
            }
        }
        
        /**
         * Send a message to this client
         */
        public void sendMessage(NetworkMessage message) {
            if (!connected || outputStream == null) return;
            
            try {
                // Write message type
                outputStream.writeInt(message.getType());
                
                // Write message
                message.write(outputStream);
                
                outputStream.flush();
                
            } catch (IOException e) {
                Log.e(TAG, "Error sending message to client: " + e.getMessage());
                disconnect();
            }
        }
        
        /**
         * Disconnect this client
         */
        public void disconnect() {
            if (!connected) return;
            
            connected = false;
            
            try {
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
                
                // Remove from server
                if (server != null) {
                    server.removeClient(this);
                }
                
            } catch (IOException e) {
                Log.e(TAG, "Error disconnecting client: " + e.getMessage());
            }
        }
        
        public int getConnectionId() {
            return connectionId;
        }
        
        public boolean isConnected() {
            return connected;
        }
    }
}
