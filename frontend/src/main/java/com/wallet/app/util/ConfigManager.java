package com.wallet.app.util;

import java.util.prefs.Preferences;

/**
 * Configuration manager for application settings.
 * Uses Java Preferences API for persistence.
 */
public class ConfigManager {
    
    private static final Preferences prefs = Preferences.userNodeForPackage(ConfigManager.class);
    
    private static final String BACKEND_URL_KEY = "backendUrl";
    private static final String DEFAULT_BACKEND_URL = "http://localhost:8080";
    
    private static final String BACKEND_TIMEOUT_KEY = "backendTimeout";
    private static final int DEFAULT_BACKEND_TIMEOUT = 30000; // 30 seconds
    
    /**
     * Get backend URL
     */
    public static String getBackendUrl() {
        return prefs.get(BACKEND_URL_KEY, DEFAULT_BACKEND_URL);
    }
    
    /**
     * Set backend URL
     */
    public static void setBackendUrl(String url) {
        prefs.put(BACKEND_URL_KEY, url);
    }
    
    /**
     * Get backend timeout in milliseconds
     */
    public static int getBackendTimeout() {
        return prefs.getInt(BACKEND_TIMEOUT_KEY, DEFAULT_BACKEND_TIMEOUT);
    }
    
    /**
     * Set backend timeout in milliseconds
     */
    public static void setBackendTimeout(int timeout) {
        prefs.putInt(BACKEND_TIMEOUT_KEY, timeout);
    }
    
    /**
     * Reset all configuration to defaults
     */
    public static void resetToDefaults() {
        prefs.remove(BACKEND_URL_KEY);
        prefs.remove(BACKEND_TIMEOUT_KEY);
    }
}
