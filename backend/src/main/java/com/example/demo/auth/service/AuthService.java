package com.example.demo.auth.service;

import com.example.demo.auth.dto.AuthResponse;
import com.example.demo.auth.util.JwtUtil;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.LegacyAddress;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

@Service
public class AuthService {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthResponse authenticateWithPassphrase(String passphrase) throws Exception {
        if (passphrase == null || passphrase.trim().isEmpty()) {
            throw new Exception("Passphrase cannot be empty");
        }

        String walletAddress = generateWalletFromPassphrase(passphrase);
        String token = jwtUtil.generateToken(walletAddress);

        return new AuthResponse(walletAddress, token, "Authentication successful");
    }

    public String generateWalletFromPassphrase(String passphrase) throws NoSuchAlgorithmException {
        // Generate a Bitcoin Testnet3 address using BitcoinJ
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(passphrase.getBytes(StandardCharsets.UTF_8));
        
        // Use the hash as seed for ECKey (deterministic key generation)
        ECKey key = ECKey.fromPrivate(hash);
        
        // Get Bitcoin Testnet3 parameters
        NetworkParameters params = TestNet3Params.get();
        
        // Generate the legacy P2PKH address (starts with 'm' or 'n' for testnet)
        return LegacyAddress.fromKey(params, key).toString();
    }

    public boolean validateToken(String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}