package com.bothengine.platform;

/**
 * PlatformAdapter
 * Base class for platform-specific adapters
 * Allows platform-specific code to be implemented separately
 */
public abstract class PlatformAdapter {
    private Platform platform;
    
    public PlatformAdapter(Platform platform) {
        this.platform = platform;
    }
    
    /**
     * Initialize the platform adapter
     */
    public abstract void initialize();
    
    /**
     * Shutdown the platform adapter
     */
    public abstract void shutdown();
    
    /**
     * Update the platform adapter
     */
    public abstract void update(float deltaTime);
    
    /**
     * Get the platform
     */
    public Platform getPlatform() {
        return platform;
    }
    
    /**
     * Check if this adapter supports the given platform
     */
    public boolean supportsPlatform(Platform platform) {
        return this.platform == platform;
    }
    
    @Override
    public String toString() {
        return "PlatformAdapter{" +
                "platform=" + platform +
                '}';
    }
    
    /**
     * PlatformAdapterFactory
     * Factory for creating platform adapters
     */
    public static class PlatformAdapterFactory {
        private static PlatformAdapterFactory instance;
        
        private PlatformAdapter currentAdapter;
        
        private PlatformAdapterFactory() {
            this.currentAdapter = null;
        }
        
        /**
         * Get the singleton instance
         */
        public static synchronized PlatformAdapterFactory getInstance() {
            if (instance == null) {
                instance = new PlatformAdapterFactory();
            }
            return instance;
        }
        
        /**
         * Create a platform adapter for the current platform
         */
        public PlatformAdapter createAdapter() {
            Platform platform = PlatformUtils.getCurrentPlatform();
            return createAdapter(platform);
        }
        
        /**
         * Create a platform adapter for a specific platform
         */
        public PlatformAdapter createAdapter(Platform platform) {
            switch (platform) {
                case ANDROID:
                    return new AndroidPlatformAdapter();
                case IOS:
                    return new IOSPlatformAdapter();
                case WINDOWS:
                    return new WindowsPlatformAdapter();
                case MAC:
                    return new MacPlatformAdapter();
                case LINUX:
                    return new LinuxPlatformAdapter();
                case WEB:
                    return new WebPlatformAdapter();
                default:
                    return new DefaultPlatformAdapter();
            }
        }
        
        /**
         * Set the current adapter
         */
        public void setCurrentAdapter(PlatformAdapter adapter) {
            if (currentAdapter != null) {
                currentAdapter.shutdown();
            }
            currentAdapter = adapter;
            if (currentAdapter != null) {
                currentAdapter.initialize();
            }
        }
        
        /**
         * Get the current adapter
         */
        public PlatformAdapter getCurrentAdapter() {
            if (currentAdapter == null) {
                currentAdapter = createAdapter();
            }
            return currentAdapter;
        }
        
        /**
         * Shutdown the factory
         */
        public void shutdown() {
            if (currentAdapter != null) {
                currentAdapter.shutdown();
                currentAdapter = null;
            }
            instance = null;
        }
    }
    
    /**
     * Android platform adapter
     */
    public static class AndroidPlatformAdapter extends PlatformAdapter {
        public AndroidPlatformAdapter() {
            super(Platform.ANDROID);
        }
        
        @Override
        public void initialize() {
            // Android-specific initialization
        }
        
        @Override
        public void shutdown() {
            // Android-specific shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // Android-specific update
        }
    }
    
    /**
     * iOS platform adapter
     */
    public static class IOSPlatformAdapter extends PlatformAdapter {
        public IOSPlatformAdapter() {
            super(Platform.IOS);
        }
        
        @Override
        public void initialize() {
            // iOS-specific initialization
        }
        
        @Override
        public void shutdown() {
            // iOS-specific shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // iOS-specific update
        }
    }
    
    /**
     * Windows platform adapter
     */
    public static class WindowsPlatformAdapter extends PlatformAdapter {
        public WindowsPlatformAdapter() {
            super(Platform.WINDOWS);
        }
        
        @Override
        public void initialize() {
            // Windows-specific initialization
        }
        
        @Override
        public void shutdown() {
            // Windows-specific shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // Windows-specific update
        }
    }
    
    /**
     * Mac platform adapter
     */
    public static class MacPlatformAdapter extends PlatformAdapter {
        public MacPlatformAdapter() {
            super(Platform.MAC);
        }
        
        @Override
        public void initialize() {
            // Mac-specific initialization
        }
        
        @Override
        public void shutdown() {
            // Mac-specific shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // Mac-specific update
        }
    }
    
    /**
     * Linux platform adapter
     */
    public static class LinuxPlatformAdapter extends PlatformAdapter {
        public LinuxPlatformAdapter() {
            super(Platform.LINUX);
        }
        
        @Override
        public void initialize() {
            // Linux-specific initialization
        }
        
        @Override
        public void shutdown() {
            // Linux-specific shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // Linux-specific update
        }
    }
    
    /**
     * Web platform adapter
     */
    public static class WebPlatformAdapter extends PlatformAdapter {
        public WebPlatformAdapter() {
            super(Platform.WEB);
        }
        
        @Override
        public void initialize() {
            // Web-specific initialization
        }
        
        @Override
        public void shutdown() {
            // Web-specific shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // Web-specific update
        }
    }
    
    /**
     * Default platform adapter
     */
    public static class DefaultPlatformAdapter extends PlatformAdapter {
        public DefaultPlatformAdapter() {
            super(Platform.UNKNOWN);
        }
        
        @Override
        public void initialize() {
            // Default initialization
        }
        
        @Override
        public void shutdown() {
            // Default shutdown
        }
        
        @Override
        public void update(float deltaTime) {
            // Default update
        }
    }
}
