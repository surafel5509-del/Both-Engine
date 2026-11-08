package com.bothengine.platform;

/**
 * PlatformUtils
 * Utility methods for platform detection and platform-specific operations
 */
public class PlatformUtils {
    private static Platform currentPlatform;
    
    static {
        // Detect current platform
        String osName = System.getProperty("os.name").toLowerCase();
        
        if (osName.contains("android")) {
            currentPlatform = Platform.ANDROID;
        } else if (osName.contains("ios") || osName.contains("iphone") || osName.contains("ipad")) {
            currentPlatform = Platform.IOS;
        } else if (osName.contains("win")) {
            currentPlatform = Platform.WINDOWS;
        } else if (osName.contains("mac")) {
            currentPlatform = Platform.MAC;
        } else if (osName.contains("linux")) {
            currentPlatform = Platform.LINUX;
        } else {
            currentPlatform = Platform.UNKNOWN;
        }
    }
    
    /**
     * Get the current platform
     */
    public static Platform getCurrentPlatform() {
        return currentPlatform;
    }
    
    /**
     * Set the current platform (for testing or manual override)
     */
    public static void setCurrentPlatform(Platform platform) {
        currentPlatform = platform;
    }
    
    /**
     * Check if running on Android
     */
    public static boolean isAndroid() {
        return currentPlatform == Platform.ANDROID;
    }
    
    /**
     * Check if running on iOS
     */
    public static boolean isIOS() {
        return currentPlatform == Platform.IOS;
    }
    
    /**
     * Check if running on Windows
     */
    public static boolean isWindows() {
        return currentPlatform == Platform.WINDOWS;
    }
    
    /**
     * Check if running on Mac
     */
    public static boolean isMac() {
        return currentPlatform == Platform.MAC;
    }
    
    /**
     * Check if running on Linux
     */
    public static boolean isLinux() {
        return currentPlatform == Platform.LINUX;
    }
    
    /**
     * Check if running on Web
     */
    public static boolean isWeb() {
        return currentPlatform == Platform.WEB;
    }
    
    /**
     * Check if running on a mobile platform
     */
    public static boolean isMobile() {
        return isAndroid() || isIOS();
    }
    
    /**
     * Check if running on a desktop platform
     */
    public static boolean isDesktop() {
        return isWindows() || isMac() || isLinux();
    }
    
    /**
     * Get the platform name
     */
    public static String getPlatformName() {
        return currentPlatform.name();
    }
    
    /**
     * Get the OS name
     */
    public static String getOSName() {
        return System.getProperty("os.name");
    }
    
    /**
     * Get the OS version
     */
    public static String getOSVersion() {
        return System.getProperty("os.version");
    }
    
    /**
     * Get the Java version
     */
    public static String getJavaVersion() {
        return System.getProperty("java.version");
    }
    
    /**
     * Get the Java vendor
     */
    public static String getJavaVendor() {
        return System.getProperty("java.vendor");
    }
    
    /**
     * Get the user directory
     */
    public static String getUserDir() {
        return System.getProperty("user.dir");
    }
    
    /**
     * Get the user home directory
     */
    public static String getUserHome() {
        return System.getProperty("user.home");
    }
    
    /**
     * Get the file separator
     */
    public static String getFileSeparator() {
        return System.getProperty("file.separator");
    }
    
    /**
     * Get the path separator
     */
    public static String getPathSeparator() {
        return System.getProperty("path.separator");
    }
    
    /**
     * Get the line separator
     */
    public static String getLineSeparator() {
        return System.getProperty("line.separator");
    }
    
    /**
     * Get the number of available processors
     */
    public static int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }
    
    /**
     * Get the total memory
     */
    public static long getTotalMemory() {
        return Runtime.getRuntime().totalMemory();
    }
    
    /**
     * Get the free memory
     */
    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
    
    /**
     * Get the max memory
     */
    public static long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }
    
    /**
     * Get the used memory
     */
    public static long getUsedMemory() {
        return getTotalMemory() - getFreeMemory();
    }
    
    /**
     * Get the memory usage percentage
     */
    public static float getMemoryUsagePercentage() {
        return (float) getUsedMemory() / getMaxMemory() * 100f;
    }
    
    /**
     * Check if the platform is 64-bit
     */
    public static boolean is64Bit() {
        String arch = System.getProperty("os.arch");
        return arch != null && arch.contains("64");
    }
    
    /**
     * Check if the platform is little-endian
     */
    public static boolean isLittleEndian() {
        return true; // Java is always big-endian, but most platforms are little-endian
    }
}
