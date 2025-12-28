package com.wallet.app.controller;

import com.wallet.app.model.WalletModel;
import com.wallet.app.service.TransactionServiceImpl;
import com.wallet.app.service.WalletService;
import com.wallet.app.service.WalletServiceImpl;
import com.wallet.app.util.CurrencyFormatter;
import com.wallet.app.util.NotificationUtil;
import com.wallet.app.util.QRCodeUtil;
import com.wallet.app.util.SessionManager;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
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
    private TransactionServiceImpl transactionService;
    private WalletModel wallet;
    private static final double MOCK_BTC_TO_USD = 42500.00;
    private double currentNetworkFee = 0.00001000; // Will be updated dynamically
    
    @FXML
    public void initialize() {
        walletService = new WalletServiceImpl();
        transactionService = new TransactionServiceImpl();
        wallet = walletService.getWallet();
        
        updateBalanceInfo();
        
        // Add listener to amount field to update USD value and estimate fee
        amountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateUSDAmount();
            estimateFee();
        });
    }
    
    private void updateBalanceInfo() {
        balanceInfoLabel.setText(String.format("Balance: %s | Max: %s", 
            CurrencyFormatter.formatBTC(wallet.getBalanceBTC()),
            CurrencyFormatter.formatBTC(wallet.getBalanceBTC())
        ));
        
        networkFeeLabel.setText("Fee: " + CurrencyFormatter.formatBTC(currentNetworkFee));
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
    
    private void estimateFee() {
        Task<TransactionServiceImpl.FeeEstimate> feeTask = new Task<TransactionServiceImpl.FeeEstimate>() {
            @Override
            protected TransactionServiceImpl.FeeEstimate call() throws Exception {
                // Estimate for 2 inputs and 2 outputs (typical transaction)
                return TransactionServiceImpl.estimateFee(2, 2);
            }
        };
        
        feeTask.setOnSucceeded(e -> {
            TransactionServiceImpl.FeeEstimate estimate = feeTask.getValue();
            currentNetworkFee = estimate.feeBTC;
            networkFeeLabel.setText("Fee: " + CurrencyFormatter.formatBTC(currentNetworkFee));
        });
        
        feeTask.setOnFailed(e -> {
            // Keep default fee if estimation fails
        });
        
        new Thread(feeTask).start();
    }
    
    @FXML
    private void handleMaxAmount() {
        double maxAmount = wallet.getBalanceBTC() - currentNetworkFee;
        if (maxAmount > 0) {
            amountField.setText(String.format("%.8f", maxAmount));
        }
    }
    
    @FXML
    private void handleScanQR() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select QR Code Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        File file = fileChooser.showOpenDialog(amountField.getScene().getWindow());
        if (file != null) {
            String decodedAddress = QRCodeUtil.decodeFromFile(file);
            if (decodedAddress != null && !decodedAddress.isEmpty()) {
                recipientField.setText(decodedAddress);
                NotificationUtil.showSuccess("QR Scanned", "Address loaded from QR code");
            } else {
                NotificationUtil.showError("QR Scan Failed", "Could not decode QR code from image");
            }
        }
    }
    
    @FXML
    private void handleReviewTransaction() {
        errorLabel.setVisible(false);
        
        // Check if private key is set
        if (!SessionManager.hasPrivateKey()) {
            NotificationUtil.showWarning("Private Key Required", 
                "Please set your private key in Settings before sending transactions");
            return;
        }
        
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
            CurrencyFormatter.formatBTC(currentNetworkFee),
            CurrencyFormatter.formatBTC(amount + currentNetworkFee)
        ));
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                sendTransaction(recipient, amount);
            }
        });
    }
    
    private void sendTransaction(String toAddress, double amount) {
        NotificationUtil.showLoading("Sending", "Broadcasting transaction...");
        
        Task<String> sendTask = new Task<String>() {
            @Override
            protected String call() throws Exception {
                return transactionService.sendTransactionWithTxId(toAddress, amount);
            }
        };
        
        sendTask.setOnSucceeded(e -> {
            String txId = sendTask.getValue();
            NotificationUtil.showSuccess("Transaction Sent", "Transaction ID: " + txId.substring(0, 16) + "...");
            clearForm();
            
            // Poll to confirm transaction appears in history
            pollForTransaction(txId);
        });
        
        sendTask.setOnFailed(e -> {
            Throwable exception = sendTask.getException();
            String errorMessage = exception != null ? exception.getMessage() : "Unknown error";
            
            NotificationUtil.showErrorWithRetry(
                "Transaction Failed",
                "Could not send transaction: " + errorMessage,
                () -> sendTransaction(toAddress, amount)
            );
        });
        
        new Thread(sendTask).start();
    }
    
    private void pollForTransaction(String txId) {
        Task<Boolean> pollTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                // Poll every 5 seconds, up to 10 times (50 seconds total)
                return transactionService.isTransactionConfirmed(txId, 10, 5000);
            }
        };
        
        pollTask.setOnSucceeded(e -> {
            boolean confirmed = pollTask.getValue();
            if (confirmed) {
                NotificationUtil.showSuccess("Confirmed", "Transaction appeared in blockchain");
            } else {
                NotificationUtil.showInfo("Processing", "Transaction is being processed");
            }
        });
        
        new Thread(pollTask).start();
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void clearForm() {
        amountField.clear();
        recipientField.clear();
        errorLabel.setVisible(false);
    }
}

