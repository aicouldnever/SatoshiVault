package com.wallet.app.util;

/**
 * Session manager for storing authentication state and wallet credentials in-memory.
 * All data is lost when the application closes (for security).
 */
public class SessionManager {
    
    private static String authToken;
    private static String walletAddress;
    private static String privateKey;
    private static String passphrase;
    
    /**
     * Initialize session after successful login
     */
    public static void login(String token, String address, String privKey, String userPassphrase) {
        authToken = token;
        walletAddress = address;
        privateKey = privKey;
        passphrase = userPassphrase;
    }
    
    /**
     * Clear all session data
     */
    public static void logout() {
        authToken = null;
        walletAddress = null;
        privateKey = null;
        passphrase = null;
    }
    
    /**
     * Check if user is logged in
     */
    public static boolean isLoggedIn() {
        return authToken != null && walletAddress != null;
    }
    
    /**
     * Get authentication token
     */
    public static String getAuthToken() {
        return authToken;
    }
    
    /**
     * Get wallet address
     */
    public static String getWalletAddress() {
        return walletAddress;
    }
    
    /**
     * Get private key (WIF format)
     */
    public static String getPrivateKey() {
        return privateKey;
    }
    
    /**
     * Get passphrase
     */
    public static String getPassphrase() {
        return passphrase;
    }
    
    /**
     * Set private key (called from Settings page)
     */
    public static void setPrivateKey(String privKey) {
        privateKey = privKey;
    }
    
    /**
     * Check if private key is set
     */
    public static boolean hasPrivateKey() {
        return privateKey != null && !privateKey.isEmpty();
    }
}
