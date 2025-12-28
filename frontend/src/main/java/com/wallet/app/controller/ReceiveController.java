package com.wallet.app.controller;

import com.wallet.app.model.WalletModel;
import com.wallet.app.service.WalletService;
import com.wallet.app.service.WalletServiceImpl;
import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.QRCodeUtil;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.shape.Rectangle;

public class ReceiveController {
    @FXML
    private ImageView qrPlaceholder;
    
    @FXML
    private Label addressLabel;
    
    private WalletService walletService;
    private WalletModel wallet;
    
    @FXML
    public void initialize() {
        walletService = new WalletServiceImpl();
        wallet = walletService.getWallet();
        
        addressLabel.setText(wallet.getAddress());
        
        // Generate QR code for the wallet address
        generateQRCode();
    }
    
    private void generateQRCode() {
        try {
            String address = wallet.getAddress();
            if (address != null && !address.isEmpty()) {
                Image qrImage = QRCodeUtil.generate(address);
                if (qrImage != null) {
                    qrPlaceholder.setImage(qrImage);
                    qrPlaceholder.setFitWidth(300);
                    qrPlaceholder.setFitHeight(300);
                    qrPlaceholder.setPreserveRatio(true);
                } else {
                    NotificationUtil.showError("QR Code Error", "Failed to generate QR code");
                }
            }
        } catch (Exception e) {
            NotificationUtil.showError("QR Code Error", "Error generating QR code: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleCopyAddress() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(wallet.getAddress());
        clipboard.setContent(content);
        
        NotificationUtil.showSuccess("Address Copied", "Bitcoin address copied to clipboard!");
    }
    
    @FXML
    private void handleShareAddress() {
        // Placeholder for share functionality
        NotificationUtil.showInfo("Share", "Share functionality is a placeholder feature.");
    }
}

