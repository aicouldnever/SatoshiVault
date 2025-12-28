package com.wallet.app.controller;

import com.wallet.app.service.WalletServiceImpl;
import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.SessionManager;
import com.wallet.app.util.ThemeManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class SettingsController {
    
    @FXML
    private CheckBox darkThemeToggle;
    
    @FXML
    private PasswordField privateKeyField;
    
    @FXML
    private TextField verifyAddressField;
    
    @FXML
    private Button setPrivateKeyButton;
    
    @FXML
    private Button generateNewKeypairButton;
    
    @FXML
    private Label privateKeyStatusLabel;
    
    @FXML
    public void initialize() {
        // Set checkbox to current theme preference
        darkThemeToggle.setSelected(ThemeManager.isDarkThemeEnabled());
        
        // Update private key status
        updatePrivateKeyStatus();
    }
    
    private void updatePrivateKeyStatus() {
        if (SessionManager.hasPrivateKey()) {
            privateKeyStatusLabel.setText("✓ Private key is set");
            privateKeyStatusLabel.setStyle("-fx-text-fill: #10b981;");
        } else {
            privateKeyStatusLabel.setText("⚠ No private key set - transactions disabled");
            privateKeyStatusLabel.setStyle("-fx-text-fill: #f59e0b;");
        }
    }
    
    @FXML
    private void handleDarkThemeToggle() {
        boolean isDarkTheme = darkThemeToggle.isSelected();
        
        // Save preference
        ThemeManager.setDarkThemeEnabled(isDarkTheme);
        
        // Apply theme instantly to current scene
        if (isDarkTheme) {
            ThemeManager.applyDarkTheme(darkThemeToggle.getScene());
        } else {
            ThemeManager.applyLightTheme(darkThemeToggle.getScene());
        }
    }
    
    @FXML
    private void handleSetPrivateKey() {
        String privateKey = privateKeyField.getText().trim();
        
        if (privateKey.isEmpty()) {
            NotificationUtil.showError("Invalid Input", "Please enter a private key");
            return;
        }
        
        setPrivateKeyButton.setDisable(true);
        
        Task<String> verifyTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                // Verify the private key by getting its address
                return WalletServiceImpl.getAddressFromPrivateKey(privateKey);
            }
        };
        
        verifyTask.setOnSucceeded(e -> {
            String derivedAddress = verifyTask.getValue();
            verifyAddressField.setText(derivedAddress);
            
            // Store private key in session (in-memory only)
            SessionManager.setPrivateKey(privateKey);
            updatePrivateKeyStatus();
            
            NotificationUtil.showSuccess("Private Key Set", 
                "Private key successfully set. You can now send transactions.");
            
            privateKeyField.clear();
            setPrivateKeyButton.setDisable(false);
        });
        
        verifyTask.setOnFailed(e -> {
            setPrivateKeyButton.setDisable(false);
            Throwable exception = verifyTask.getException();
            String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
            
            NotificationUtil.showError("Invalid Private Key", 
                "Could not verify private key: " + errorMessage);
        });
        
        new Thread(verifyTask).start();
    }
    
    @FXML
    private void handleGenerateNewKeypair() {
        generateNewKeypairButton.setDisable(true);
        
        Task<WalletServiceImpl.KeypairResponse> generateTask = new Task<WalletServiceImpl.KeypairResponse>() {
            @Override
            protected WalletServiceImpl.KeypairResponse call() throws Exception {
                return WalletServiceImpl.generateKeypair();
            }
        };
        
        generateTask.setOnSucceeded(e -> {
            WalletServiceImpl.KeypairResponse keypair = generateTask.getValue();
            
            privateKeyField.setText(keypair.privateKey);
            verifyAddressField.setText(keypair.address);
            
            NotificationUtil.showSuccess("Keypair Generated", 
                "New Bitcoin keypair generated. Click 'Set Private Key' to use it.");
            
            generateNewKeypairButton.setDisable(false);
        });
        
        generateTask.setOnFailed(e -> {
            generateNewKeypairButton.setDisable(false);
            Throwable exception = generateTask.getException();
            String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
            
            NotificationUtil.showErrorWithRetry("Generation Failed", 
                "Could not generate keypair: " + errorMessage,
                this::handleGenerateNewKeypair);
        });
        
        new Thread(generateTask).start();
    }
    
    @FXML
    private void handleClearPrivateKey() {
        SessionManager.setPrivateKey(null);
        privateKeyField.clear();
        verifyAddressField.clear();
        updatePrivateKeyStatus();
        
        NotificationUtil.showInfo("Private Key Cleared", 
            "Private key removed from memory. Transactions are now disabled.");
    }
}

