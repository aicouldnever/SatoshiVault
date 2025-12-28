package com.wallet.app.service;

import com.google.gson.JsonObject;
import com.wallet.app.model.WalletModel;
import com.wallet.app.util.ConversionUtil;
import com.wallet.app.util.SessionManager;

/**
 * Real implementation of WalletService that calls backend REST API.
 */
public class WalletServiceImpl extends ApiClient implements WalletService {
    
    private static final double MOCK_BTC_TO_USD = 42500.00;
    
    @Override
    public String getAddress() {
        return SessionManager.getWalletAddress();
    }
    
    @Override
    public double getBalance() {
        try {
            String address = SessionManager.getWalletAddress();
            if (address == null) {
                return 0.0;
            }
            
            String response = get("/api/balance/" + address);
            JsonObject json = parseJson(response);
            
            long balanceSatoshis = json.get("balance").getAsLong();
            return ConversionUtil.satoshisToBTC(balanceSatoshis);
            
        } catch (Exception e) {
            System.err.println("Error fetching balance: " + e.getMessage());
            return 0.0;
        }
    }
    
    @Override
    public WalletModel getWallet() {
        String address = getAddress();
        double balanceBTC = getBalance();
        double balanceUSD = balanceBTC * MOCK_BTC_TO_USD;
        
        return new WalletModel(address, balanceBTC, balanceUSD, false);
    }
    
    /**
     * Login with passphrase and get wallet credentials (legacy: passphrase only)
     */
    public static LoginResponse login(String passphrase) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("passphrase", passphrase);
        
        String response = post("/api/auth/login", requestBody);
        JsonObject json = parseJson(response);
        
        String token = json.get("token").getAsString();
        String address = json.get("walletAddress").getAsString();
        
        return new LoginResponse(token, address);
    }

    /**
     * Login with password + passphrase; backend will validate credentials
     */
    public static LoginResponse login(String password, String passphrase) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("password", password);
        requestBody.addProperty("passphrase", passphrase);

        String response = post("/api/auth/login", requestBody);
        JsonObject json = parseJson(response);

        String token = json.get("token").getAsString();
        String address = json.get("walletAddress").getAsString();

        return new LoginResponse(token, address);
    }

    /**
     * Register a new wallet (store password + passphrase)
     */
    public static void register(String password, String passphrase) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("password", password);
        requestBody.addProperty("passphrase", passphrase);

        post("/api/auth/register", requestBody);
    }
    
    /**
     * Generate keypair for wallet (includes private key)
     */
    public static KeypairResponse generateKeypair() throws Exception {
        String response = post("/api/generate-keypair", new JsonObject());
        JsonObject json = parseJson(response);
        
        String address = json.get("address").getAsString();
        String privateKey = json.get("privateKey").getAsString();
        String publicKey = json.get("publicKey").getAsString();
        
        return new KeypairResponse(address, privateKey, publicKey);
    }
    
    /**
     * Get address from private key
     */
    public static String getAddressFromPrivateKey(String privateKey) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("privateKey", privateKey);
        
        String response = post("/api/get-address", requestBody);
        JsonObject json = parseJson(response);
        
        return json.get("address").getAsString();
    }
    
    /**
     * Response class for login
     */
    public static class LoginResponse {
        public final String token;
        public final String address;
        
        public LoginResponse(String token, String address) {
            this.token = token;
            this.address = address;
        }
    }
    
    /**
     * Response class for keypair generation
     */
    public static class KeypairResponse {
        public final String address;
        public final String privateKey;
        public final String publicKey;
        
        public KeypairResponse(String address, String privateKey, String publicKey) {
            this.address = address;
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }
}
