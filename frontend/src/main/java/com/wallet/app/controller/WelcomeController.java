package com.wallet.app.controller;

import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Welcome controller - login/register entry point
 */
public class WelcomeController {
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Label appNameLabel;
    
    @FXML
    public void initialize() {
    }
    
    @FXML
    private void handleLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/LoginView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            
            ThemeManager.applyTheme(scene);
            
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            NotificationUtil.showError("Error", "Failed to load login view: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/PasswordSetupView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) registerButton.getScene().getWindow();
            Scene scene = new Scene(root, 1100, 700);
            
            ThemeManager.applyTheme(scene);
            
            stage.setResizable(false);
            stage.setFullScreen(false);
            stage.setWidth(900);
            stage.setHeight(650);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            NotificationUtil.showError("Error", "Failed to load registration view: " + e.getMessage());
        }
    }
}
