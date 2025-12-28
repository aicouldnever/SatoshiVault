package com.wallet.app.controller;

import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Password setup controller - validates password strength
 */
public class PasswordSetupController {
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button nextButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label errorLabel;
    
    private static String setupPassword;
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void handleNext() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        errorLabel.setVisible(false);
        
        if (password.isEmpty()) {
            showError("Please enter a password");
            return;
        }
        
        if (confirmPassword.isEmpty()) {
            showError("Please confirm your password");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }
        
        if (!isPasswordStrong(password)) {
            showError("Password does not meet the requirements");
            return;
        }
        
        setupPassword = password;
        navigateToPassphraseGeneration();
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
            showError("Failed to load welcome view: " + e.getMessage());
        }
    }
    
    private void navigateToPassphraseGeneration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/PassphraseGenerationView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nextButton.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            
            ThemeManager.applyTheme(scene);
            
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
            stage.show();
            
            NotificationUtil.showSuccess("Password", "Password set successfully. Now generating your seed phrase...");
        } catch (IOException e) {
            showError("Failed to load passphrase generation view: " + e.getMessage());
        }
    }
    
    /**
     * Validates password meets strength requirements
     */
    private boolean isPasswordStrong(String password) {
        if (password.length() < 8) {
            return false;
        }
        
        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasLowercase = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*[0-9].*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()\\-_=+\\[\\]{};:'\",.<>?/\\\\|`~].*");
        
        return hasUppercase && hasLowercase && hasNumber && hasSpecialChar;
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    /**
     * Get stored password
     */
    public static String getSetupPassword() {
        return setupPassword;
    }
    
    /**
     * Clear password
     */
    public static void clearSetupPassword() {
        setupPassword = null;
    }
}
