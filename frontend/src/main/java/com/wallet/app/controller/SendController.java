package com.wallet.app.controller;

import com.wallet.app.model.WalletModel;
import com.wallet.app.service.MockWalletService;
import com.wallet.app.service.WalletService;
import com.wallet.app.util.CurrencyFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

import java.io.File;

public class SendController {
    @FXML
    private TextField amountField;
    
    @FXML
    private TextField recipientField;
    
    @FXML
    private Label balanceInfoLabel;
    
    @FXML
    private Label amountUSDLabel;
    
    @FXML
    private Label networkFeeLabel;
    
    @FXML
    private Label confirmationTimeLabel;
    
    @FXML
    private Label errorLabel;
    
    private WalletService walletService;
    private WalletModel wallet;
    private static final double MOCK_BTC_TO_USD = 42500.00;
    private static final double NETWORK_FEE = 0.00001000;
    
    @FXML
    public void initialize() {
        walletService = new MockWalletService();
        wallet = walletService.getWallet();
        
        updateBalanceInfo();
        
        // Add listener to amount field to update USD value
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateUSDAmount();
        });
    }
    
    private void updateBalanceInfo() {
        balanceInfoLabel.setText(String.format("Balance: %s | Max: %s", 
            CurrencyFormatter.formatBTC(wallet.getBalanceBTC()),
            CurrencyFormatter.formatBTC(wallet.getBalanceBTC())
        ));
        
        networkFeeLabel.setText("Fee: " + CurrencyFormatter.formatBTC(NETWORK_FEE));
        confirmationTimeLabel.setText("Estimated confirmation time: ~10 minutes");
    }
    
    private void updateUSDAmount() {
        try {
            String amountText = amountField.getText().trim();
            if (amountText.isEmpty()) {
                amountUSDLabel.setText("≈ $0.00 USD");
                return;
            }
            
            double amount = Double.parseDouble(amountText);
            double usdValue = amount * MOCK_BTC_TO_USD;
            amountUSDLabel.setText("≈ " + CurrencyFormatter.formatUSD(usdValue) + " USD");
        } catch (NumberFormatException e) {
            amountUSDLabel.setText("≈ $0.00 USD");
        }
    }
    
    @FXML
    private void handleMaxAmount() {
        double maxAmount = wallet.getBalanceBTC() - NETWORK_FEE;
        if (maxAmount > 0) {
            amountField.setText(String.format("%.8f", maxAmount));
        }
    }
    
    @FXML
    private void handleScanQR() {
        // Placeholder: Open file chooser to upload QR code image
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select QR Code Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(amountField.getScene().getWindow());
        if (file != null) {
            // Placeholder - QRCodeUtil.decode would be called here
            showInfo("QR Scan", "QR code scanning is a placeholder feature.");
        }
    }
    
    @FXML
    private void handleReviewTransaction() {
        errorLabel.setVisible(false);
        
        // Validate inputs
        String amountText = amountField.getText().trim();
        String recipient = recipientField.getText().trim();
        
        if (amountText.isEmpty()) {
            showError("Please enter an amount");
            return;
        }
        
        if (recipient.isEmpty()) {
            showError("Please enter a recipient address");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountText);
            
            if (amount <= 0) {
                showError("Amount must be greater than 0");
                return;
            }
            
            if (amount > wallet.getBalanceBTC()) {
                showError("Insufficient balance");
                return;
            }
            
            // Show confirmation dialog
            showConfirmationDialog(amount, recipient);
            
        } catch (NumberFormatException e) {
            showError("Invalid amount format");
        }
    }
    
    private void showConfirmationDialog(double amount, String recipient) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Review Transaction");
        alert.setHeaderText("Please review your transaction");
        alert.setContentText(String.format(
            "Amount: %s\nRecipient: %s\nNetwork Fee: %s\n\nTotal: %s",
            CurrencyFormatter.formatBTC(amount),
            recipient,
            CurrencyFormatter.formatBTC(NETWORK_FEE),
            CurrencyFormatter.formatBTC(amount + NETWORK_FEE)
        ));
        
        alert.showAndWait().ifPresent(response -> {
            if (response.getText().equals("OK")) {
                // Placeholder - transaction would be sent here
                showInfo("Success", "Transaction review completed. (This is a mock - no actual transaction sent)");
                clearForm();
            }
        });
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void clearForm() {
        amountField.clear();
        recipientField.clear();
        errorLabel.setVisible(false);
    }
}
