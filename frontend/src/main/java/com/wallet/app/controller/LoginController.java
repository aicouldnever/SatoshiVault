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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Login controller - validates password and passphrase
 */
public class LoginController {
    
    @FXML
    private TextField passphraseField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    public void initialize() {
        checkBackendConnection();
    }
    
    private void checkBackendConnection() {
        loginButton.setDisable(true);
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
                loginButton.setDisable(false);
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
        loginButton.setDisable(true);
        
        NotificationUtil.showErrorWithRetry(
            "Backend Unavailable",
            "Cannot connect to backend server. Please start the backend.",
            this::checkBackendConnection
        );
    }
    
    @FXML
    private void handleLogin() {
        String password = passwordField.getText().trim();
        String passphrase = passphraseField.getText().trim();
        
        if (password.isEmpty()) {
            errorLabel.setText("Please enter your password");
            errorLabel.setVisible(true);
            return;
        }
        
        if (passphrase.isEmpty()) {
            errorLabel.setText("Please enter your passphrase");
            errorLabel.setVisible(true);
            return;
        }
        
        loginButton.setDisable(true);
        errorLabel.setText("Authenticating...");
        errorLabel.setVisible(true);
        
        Task<WalletServiceImpl.LoginResponse> loginTask = new Task<WalletServiceImpl.LoginResponse>() {
            @Override
            protected WalletServiceImpl.LoginResponse call() throws Exception {
                return WalletServiceImpl.login(passphrase);
            }
        };
        
        loginTask.setOnSucceeded(e -> {
            WalletServiceImpl.LoginResponse response = loginTask.getValue();
            
            SessionManager.login(response.token, response.address, null, passphrase);
            
            NotificationUtil.showSuccess("Login Successful", "Welcome to Satoshi Vault!");
            loadMainView();
        });
        
        loginTask.setOnFailed(e -> {
            loginButton.setDisable(false);
            Throwable exception = loginTask.getException();
            String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
            
            errorLabel.setText("Login failed: " + errorMessage);
            errorLabel.setVisible(true);
            
            NotificationUtil.showErrorWithRetry(
                "Login Failed",
                "Could not authenticate. " + errorMessage,
                this::handleLogin
            );
        });
        
        new Thread(loginTask).start();
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/WelcomeView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) backButton.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            
            ThemeManager.applyTheme(scene);
            
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            NotificationUtil.showError("Error", "Failed to load welcome view: " + e.getMessage());
        }
    }
    
    private void loadMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/layout/RootLayout.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) passphraseField.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            
            ThemeManager.applyTheme(scene);
            
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            NotificationUtil.showError("Error", "Failed to load main view: " + e.getMessage());
        }
    }
}
