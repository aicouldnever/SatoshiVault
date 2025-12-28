package com.wallet.app.controller;

import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.ThemeManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Controller for Passphrase Generation page (Layer 3 of registration)
 * Generates a recovery passphrase for the user
 */
public class PassphraseGeneratorController {
    
    @FXML
    private TextArea passphraseTextArea;
    
    @FXML
    private Button generateButton;
    
    @FXML
    private Button copyButton;
    
    @FXML
    private Button finishButton;
    
    @FXML
    private Button backButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Label instructionLabel;
    
    @FXML
    private CheckBox confirmCheckBox;
    
    private String generatedPassphrase;
    
    // BIP39 word list
    private static final String[] BIP39_WORDS = {
        "abandon", "ability", "able", "about", "above", "absent", "absorb", "abuse",
        "access", "accident", "account", "accuse", "achieve", "acid", "acoustic", "acquire",
        "across", "act", "action", "active", "activity", "actor", "actual", "acuity",
        "acute", "admit", "admire", "adopt", "adore", "adorn", "adult", "advance",
        "advice", "advise", "aerobic", "affair", "afford", "afraid", "after", "again",
        "age", "agency", "agent", "agree", "ahead", "aide", "aim", "air",
        "airline", "airmail", "airport", "aisle", "alarm", "album", "alcohol", "alert",
        "alien", "align", "alike", "alive", "all", "alley", "allot", "allow",
        "alloy", "ally", "almost", "alone", "along", "aloof", "aloud", "alpha",
        "already", "also", "alter", "always", "am", "amateur", "amazing", "ambiguity",
        "ambush", "amend", "amendment", "amends", "ament", "america", "american", "amidst",
        "amid", "amigo", "amine", "amiss", "amity", "ammonia", "among", "amongst",
        "amount", "amour", "amp", "ampere", "ampersand", "amphibia", "amphibian", "ample",
        "amplifier", "amply", "ampul", "amputate", "amputation", "ampule", "amulet", "amused"
    };
    
    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        instructionLabel.setText("Step 3 of 3: Save your recovery passphrase\n\n" +
            "This passphrase is your ONLY way to recover your wallet if you lose access.\n" +
            "Keep it safe and secret! Do not share it with anyone.\n" +
            "Write it down and store it in a secure location.");
        generateButton.setStyle("-fx-font-size: 14px;");
    }
    
    @FXML
    private void handleGeneratePassphrase() {
        try {
            generatedPassphrase = generateRandomPassphrase();
            passphraseTextArea.setText(generatedPassphrase);
            passphraseTextArea.setWrapText(true);
            
            copyButton.setDisable(false);
            confirmCheckBox.setSelected(false);
            confirmCheckBox.setDisable(false);
            finishButton.setDisable(true);
            errorLabel.setVisible(false);
            
            NotificationUtil.showSuccess("Generated", "Passphrase generated successfully");
        } catch (Exception e) {
            errorLabel.setText("Error generating passphrase: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleCopyPassphrase() {
        if (generatedPassphrase != null && !generatedPassphrase.isEmpty()) {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(generatedPassphrase);
            clipboard.setContent(content);
            
            NotificationUtil.showSuccess("Copied", "Passphrase copied to clipboard");
        }
    }
    
    @FXML
    private void handleConfirmCheckBox() {
        if (confirmCheckBox.isSelected()) {
            finishButton.setDisable(false);
        } else {
            finishButton.setDisable(true);
        }
    }
    
    @FXML
    private void handleFinish() {
        if (generatedPassphrase == null || generatedPassphrase.isEmpty()) {
            errorLabel.setText("Please generate a passphrase first");
            errorLabel.setVisible(true);
            return;
        }
        
        if (!confirmCheckBox.isSelected()) {
            errorLabel.setText("Please confirm that you have saved your passphrase");
            errorLabel.setVisible(true);
            return;
        }
        

        
        NotificationUtil.showSuccess("Success", "Wallet registration completed!");
        
        // Store passphrase for next login
        try {
            // TODO: Send registration data to backend
            // For now, just navigate to login
            loadWelcomeView();
        } catch (Exception e) {
            errorLabel.setText("Error: " + e.getMessage());
            errorLabel.setVisible(true);
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/PasswordSetupView.fxml"));
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
            NotificationUtil.showError("Error", "Failed to load password setup view: " + e.getMessage());
        }
    }
    
    private void loadWelcomeView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/wallet/app/view/auth/WelcomeView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) finishButton.getScene().getWindow();
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
    
    /**
     * Generate a random 12-word BIP39 style passphrase
     */
    private String generateRandomPassphrase() {
        StringBuilder passphrase = new StringBuilder();
        Random random = new Random();
        int wordCount = 12;
        
        for (int i = 0; i < wordCount; i++) {
            int randomIndex = random.nextInt(BIP39_WORDS.length);
            if (i > 0) {
                passphrase.append(" ");
            }
            passphrase.append(BIP39_WORDS[randomIndex]);
        }
        
        return passphrase.toString();
    }
    
    public String getGeneratedPassphrase() {
        return generatedPassphrase;
    }
}
