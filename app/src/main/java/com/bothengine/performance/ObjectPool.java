package com.bothengine.performance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * ObjectPool
 * Generic object pooling system for reducing garbage collection
 * Reuses objects instead of creating new ones
 */
public class ObjectPool<T> {
    private String name;
    private ObjectFactory<T> factory;
    private ObjectReset<T> reset;
    
    private Stack<T> available;
    private List<T> inUse;
    
    private int initialSize;
    private int maxSize;
    private int growSize;
    
    private int createdCount;
    private int reusedCount;
    
    public ObjectPool(String name, ObjectFactory<T> factory) {
        this(name, factory, null);
    }
    
    public ObjectPool(String name, ObjectFactory<T> factory, ObjectReset<T> reset) {
        this.name = name;
        this.factory = factory;
        this.reset = reset;
        this.available = new Stack<>();
        this.inUse = new ArrayList<>();
        this.initialSize = 10;
        this.maxSize = 1000;
        this.growSize = 10;
        this.createdCount = 0;
        this.reusedCount = 0;
    }
    
    /**
     * Create an object pool with configuration
     */
    public static <T> ObjectPool<T> create(String name, ObjectFactory<T> factory, int initialSize, int maxSize, int growSize) {
        ObjectPool<T> pool = new ObjectPool<>(name, factory);
        pool.initialSize = initialSize;
        pool.maxSize = maxSize;
        pool.growSize = growSize;
        return pool;
    }
    
    /**
     * Initialize the pool with initial objects
     */
    public void initialize() {
        for (int i = 0; i < initialSize; i++) {
            T obj = factory.create();
            available.push(obj);
            createdCount++;
        }
    }
    
    /**
     * Get an object from the pool
     */
    public T acquire() {
        T obj;
        
        if (!available.isEmpty()) {
            obj = available.pop();
            reusedCount++;
        } else if (inUse.size() < maxSize) {
            obj = factory.create();
            createdCount++;
        } else {
            // Pool is full, grow it
            if (growSize > 0) {
                maxSize += growSize;
                obj = factory.create();
                createdCount++;
            } else {
                // Cannot grow, create anyway
                obj = factory.create();
                createdCount++;
            }
        }
        
        inUse.add(obj);
        return obj;
    }
    
    /**
     * Return an object to the pool
     */
    public void release(T obj) {
        if (obj == null) return;
        
        // Reset the object
        if (reset != null) {
            reset.reset(obj);
        }
        
        // Remove from in-use list
        inUse.remove(obj);
        
        // Add to available stack
        available.push(obj);
    }
    
    /**
     * Release all objects in a list
     */
    public void releaseAll(List<T> objects) {
        for (T obj : objects) {
            release(obj);
        }
        objects.clear();
    }
    
    /**
     * Clear the pool
     */
    public void clear() {
        available.clear();
        inUse.clear();
        createdCount = 0;
        reusedCount = 0;
    }
    
    /**
     * Pre-warm the pool by creating additional objects
     */
    public void preWarm(int count) {
        for (int i = 0; i < count; i++) {
            if (available.size() < maxSize) {
                T obj = factory.create();
                available.push(obj);
                createdCount++;
            }
        }
    }
    
    /**
     * Get statistics about the pool
     */
    public PoolStats getStats() {
        return new PoolStats(available.size(), inUse.size(), createdCount, reusedCount, maxSize);
    }
    
    // Getters and setters
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public ObjectFactory<T> getFactory() {
        return factory;
    }
    
    public void setFactory(ObjectFactory<T> factory) {
        this.factory = factory;
    }
    
    public ObjectReset<T> getReset() {
        return reset;
    }
    
    public void setReset(ObjectReset<T> reset) {
        this.reset = reset;
    }
    
    public int getInitialSize() {
        return initialSize;
    }
    
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }
    
    public int getMaxSize() {
        return maxSize;
    }
    
    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
    
    public int getGrowSize() {
        return growSize;
    }
    
    public void setGrowSize(int growSize) {
        this.growSize = growSize;
    }
    
    public int getAvailableCount() {
        return available.size();
    }
    
    public int getInUseCount() {
        return inUse.size();
    }
    
    public int getCreatedCount() {
        return createdCount;
    }
    
    public int getReusedCount() {
        return reusedCount;
    }
    
    @Override
    public String toString() {
        return "ObjectPool{" +
                "name='" + name + '\'' +
                ", available=" + available.size() +
                ", inUse=" + inUse.size() +
                ", maxSize=" + maxSize +
                ", created=" + createdCount +
                ", reused=" + reusedCount +
                '}';
    }
    
    /**
     * ObjectFactory interface
     * Creates new objects for the pool
     */
    @FunctionalInterface
    public interface ObjectFactory<T> {
        T create();
    }
    
    /**
     * ObjectReset interface
     * Resets objects when they are returned to the pool
     */
    @FunctionalInterface
    public interface ObjectReset<T> {
        void reset(T obj);
    }
    
    /**
     * PoolStats class
     * Statistics about the pool
     */
    public static class PoolStats {
        private int available;
        private int inUse;
        private int created;
        private int reused;
        private int maxSize;
        
        public PoolStats(int available, int inUse, int created, int reused, int maxSize) {
            this.available = available;
            this.inUse = inUse;
            this.created = created;
            this.reused = reused;
            this.maxSize = maxSize;
        }
        
        // Getters
        
        public int getAvailable() {
            return available;
        }
        
        public int getInUse() {
            return inUse;
        }
        
        public int getCreated() {
            return created;
        }
        
        public int getReused() {
            return reused;
        }
        
        public int getMaxSize() {
            return maxSize;
        }
        
        public float getReuseRatio() {
            return created > 0 ? (float) reused / created : 0f;
        }
        
        @Override
        public String toString() {
            return "PoolStats{" +
                    "available=" + available +
                    ", inUse=" + inUse +
                    ", created=" + created +
                    ", reused=" + reused +
                    ", reuseRatio=" + String.format("%.2f%%", getReuseRatio() * 100) +
                    '}';
        }
    }
    
    /**
     * ObjectPoolManager
     * Manages multiple object pools
     */
    public static class ObjectPoolManager {
        private static ObjectPoolManager instance;
        
        private Map<String, ObjectPool<?>> pools;
        
        private ObjectPoolManager() {
            this.pools = new HashMap<>();
        }
        
        /**
         * Get the singleton instance
         */
        public static synchronized ObjectPoolManager getInstance() {
            if (instance == null) {
                instance = new ObjectPoolManager();
            }
            return instance;
        }
        
        /**
         * Create a new pool
         */
        public <T> ObjectPool<T> createPool(String name, ObjectFactory<T> factory) {
            ObjectPool<T> pool = new ObjectPool<>(name, factory);
            pools.put(name, pool);
            return pool;
        }
        
        /**
         * Create a new pool with configuration
         */
        public <T> ObjectPool<T> createPool(String name, ObjectFactory<T> factory, int initialSize, int maxSize, int growSize) {
            ObjectPool<T> pool = ObjectPool.create(name, factory, initialSize, maxSize, growSize);
            pools.put(name, pool);
            return pool;
        }
        
        /**
         * Get a pool by name
         */
        @SuppressWarnings("unchecked")
        public <T> ObjectPool<T> getPool(String name) {
            return (ObjectPool<T>) pools.get(name);
        }
        
        /**
         * Check if a pool exists
         */
        public boolean hasPool(String name) {
            return pools.containsKey(name);
        }
        
        /**
         * Remove a pool
         */
        public void removePool(String name) {
            ObjectPool<?> pool = pools.remove(name);
            if (pool != null) {
                pool.clear();
            }
        }
        
        /**
         * Clear all pools
         */
        public void clearAll() {
            for (ObjectPool<?> pool : pools.values()) {
                pool.clear();
            }
            pools.clear();
        }
        
        /**
         * Initialize all pools
         */
        public void initializeAll() {
            for (ObjectPool<?> pool : pools.values()) {
                pool.initialize();
            }
        }
        
        /**
         * Get statistics for all pools
         */
        public Map<String, PoolStats> getAllStats() {
            Map<String, PoolStats> stats = new HashMap<>();
            for (Map.Entry<String, ObjectPool<?>> entry : pools.entrySet()) {
                stats.put(entry.getKey(), entry.getValue().getStats());
            }
            return stats;
        }
        
        /**
         * Shutdown the pool manager
         */
        public void shutdown() {
            clearAll();
            instance = null;
        }
        
        // Getters and setters
        
        public Map<String, ObjectPool<?>> getPools() {
            return new HashMap<>(pools);
        }
        
        @Override
        public String toString() {
            return "ObjectPoolManager{" +
                    "pools=" + pools.size() +
                    '}';
        }
    }
}
