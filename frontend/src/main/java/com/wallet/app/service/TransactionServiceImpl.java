package com.wallet.app.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wallet.app.model.TransactionModel;
import com.wallet.app.util.ConversionUtil;
import com.wallet.app.util.SessionManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Real implementation of TransactionService that calls backend REST API.
 */
public class TransactionServiceImpl extends ApiClient implements TransactionService {
    
    @Override
    public List<TransactionModel> getTransactions() {
        try {
            String address = SessionManager.getWalletAddress();
            if (address == null) {
                return new ArrayList<>();
            }
            
            String response = get("/api/transactions/" + address + "?limit=50");
            JsonObject json = parseJson(response);
            
            List<TransactionModel> transactions = new ArrayList<>();
            JsonArray txArray = json.getAsJsonArray("transactions");
            
            for (int i = 0; i < txArray.size(); i++) {
                JsonObject tx = txArray.get(i).getAsJsonObject();
                
                String txId = tx.get("txid").getAsString();
                long amountSatoshis = tx.get("amount").getAsLong();
                double amountBTC = ConversionUtil.satoshisToBTC(amountSatoshis);
                
                // Determine status based on confirmations
                int confirmations = tx.has("confirmations") ? tx.get("confirmations").getAsInt() : 0;
                TransactionModel.Status status = confirmations >= 1 ? 
                    TransactionModel.Status.RECEIVED : TransactionModel.Status.PENDING;
                
                // Use current time if timestamp not available
                LocalDateTime date = tx.has("timestamp") ? 
                    ConversionUtil.timestampToLocalDateTime(tx.get("timestamp").getAsLong()) :
                    LocalDateTime.now();
                
                transactions.add(new TransactionModel(txId, date, amountBTC, status));
            }
            
            return transactions;
            
        } catch (Exception e) {
            System.err.println("Error fetching transactions: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean sendTransaction(String address, double amount) {
        try {
            String fromAddress = SessionManager.getWalletAddress();
            String privateKey = SessionManager.getPrivateKey();
            
            if (fromAddress == null || privateKey == null) {
                throw new Exception("Not logged in or private key not set");
            }
            
            long amountSatoshis = ConversionUtil.btcToSatoshis(amount);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fromAddress", fromAddress);
            requestBody.put("toAddress", address);
            requestBody.put("amount", amountSatoshis);
            requestBody.put("privateKey", privateKey);
            
            String response = post("/api/send", requestBody);
            JsonObject json = parseJson(response);
            
            return json.get("success").getAsBoolean();
            
        } catch (Exception e) {
            System.err.println("Error sending transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Send transaction and return transaction ID
     */
    public String sendTransactionWithTxId(String toAddress, double amount) throws Exception {
        String fromAddress = SessionManager.getWalletAddress();
        String privateKey = SessionManager.getPrivateKey();
        
        if (fromAddress == null || privateKey == null) {
            throw new Exception("Not logged in or private key not set");
        }
        
        long amountSatoshis = ConversionUtil.btcToSatoshis(amount);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("fromAddress", fromAddress);
        requestBody.put("toAddress", toAddress);
        requestBody.put("amount", amountSatoshis);
        requestBody.put("privateKey", privateKey);
        
        String response = post("/api/send", requestBody);
        JsonObject json = parseJson(response);
        
        if (json.get("success").getAsBoolean()) {
            return json.get("txId").getAsString();
        } else {
            throw new Exception("Transaction failed");
        }
    }
    
    /**
     * Estimate transaction fee
     */
    public static FeeEstimate estimateFee(int inputs, int outputs) throws Exception {
        Map<String, Integer> requestBody = new HashMap<>();
        requestBody.put("inputs", inputs);
        requestBody.put("outputs", outputs);
        
        String response = post("/api/fee-estimate", requestBody);
        JsonObject json = parseJson(response);
        
        long feeSatoshis = json.get("estimatedFee").getAsLong();
        double feeBTC = ConversionUtil.satoshisToBTC(feeSatoshis);
        int estimatedSize = json.get("estimatedSize").getAsInt();
        
        return new FeeEstimate(feeBTC, feeSatoshis, estimatedSize);
    }
    
    /**
     * Check if transaction appears in transaction history (polling)
     */
    public boolean isTransactionConfirmed(String txId, int maxAttempts, int delayMs) {
        for (int i = 0; i < maxAttempts; i++) {
            try {
                Thread.sleep(delayMs);
                List<TransactionModel> transactions = getTransactions();
                
                for (TransactionModel tx : transactions) {
                    if (tx.getTxId().equals(txId)) {
                        return true;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }
    
    /**
     * Fee estimate response
     */
    public static class FeeEstimate {
        public final double feeBTC;
        public final long feeSatoshis;
        public final int estimatedSize;
        
        public FeeEstimate(double feeBTC, long feeSatoshis, int estimatedSize) {
            this.feeBTC = feeBTC;
            this.feeSatoshis = feeSatoshis;
            this.estimatedSize = estimatedSize;
        }
    }
}
