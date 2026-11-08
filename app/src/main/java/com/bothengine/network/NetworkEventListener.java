package com.bothengine.network;

/**
 * NetworkEventListener
 * Interface for listening to network events
 */
public interface NetworkEventListener {
    /**
     * Called when a network event occurs
     * 
     * @param event The network event
     */
    void onNetworkEvent(NetworkEvent event);
}
