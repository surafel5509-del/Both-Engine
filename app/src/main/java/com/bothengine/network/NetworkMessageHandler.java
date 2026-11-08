package com.bothengine.network;

/**
 * NetworkMessageHandler
 * Interface for handling network messages
 */
public interface NetworkMessageHandler {
    /**
     * Handle an incoming network message
     * 
     * @param connectionId The connection ID that sent the message
     * @param message The network message
     */
    void handleMessage(int connectionId, NetworkMessage message);
}
