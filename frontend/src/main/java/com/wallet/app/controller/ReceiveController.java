package com.wallet.app.controller;

import com.wallet.app.model.WalletModel;
import com.wallet.app.service.MockWalletService;
import com.wallet.app.service.WalletService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.shape.Rectangle;

public class ReceiveController {
    @FXML
    private Rectangle qrPlaceholder;
    
    @FXML
    private Label addressLabel;
    
    private WalletService walletService;
    private WalletModel wallet;
    
    @FXML
    public void initialize() {
        walletService = new MockWalletService();
        wallet = walletService.getWallet();
        
        addressLabel.setText(wallet.getAddress());
        
        // QR code would be generated here using QRCodeUtil.generate()
        // For now, we just have the placeholder rectangle
    }
    
    @FXML
    private void handleCopyAddress() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(wallet.getAddress());
        clipboard.setContent(content);
        
        showInfo("Address Copied", "Bitcoin address copied to clipboard!");
    }
    
    @FXML
    private void handleShareAddress() {
        // Placeholder for share functionality
        showInfo("Share", "Share functionality is a placeholder feature.");
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
