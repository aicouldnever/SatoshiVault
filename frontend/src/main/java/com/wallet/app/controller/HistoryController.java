package com.wallet.app.controller;

import com.wallet.app.model.TransactionModel;
import com.wallet.app.model.TransactionModel.Status;
import com.wallet.app.service.MockTransactionService;
import com.wallet.app.service.TransactionService;
import com.wallet.app.util.CurrencyFormatter;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class HistoryController {
    @FXML
    private TextField searchField;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private ListView<String> transactionList;
    
    @FXML
    private Button filterAll;
    @FXML
    private Button filterSent;
    @FXML
    private Button filterReceived;
    @FXML
    private Button filterPending;
    
    private TransactionService transactionService;
    private List<TransactionModel> allTransactions;
    private String currentFilter = "ALL";
    
    @FXML
    public void initialize() {
        transactionService = new MockTransactionService();
        allTransactions = transactionService.getTransactions();
        
        loadTransactions();
        
        // Add listeners for search and date picker
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTransactions();
        });
        
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterTransactions();
        });
        
        // Add click listener for transaction details
        transactionList.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                String selectedItem = transactionList.getSelectionModel().getSelectedItem();
                if (selectedItem != null) {
                    showTransactionDetails(selectedItem);
                }
            }
        });
    }
    
    private void loadTransactions() {
        filterTransactions();
    }
    
    private void filterTransactions() {
        List<TransactionModel> filtered = allTransactions;
        
        // Filter by status
        if (!currentFilter.equals("ALL")) {
            Status filterStatus = Status.valueOf(currentFilter);
            filtered = filtered.stream()
                .filter(tx -> tx.getStatus() == filterStatus)
                .collect(Collectors.toList());
        }
        
        // Filter by search text
        String searchText = searchField.getText().trim().toLowerCase();
        if (!searchText.isEmpty()) {
            filtered = filtered.stream()
                .filter(tx -> tx.getTxId().toLowerCase().contains(searchText) ||
                            String.valueOf(tx.getAmount()).contains(searchText))
                .collect(Collectors.toList());
        }
        
        // Filter by date
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate != null) {
            filtered = filtered.stream()
                .filter(tx -> tx.getDate().toLocalDate().equals(selectedDate))
                .collect(Collectors.toList());
        }
        
        displayTransactions(filtered);
    }
    
    private void displayTransactions(List<TransactionModel> transactions) {
        transactionList.getItems().clear();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        DateTimeFormatter dayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        
        // Group by day
        String currentDay = "";
        
        for (TransactionModel tx : transactions) {
            String txDay = tx.getDate().format(dayFormatter);
            
            // Add day separator
            if (!txDay.equals(currentDay)) {
                currentDay = txDay;
                transactionList.getItems().add("─────── " + txDay + " ───────");
            }
            
            // Format transaction
            String statusIcon = getStatusIcon(tx.getStatus());
            String amount = CurrencyFormatter.formatBTCAmount(tx.getAmount());
            String time = tx.getDate().format(DateTimeFormatter.ofPattern("HH:mm"));
            String txIdShort = tx.getTxId().substring(0, 16) + "...";
            
            String item = String.format("%s %s | %s | %s", 
                statusIcon, tx.getStatus(), amount, time);
            
            transactionList.getItems().add(item);
        }
        
        if (transactions.isEmpty()) {
            transactionList.getItems().add("No transactions found");
        }
    }
    
    private String getStatusIcon(Status status) {
        switch (status) {
            case RECEIVED: return "↓";
            case SENT: return "↑";
            case PENDING: return "⏳";
            default: return "•";
        }
    }
    
    @FXML
    private void handleFilterAll() {
        currentFilter = "ALL";
        updateActiveFilter(filterAll);
        filterTransactions();
    }
    
    @FXML
    private void handleFilterSent() {
        currentFilter = "SENT";
        updateActiveFilter(filterSent);
        filterTransactions();
    }
    
    @FXML
    private void handleFilterReceived() {
        currentFilter = "RECEIVED";
        updateActiveFilter(filterReceived);
        filterTransactions();
    }
    
    @FXML
    private void handleFilterPending() {
        currentFilter = "PENDING";
        updateActiveFilter(filterPending);
        filterTransactions();
    }
    
    private void updateActiveFilter(Button activeButton) {
        filterAll.setStyle("");
        filterSent.setStyle("");
        filterReceived.setStyle("");
        filterPending.setStyle("");
        
        activeButton.setStyle("-fx-background-color: #0078d4; -fx-text-fill: white;");
    }
    
    private void showTransactionDetails(String transactionItem) {
        // Placeholder for transaction details dialog
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Transaction Details");
        alert.setHeaderText("Transaction Information");
        alert.setContentText("Transaction details dialog is a placeholder feature.\n\n" + transactionItem);
        alert.showAndWait();
    }
}
