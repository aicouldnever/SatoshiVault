package com.wallet.app.controller;

import com.wallet.app.service.ApiClient;
import com.wallet.app.service.WalletServiceImpl;
import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.SessionManager;
import com.wallet.app.util.ThemeManager;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class AuthController {
    @FXML
    private TextField recoveryPhraseField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button unlockButton;
    
    private ProgressIndicator loadingIndicator;
    
    @FXML
    public void initialize() {
        // Check backend health on startup
        checkBackendConnection();
    }
    
    private void checkBackendConnection() {
        unlockButton.setDisable(true);
        errorLabel.setText("Connecting to backend...");
        errorLabel.setVisible(true);
        
        Task<Boolean> healthCheckTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return ApiClient.checkBackendHealth();
            }
        };
        
        healthCheckTask.setOnSucceeded(e -> {
            boolean isHealthy = healthCheckTask.getValue();
            if (isHealthy) {
                unlockButton.setDisable(false);
                errorLabel.setVisible(false);
                NotificationUtil.showSuccess("Connected", "Backend is ready");
            } else {
                showBackendError();
            }
        });
        
        healthCheckTask.setOnFailed(e -> {
            showBackendError();
        });
        
        new Thread(healthCheckTask).start();
    }
    
    private void showBackendError() {
        errorLabel.setText("Cannot connect to backend. Please ensure the backend is running on http://localhost:8080");
        errorLabel.setVisible(true);
        unlockButton.setDisable(true);
        
        NotificationUtil.showErrorWithRetry(
            "Backend Unavailable",
            "Cannot connect to backend server. Please start the backend.",
            this::checkBackendConnection
        );
    }
    
    @FXML
    private void handleUnlock() {
        String phrase = recoveryPhraseField.getText().trim();
        
        // Simple validation: non-empty only
        if (phrase.isEmpty()) {
            errorLabel.setText("Please enter your recovery phrase");
            errorLabel.setVisible(true);
            return;
        }
        
        // Disable button and show loading
        unlockButton.setDisable(true);
        errorLabel.setText("Authenticating...");
        errorLabel.setVisible(true);
        
        // Perform login in background thread
        Task<WalletServiceImpl.LoginResponse> loginTask = new Task<WalletServiceImpl.LoginResponse>() {
            @Override
            protected WalletServiceImpl.LoginResponse call() throws Exception {
                return WalletServiceImpl.login(phrase);
            }
        };
        
        loginTask.setOnSucceeded(e -> {
            WalletServiceImpl.LoginResponse response = loginTask.getValue();
            
            // Store session data (in-memory only, no private key yet)
            SessionManager.login(response.token, response.address, null, phrase);
            
            NotificationUtil.showSuccess("Login Successful", "Welcome to your Bitcoin wallet!");
            loadMainView();
        });
        
        loginTask.setOnFailed(e -> {
            unlockButton.setDisable(false);
            Throwable exception = loginTask.getException();
            String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
            
            errorLabel.setText("Login failed: " + errorMessage);
            errorLabel.setVisible(true);
            
            NotificationUtil.showErrorWithRetry(
                "Login Failed",
                "Could not authenticate. " + errorMessage,
                this::handleUnlock
            );
        });
        
        new Thread(loginTask).start();
    }
    
    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/layout/RootLayout.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) recoveryPhraseField.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            
            // Apply theme based on user preference
            ThemeManager.applyTheme(scene);
            
            // Set fixed window size and prevent fullscreen
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
            stage.setTitle("Bitcoin Wallet");
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Failed to load main view");
            errorLabel.setVisible(true);
            NotificationUtil.showError("Error", "Failed to load wallet interface");
        }
    }
}

